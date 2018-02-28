package org.gmagnotta.bitcoin.script;

import java.util.Objects;
import java.util.Stack;

import org.gmagnotta.bitcoin.parser.script.OpCode;

/**
 * Represent an Operation in bitcoin Script
 */
public class ScriptElement {
	
	private OpCode opCode;
	
	public ScriptElement(OpCode opCode) {
		this.opCode = opCode;
	}
	
	public OpCode getOpCode() {
		return opCode;
	}
	
	@Override
	public boolean equals(Object object) {

		if (!(object instanceof ScriptElement))
			return false;

		if (this == object)
			return true;

		ScriptElement element = (ScriptElement) object;

		return Objects.equals(opCode, element.opCode);
		
	}

	@Override
	public String toString() {
		return opCode.name();
	}

	public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
		// NOTHING DO TO!
	}
}
