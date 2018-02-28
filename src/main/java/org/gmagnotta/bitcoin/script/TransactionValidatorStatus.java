package org.gmagnotta.bitcoin.script;

import java.util.Stack;

public interface TransactionValidatorStatus {
	
	public enum TransactionValidatorStatusEnum {
		
		SEQUENTIAL,
		IF_BRANCH,
		ELSE_BRANCH;
		
	}
	
	public void executeScript(ScriptElement scriptElement, Stack<byte[]> stack, ScriptContext scriptContext) throws Exception;
	
	public TransactionValidatorStatusEnum getTransactionValidatorStatusEnum();

}
