package org.gmagnotta.bitcoin.wire.serializer;

import org.gmagnotta.bitcoin.message.BitcoinMessage;

public interface BitcoinMessageSerializer {
	
	BitcoinMessage deserialize(byte[] payload) throws BitcoinMessageSerializerException;
	
	byte[] serialize(BitcoinMessage messageToSerialize) throws BitcoinMessageSerializerException;

}
