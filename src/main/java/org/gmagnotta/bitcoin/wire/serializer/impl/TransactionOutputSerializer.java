package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.math.BigInteger;
import java.nio.ByteBuffer;
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
	
	public byte[] serialize(TransactionOutput transactionOutput) {
		
		VarInt pkScriptLen = new VarInt(transactionOutput.getScriptPubKey().length);
		
		ByteBuffer buffer = ByteBuffer.allocate(8 + pkScriptLen.getSizeInBytes() + transactionOutput.getScriptPubKey().length);
		
		buffer.put(Utils.writeInt64LE(transactionOutput.getValue().longValue()));
		
		buffer.put(pkScriptLen.encode());
		
		buffer.put(transactionOutput.getScriptPubKey());
		
		return buffer.array();
		
	}
	
}
