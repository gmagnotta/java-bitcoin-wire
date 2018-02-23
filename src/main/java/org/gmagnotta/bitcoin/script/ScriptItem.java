package org.gmagnotta.bitcoin.script;

import java.util.Stack;

import org.gmagnotta.bitcoin.parser.script.OpCode;

public interface ScriptItem {

	public OpCode getOpCode();
	
	public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception;

}
