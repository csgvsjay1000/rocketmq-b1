package invengo.cn.rocketmq.remoting.protocol;

import java.nio.ByteBuffer;

public class RemotingCommand {

	private int code;
	
	private transient byte[] body;
	
	public ByteBuffer encodeHeader() {
		return encodeHeader(this.body == null?0:this.body.length);
	}
	
	public ByteBuffer encodeHeader(final int bodyLength) {
		// 1> header length size
		int length = 4;
		
		// 2> header data length
		byte[] headerData = this.headerEncode();
		length += headerData.length;
		
		ByteBuffer result = ByteBuffer.allocate(4+length);
		
		// total length
		result.putInt(length);
		
		// header length
		result.putInt(headerData.length);
		
		// header data
		result.put(headerData);
		
		result.flip();
		
		return result;
	}
	
	public static RemotingCommand decode(final ByteBuffer byteBuffer) {
		int length = byteBuffer.limit();
		int headerLength = byteBuffer.getInt();
		
		byte[] headerData = new byte[headerLength];
		byteBuffer.get(headerData);
		
		RemotingCommand command = headerDecode(headerData);
		
		return command;
	}
	
	private byte[] headerEncode(){
		return RocketMQSerializable.rocketMQProtocolEncode(this);
	}
	
	private static RemotingCommand headerDecode(byte[] headerArray){
		return RocketMQSerializable.rocketMQProtocolDecode(headerArray);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	
}
