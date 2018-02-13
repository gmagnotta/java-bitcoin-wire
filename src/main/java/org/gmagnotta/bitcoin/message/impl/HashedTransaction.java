package org.gmagnotta.bitcoin.message.impl;

import java.util.List;

import org.gmagnotta.bitcoin.utils.Sha256Hash;

public class HashedTransaction extends Transaction {
	
	private Sha256Hash txId;

	public HashedTransaction(long version, List<TransactionInput> transactionInput,
			List<TransactionOutput> transactionOutput, long lockTime, Sha256Hash txId) {
		super(version, transactionInput, transactionOutput, lockTime);
		
		this.txId = txId;
	}
	
	public Sha256Hash getTxId() {
		return txId;
	}

}
