package org.gmagnotta.bitcoin.wire;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

import org.gmagnotta.bitcoin.wire.exception.MagicVersionException;

/**
 * Contains all Magic version
 */
public enum MagicVersion {

	/* mainnet */
	MAIN(new byte[] { (byte) 0xf9, (byte) 0xbe, (byte) 0xb4, (byte) 0xd9 } ),
	
	/* testnet */
	TESTNET(new byte[] { (byte) 0xfa, (byte) 0xbf, (byte) 0xb5, (byte) 0xda } ),
	
	/* testnet 3 */
	TESTNET3(new byte[] { (byte) 0x0b, (byte) 0x11, (byte) 0x09, (byte) 0x07 } ),
	
	/* regtest */
	REGTEST(new byte[] { (byte) 0xfa, (byte) 0xbf, (byte) 0xb5, (byte) 0xda } );
	
	/* map that contains all versions to fast lookup */
	private static HashMap<ByteBuffer, MagicVersion> VERSION_MAP = new HashMap<ByteBuffer, MagicVersion>();
	
	/* static block to initialize map for all versions */
	static {
		
		for (MagicVersion vers : MagicVersion.values()) {
			
			VERSION_MAP.put(ByteBuffer.wrap(vers.getBytes()), vers);
			
		}
		
	}
	
	private byte[] magic;
	
	private MagicVersion(byte[] magic) {
		this.magic = magic;
	}
	
	public byte[] getBytes() {
		return magic;
	}
	
	public static MagicVersion fromByteArray(byte[] array, int offset) throws MagicVersionException {
		
		MagicVersion version = VERSION_MAP.get(ByteBuffer.wrap(Arrays.copyOfRange(array, offset, offset + 4)));
		
		if (version == null) {
			throw new MagicVersionException("Unknown magic");
		}
		
		return version;
	}

}
