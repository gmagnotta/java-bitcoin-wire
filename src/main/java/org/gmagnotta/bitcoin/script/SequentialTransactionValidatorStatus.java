package org.gmagnotta.bitcoin.script;

import java.util.Stack;

public class SequentialTransactionValidatorStatus implements TransactionValidatorStatus {

	@Override
	public void executeScript(ScriptElement scriptElement, Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
		
		// simply execute statements sequentially
		scriptElement.doOperation(stack, scriptContext);
		
	}

	@Override
	public TransactionValidatorStatusEnum getTransactionValidatorStatusEnum() {
		return TransactionValidatorStatusEnum.SEQUENTIAL;
	}

}
