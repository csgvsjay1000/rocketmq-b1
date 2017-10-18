package invengo.cn.rocketmq.namesrv;

import invengo.cn.rocketmq.remoting.RemotingServer;
import invengo.cn.rocketmq.remoting.netty.NettyRemotingServer;
import invengo.cn.rocketmq.remoting.netty.NettyServerConfig;

public class NamesrvController {
	
	final NettyServerConfig nettyServerConfig;
	
	RemotingServer remotingServer;
	
	public NamesrvController() {
		this.nettyServerConfig = new NettyServerConfig();
	}
	
	public void initialize() {
		this.remotingServer = new NettyRemotingServer(nettyServerConfig);

	}
	
	public void registerProcessor() {
		//this.remotingServer
	}

	public void start() {
		
	}
	
	public void shutdown() {
		
	}
	
}
