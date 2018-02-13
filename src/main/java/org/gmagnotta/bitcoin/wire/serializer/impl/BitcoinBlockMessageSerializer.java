package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.message.impl.BlockMessage;
import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializerException;

public class BitcoinBlockMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload, int offset, int lenght) throws BitcoinMessageSerializerException {
		
		try {
			
			BlockHeadersSerializer blockHeadersSerializer = new BlockHeadersSerializer();
			
			BlockHeader header = blockHeadersSerializer.deserialize(payload, offset + 0, lenght);
			
			VarInt txCount = new VarInt(header.getTxnCount());
			int lastIndex = offset + 0 + 80 + txCount.getSizeInBytes();
			List<Transaction> transactions = new ArrayList<Transaction>();
			for (int i = 0; i < txCount.value; i++) {
			
				TransactionSerializer transactionSerializer = new TransactionSerializer();
				
				TransactionSize transactionSize = transactionSerializer.deserialize(payload, lastIndex, payload.length);
			
				lastIndex = (int) transactionSize.getSize();
				
				transactions.add(transactionSize.getTransaction());
				
			}
			// return assembled message
			return new BlockMessage(header, transactions);
		
		} catch (Exception ex) {
			throw new BitcoinMessageSerializerException("Exception", ex);
		}
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		BlockMessage blockMessage = (BlockMessage) messageToSerialize;
		
		BlockHeadersSerializer blockHeadersSerializer = new BlockHeadersSerializer(true);
		
		byte[] b = blockHeadersSerializer.serialize(blockMessage.getBlockHeader());
		
		List<byte[]> serialized = new ArrayList<byte[]>();
		int size = 0;
		
		TransactionSerializer transactionSerializer = new TransactionSerializer();
		
		for (Transaction tx : blockMessage.getTxns()) {
			
			byte[] txSerialized = transactionSerializer.serialize(tx);
			
			serialized.add(txSerialized);
			
			size += txSerialized.length;
			
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(b.length + size);
		
		buffer.put(b);
		
		for (byte[] tx : serialized) {
			buffer.put(tx);
		}
		
		return buffer.array();

	}
	
}

