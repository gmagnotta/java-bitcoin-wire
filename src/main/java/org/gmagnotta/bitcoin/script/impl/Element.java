package org.gmagnotta.bitcoin.script.impl;

import java.util.Stack;

import org.gmagnotta.bitcoin.parser.script.OpCode;
import org.gmagnotta.bitcoin.script.ScriptItem;
import org.gmagnotta.bitcoin.utils.Utils;
import org.spongycastle.util.encoders.Hex;

// Just encapsulates data
public class Element implements ScriptItem {
	
	private OpCode opCode;
	private byte[] data;
	
	public Element(OpCode opCode, byte[] data) {
		this.opCode = opCode;
		this.data = data;
	}

	@Override
	public String toString() {
		return opCode.name() + " " + Hex.toHexString(data);
	}

	@Override
	public void doOperation(Stack<byte[]> stack) {
		stack.push(Utils.reverseBytesClone(data));
	}
}
