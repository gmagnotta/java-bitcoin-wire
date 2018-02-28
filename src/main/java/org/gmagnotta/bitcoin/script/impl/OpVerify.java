package org.gmagnotta.bitcoin.script.impl;

import java.math.BigInteger;
import java.util.Stack;

import org.gmagnotta.bitcoin.parser.script.OpCode;
import org.gmagnotta.bitcoin.script.ScriptContext;
import org.gmagnotta.bitcoin.script.ScriptElement;

public class OpVerify extends ScriptElement {

	public OpVerify(OpCode opCode) {
		super(opCode);
	}
	
	@Override
	public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
		
		// Retrieve without removing
		byte[] top = stack.peek();
		
		BigInteger value = new BigInteger(top);
		
		if (!BigInteger.ZERO.equals(value)) {
			stack.pop();
		} else {
			throw new Exception("Transaction is invalid because top stack is zero");
		}
		
	}
	
}