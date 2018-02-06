package org.gmagnotta.bitcoin.parser;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class PayloadState implements MessageState {
	
	private Context context;
	
	private ByteBuffer payload;
	private int index = 0;
	private int size = 0;
	
	public PayloadState(Context context) {
		this.context = context;
		
		size = new BigInteger(reverse(context.getLength())).intValue();
		
		payload = ByteBuffer.allocate(size);
	}

	@Override
	public void process(byte buffer) {

		payload.put(buffer);
		
		if (index == (size - 1)) {
			
			context.setPayload(payload);
			context.setComplete();
		}
		
		index++;
		
	}
	
	private static byte[] reverse(byte[] data) {
		
		byte[] bytes = data.clone();
		for (int i = 0; i < bytes.length / 2; i++) {
			byte temp = bytes[i];
			bytes[i] = bytes[bytes.length - i - 1];
			bytes[bytes.length - i - 1] = temp;
		}
		
		return bytes;
		
	}

}
