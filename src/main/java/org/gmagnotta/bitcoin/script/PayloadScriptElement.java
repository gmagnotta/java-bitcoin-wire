package org.gmagnotta.bitcoin.script;

import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;

import org.bouncycastle.util.encoders.Hex;
import org.gmagnotta.bitcoin.parser.script.OpCode;

public class PayloadScriptElement extends ScriptElement {

	private byte[] payload;
	int expectedLen;
	
	public PayloadScriptElement(OpCode opCode, int expectedLen, byte[] payload) {
		super(opCode);
		this.expectedLen = expectedLen;
		this.payload = payload;
	}
	
	public byte[] getPayload() {
		return payload;
	}
	
	@Override
	public boolean equals(Object object) {

		if (!(object instanceof PayloadScriptElement))
			return false;

		if (this == object)
			return true;

		PayloadScriptElement element = (PayloadScriptElement) object;

		return Objects.equals(getOpCode(), element.getOpCode()) &&
				Arrays.equals(payload, element.payload);
		
	}
	
	@Override
	public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
		
		if (payload == null ||
				expectedLen != payload.length) throw new Exception("Payload lenght is not the expected one!");
		
		stack.push(payload);
	}
	
	@Override
	public String toString() {
		return "[" + getOpCode().name() + " " + Hex.toHexString(payload) + "]";
	}

}
