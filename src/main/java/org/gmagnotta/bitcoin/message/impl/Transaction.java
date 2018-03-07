package org.gmagnotta.bitcoin.message.impl;

import java.util.List;

public class Transaction {
	
	private long version;
	private List<TransactionInput> transactionInput;
	private List<TransactionOutput> transactionOutput;
	private long lockTime;
	
	public Transaction(long version, List<TransactionInput> transactionInput, List<TransactionOutput> transactionOutput, long lockTime) {
		this.version = version;
		this.transactionInput = transactionInput;
		this.transactionOutput = transactionOutput;
		this.lockTime = lockTime;
	}

	public long getVersion() {
		return version;
	}

	public List<TransactionInput> getTransactionInput() {
		return transactionInput;
	}
	
	public void setTransactionInput(List<TransactionInput> transactionInputs) {
		this.transactionInput = transactionInputs;
	}

	public List<TransactionOutput> getTransactionOutput() {
		return transactionOutput;
	}
	
	public void setTransactionOutput(List<TransactionOutput> transactionOutputs) {
		this.transactionOutput = transactionOutputs;
	}

	public long getLockTime() {
		return lockTime;
	}
	
	@Override
	public String toString() {
		return String.format("Version: %d, input: %s, output: %s, lockTime %d", version, transactionInput, transactionOutput, lockTime);
	}
	
}
