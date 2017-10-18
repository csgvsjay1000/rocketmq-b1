package invengo.cn.rocketmq.common;

public class BrokerConfig {

	private String namesrvAddr;
	
	private int sendMessageThreadPoolNums = 1; // 16+Runtime.getRuntime().availableProcessors() * 4;
	private int pullMessageThreadPoolNums = 16+Runtime.getRuntime().availableProcessors() * 2;
	private int sendThreadPoolQueueCapacity = 10000;  //发送消息线程池队列容量
	private int pullThreadPoolQueueCapacity = 100000;  //拉去消息线程池队列容量
	

	public String getNamesrvAddr() {
		return namesrvAddr;
	}

	public void setNamesrvAddr(String namesrvAddr) {
		this.namesrvAddr = namesrvAddr;
	}

	public int getSendMessageThreadPoolNums() {
		return sendMessageThreadPoolNums;
	}

	public void setSendMessageThreadPoolNums(int sendMessageThreadPoolNums) {
		this.sendMessageThreadPoolNums = sendMessageThreadPoolNums;
	}

	public int getPullMessageThreadPoolNums() {
		return pullMessageThreadPoolNums;
	}

	public void setPullMessageThreadPoolNums(int pullMessageThreadPoolNums) {
		this.pullMessageThreadPoolNums = pullMessageThreadPoolNums;
	}

	public int getSendThreadPoolQueueCapacity() {
		return sendThreadPoolQueueCapacity;
	}

	public void setSendThreadPoolQueueCapacity(int sendThreadPoolQueueCapacity) {
		this.sendThreadPoolQueueCapacity = sendThreadPoolQueueCapacity;
	}

	public int getPullThreadPoolQueueCapacity() {
		return pullThreadPoolQueueCapacity;
	}

	public void setPullThreadPoolQueueCapacity(int pullThreadPoolQueueCapacity) {
		this.pullThreadPoolQueueCapacity = pullThreadPoolQueueCapacity;
	}
	
	
}
