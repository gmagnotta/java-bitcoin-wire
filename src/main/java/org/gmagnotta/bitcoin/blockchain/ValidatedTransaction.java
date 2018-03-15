package org.gmagnotta.bitcoin.blockchain;

import java.util.List;

import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.message.impl.TransactionInput;
import org.gmagnotta.bitcoin.message.impl.TransactionOutput;
import org.gmagnotta.bitcoin.utils.Sha256Hash;

public class ValidatedTransaction extends Transaction {

	private String block;
	private Sha256Hash hash;
	
	public ValidatedTransaction(long version, List<TransactionInput> transactionInput,
			List<TransactionOutput> transactionOutput, long lockTime, String block, Sha256Hash hash) {
		super(version, transactionInput, transactionOutput, lockTime);
		
		this.block = block;
		this.hash = hash;
		
	}
	
	public String getBlock() {
		return block;
	}
	
	public Sha256Hash getHash() {
		return hash;
	}

}
