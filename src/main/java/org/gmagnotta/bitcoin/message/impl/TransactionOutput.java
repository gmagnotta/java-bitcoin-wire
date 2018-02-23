package org.gmagnotta.bitcoin.message.impl;

import java.math.BigInteger;

import org.spongycastle.util.encoders.Hex;

public class TransactionOutput {
	
	private BigInteger value;
	private byte[] scriptPubKey;

	public TransactionOutput(BigInteger value, byte[] scriptPubKey) {
		this.value = value;
		this.scriptPubKey = scriptPubKey;
	}

	public BigInteger getValue() {
		return value;
	}

	public byte[] getScriptPubKey() {
		return scriptPubKey;
	}
	
	@Override
	public String toString() {
		return String.format("Value: %d, scriptPubKey %s", value.longValue(), Hex.toHexString(scriptPubKey));
	}
}
