package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.message.impl.BlockMessage;
import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializerException;

import com.subgraph.orchid.encoders.Hex;

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
		
		BitcoinHeadersMessage message = ((BitcoinHeadersMessage) messageToSerialize);
		
		VarInt v = new VarInt(message.getHeaders().size());
		
		ByteBuffer buffer = ByteBuffer.allocate(4 + v.getSizeInBytes() + 81 * message.getHeaders().size() + 81);
		
		buffer.put(v.encode());
		
		BlockHeadersSerializer blockHeadersSerializer = new BlockHeadersSerializer();
		
		for (BlockHeader header : message.getHeaders()) {
			
			buffer.put(blockHeadersSerializer.serialize(header));
			
		}
		
		buffer.put(Hex.decode("0000000000000000000000000000000000000000000000000000000000000000"));
		
		return buffer.array();

	}
	
}

