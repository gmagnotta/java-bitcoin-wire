package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.message.impl.TransactionInput;
import org.gmagnotta.bitcoin.message.impl.TransactionOutput;
import org.gmagnotta.bitcoin.wire.Utils;

public class TransactionSerializer {
	
	public TransactionSize deserialize(byte[] payload, int offset, int lenght) {
		
		// read version
		long version = Utils.readSint32LE(payload, offset + 0);
		
		// read how many input we have
		VarInt txIn = new VarInt(payload, offset + 4);
		
		TransactionInputSerializer transactionInputSerializer = new TransactionInputSerializer();
		TransactionOutputSerializer transactionOutputSerializer = new TransactionOutputSerializer();
		
		int lastIndex = offset + 0 + 4 + txIn.getSizeInBytes();
		List<TransactionInput> txInputs = new ArrayList<TransactionInput>();
		for (int i = 0; i < txIn.value; i++) {
		
			TransactionInputSize transactionSize = transactionInputSerializer.deserialize(payload, lastIndex);
			
			lastIndex = (int) transactionSize.getSize();
			
			txInputs.add(transactionSize.getTransactionInput());
		
		}
		
		VarInt txOut = new VarInt(payload, lastIndex);
		
		lastIndex = lastIndex + txOut.getSizeInBytes();
		List<TransactionOutput> txOutputs = new ArrayList<TransactionOutput>();
		for (int i = 0; i < txOut.value; i++) {
		
			TransactionOutputSize transactionOutput = transactionOutputSerializer.deserialize(payload, lastIndex);
			
			lastIndex = (int) transactionOutput.getSize();
			
			txOutputs.add(transactionOutput.getTransactionOutput());
			
		}
		
		long lockTime = Utils.readUint32LE(payload, lastIndex);
		
		Transaction transaction = new Transaction(version, txInputs, txOutputs, lockTime);
		
		return new TransactionSize(lastIndex + 4, transaction);
	}
	
	public byte[] serialize(Transaction transaction) {
		
		List<byte[]> inputSerialized = new ArrayList<byte[]>();
		
		TransactionInputSerializer transactionInputSerializer = new TransactionInputSerializer();
		int sizeIn = 0;
		
		for (TransactionInput txIn : transaction.getTransactionInput()) {
			
			byte[] serialized = transactionInputSerializer.serialize(txIn);
			
			inputSerialized.add(serialized);
			
			sizeIn += serialized.length;
			
		}
		
		List<byte[]> outputSerialized = new ArrayList<byte[]>();
		
		TransactionOutputSerializer transactionOutputSerializer = new TransactionOutputSerializer();
		int sizeOut = 0;
		
		for (TransactionOutput txOut : transaction.getTransactionOutput()) {
			
			byte[] serialized = transactionOutputSerializer.serialize(txOut);
			
			outputSerialized.add(serialized);
			
			sizeOut += serialized.length;
			
		}
		
		
		VarInt txInCount = new VarInt(transaction.getTransactionInput().size());
		
		VarInt txOutCount = new VarInt(transaction.getTransactionOutput().size());
		
		ByteBuffer buffer = ByteBuffer.allocate(4 + txInCount.getSizeInBytes() + sizeIn +
				txOutCount.getSizeInBytes() + sizeOut + 4);
		
		buffer.put(Utils.writeInt32LE(transaction.getVersion()));
		
		buffer.put(txInCount.encode());
		
		for (byte[] b : inputSerialized) {
			buffer.put(b);
		}
		
		buffer.put(txOutCount.encode());
		
		for (byte[] b : outputSerialized) {
			buffer.put(b);
		}
		
		buffer.put(Utils.writeInt32LE(transaction.getLockTime()));
		
		return buffer.array();		
		
	}

}
