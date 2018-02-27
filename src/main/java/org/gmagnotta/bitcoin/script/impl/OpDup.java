package org.gmagnotta.bitcoin.script.impl;

import java.util.Stack;

import org.gmagnotta.bitcoin.parser.script.OpCode;
import org.gmagnotta.bitcoin.script.ScriptContext;
import org.gmagnotta.bitcoin.script.ScriptElement;

public class OpDup extends ScriptElement {

	public OpDup(OpCode opCode) {
		super(opCode);
	}
	
	@Override
	public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) {
		byte[] top = stack.peek();
		stack.push(top);
	}

}
