package org.gmagnotta.bitcoin.wire;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinAddrMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinGetBlocksMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinGetHeadersMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinHeadersMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinPingMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinPongMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinVerackMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinVersionMessageSerializer;

/**
 * Contains all supported Bitcoin Commands
 */
public enum BitcoinCommand {
	
	VERSION("version", new BitcoinVersionMessageSerializer()),
	
	VERACK("verack", new BitcoinVerackMessageSerializer()),
	
	PING("ping", new BitcoinPingMessageSerializer()),
	
	ADDR("addr", new BitcoinAddrMessageSerializer()),
	
	GETHEADERS("getheaders", new BitcoinGetHeadersMessageSerializer()),
	
	GETBLOCKS("getblocks", new BitcoinGetBlocksMessageSerializer()),
	
	PONG("pong", new BitcoinPongMessageSerializer()),
	
	HEADERS("headers", new BitcoinHeadersMessageSerializer());

	private BitcoinCommand(String command, BitcoinMessageSerializer bitcoinMessageSerializer) {
		this.command = command;
		this.bitcoinMessageSerializer = bitcoinMessageSerializer;
	}
	
	private String command;
	private BitcoinMessageSerializer bitcoinMessageSerializer;
	
	public String getCommand() {
		return command;
	}
	
	public BitcoinMessageSerializer getBitcoinMessageSerializer() {
		return bitcoinMessageSerializer;
	}
	
	public byte[] serialize() {
		
		ByteBuffer buffer = ByteBuffer.allocate(12);
		buffer.put(command.getBytes());
		return buffer.array();
		
	}
	
	public static BitcoinCommand fromByteArray(byte[] array, int offset) {
		
		for (BitcoinCommand c : values()) {

			String str = new String(c.command).trim();
			
			String str2 = new String(Arrays.copyOfRange(array, offset, offset + 12)).trim();
			
			if (str.equals(str2)) {
				return c;
			}
			
		}

		return null;
	}
	
	public BitcoinMessage deserialize(byte[] payload) throws Exception {

		return bitcoinMessageSerializer.deserialize(payload);

	}
	
	public byte[] serialize(BitcoinMessage bitcoinMessage) {
		
		return bitcoinMessageSerializer.serialize(bitcoinMessage);

	}
	
}
