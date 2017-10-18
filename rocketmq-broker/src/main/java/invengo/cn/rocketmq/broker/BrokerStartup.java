package invengo.cn.rocketmq.broker;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import invengo.cn.rocketmq.common.BrokerConfig;
import invengo.cn.rocketmq.common.MixAll;
import invengo.cn.rocketmq.common.srvutil.ServerUtil;
import invengo.cn.rocketmq.remoting.netty.NettyClientConfig;
import invengo.cn.rocketmq.remoting.netty.NettyServerConfig;
import invengo.cn.rocketmq.store.config.MessageStoreConfig;

public class BrokerStartup {
	
	public static Properties properties = null;
	
	public static CommandLine commandLine = null;
	
	public static String configFile = null;
	
	public static Logger logger = null;
	
	public static void main(String[] args) {
		start(createBrokerController(args));
	}
	
	public static void start(BrokerController brokerController) {
		
	}
	
	public static BrokerController createBrokerController(String[] args) {
		Options options = ServerUtil.buildCommandlineOptions(new Options());
		commandLine = ServerUtil.parseCmdLine("broker", args, buildCommandlineOptions(options), new PosixParser());
		if (commandLine == null) {
			System.exit(-1);
		}
		
		try {
			final BrokerConfig brokerConfig = new BrokerConfig();
			final NettyServerConfig nettyServerConfig = new NettyServerConfig();
			final NettyClientConfig nettyClientConfig = new NettyClientConfig();
			final MessageStoreConfig messageStoreConfig = new MessageStoreConfig();
			
			if (commandLine.hasOption('c')) {
				// broker 配置文件
				String file = commandLine.getOptionValue('c');
				if (file != null) {
					configFile = file;
					InputStream in = new FileInputStream(file);
					properties = new Properties();
					properties.load(in);
					MixAll.properties2Object(properties, brokerConfig);
					MixAll.properties2Object(properties, nettyServerConfig);
					MixAll.properties2Object(properties, nettyClientConfig);
					MixAll.properties2Object(properties, messageStoreConfig);
					
					in.close();
				}
			}
			
			logger = LoggerFactory.getLogger(BrokerStartup.class);
			
			final BrokerController brokerController = new BrokerController(brokerConfig,
					nettyServerConfig,nettyClientConfig,messageStoreConfig);
			
			brokerController.initialize();
			
			return brokerController;

		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(-3);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			public void run() {
				
				System.out.println("shutdown");
			}
		}));
		System.out.println("shutdown");
		return null;
	}
	
	public static Options buildCommandlineOptions(final Options options) {
		Option opt = new Option("c", "configFile", true, "Broker config properties file");
		opt.setRequired(false);
		options.addOption(opt);
		
		opt = new Option("p", "printConfigItem", false, "Print all config item");
		opt.setRequired(false);
		options.addOption(opt);

		opt = new Option("m", "printImportantConfigItem", false, "Print Important config item");
		opt.setRequired(false);
		options.addOption(opt);
		
		return options;
	}

}
