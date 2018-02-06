package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.util.Arrays;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.impl.OutPoint;
import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.message.impl.TransactionInput;
import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.wire.Utils;

public class TransactionSerializer {
	
	public Transaction deserialize(byte[] payload, int offset, int lenght) {
		
		long version = Utils.readSint32LE(payload, offset + 0);
		
		VarInt txIn = new VarInt(payload, offset + 4);
		
		long lastIndex = offset + 4 + txIn.getSizeInBytes();
		int txRead = 1;
		do {
			
			Sha256Hash hash = Sha256Hash.wrapReversed(payload, (int)lastIndex, 32);
			long index = Utils.readUint32LE(payload, (int)lastIndex + 32);
			VarInt scriptLen = new VarInt(payload, (int)lastIndex + 36);
			
			byte[] script = Arrays.copyOfRange(payload, (int)lastIndex + 36 + scriptLen.getSizeInBytes(), (int)(lastIndex + 36 + scriptLen.getSizeInBytes() + scriptLen.value));
			
			long sequence = Utils.readUint32LE(payload, (int)(lastIndex + 36 + scriptLen.getSizeInBytes() + scriptLen.value));
			
			OutPoint outPoint = new OutPoint(hash, index);
			
			TransactionInput input = new TransactionInput(outPoint, script, sequence);
			
			txRead++;
		} while (txRead == txIn.value);
		
		return null;
	}

}
