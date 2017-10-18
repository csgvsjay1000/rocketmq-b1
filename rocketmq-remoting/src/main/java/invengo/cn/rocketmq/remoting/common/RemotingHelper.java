package invengo.cn.rocketmq.remoting.common;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class RemotingHelper {
	
	public static final String ROCKETMQ_REMOTING = "rocketmq_remoting";

	public static SocketAddress string2SocketAddress(final String addr) {
		String str[] = addr.split(":");
		InetSocketAddress socketAddress = new InetSocketAddress(str[0], Integer.parseInt(str[1]));
		return socketAddress;
	}
	
	public static String parseSocketAddress(SocketAddress socketAddress) {
		if (socketAddress == null) {
			return "";
		}
		String addr = socketAddress.toString(); 
		if (addr.length()>0) {
			addr = addr.substring(1);
			return addr;
		}
		return "";
	}
	
}
