package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.impl.OutPoint;
import org.gmagnotta.bitcoin.message.impl.TransactionInput;
import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.wire.Utils;

public class TransactionInputSerializer {

	public TransactionInputSize deserialize(byte[] payload, int offset) {
		
		Sha256Hash hash = Sha256Hash.wrapReversed(payload, offset + 0, 32);
		long index = Utils.readUint32LE(payload, offset + 0 + 32);
		VarInt scriptLen = new VarInt(payload, offset + 0 + 32 + 4);
		
		byte[] script = Arrays.copyOfRange(payload, offset + 0 + 32 + 4 + scriptLen.getSizeInBytes(), offset + 0 + 32 + 4 + scriptLen.getSizeInBytes() + (int) scriptLen.value);
		
		long sequence = Utils.readUint32LE(payload, offset + 0 + 32 + 4 + scriptLen.getSizeInBytes() + script.length);
		
		OutPoint outPoint = new OutPoint(hash, index);
		
		TransactionInput input = new TransactionInput(outPoint, script, sequence);
		
		return new TransactionInputSize(offset + 0 + 32 + 4 + scriptLen.getSizeInBytes() + (int) scriptLen.value + 4, input);
		
	}
	
	public byte[] serialize(TransactionInput transactionInput) {
		
		OutPoint previousOutput = transactionInput.getPreviousOutput();

		VarInt scriptLen = new VarInt(transactionInput.getSignatureScript().length);

		ByteBuffer buffer = ByteBuffer.allocate(32 + 4 + scriptLen.getSizeInBytes() + transactionInput.getSignatureScript().length + 4);
		
		buffer.put(previousOutput.getHash().getReversedBytes());
		buffer.put(Utils.writeInt32LE(previousOutput.getIndex()));
		
		buffer.put(scriptLen.encode());
		
		buffer.put(transactionInput.getSignatureScript());
		
		buffer.put(Utils.writeInt32LE(transactionInput.getSequence()));
		
		return buffer.array();
		
	}
	
}
