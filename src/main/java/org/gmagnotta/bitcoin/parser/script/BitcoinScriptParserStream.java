package org.gmagnotta.bitcoin.parser.script;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.gmagnotta.bitcoin.script.BitcoinScript;
import org.gmagnotta.bitcoin.script.ScriptElement;

public class BitcoinScriptParserStream implements Context {

	private ScriptParserState scriptParserState;
	private InputStream inputStream;
	private List<ScriptElement> elements;
	
	public BitcoinScriptParserStream(InputStream inputStream) {
		this.inputStream = inputStream;
		this.scriptParserState = new ByteParseState(this);
		this.elements = new ArrayList<ScriptElement>();
	}
	
	public BitcoinScript getBitcoinScript() throws Exception {
		
		while (true) {
			
			int input = inputStream.read();
			
			if (input == -1) {
				
				break;
				
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
