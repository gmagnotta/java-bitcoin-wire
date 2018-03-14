package org.gmagnotta.bitcoin.wire;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.gmagnotta.bitcoin.blockchain.BlockChainParameters;
import org.gmagnotta.bitcoin.wire.exception.MagicVersionException;

/**
 * Contains all Magic version
 */
public enum MagicVersion {

	/* mainnet */
	MAINNET(new byte[] { (byte) 0xf9, (byte) 0xbe, (byte) 0xb4, (byte) 0xd9 },
			
			BlockChainParameters.MAINNET
			
	),
	
	/* testnet 3 */
	TESTNET3(new byte[] { (byte) 0x0b, (byte) 0x11, (byte) 0x09, (byte) 0x07 },
			
			BlockChainParameters.TESTNET3
			
	),
	
	/* regtest */
	REGTEST(new byte[] { (byte) 0xfa, (byte) 0xbf, (byte) 0xb5, (byte) 0xda },
			
			BlockChainParameters.REGTEST
			
	);
	
	/* map that contains all versions to fast lookup */
	private static HashMap<ByteBuffer, MagicVersion> VERSION_MAP = new HashMap<ByteBuffer, MagicVersion>();
	
	/* static block to initialize map for all versions */
	static {
		
		for (MagicVersion vers : MagicVersion.values()) {
			
			VERSION_MAP.put(ByteBuffer.wrap(vers.getBytes()), vers);
			
		}
		
	}
	
	private byte[] magic;
	private BlockChainParameters blockChainParameters;
	
	private MagicVersion(byte[] magic, BlockChainParameters blockChainParameters) {
		this.magic = magic;
		this.blockChainParameters = blockChainParameters;
	}
	
	public byte[] getBytes() {
		return magic;
	}
	
	public BlockChainParameters getBlockChainParameters() {
		return blockChainParameters;
	}
	
	public static MagicVersion fromByteArray(byte[] array, int offset) throws MagicVersionException {
		
		MagicVersion version = VERSION_MAP.get(ByteBuffer.wrap(array, offset, 4));
		
		if (version == null) {
			throw new MagicVersionException("Unknown magic");
		}
		
		return version;
	}

}
