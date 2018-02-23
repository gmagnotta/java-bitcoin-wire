package org.gmagnotta.bitcoin.script;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.gmagnotta.bitcoin.parser.script.BitcoinScriptParserStream;
import org.gmagnotta.bitcoin.script.impl.Element;
import org.gmagnotta.bitcoin.script.impl.EmptyOperation;

public class BitcoinScriptSerializer {
	
	public BitcoinScript deserialize(byte[] payload, int offset) throws Exception {
		
		BitcoinScriptParserStream bitcoinScriptParserStream = new BitcoinScriptParserStream(new ByteArrayInputStream(payload, offset, payload.length-offset));
		
		return bitcoinScriptParserStream.getBitcoinScript();
		
	}
	
	public byte[] serialize(BitcoinScript bitcoinScript) throws Exception {
		
		List<ScriptItem> items = bitcoinScript.getItems();
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		for (int index = 0; index < items.size(); index++) {
			
			ScriptItem item = items.get(index);
			
			if (item instanceof Element) {
				
				Element el = (Element) item;
				
				byteArrayOutputStream.write(el.getOpCode().getValue());
				byteArrayOutputStream.write(((Element) item).getData());
				
			} else if (item instanceof EmptyOperation) {
				
				byteArrayOutputStream.write(item.getOpCode().getValue());
				
			}
			
		}
		
		return byteArrayOutputStream.toByteArray();
		
	}

}
