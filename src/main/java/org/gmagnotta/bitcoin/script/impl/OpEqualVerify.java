package org.gmagnotta.bitcoin.script.impl;

import java.util.Stack;

import org.gmagnotta.bitcoin.parser.script.OpCode;
import org.gmagnotta.bitcoin.script.ScriptContext;
import org.gmagnotta.bitcoin.script.ScriptElement;
import org.spongycastle.util.Arrays;

public class OpEqualVerify extends ScriptElement {

	public OpEqualVerify(OpCode opCode) {
		super(opCode);
	}
	
	@Override
	public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
		byte[] first = stack.pop();
		byte[] second = stack.pop();
		
		if (!Arrays.areEqual(first, second)) {
			throw new Exception("Transaction invalid!");
		}
	}

}
