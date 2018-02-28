package org.gmagnotta.bitcoin.script;

import java.util.Arrays;
import java.util.Objects;

import org.bouncycastle.util.encoders.Hex;
import org.gmagnotta.bitcoin.parser.script.OpCode;

public class PayloadScriptElement extends ScriptElement {

	private byte[] payload;
	
	public PayloadScriptElement(OpCode opCode, byte[] payload) {
		super(opCode);
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
	public String toString() {
		return getOpCode().name() + Hex.toHexString(payload);
	}

}
