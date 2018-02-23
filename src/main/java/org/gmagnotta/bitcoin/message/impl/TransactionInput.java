package org.gmagnotta.bitcoin.message.impl;

import org.spongycastle.util.encoders.Hex;

public class TransactionInput {
	
	private OutPoint previousOutput;
	private byte[] scriptSig;
	private long sequence;
	
	public TransactionInput(OutPoint previousOutput, byte[] scriptSig, long sequence) {
		this.previousOutput = previousOutput;
		this.scriptSig = scriptSig;
		this.sequence = sequence;
	}

	public OutPoint getPreviousOutput() {
		return previousOutput;
	}

	public byte[] getScriptSig() {
		return scriptSig;
	}

	public long getSequence() {
		return sequence;
	}
	
	public String toString() {
		return String.format("OutPoint: %s, scriptSig %s, sequence %d", previousOutput, Hex.toHexString(scriptSig), sequence);
	}
	
}
