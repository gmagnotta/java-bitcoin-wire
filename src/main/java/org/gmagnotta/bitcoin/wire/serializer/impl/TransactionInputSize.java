package org.gmagnotta.bitcoin.wire.serializer.impl;

import org.gmagnotta.bitcoin.message.impl.TransactionInput;

public class TransactionInputSize {
	
	private long size;
	private TransactionInput transactionInput;
	
	public TransactionInputSize(long size, TransactionInput transationInput) {
		this.size = size;
		this.transactionInput = transationInput;
	}

	public long getSize() {
		return size;
	}

	public TransactionInput getTransactionInput() {
		return transactionInput;
	}
	
}
