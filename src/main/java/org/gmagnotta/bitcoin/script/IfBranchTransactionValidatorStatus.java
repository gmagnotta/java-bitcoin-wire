package org.gmagnotta.bitcoin.script;

import java.util.Stack;

import org.gmagnotta.bitcoin.parser.script.OpCode;

public class IfBranchTransactionValidatorStatus implements TransactionValidatorStatus {

	private boolean shouldExecute;
	
	public IfBranchTransactionValidatorStatus(boolean shouldExecute) {
		this.shouldExecute = shouldExecute;
	}
	
	@Override
	public void executeScript(ScriptElement scriptElement, Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
		
		// we should execute everything until OP_ELSE or until OP_ENDIF
		if (OpCode.OP_ENDIF.equals(scriptElement.getOpCode())) {
			
			scriptContext.setTransactionValidatorStatus(new SequentialTransactionValidatorStatus());
			
		} else if (OpCode.OP_ELSE.equals(scriptElement.getOpCode())) { 
			
			scriptContext.setTransactionValidatorStatus(new ElseBranchTransactionValidatorStatus(!shouldExecute));
			
		} else if (shouldExecute) {
			
			// simply execute statements sequentially
			scriptElement.doOperation(stack, scriptContext);
			
		}
		
	}

	@Override
	public TransactionValidatorStatusEnum getTransactionValidatorStatusEnum() {
		return TransactionValidatorStatusEnum.IF_BRANCH;
	}

}
