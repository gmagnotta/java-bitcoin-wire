package org.gmagnotta.bitcoin.message.impl;

public class TransactionInput {
	
	private OutPoint previousOutput;
	private byte[] signatureScript;
	private long sequence;
	
	public TransactionInput(OutPoint previousOutput, byte[] signatureScript, long sequence) {
		this.previousOutput = previousOutput;
		this.signatureScript = signatureScript;
		this.sequence = sequence;
	}

	public OutPoint getPreviousOutput() {
		return previousOutput;
	}

	public byte[] getSignatureScript() {
		return signatureScript;
	}

	public long getSequence() {
		return sequence;
	}
	
}
