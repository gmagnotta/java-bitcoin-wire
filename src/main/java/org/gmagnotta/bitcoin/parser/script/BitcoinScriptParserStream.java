package org.gmagnotta.bitcoin.parser.script;

import java.io.InputStream;
import java.util.Stack;

import org.gmagnotta.bitcoin.script.BitcoinScript;

public class BitcoinScriptParserStream implements Context {

	private ScriptState scriptState;
	private InputStream inputStream;
	private boolean isComplete;
	private Stack<byte[]> stack;
	
	public BitcoinScriptParserStream(InputStream inputStream) {
		this.inputStream = inputStream;
		this.scriptState = new ParseState(this);
		this.isComplete = false;
		this.stack = new Stack<byte[]>();
	}
	
	public BitcoinScript getBitcoinScript() throws Exception {
		
		while (!isComplete) {
			
			int input = inputStream.read();
			
			if (input == -1) {
				
				throw new Exception();
				
			}
			
			scriptState.process((byte) input);
			
		}
		
		return new BitcoinScript();
		
	}

	@Override
	public void setNetxtState(ScriptState scriptState) {
		this.scriptState = scriptState;
	}

	@Override
	public void push(byte[] item) {
		stack.push(item);
	}

	@Override
	public byte[] pop() {
		return stack.pop();
	}
}
