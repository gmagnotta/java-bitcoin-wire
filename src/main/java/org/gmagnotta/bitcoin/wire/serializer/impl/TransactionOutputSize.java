package org.gmagnotta.bitcoin.wire.serializer.impl;

import org.gmagnotta.bitcoin.message.impl.TransactionOutput;

public class TransactionOutputSize {
	
	private long size;
	private TransactionOutput transactionOutput;
	
	public TransactionOutputSize(long size, TransactionOutput transationOutput) {
		this.size = size;
		this.transactionOutput = transationOutput;
	}

	public long getSize() {
		return size;
	}

	public TransactionOutput getTransactionOutput() {
		return transactionOutput;
	}
	
}
