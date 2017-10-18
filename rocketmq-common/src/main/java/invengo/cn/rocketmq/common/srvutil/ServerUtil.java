package invengo.cn.rocketmq.common.srvutil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class ServerUtil {

	public static Options buildCommandlineOptions(final Options options) {
		Option option = new Option("h", "help", false, "print help");
		option.setRequired(false);
		options.addOption(option);
		
		option = new Option("n", "namesrvAddr", true, 
				"Name server address list, eg: 192.168.0.100:9876;192.168.0.101:9876");
		option.setRequired(false);
		options.addOption(option);
		return options;
	}
	
	public static CommandLine parseCmdLine(final String appName,String[] args,Options options,
			CommandLineParser parser) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.setWidth(110);
		CommandLine commandLine = null;
		try {
			commandLine = parser.parse(options, args);
			if (commandLine.hasOption('h')) {
				helpFormatter.printHelp(appName, options, true);
				return null;
			}
		} catch (Exception e) {
			helpFormatter.printHelp(appName, options, true);
		}
		return commandLine;
	}
	
}
