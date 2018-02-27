package org.gmagnotta.bitcoin.script;

import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;

import org.bouncycastle.util.encoders.Hex;
import org.gmagnotta.bitcoin.parser.script.OpCode;

public class ScriptElement {
	
	private OpCode opCode;
	private byte[] payload;
	
	public ScriptElement(OpCode opCode) {
		this.opCode = opCode;
	}
	
	public ScriptElement(OpCode opCode, byte[] payload) {
		this.opCode = opCode;
		this.payload = payload;
	}
	
	public OpCode getOpCode() {
		return opCode;
	}
	
	public byte[] getPayload() {
		return payload;
	}
	
	@Override
	public boolean equals(Object object) {

		if (!(object instanceof ScriptElement))
			return false;

		if (this == object)
			return true;

		ScriptElement element = (ScriptElement) object;

		return Objects.equals(opCode, element.opCode) &&
				Arrays.equals(payload, element.payload);
		
	}

	@Override
	public String toString() {
		return opCode.name() + (payload != null ? " " + Hex.toHexString(payload) : "");
	}

	public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
		
		if (payload != null) {
			stack.push(payload);
		} else {
			throw new Exception("This should be an operation but there is no implementation!");
		}
	}
}
