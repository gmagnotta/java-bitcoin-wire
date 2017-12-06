package org.gmagnotta.bitcoin.parser;

import java.io.InputStream;
import java.nio.ByteBuffer;

import org.gmagnotta.bitcoin.wire.BitcoinFrame;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.gmagnotta.bitcoin.wire.Utils;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinFrameSerializer;

public class BitcoinFrameParserStream implements Context {
	
	private MessageState messageState;
	private byte[] magic;
	private byte[] command;
	private byte[] length;
	private byte[] checksum;
	private byte[] payload;
	private boolean isComplete;
	private InputStream inputStream;
	private MagicVersion magicVersion;
	
	public BitcoinFrameParserStream(MagicVersion magicVersion, InputStream inputStream) {
		this.magicVersion = magicVersion;
		this.inputStream = inputStream;
		reset();
	}

	private void reset() {
		this.messageState = new MagicState(magicVersion, this);
		this.magic = null;
		this.command = null;
		this.length = null;
		this.checksum = null;
		this.payload = null;
		this.isComplete = false;
	}

	@Override
	public void setNextState(MessageState messageState) {
		this.messageState = messageState;
	}

	@Override
	public void setMagic(byte[] magic) {
		this.magic = magic;
	}

	@Override
	public void setCommand(byte[] command) {
		this.command = command;
	}

	@Override
	public void setLength(byte[] length) {
		this.length = length;
	}
	
	@Override
	public byte[] getLength() {
		return length;
	}

	@Override
	public void setChecksum(byte[] checksum) {
		this.checksum = checksum;
	}

	@Override
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	@Override
	public void setComplete() {
		isComplete = true;
	}
	
	public BitcoinFrame getFrame() throws Exception {
		
		while (!isComplete) {
			
			int input = inputStream.read();
			
			messageState.read((byte) input);
			
		}
		
		long len = Utils.readUint32LE(length, 0);
		
		ByteBuffer buffer = ByteBuffer.allocate((int) (4 + 12 + 4 + 4 + len));
		
		buffer.put(magic);
		buffer.put(command);
		buffer.put(length);
		buffer.put(checksum);
		buffer.put(payload);
		
		BitcoinFrame frame = new BitcoinFrameSerializer().deserialize(buffer.array());
		
		// reset status
		reset();
		
		return frame;
		
	}
	
}
