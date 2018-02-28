package org.gmagnotta.bitcoin.script.impl;

import java.util.Stack;

import org.bouncycastle.util.Arrays;
import org.gmagnotta.bitcoin.parser.script.OpCode;
import org.gmagnotta.bitcoin.script.ScriptContext;
import org.gmagnotta.bitcoin.script.ScriptElement;

public class OpEqual extends ScriptElement {

	public OpEqual(OpCode opCode) {
		super(opCode);
	}
	
	// check http://www.righto.com/2014/02/bitcoins-hard-way-using-raw-bitcoin.html
	// check https://en.bitcoin.it/w/images/en/7/70/Bitcoin_OpCheckSig_InDetail.png
	
	@Override
	public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
		
		// Retrieve top elements
		byte[] first = stack.pop();
		byte[] second = stack.pop();
		
		if (Arrays.areEqual(first, second)) {
			stack.push(new byte[] {1});
		} else {
			stack.push(new byte[] {0});
		}
	}

}
