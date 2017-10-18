package invengo.cn.rocketmq.broker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import invengo.cn.rocketmq.broker.filter.ConsumerFilterManager;
import invengo.cn.rocketmq.broker.latency.BrokerFixedThreadPoolExecutor;
import invengo.cn.rocketmq.broker.offset.ConsumerOffsetManager;
import invengo.cn.rocketmq.broker.subscription.SubscriptionGroupManager;
import invengo.cn.rocketmq.broker.topic.TopicConfigManager;
import invengo.cn.rocketmq.common.BrokerConfig;
import invengo.cn.rocketmq.common.ThreadFactoryImpl;
import invengo.cn.rocketmq.remoting.RemotingServer;
import invengo.cn.rocketmq.remoting.netty.NettyClientConfig;
import invengo.cn.rocketmq.remoting.netty.NettyRemotingServer;
import invengo.cn.rocketmq.remoting.netty.NettyServerConfig;
import invengo.cn.rocketmq.store.config.MessageStoreConfig;

public class BrokerController {
	
	final BrokerConfig brokerConfig;
	final NettyServerConfig nettyServerConfig;
	final NettyClientConfig nettyClientConfig;
	final MessageStoreConfig messageStoreConfig;
	
	private final ConsumerOffsetManager consumerOffsetManager;
	private final SubscriptionGroupManager subscriptionGroupManager;
	private final ConsumerFilterManager consumerFilterManager;
	
	private TopicConfigManager topicConfigManager;
	private RemotingServer remotingServer;
	private RemotingServer fastRemotingServer;
	
	private ExecutorService sendMessageExecutor;
	private ExecutorService pullMessageExecutor;
	
	private BlockingQueue<Runnable> sendThreadPoolQueue;
	private BlockingQueue<Runnable> pullThreadPoolQueue;

	public BrokerController(final BrokerConfig brokerConfig,
			final NettyServerConfig nettyServerConfig,
			final NettyClientConfig nettyClientConfig,
			final MessageStoreConfig messageStoreConfig) {
		this.brokerConfig = brokerConfig;
		this.messageStoreConfig = messageStoreConfig;
		this.nettyClientConfig = nettyClientConfig;
		this.nettyServerConfig = nettyServerConfig;
		
		this.consumerOffsetManager = new ConsumerOffsetManager(this);
		this.subscriptionGroupManager = new SubscriptionGroupManager(this);
		this.consumerFilterManager = new ConsumerFilterManager(this);
		
		this.topicConfigManager = new TopicConfigManager(this);
		
		this.sendThreadPoolQueue = new LinkedBlockingQueue<Runnable>(this.brokerConfig.getSendThreadPoolQueueCapacity());
		this.pullThreadPoolQueue = new LinkedBlockingQueue<Runnable>(this.brokerConfig.getPullThreadPoolQueueCapacity());
		
	}
	
	public boolean initialize() throws CloneNotSupportedException  {
		boolean result = this.topicConfigManager.load();
		result = result && this.consumerOffsetManager.load();
		result = result && this.subscriptionGroupManager.load();
		result = result && this.consumerFilterManager.load();
		
		if (result) {
			this.remotingServer = new NettyRemotingServer(nettyServerConfig);
			NettyServerConfig fastConfig = (NettyServerConfig) this.nettyServerConfig.clone();
			this.fastRemotingServer = new NettyRemotingServer(fastConfig);
			
			this.sendMessageExecutor = new BrokerFixedThreadPoolExecutor(this.brokerConfig.getSendMessageThreadPoolNums(),
					this.brokerConfig.getSendMessageThreadPoolNums(), 1000*60, TimeUnit.MILLISECONDS, this.sendThreadPoolQueue, 
					new ThreadFactoryImpl("SendMessageThread_"));
			
			this.pullMessageExecutor = new BrokerFixedThreadPoolExecutor(this.brokerConfig.getPullMessageThreadPoolNums(),
					this.brokerConfig.getPullMessageThreadPoolNums(), 1000*60, TimeUnit.MILLISECONDS, this.pullThreadPoolQueue, 
					new ThreadFactoryImpl("PullMessageThread_"));
			
		}
		
		return result;
	}
	
	public void registerProcessor() {
		
	}
	
}
