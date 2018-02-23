package org.gmagnotta.bitcoin.script.impl;

import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;

import org.gmagnotta.bitcoin.parser.script.OpCode;
import org.gmagnotta.bitcoin.script.ScriptContext;
import org.gmagnotta.bitcoin.script.ScriptItem;
import org.spongycastle.util.encoders.Hex;

// Just encapsulates data
public class Element implements ScriptItem {
	
	private OpCode opCode;
	private byte[] data;
	
	public Element(OpCode opCode, byte[] data) {
		this.opCode = opCode;
		this.data = data;
	}
	
	public OpCode getOpCode() {
		return opCode;
	}
	
	public byte[] getData() {
		return data;
	}
	
	@Override
	public boolean equals(Object object) {

		if (!(object instanceof Element))
			return false;

		if (this == object)
			return true;

		Element element = (Element) object;

		return Objects.equals(opCode, element.opCode) &&
				Arrays.equals(data, element.data);
		
	}

	@Override
	public String toString() {
		return opCode.name() + " " + Hex.toHexString(data);
	}

	@Override
	public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) {
		stack.push(data);
	}
}
