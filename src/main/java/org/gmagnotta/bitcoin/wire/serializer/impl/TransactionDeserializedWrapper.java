package org.gmagnotta.bitcoin.wire.serializer.impl;

import org.gmagnotta.bitcoin.message.impl.DeserializedTransaction;

public class TransactionDeserializedWrapper {
	
	private long size;
	private DeserializedTransaction transaction;
	
	public TransactionDeserializedWrapper(long size, DeserializedTransaction transaction) {
		this.size = size;
		this.transaction = transaction;
	}

	public long getSize() {
		return size;
	}

	public DeserializedTransaction getTransaction() {
		return transaction;
	}
	
}
