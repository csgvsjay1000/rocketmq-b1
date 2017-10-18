package invengo.cn.rocketmq.remoting.test;

import invengo.cn.rocketmq.remoting.RemotingClient;
import invengo.cn.rocketmq.remoting.netty.NettyClientConfig;
import invengo.cn.rocketmq.remoting.netty.NettyRemotingClient;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;

public class RemotingClientTest {
	
	RemotingClient remotingClient;
	
	public RemotingClientTest() {
		NettyClientConfig clientConfig = new NettyClientConfig();
		remotingClient = new NettyRemotingClient(clientConfig);
	}
	
	public void start() {
		remotingClient.start();
		
		new Thread(new Runnable() {
			
			public void run() {
				try {
					
					while (true) {
						RemotingCommand command = new RemotingCommand();
						command.setCode(2);
						remotingClient.invokeOneway("127.0.0.1:8888", command, 3000);
						Thread.sleep(1000*60*5); //5分钟
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
			}
		}).start();
		
	}

	public static void main(String[] args) {
		new RemotingClientTest().start();
	}
	
}
