package org.gmagnotta.bitcoin.script;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.gmagnotta.bitcoin.parser.script.BitcoinScriptParserStream;

public class BitcoinScriptSerializer {
	
	public BitcoinScript deserialize(byte[] payload, int offset) throws Exception {
		
		BitcoinScriptParserStream bitcoinScriptParserStream = new BitcoinScriptParserStream(new ByteArrayInputStream(payload, offset, payload.length-offset));
		
		return bitcoinScriptParserStream.getBitcoinScript();
		
	}
	
	public byte[] serialize(BitcoinScript bitcoinScript) throws Exception {
		
		List<ScriptElement> items = bitcoinScript.getElements();
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		for (int index = 0; index < items.size(); index++) {
			
			ScriptElement item = items.get(index);
			
			byteArrayOutputStream.write(item.getOpCode().getSerializer().serialize(item));
			
		}
		
		return byteArrayOutputStream.toByteArray();
		
	}

}
