package org.gmagnotta.bitcoin.parser.script;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.gmagnotta.bitcoin.message.impl.TransactionInput;
import org.gmagnotta.bitcoin.script.BitcoinScript;
import org.gmagnotta.bitcoin.script.ScriptItem;

public class BitcoinScriptParserStream implements Context {

	private ScriptParserState scriptParserState;
	private InputStream inputStream;
	private List<ScriptItem> items;
	private TransactionInput transactionInput;
	
	public BitcoinScriptParserStream(InputStream inputStream, TransactionInput transactionInput) {
		this.inputStream = inputStream;
		this.transactionInput = transactionInput;
		this.scriptParserState = new ParseState(this);
		this.items = new ArrayList<ScriptItem>();
	}
	
	public BitcoinScript getBitcoinScript() throws Exception {
		
		while (true) {
			
			int input = inputStream.read();
			
			if (input == -1) {
				
				break;
				
			}
			
			scriptParserState.parse((byte) input);
			
		}
		
		return new BitcoinScript(items, transactionInput);
		
	}

	@Override
	public void setNextParserState(ScriptParserState scriptState) {
		this.scriptParserState = scriptState;
	}

	@Override
	public void add(ScriptItem item) {
		items.add(item);
	}
}
