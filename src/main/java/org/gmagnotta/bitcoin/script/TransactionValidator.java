package org.gmagnotta.bitcoin.script;

import java.util.Stack;

import org.apache.commons.lang3.ArrayUtils;
import org.gmagnotta.bitcoin.blockchain.BlockChain;
import org.gmagnotta.bitcoin.message.impl.BlockMessage;
import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.message.impl.TransactionInput;

public class TransactionValidator {
	
	private BlockChain blockChain;
	private Stack<byte[]> stack;
	
	public TransactionValidator(BlockChain blockChain, BlockMessage blockMessage) {
		this.blockChain = blockChain;
		this.stack = new Stack<byte[]>();
	}
	
	/**
	 * Execute the script and returns true if script is corrent, otherwise false
	 * @param script
	 * @return
	 */
	public boolean isValid(Transaction transaction) {
		
		for (TransactionInput i : transaction.getTransactionInput()) {
			
			Transaction previous = blockChain.getTransaction(i.getPreviousOutput().getHash().toString());
			
		}
		
		byte[] top = stack.pop();
		
		if (ArrayUtils.isEmpty(top)) {
			return false;
		}
		
		return true;
		
	}

}
