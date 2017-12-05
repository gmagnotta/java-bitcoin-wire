package org.gmagnotta.bitcoin.raw.serializer;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.raw.BitcoinCommand;
import org.gmagnotta.bitcoin.raw.BitcoinFrame;
import org.gmagnotta.bitcoin.raw.MagicVersion;
import org.gmagnotta.bitcoin.raw.Utils;

public class BitcoinFrameSerializer {
	
	public BitcoinFrame deserialize(byte[] payload) throws Exception {
		
		MagicVersion magicVersion = MagicVersion.fromByteArray(payload, 0);
		
		BitcoinCommand bitcoinCommand = BitcoinCommand.fromByteArray(payload, 4);
		
		long len = Utils.readUint32LE(payload, 16);
		
		long checksum =  Utils.readUint32BE(payload, 20);
		
		byte[] messagepart = Arrays.copyOfRange(payload, 24, (int) (24 + len));
		
		Sha256Hash hash = Sha256Hash.twiceOf(messagepart);
		
		long cksum2 = Utils.readUint32BE(hash.getBytes(), 0);
		
		if (checksum != cksum2) {
			throw new Exception("Invalid checksum");
		}

		return new BitcoinFrame(magicVersion, bitcoinCommand, len, checksum, bitcoinCommand.deserialize(messagepart));
	}
	
	public byte[] serialize(BitcoinFrame bitcoinFrame) {
		
		BitcoinCommand command = bitcoinFrame.getPayload().getCommand();
		
		byte[] messageSerialized = command.serialize(bitcoinFrame.getPayload());
		
		ByteBuffer buffer = ByteBuffer.allocate(4 + 12 + 4 + 4 + messageSerialized.length);
		
		buffer.put(bitcoinFrame.getMagic().getBytes());
		
		buffer.put(bitcoinFrame.getCommand().serialize());
		
		buffer.put(Utils.writeInt32LE(messageSerialized.length));
		
		buffer.put(Arrays.copyOfRange(Sha256Hash.twiceOf(messageSerialized).getBytes(), 0, 4));
		
		buffer.put(messageSerialized);
		
		return buffer.array();
		
	}
	
}
