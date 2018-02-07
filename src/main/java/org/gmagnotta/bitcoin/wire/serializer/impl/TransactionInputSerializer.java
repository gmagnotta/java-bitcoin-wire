package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.util.Arrays;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.impl.OutPoint;
import org.gmagnotta.bitcoin.message.impl.TransactionInput;
import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.wire.Utils;
import org.spongycastle.util.encoders.Hex;

public class TransactionInputSerializer {

	public TransactionInputSize deserialize(byte[] payload, int offset) {
		
		Sha256Hash hash = Sha256Hash.wrapReversed(payload, offset + 0, 32);
		long index = Utils.readUint32LE(payload, offset + 0 + 32);
		VarInt scriptLen = new VarInt(payload, offset + 0 + 32 + 4);
		
		byte[] script = Arrays.copyOfRange(payload, offset + 0 + 32 + 4 + scriptLen.getSizeInBytes(), offset + 0 + 32 + 4 + scriptLen.getSizeInBytes() + (int) scriptLen.value);
		
		Hex.toHexString(script);
		
		long sequence = Utils.readUint32LE(payload, offset + 0 + 32 + 4 + scriptLen.getSizeInBytes() + script.length);
		
		OutPoint outPoint = new OutPoint(hash, index);
		
		TransactionInput input = new TransactionInput(outPoint, script, sequence);
		
		return new TransactionInputSize(offset + 0 + 32 + 4 + scriptLen.getSizeInBytes() + (int) scriptLen.value + 4, input);
		
	}
	
}
