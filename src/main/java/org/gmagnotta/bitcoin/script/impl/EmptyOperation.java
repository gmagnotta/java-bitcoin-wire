package org.gmagnotta.bitcoin.script.impl;

import java.util.Objects;
import java.util.Stack;

import org.gmagnotta.bitcoin.parser.script.OpCode;
import org.gmagnotta.bitcoin.script.ScriptContext;
import org.gmagnotta.bitcoin.script.ScriptItem;

// Operation that does something on the elements
public class EmptyOperation implements ScriptItem {
	
	private OpCode opCode;
	
	public EmptyOperation(OpCode opCode) {
		this.opCode = opCode;
	}
	
	public OpCode getOpCode() {
		return opCode;
	}
	
	@Override
	public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean equals(Object object) {

		if (!(object instanceof EmptyOperation))
			return false;

		if (this == object)
			return true;

		EmptyOperation operation = (EmptyOperation) object;

		return Objects.equals(opCode, operation.opCode);
		
	}
	
	@Override
	public String toString() {
		return String.format("%s", opCode.name());
	}

}
