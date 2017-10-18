package invengo.cn.rocketmq.remoting.netty;

import invengo.cn.rocketmq.common.log.Logger;
import invengo.cn.rocketmq.common.log.LoggerFactory;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public abstract class NettyRemotingAbstract {

	private static Logger logger = LoggerFactory.getLogger(NettyRemotingAbstract.class);
	
	protected void invokeOnewayImpl(final Channel channel,final RemotingCommand request,long timeoutMills) {
		
		channel.writeAndFlush(request).addListener(new ChannelFutureListener(){

			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					logger.getLogger().info("send a command request to channel <"+channel.remoteAddress()+"> complete.");
				}else {
					logger.getLogger().warn("send a command request to channel <"+channel.remoteAddress()+"> failed.");
				}
			}
			
		});
	}
	
}
