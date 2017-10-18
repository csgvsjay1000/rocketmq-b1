package invengo.cn.rocketmq.broker;

import java.util.Arrays;

public class EPCDecode {
	
	//static String epc = "55000D810001066666666666666666668D9C";
	static String epc = "55000DE40001000100000101000101014EA35500058100018000246355000F8100010000000000000000000000005FA755000D81000177777777777777777777C7E9";
		//	+ "FA755000D81000177777777777777777777C7E9";
	//static byte[] epc = [55,00,0D,81,00,01,06,66,66,66,66,66,66,66,66,66,8D,9C];
	// 55,00 两位开始位
	// 0D 长度位len
	// 3位 cmd 81 00 01
	// epc 位 长度位-cmd3位
	// 校验位  28 开头结束，4E开头开始读
	
	static String beginCmd = "E40001";
	
	static String endCmd = "026100";
	
	public static void main(String[] args) {
		
		int readIndex = 0;
		
		byte[] epcBytes = hexStringToByteArray(epc);
		while (readIndex<epcBytes.length) {
			
			readIndex = nextHead(epcBytes, readIndex);
			
			int len = (int)byteArrayToInt(Arrays.copyOfRange(epcBytes, readIndex+2, readIndex+3));
			System.out.println("len:"+len);
			byte[] cmd = Arrays.copyOfRange(epcBytes, readIndex+3, readIndex+6);
			System.out.println("cmd:"+Bytes2HexString(cmd));
			String cmdStr = Bytes2HexString(cmd);
			if (beginCmd.equals(cmdStr)) {
				readIndex += 3+len+2;
			}else  if (endCmd.equals(cmdStr)) {
				break;
			}else {
				byte[] epcret = Arrays.copyOfRange(epcBytes, readIndex+6, readIndex+len-3+6);
				System.out.println(Bytes2HexString(epcret));
				byte[] endBytes = Arrays.copyOfRange(epcBytes, readIndex+3+len, readIndex+3+len+2);
				
				System.out.println(Bytes2HexString(endBytes));
				readIndex += 3+len+2;
			}
			System.out.println("readIndex:"+readIndex+"/"+epcBytes.length);

		}

	}
	
	public static int nextHead(byte[] epcBytes,int readIndex) {
		byte[] headBytes = Arrays.copyOfRange(epcBytes, readIndex+0, readIndex+2);
		//System.out.println(Bytes2HexString(headBytes));
		String headStr = Bytes2HexString(headBytes);
		if (!"5500".endsWith(headStr)) {
			return nextHead(epcBytes,readIndex+1);
		}else {
			return readIndex;
		}
	}
	
	public static int byteArrayToInt(byte[] b) {  
	    return   b[0] & 0xFF
	           ;  
	} 

	public static String Bytes2HexString(byte[] b) { 
	     String ret = ""; 
	     for (int i = 0; i < b.length; i++) { 
	        String hex = Integer.toHexString(b[i] & 0xFF); 
	        if (hex.length() == 1) { 
	          hex = '0' + hex; 
	         } 
	        ret += hex.toUpperCase(); 
	      }
	  return ret;
	    }
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] b = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
	        b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
	                .digit(s.charAt(i + 1), 16));
	    }
	    return b;
	}
}
