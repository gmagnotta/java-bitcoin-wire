package org.gmagnotta.bitcoin.wire.serializer;

import org.gmagnotta.bitcoin.message.BitcoinMessage;

public interface BitcoinMessageSerializer {
	
	/**
	 * Deserialize an array of byte to a BitcoinMessage
	 * 
	 * @param payload
	 * @return
	 * @throws BitcoinMessageSerializerException
	 */
	public BitcoinMessage deserialize(byte[] payload) throws BitcoinMessageSerializerException;
	
	/**
	 * Serialize a BitcoinMessage to a byte array
	 * 
	 * @param messageToSerialize
	 * @return
	 * @throws BitcoinMessageSerializerException
	 */
	public byte[] serialize(BitcoinMessage messageToSerialize) throws BitcoinMessageSerializerException;

}
