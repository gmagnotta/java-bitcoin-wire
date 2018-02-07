package org.gmagnotta.bitcoin.wire.serializer.impl;

import org.gmagnotta.bitcoin.message.impl.Transaction;

public class TransactionSize {
	
	private long size;
	private Transaction transaction;
	
	public TransactionSize(long size, Transaction transaction) {
		this.size = size;
		this.transaction = transaction;
	}

	public long getSize() {
		return size;
	}

	public Transaction getTransaction() {
		return transaction;
	}
	
}
