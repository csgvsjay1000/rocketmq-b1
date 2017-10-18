package invengo.cn.rocketmq.remoting.netty;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import invengo.cn.rocketmq.remoting.RemotingClient;
import invengo.cn.rocketmq.remoting.common.RemotingHelper;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;


public class NettyRemotingClient extends NettyRemotingAbstract implements RemotingClient{

	private static Logger logger = LogManager.getLogger(NettyRemotingClient.class);

	private NettyClientConfig nettyClientConfig;
	private Bootstrap bootstrap = new Bootstrap();
	private EventLoopGroup eventLoopGroupWorker;
	private DefaultEventExecutorGroup defaultEventExecutorGroup;
	
	private final ConcurrentMap<String/*addr*/, ChannelWrapper> channelTables = new ConcurrentHashMap<String, NettyRemotingClient.ChannelWrapper>();
	
	public NettyRemotingClient(NettyClientConfig nettyClientConfig) {
		this.nettyClientConfig = nettyClientConfig;
		
		this.eventLoopGroupWorker = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("NettyClientSelector_%d", this.threadIndex.incrementAndGet()));
            }
        });
		
	}
	
	
	public void start() {
		this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(nettyClientConfig.getClientWorkerThreads(), new ThreadFactory() {
			private AtomicInteger threadIndex = new AtomicInteger(0);
			public Thread newThread(Runnable r) {
                return new Thread(r, "NettyClientWorkerThread_" + this.threadIndex.incrementAndGet());
			}
		});
		
		Bootstrap handler = this.bootstrap.group(eventLoopGroupWorker).channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_KEEPALIVE, false)
				.option(ChannelOption.SO_SNDBUF, nettyClientConfig.getClientSocketSndBufSize())
				.option(ChannelOption.SO_RCVBUF, nettyClientConfig.getClientSocketRcvBufSize())
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyClientConfig.getConnectTimeoutMillis())
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(defaultEventExecutorGroup,
								new NettyEncoder(),
								new NettyDecoder(),
								new NettyConnectManagerHandler(),
								new NettyClientHandler());
						
					}
				});
				
		
	}

	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
	private Channel createChannel(final String addr){
		ChannelWrapper cw = null;
		try {
			ChannelFuture channelFuture = this.bootstrap.connect(RemotingHelper.string2SocketAddress(addr));
			logger.info("createChannel: connect remote host [{}]",addr);
			cw = new ChannelWrapper(channelFuture);
			this.channelTables.put(addr, cw);
		} catch (Exception e) {
			logger.error("createChannel: exception",e);
		}
		
		if (cw != null) {
			ChannelFuture channelFuture = cw.getChannelFuture();
			if(channelFuture.awaitUninterruptibly(nettyClientConfig.getConnectTimeoutMillis())){
				if (cw.isOK()) {
					logger.info("createChannel: connect remote host [{}] success, {} ",addr,channelFuture.toString());
					return channelFuture.channel();
				}else {
					logger.warn("createChannel: connect remote host ["+addr+"] failed, "+channelFuture.toString());
				}
			}else {
				logger.warn("createChannel: connect remote host [{}] timeout {}ms , ",addr,nettyClientConfig.getConnectTimeoutMillis(),
						channelFuture.toString());
			}
		}
		
		
		
		return null;
	}
	
	public void invokeOneway(String addr, RemotingCommand request, long timeoutMills) {
		Channel channel = createChannel(addr);
		
		this.invokeOnewayImpl(channel, request, timeoutMills);
		
	}
	
	class NettyConnectManagerHandler extends ChannelDuplexHandler{
		
		@Override
		public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress,
				ChannelPromise promise) throws Exception {
			
			String local = localAddress == null ? "unkonw" : RemotingHelper.parseSocketAddress(localAddress);
			String remote = RemotingHelper.parseSocketAddress(remoteAddress);
			logger.info("local: "+local);
			logger.info("remote: "+remote);
			super.connect(ctx, remoteAddress, localAddress, promise);
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			
			final String remoteAddr = RemotingHelper.parseSocketAddress(ctx.channel().remoteAddress());
			logger.info("Netty client pipeline exceptionCaught {}",remoteAddr);
			logger.info("Netty client pipeline exceptionCaught exception.",cause);
			
			super.exceptionCaught(ctx, cause);
		}
		
	}
	
	class NettyClientHandler extends SimpleChannelInboundHandler<RemotingCommand>{

		@Override
		protected void channelRead0(ChannelHandlerContext arg0, RemotingCommand command) throws Exception {
			// TODO Auto-generated method stub
			System.out.println(command.getCode());
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			super.channelActive(ctx);
		}
		
	}
	
	static class ChannelWrapper{
		private final ChannelFuture channelFuture;
		public ChannelWrapper(ChannelFuture channelFuture) {
			this.channelFuture = channelFuture;
		}
		public ChannelFuture getChannelFuture() {
			return channelFuture;
		}
		
		public boolean isOK() {
			return this.channelFuture.channel() != null && this.channelFuture.channel().isActive();
		}
		
		public boolean isWriteable() {
			return this.channelFuture.channel().isWritable();
		}
	}

	

}
