package org.gmagnotta.bitcoin.script;

import java.util.Stack;

import org.gmagnotta.bitcoin.parser.script.OpCode;

public class ElseBranchTransactionValidatorStatus implements TransactionValidatorStatus {
	
	private boolean shouldExecuteBranch;
	
	public ElseBranchTransactionValidatorStatus(boolean shouldExecuteBranch) {
		this.shouldExecuteBranch = shouldExecuteBranch;
	}

	@Override
	public void executeScript(ScriptElement scriptElement, Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
		
		if (OpCode.OP_ENDIF.equals(scriptElement.getOpCode())) {
			
			scriptContext.setTransactionValidatorStatus(new SequentialTransactionValidatorStatus());
			
		} else if (shouldExecuteBranch) {
			
			// simply execute statements sequentially
			scriptElement.doOperation(stack, scriptContext);
			
		}
		
	}

	@Override
	public TransactionValidatorStatusEnum getTransactionValidatorStatusEnum() {
		return TransactionValidatorStatusEnum.ELSE_BRANCH;
	}

}
