package org.gmagnotta.bitcoin.raw.serializer;

import org.gmagnotta.bitcoin.message.BitcoinMessage;

public interface BitcoinMessageSerializer {
	
	BitcoinMessage deserialize(byte[] payload) throws Exception;
	
	byte[] serialize(BitcoinMessage messageToSerialize);

}
