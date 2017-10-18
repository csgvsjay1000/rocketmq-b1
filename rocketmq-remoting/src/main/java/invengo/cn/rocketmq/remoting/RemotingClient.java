package invengo.cn.rocketmq.remoting;

import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;

public interface RemotingClient extends RemotingService{

	public void invokeOneway(final String addr,final RemotingCommand request,final long timeoutMills);
	
}
