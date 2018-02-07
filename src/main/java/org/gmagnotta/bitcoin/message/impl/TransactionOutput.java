package org.gmagnotta.bitcoin.message.impl;

import java.math.BigInteger;

public class TransactionOutput {
	
	private BigInteger value;
	private byte[] pkScript;

	public TransactionOutput(BigInteger value, byte[] pkScript) {
		this.value = value;
		this.pkScript = pkScript;
	}

	public BigInteger getValue() {
		return value;
	}

	public byte[] getPkScript() {
		return pkScript;
	}
	
}
