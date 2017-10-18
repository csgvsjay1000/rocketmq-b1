package invengo.cn.rocketmq.store;

public interface MessageStore {
	
	boolean load();
	
	void start() throws Exception;
	
	void shutdown();

}
