package invengo.cn.rocketmq.namesrv;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import invengo.cn.rocketmq.common.srvutil.ServerUtil;

public class NamesrvStartup {
	
	public static Properties properties = null;
	
	public static CommandLine commandLine = null;

	public static void main(String[] args) {
		main0(args);
	}
	
	public static void main0(String[] args) {
		Options options = ServerUtil.buildCommandlineOptions(new Options());
		commandLine = ServerUtil.parseCmdLine("mqnamesrv", args, buildCommandlineOptions(options), new PosixParser());
        if (null == commandLine) {
            System.exit(-1);
            return;
        }
        
        
        
	}
	
	public static Options buildCommandlineOptions(final Options options) {
        Option opt = new Option("c", "configFile", true, "Name server config properties file");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("p", "printConfigItem", false, "Print all config item");
        opt.setRequired(false);
        options.addOption(opt);

        return options;
    }
}
