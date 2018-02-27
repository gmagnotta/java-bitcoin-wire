package org.gmagnotta.bitcoin.script.impl;

import java.util.Stack;

import org.gmagnotta.bitcoin.parser.script.OpCode;
import org.gmagnotta.bitcoin.script.ScriptContext;
import org.gmagnotta.bitcoin.script.ScriptElement;
import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.utils.Utils;

public class OpHash160 extends ScriptElement {

	public OpHash160(OpCode opCode) {
		super(opCode);
	}
	
	@Override
	public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) {
		byte[] top = stack.pop();
		Sha256Hash hash = Sha256Hash.of(top);
		
		stack.push(Utils.hash160(hash.getBytes()));
	}

}
