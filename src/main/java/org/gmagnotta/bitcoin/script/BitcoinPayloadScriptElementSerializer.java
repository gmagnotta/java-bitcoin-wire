package org.gmagnotta.bitcoin.script;

import java.io.ByteArrayOutputStream;

public class BitcoinPayloadScriptElementSerializer extends BitcoinScriptItemSerializer {
	
	private int expectedSize;
	
	public BitcoinPayloadScriptElementSerializer(int expectedSize) {
		this.expectedSize = expectedSize;
	}
	
	@Override
	public byte[] serialize(ScriptElement scriptElement) throws Exception {
		
		PayloadScriptElement payloadScriptElement = (PayloadScriptElement) scriptElement;
		
		byte[] payload = payloadScriptElement.getPayload();
		
		if (payload == null) {
			throw new Exception("Payload is null!");
		}
		
		if (payload.length != expectedSize) {
			throw new Exception("Payload size is wrong!");
		}
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		byteArrayOutputStream.write(payloadScriptElement.getOpCode().getValue());
		byteArrayOutputStream.write(payloadScriptElement.getPayload());
		
		return byteArrayOutputStream.toByteArray();
		
	}
	
}
