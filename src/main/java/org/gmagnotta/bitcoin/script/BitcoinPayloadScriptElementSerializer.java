package org.gmagnotta.bitcoin.script;

import java.io.ByteArrayOutputStream;

public class BitcoinPayloadScriptElementSerializer extends BitcoinScriptItemSerializer {
	
	@Override
	public byte[] serialize(ScriptElement scriptElement) throws Exception {
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		byteArrayOutputStream.write(scriptElement.getOpCode().getValue());
		byteArrayOutputStream.write(scriptElement.getPayload());
		
		return byteArrayOutputStream.toByteArray();
		
	}
	
}
