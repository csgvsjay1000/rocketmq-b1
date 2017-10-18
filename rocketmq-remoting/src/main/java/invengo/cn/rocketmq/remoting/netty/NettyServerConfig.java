package invengo.cn.rocketmq.remoting.netty;

public class NettyServerConfig implements Cloneable{

	private int listenPort = 8888;
	private int serverWorkerThreads = 8;
	private int serverSelectorThreads = 3;
	private int serverSocketSndBufSize = 65565;  //64K-1
	private int serverSocketRcvBufSize = 65565;  //64K-1
	
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return (NettyServerConfig) super.clone();
	}

	public int getListenPort() {
		return listenPort;
	}

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	public int getServerWorkerThreads() {
		return serverWorkerThreads;
	}

	public void setServerWorkerThreads(int serverWorkerThreads) {
		this.serverWorkerThreads = serverWorkerThreads;
	}

	public int getServerSelectorThreads() {
		return serverSelectorThreads;
	}

	public void setServerSelectorThreads(int serverSelectorThreads) {
		this.serverSelectorThreads = serverSelectorThreads;
	}

	public int getServerSocketSndBufSize() {
		return serverSocketSndBufSize;
	}

	public void setServerSocketSndBufSize(int serverSocketSndBufSize) {
		this.serverSocketSndBufSize = serverSocketSndBufSize;
	}

	public int getServerSocketRcvBufSize() {
		return serverSocketRcvBufSize;
	}

	public void setServerSocketRcvBufSize(int serverSocketRcvBufSize) {
		this.serverSocketRcvBufSize = serverSocketRcvBufSize;
	}
	
	
}
