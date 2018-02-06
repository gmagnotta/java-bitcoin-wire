package org.gmagnotta.bitcoin.wire;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.exception.BitcoinCommandException;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializerException;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinAddrMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinBlockMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinGetAddrMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinGetDataMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinGetHeadersMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinHeadersMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinInvMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinPingMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinPongMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinRejectMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinSendHeadersMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinVerackMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinVersionMessageSerializer;

/**
 * Contains all supported Bitcoin Commands
 */
public enum BitcoinCommand {
	
	VERSION("version", new BitcoinVersionMessageSerializer()),
	
	VERACK("verack", new BitcoinVerackMessageSerializer()),
	
	PING("ping", new BitcoinPingMessageSerializer()),
	
	ADDR("addr", new BitcoinAddrMessageSerializer()),
	
	GETADDR("getaddr", new BitcoinGetAddrMessageSerializer()),
	
	GETHEADERS("getheaders", new BitcoinGetHeadersMessageSerializer()),
	
	PONG("pong", new BitcoinPongMessageSerializer()),
	
	HEADERS("headers", new BitcoinHeadersMessageSerializer()),
	
	REJECT("reject", new BitcoinRejectMessageSerializer()),
	
	SENDHEADERS("sendheaders", new BitcoinSendHeadersMessageSerializer()),
	
	GETDATA("getdata", new BitcoinGetDataMessageSerializer()),
	
	BLOCK("block",  new BitcoinBlockMessageSerializer()),
	
	INV("inv", new BitcoinInvMessageSerializer());
	
	/* map that contains all commands to fast lookup */
	private static HashMap<String, BitcoinCommand> COMMAND_MAP = new HashMap<String, BitcoinCommand>();

	/* static block to initialize map for all versions */
	static {
		
		for (BitcoinCommand command : BitcoinCommand.values()) {
			
			COMMAND_MAP.put(command.getCommand(), command);
			
		}
		
	}
	
	private String command;
	private BitcoinMessageSerializer bitcoinMessageSerializer;

	private BitcoinCommand(String command, BitcoinMessageSerializer bitcoinMessageSerializer) {
		this.command = command;
		this.bitcoinMessageSerializer = bitcoinMessageSerializer;
	}
	
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
	
	public static BitcoinCommand fromByteArray(byte[] array, int offset) throws BitcoinCommandException {
		
		String command = new String(array, offset, 12).trim();
		
		BitcoinCommand readValue = COMMAND_MAP.get(command);
		
		if (readValue == null) {
			throw new BitcoinCommandException("Unknown command " + new String(array, offset, 12).trim());
		}
		
		return readValue;
		
	}
	
	public BitcoinMessage deserialize(byte[] payload, int offset, int lenght) throws BitcoinMessageSerializerException {

		return bitcoinMessageSerializer.deserialize(payload, offset, lenght);

	}
	
	public byte[] serialize(BitcoinMessage bitcoinMessage) throws BitcoinMessageSerializerException {
		
		return bitcoinMessageSerializer.serialize(bitcoinMessage);

	}
	
}
