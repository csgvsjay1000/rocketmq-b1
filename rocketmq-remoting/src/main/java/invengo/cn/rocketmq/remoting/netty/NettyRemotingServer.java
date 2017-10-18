package invengo.cn.rocketmq.remoting.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import invengo.cn.rocketmq.remoting.RemotingServer;
import invengo.cn.rocketmq.remoting.common.RemotingHelper;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

public class NettyRemotingServer extends NettyRemotingAbstract implements RemotingServer{

	private final NettyServerConfig nettyServerConfig;
	
	private ServerBootstrap serverBootstrap;
	private EventLoopGroup eventLoopGroupSelector;
	private EventLoopGroup eventLoopGroupBoss;
	
	private DefaultEventExecutorGroup defaultEventExecutorGroup;
	
	private static Logger logger = LogManager.getLogger(NettyRemotingServer.class);
	
	private int port;
		
	public NettyRemotingServer(final NettyServerConfig nettyServerConfig) {
		
		System.out.println("NettyRemotingServer");
		this.nettyServerConfig = nettyServerConfig;
		this.serverBootstrap = new ServerBootstrap();
		
		this.eventLoopGroupBoss = new NioEventLoopGroup(1, new ThreadFactory() {
			
			public Thread newThread(Runnable r) {
				return new Thread(r, "NettyBoss_1");
			}
		});
		
		this.eventLoopGroupSelector = new NioEventLoopGroup(nettyServerConfig.getServerSelectorThreads(), new ThreadFactory() {
			private AtomicInteger threadIndex = new AtomicInteger(0);
			private int threadTotal = nettyServerConfig.getServerSelectorThreads();
			public Thread newThread(Runnable r) {
				
                return new Thread(r, String.format("NettyServerNIOSelector_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
			}
		});
		
		// 只有linux平台才支持epoll
		/*this.eventLoopGroupSelector = new EpollEventLoopGroup(nettyServerConfig.getServerSelectorThreads(), new ThreadFactory() {
			private AtomicInteger threadIndex = new AtomicInteger(0);
			private int threadTotal = nettyServerConfig.getServerSelectorThreads();
			public Thread newThread(Runnable r) {
				
                return new Thread(r, String.format("NettyServerEPOLLSelector_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
			}
		});*/
	}
	
	public void start() {
		this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(nettyServerConfig.getServerWorkerThreads(), new ThreadFactory() {
			private AtomicInteger threadIndex = new AtomicInteger(0);
			public Thread newThread(Runnable r) {
                return new Thread(r, "NettyServerCodecThread_" + this.threadIndex.incrementAndGet());
			}
		});
		
		ServerBootstrap childHandler = this.serverBootstrap.group(eventLoopGroupBoss, eventLoopGroupSelector)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.option(ChannelOption.SO_REUSEADDR, true)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSndBufSize())
				.childOption(ChannelOption.SO_RCVBUF, nettyServerConfig.getServerSocketRcvBufSize())
				.localAddress(new InetSocketAddress(nettyServerConfig.getListenPort()))
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(defaultEventExecutorGroup,
								new NettyEncoder(),
								new NettyDecoder(),
								new NettyConnectManagerHandler(),
								new NettyServerHandler());
						
					}
				});
		
		try {
			ChannelFuture sync = this.serverBootstrap.bind().sync();
			InetSocketAddress socketAddress = (InetSocketAddress) sync.channel().localAddress();
			logger.info("Netty server bind: "+socketAddress.toString());
			this.port = socketAddress.getPort();
		} catch (InterruptedException e) {
			throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e);
		}
		
	}

	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
	class NettyConnectManagerHandler extends ChannelDuplexHandler{
		
		@Override
		public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
			final String remoteAddr = RemotingHelper.parseSocketAddress(ctx.channel().remoteAddress());
			logger.info("Netty server channelRegistered {}",remoteAddr);
			super.channelRegistered(ctx);
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			final String remoteAddr = RemotingHelper.parseSocketAddress(ctx.channel().remoteAddress());
			logger.info("Netty server channelActive {}",remoteAddr);
			super.channelActive(ctx);
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			
			final String remoteAddr = RemotingHelper.parseSocketAddress(ctx.channel().remoteAddress());
			logger.info("Netty server exceptionCaught {}",remoteAddr);
			logger.info("Netty server exceptionCaught exception.",cause);

			super.exceptionCaught(ctx, cause);
		}
		
	}
	
	class NettyServerHandler extends SimpleChannelInboundHandler<RemotingCommand>{

		@Override
		protected void channelRead0(ChannelHandlerContext arg0, RemotingCommand command) throws Exception {
			
			System.out.println(command.getCode());
			
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			super.channelActive(ctx);
		}
		
	}

}
