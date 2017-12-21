package org.gmagnotta.bitcoin.parser;

import java.math.BigInteger;

public class ChecksumState implements MessageState {
	
	private Context context;
	
	private byte[] checksum = new byte[4];
	private int index = 0;
	
	public ChecksumState(Context context) {
		this.context = context;
	}

	@Override
	public void process(byte buffer) {
		
		checksum[index] = buffer;

		if (index == 3) {
			
			context.setChecksum(checksum);
			
			// how much data for payload do we need to read?
			
			int size = new BigInteger(reverse(context.getLength())).intValue();

			// if size is positive then we can go to to payloadState
			if (size == 0) {
				
				context.setPayload(new byte[0]);
				context.setComplete();
		
			} else {

				context.setNextState(new PayloadState(context));
				
			}
			
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
