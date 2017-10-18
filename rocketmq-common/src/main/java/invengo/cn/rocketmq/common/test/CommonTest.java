package invengo.cn.rocketmq.common.test;

import invengo.cn.rocketmq.common.log.Logger;
import invengo.cn.rocketmq.common.log.LoggerFactory;

public class CommonTest {

	private static Logger logger = LoggerFactory.getLogger(CommonTest.class);
	
	public static void main(String[] args) {
		logger.getLogger().info("test");
	}
	
}
