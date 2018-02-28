package org.gmagnotta.bitcoin.script;

import java.io.ByteArrayOutputStream;

public class BitcoinPayloadScriptElementSerializer extends BitcoinScriptItemSerializer {
	
	@Override
	public byte[] serialize(ScriptElement scriptElement) throws Exception {
		
		PayloadScriptElement payloadScriptElement = (PayloadScriptElement) scriptElement;
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		byteArrayOutputStream.write(payloadScriptElement.getOpCode().getValue());
		byteArrayOutputStream.write(payloadScriptElement.getPayload());
		
		return byteArrayOutputStream.toByteArray();
		
	}
	
}
