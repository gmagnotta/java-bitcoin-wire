package org.gmagnotta.bitcoin.message.impl;

public class TransactionOutput {
	
	private long value;
	private byte[] pkScript;

	public TransactionOutput(long value, byte[] pkScript) {
		this.value = value;
		this.pkScript = pkScript;
	}

	public long getValue() {
		return value;
	}

	public byte[] getPkScript() {
		return pkScript;
	}
	
}
