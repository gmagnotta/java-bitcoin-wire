package org.gmagnotta.bitcoin.wire.serializer.impl;

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
		
		return null;

	}
	
}

