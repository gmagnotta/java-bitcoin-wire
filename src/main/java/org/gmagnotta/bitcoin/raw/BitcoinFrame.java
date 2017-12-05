package org.gmagnotta.bitcoin.raw;

import java.util.Objects;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.gmagnotta.bitcoin.message.BitcoinMessage;

/**
 * This class represents a Bitcoin Frame: the low level message that transport higher level application messages.
 */
public class BitcoinFrame {
	
	// Magic value indicating message origin network, and used to seek to next message when stream state is unknown
	private MagicVersion magic;
	// 	ASCII string identifying the packet content, NULL padded (non-NULL padding results in packet rejected)
	private BitcoinCommand command;
	// Length of payload in number of bytes
	private long lenght;
	// First 4 bytes of sha256(sha256(payload))
	private long checksum;
	// The actual data
	private BitcoinMessage payload;
	
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
		
		public BitcoinFrame build() {
			
			byte[] serialized = bitcoinMessage.getCommand().getBitcoinMessageSerializer().serialize(bitcoinMessage);
			
			Sha256Hash hash = Sha256Hash.twiceOf(serialized);
			
			long len = serialized.length;
			
			return new BitcoinFrame(MagicVersion.TESTNET, bitcoinMessage.getCommand(), len, Utils.readUint32BE(hash.getBytes(), 0), bitcoinMessage);
			
		}
		
	}
	
}
