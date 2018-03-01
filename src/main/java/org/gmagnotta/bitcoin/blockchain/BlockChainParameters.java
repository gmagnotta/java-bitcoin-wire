package org.gmagnotta.bitcoin.blockchain;

import java.math.BigInteger;

import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;

public enum BlockChainParameters {

	/* mainnet */
	MAINNET(

			new BlockHeader(1, Sha256Hash.wrap("0000000000000000000000000000000000000000000000000000000000000000"),
					Sha256Hash.wrap("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"), 1231006505,
					486604799, 2083236893, 1),

			new BigInteger("00000000ffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16),

			false,
			
			false,

			14 * 24 * 60 * 60,

			10 * 60,
			
			new String[]{ /*"seed.bitcoinabc.org", */"seed.bitcoin.jonasschnelli.ch", "seed.bitcoin.sipa.be", "dnsseed.bluematt.me", "dnsseed.bitcoin.dashjr.org", "seed.btc.petertodd.org", "seed.bitcoinstats.com" },
			
			8333

	),

	/* testnet 3 */
	TESTNET3(

			new BlockHeader(1, Sha256Hash.wrap("0000000000000000000000000000000000000000000000000000000000000000"),
					Sha256Hash.wrap("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"), 1296688602,
					486604799, 414098458, 1),

			new BigInteger("00000000ffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16),

			true,
			
			false,

			14 * 24 * 60 * 60,

			10 * 60,
			
			new String[]{ "testnet-seed.bitcoin.jonasschnelli.ch", "seed.tbtc.petertodd.org" },
			
			18333

	),

	/* regtest */
	REGTEST(

			new BlockHeader(1, Sha256Hash.wrap("0000000000000000000000000000000000000000000000000000000000000000"),
					Sha256Hash.wrap("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"), 1296688602,
					545259519, 2, 1),

			new BigInteger("7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16),

			true,
			
			true,

			14 * 24 * 60 * 60,

			10 * 60,
			
			new String[]{"52.225.217.168"},
			
			19000

	);

	private BlockHeader genesis;
	private BigInteger powLimit;
	private boolean powNoRetargeting;
	private int targetTimespan;
	private int targetSpacing;
	private boolean powAllowMinDifficultyBlocks;
	private String[] seeds;
	private int port;

	private BlockChainParameters(BlockHeader genesis, BigInteger powLimit, boolean powAllowMinDifficultyBlocks,
			boolean powNoRetargeting, int targetTimespan, int targetSpacing, String[] seeds, int port) {
		this.genesis = genesis;
		this.powLimit = powLimit;
		this.powAllowMinDifficultyBlocks = powAllowMinDifficultyBlocks;
		this.powNoRetargeting = powNoRetargeting;
		this.targetTimespan = targetTimespan;
		this.targetSpacing = targetSpacing;
		this.seeds = seeds;
		this.port = port;
	}

	public BlockHeader getGenesis() {
		return genesis;
	}

	public BigInteger getPowLimit() {
		return powLimit;
	}

	public boolean getPowNoRetargeting() {
		return powNoRetargeting;
	}

	public int getTargetTimespan() {
		return targetTimespan;
	}

	public int getTargetSpacing() {
		return targetSpacing;
	}

	public int getDifficultyAdjustmentInterval() {
		return targetTimespan / targetSpacing;
	}

	public boolean getAllowMinDifficultyBlocks() {
		return powAllowMinDifficultyBlocks;
	}
	
	public String[] getSeeds() {
		return seeds;
	}
	
	public int getPort() {
		return port;
	}

}
