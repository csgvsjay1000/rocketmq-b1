package invengo.cn.rocketmq.remoting.test;

import invengo.cn.rocketmq.remoting.RemotingServer;
import invengo.cn.rocketmq.remoting.netty.NettyRemotingServer;
import invengo.cn.rocketmq.remoting.netty.NettyServerConfig;

public class RemotingTest {
	
	RemotingServer remotingServer;

	public RemotingTest() {
		NettyServerConfig nettyServerConfig = new NettyServerConfig();
		this.remotingServer = new NettyRemotingServer(nettyServerConfig);	
	}
	
	public void start() {
		this.remotingServer.start();
	}
	
	public static void main(String[] args) {
		main0(args);
	}
	
	public static void main0(String[] args) {
		new RemotingTest().start();
	}
	
}
