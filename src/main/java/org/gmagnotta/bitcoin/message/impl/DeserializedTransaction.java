package org.gmagnotta.bitcoin.message.impl;

import java.util.List;

import org.gmagnotta.bitcoin.utils.Sha256Hash;

public class DeserializedTransaction extends Transaction {
	
	private Sha256Hash txId;
	private long size;

	public DeserializedTransaction(long version, List<TransactionInput> transactionInput,
			List<TransactionOutput> transactionOutput, long lockTime, Sha256Hash txId, long size) {
		super(version, transactionInput, transactionOutput, lockTime);
		
		this.txId = txId;
		this.size = size;
	}
	
	public Sha256Hash getTxId() {
		return txId;
	}
	
	public long getSize() {
		return size;
	}
	
}
