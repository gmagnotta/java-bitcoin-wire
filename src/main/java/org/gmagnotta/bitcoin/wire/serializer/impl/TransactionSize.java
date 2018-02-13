package org.gmagnotta.bitcoin.wire.serializer.impl;

import org.gmagnotta.bitcoin.message.impl.HashedTransaction;

public class TransactionSize {
	
	private long size;
	private HashedTransaction transaction;
	
	public TransactionSize(long size, HashedTransaction transaction) {
		this.size = size;
		this.transaction = transaction;
	}

	public long getSize() {
		return size;
	}

	public HashedTransaction getTransaction() {
		return transaction;
	}
	
}
