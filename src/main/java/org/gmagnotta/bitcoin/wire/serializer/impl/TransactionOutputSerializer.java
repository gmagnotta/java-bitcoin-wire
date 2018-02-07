package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.math.BigInteger;
import java.util.Arrays;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.impl.TransactionOutput;
import org.gmagnotta.bitcoin.wire.Utils;

public class TransactionOutputSerializer {

	public TransactionOutputSize deserialize(byte[] payload, int offset) {
		
		long value = Utils.readSint64LE(payload, offset + 0);
		
		VarInt pkScriptLen = new VarInt(payload, offset + 0 + 8);
		
		byte[] pkScrip = Arrays.copyOfRange(payload,
				offset + 0 + 8 + pkScriptLen.getSizeInBytes(),
				offset + 0 + 8 + pkScriptLen.getSizeInBytes() + (int) pkScriptLen.value);
		
		TransactionOutput output = new TransactionOutput(new BigInteger(""+value), pkScrip);
		
		return new TransactionOutputSize(offset + 0 + 8 + pkScriptLen.getSizeInBytes() + pkScriptLen.value, output);
		
	}
	
}
