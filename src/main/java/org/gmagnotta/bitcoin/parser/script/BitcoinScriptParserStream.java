package org.gmagnotta.bitcoin.parser.script;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.gmagnotta.bitcoin.script.BitcoinScript;
import org.gmagnotta.bitcoin.script.ScriptElement;

public class BitcoinScriptParserStream implements Context {

	// Maximum script length in bytes
	private static final int MAX_SCRIPT_SIZE = 10000;
	
	private ScriptParserState scriptParserState;
	private InputStream inputStream;
	private List<ScriptElement> elements;
	
	public BitcoinScriptParserStream(InputStream inputStream) {
		this.inputStream = inputStream;
		this.scriptParserState = new ByteParseState(this);
		this.elements = new ArrayList<ScriptElement>();
	}
	
	public BitcoinScript getBitcoinScript() throws Exception {
		
		int read = 0;
		
		while (true) {
			
			int input = inputStream.read();
			
			if (input == -1) {
				
				if (scriptParserState.isStillExpectingData()) {
					throw new Exception("Uncomplete script!");
					
				}
				
				break;
				
			} else {
				
				read++;

				if (read > MAX_SCRIPT_SIZE) {
					throw new Exception("Script lenght is bigger than maximum allowed");
				}
			}
			
			scriptParserState.parse((byte) input);
			
		}
		
		return new BitcoinScript(elements);
		
	}

	@Override
	public void setNextParserState(ScriptParserState scriptState) {
		this.scriptParserState = scriptState;
	}

	@Override
	public void add(ScriptElement element) {
		elements.add(element);
	}
}
