package org.gmagnotta.bitcoin.script;

import java.util.Stack;

import org.apache.commons.lang3.ArrayUtils;
import org.gmagnotta.bitcoin.blockchain.BlockChain;

public class ScriptEngine {
	
	private BlockChain blockChain;
	private Stack<byte[]> stack;
	
	public ScriptEngine() {
		this.stack = new Stack<byte[]>();
	}
	
	/**
	 * Execute the script and returns true if script is corrent, otherwise false
	 * @param script
	 * @return
	 */
	public boolean isValid(BitcoinScript script) {
		
		for (ScriptItem i : script.getItems()) {
			
			if (i instanceof Element) {
				
				stack.push(((Element) i).getData());
				
			} else if (i instanceof Operation) {
				
				((Operation) i).execute(stack);
				
			}
			
		}
		
		byte[] top = stack.pop();
		
		if (ArrayUtils.isEmpty(top)) {
			return false;
		}
		
		return true;
		
	}

}
