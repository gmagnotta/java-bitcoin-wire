package org.gmagnotta.bitcoin.script.impl;

import java.math.BigInteger;
import java.util.Stack;

import org.gmagnotta.bitcoin.parser.script.OpCode;
import org.gmagnotta.bitcoin.script.ScriptContext;
import org.gmagnotta.bitcoin.script.ScriptElement;

public class OpNumEqual extends ScriptElement {

	public OpNumEqual(OpCode opCode) {
		super(opCode);
	}
	
	// check http://www.righto.com/2014/02/bitcoins-hard-way-using-raw-bitcoin.html
	// check https://en.bitcoin.it/w/images/en/7/70/Bitcoin_OpCheckSig_InDetail.png
	
	@Override
	public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
		
		byte[] a = stack.pop();
		byte[] b = stack.pop();
		
		BigInteger first = new BigInteger(a);
		BigInteger second = new BigInteger(b);
		
		if (first.equals(second)) {
			stack.push(new byte[] {1});
		} else {
			stack.push(new byte[] {0});
		}
		
	}

}
