package org.gmagnotta.bitcoin.wire;

import java.util.Arrays;

public enum MagicVersion {

	MAIN(new byte[] { (byte) 0xf9, (byte) 0xbe, (byte) 0xb4, (byte) 0xd9} ),
	
	TESTNET(new byte[] { (byte) 0xfa, (byte) 0xbf, (byte) 0xb5, (byte) 0xda} ),
	
	TESTNET3(new byte[] { (byte) 0x0b, (byte) 0x11, (byte) 0x09, (byte) 0x07} );
	
	private byte[] magic;
	
	MagicVersion(byte[] magic) {
		this.magic = magic;
	}
	
	public byte[] getBytes() {
		return magic;
	}
	
	public static MagicVersion fromByteArray(byte[] array, int offset) {
		
		for (MagicVersion v : values()) {
			
			if (Arrays.equals(Arrays.copyOfRange(array, offset, offset + 4), v.magic)) {
				return v;
			}
		}
		
		return null;
	}

}
