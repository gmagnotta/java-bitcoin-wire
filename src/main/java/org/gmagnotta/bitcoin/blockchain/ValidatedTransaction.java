package org.gmagnotta.bitcoin.blockchain;

import java.util.List;

import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.message.impl.TransactionInput;
import org.gmagnotta.bitcoin.message.impl.TransactionOutput;

public class ValidatedTransaction extends Transaction {

	private String block;
	
	public ValidatedTransaction(long version, List<TransactionInput> transactionInput,
			List<TransactionOutput> transactionOutput, long lockTime, String block) {
		super(version, transactionInput, transactionOutput, lockTime);
		
		this.block = block;
		
	}
	
	public String getBlock() {
		return block;
	}

}
