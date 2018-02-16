package org.gmagnotta.bitcoin.script.impl;

import java.util.Stack;

import org.gmagnotta.bitcoin.parser.script.OpCode;
import org.gmagnotta.bitcoin.script.ScriptItem;

// Operation that does something on the elements
public class EmptyOperation implements ScriptItem {
	
	private OpCode opCode;
	
	public EmptyOperation(OpCode opCode) {
		this.opCode = opCode;
	}
	
	@Override
	public void doOperation(Stack<byte[]> stack) throws Exception{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString() {
		return String.format("%s", opCode.name());
	}

}
