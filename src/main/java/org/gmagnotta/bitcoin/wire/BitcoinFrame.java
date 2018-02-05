package org.gmagnotta.bitcoin.wire;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.exception.BitcoinFrameBuilderException;

/**
 * This class represents a Bitcoin Frame: the low level message that transport higher level application messages.
 */
public class BitcoinFrame {
	
	/* Magic value indicating message origin network, and used to seek to next message when stream state is unknown */
	private MagicVersion magic;
	/* ASCII string identifying the packet content, NULL padded (non-NULL padding results in packet rejected) */
	private BitcoinCommand command;
	/* Length of payload in number of bytes */
	private long lenght;
	/* First 4 bytes of sha256(sha256(payload)) */
	private long checksum;
	/* The actual data */
	private BitcoinMessage payload;
	
	/**
	 * 
	 * @param magic
	 * @param command
	 * @param length
	 * @param checksum
	 * @param payload
	 */
	public BitcoinFrame(MagicVersion magic, BitcoinCommand command, long length, long checksum, BitcoinMessage payload) {
		this.magic = magic;
		this.command = command;
		this.lenght = length;
		this.checksum = checksum;
		this.payload = payload;
	}
	
	public MagicVersion getMagic() {
		return magic;
	}
	
	public BitcoinCommand getCommand() {
		return command;
	}
	
	public long getLenght() {
		return lenght;
	}
	
	public long getChecksum() {
		return checksum;
	}
	
	public BitcoinMessage getPayload() {
		return payload;
	}
	
	@Override
	public boolean equals(final Object object) {
		
		if (!(object instanceof BitcoinFrame))
			return false;
		
		if (this == object)
			return true;
		
		final BitcoinFrame other = (BitcoinFrame) object;
		
		return Objects.equals(magic, other.getMagic()) &&
				Objects.equals(command, other.getCommand()) &&
				Objects.equals(lenght, other.getLenght()) &&
				Objects.equals(checksum, other.getChecksum()) &&
				Objects.equals(payload, other.getPayload());
		
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(magic, command, lenght, checksum, payload);
		
	}
	
	@Override
	public String toString() {
		
		return String.format("%s, %s, %d, %d, %s", magic, command, lenght, checksum, payload);
		
	}
	
	/**
	 * Deserialize a BitcoinFrame from a byte array
	 * @param payload
	 * @return
	 * @throws BitcoinFrameBuilderException
	 */
	public static BitcoinFrame deserialize(byte[] payload, int offset) throws BitcoinFrameBuilderException {
		
		try {
			
			MagicVersion magicVersion = MagicVersion.fromByteArray(payload, offset + 0);
			
			BitcoinCommand bitcoinCommand = BitcoinCommand.fromByteArray(payload, offset + 4);
			
			long len = Utils.readUint32LE(payload, offset + 16);
			
			long checksum =  Utils.readUint32BE(payload, offset + 20);
			
			Sha256Hash hash = Sha256Hash.twiceOf(payload, offset + 24, (int) (len));
			
			long cksum2 = Utils.readUint32BE(hash.getBytes(), 0);
			
			if (checksum != cksum2) {
				throw new Exception("Invalid checksum");
			}
			
			return new BitcoinFrame(magicVersion, bitcoinCommand, len, checksum, bitcoinCommand.deserialize(payload, offset + 24, (int) (len)));
		
		} catch (Exception ex) {
			
			throw new BitcoinFrameBuilderException("Exception while deserializing frame", ex);
			
		}
	}
	
	public static byte[] serialize(BitcoinFrame bitcoinFrame) throws BitcoinFrameBuilderException {
		
		try {
			
			BitcoinCommand command = bitcoinFrame.getPayload().getCommand();
			
			byte[] messageSerialized = command.serialize(bitcoinFrame.getPayload());
			
			ByteBuffer buffer = ByteBuffer.allocate(4 + 12 + 4 + 4 + messageSerialized.length);
			
			buffer.put(bitcoinFrame.getMagic().getBytes());
			
			buffer.put(bitcoinFrame.getCommand().serialize());
			
			buffer.put(Utils.writeInt32LE(messageSerialized.length));
			
			buffer.put(Arrays.copyOfRange(Sha256Hash.twiceOf(messageSerialized).getBytes(), 0, 4));
			
			buffer.put(messageSerialized);
			
			return buffer.array();
			
		} catch (Exception ex) {
			
			throw new BitcoinFrameBuilderException("Exception", ex);
			
		}
		
	}
	
	public static class BitcoinFrameBuilder {
		
		private MagicVersion magicVersion;
		private BitcoinMessage bitcoinMessage;
		
		public BitcoinFrameBuilder() {
		}

		public MagicVersion getMagicVersion() {
			return magicVersion;
		}

		public BitcoinFrameBuilder setMagicVersion(MagicVersion magicVersion) {
			this.magicVersion = magicVersion;
			return this;
		}

		public BitcoinMessage getBitcoinMessage() {
			return bitcoinMessage;
		}

		public BitcoinFrameBuilder setBitcoinMessage(BitcoinMessage bitcoinMessage) {
			this.bitcoinMessage = bitcoinMessage;
			return this;
		}
		
		public BitcoinFrame build() throws BitcoinFrameBuilderException {
			
			try {
				
				byte[] serialized = bitcoinMessage.getCommand().getBitcoinMessageSerializer().serialize(bitcoinMessage);
				
				Sha256Hash hash = Sha256Hash.twiceOf(serialized);
				
				long len = serialized.length;
				
				return new BitcoinFrame(magicVersion, bitcoinMessage.getCommand(), len, Utils.readUint32BE(hash.getBytes(), 0), bitcoinMessage);
			
			} catch (Exception ex) {
				
				throw new BitcoinFrameBuilderException("Exception", ex);
				
			}
			
		}
		
	}
	
}
