package org.gmagnotta.bitcoin.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.gmagnotta.bitcoin.wire.BitcoinFrame;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.gmagnotta.bitcoin.wire.exception.BitcoinFrameBuilderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitcoinFrameParserStream implements Context {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BitcoinFrameParserStream.class);
	
	private MessageState messageState;
	private byte[] magic;
	private byte[] command;
	private byte[] length;
	private byte[] checksum;
	private ByteBuffer payload;
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
	public void setPayload(ByteBuffer payload) {
		this.payload = payload;
	}
	
	@Override
	public void setComplete() {
		isComplete = true;
	}
	
	public BitcoinFrame getBitcoinFrame() throws BitcoinFrameBuilderException, EndOfStreamException, IOException {
		
		try {
			
			while (!isComplete) {
				
				int input = inputStream.read();
				
				if (input == -1) {
					
					//End of Stream reached
					throw new EndOfStreamException("End of Stream reached!");
					
				}
				
				messageState.process((byte) input);
				
			}
			
			return BitcoinFrame.deserialize(magic, command, length, checksum, payload);
		
		} finally {
			
			// reset status
			reset();
			
		}
		
	}
	
}
