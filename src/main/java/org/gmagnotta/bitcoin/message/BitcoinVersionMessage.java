package org.gmagnotta.bitcoin.message;

import java.math.BigInteger;
import java.util.Objects;

import org.gmagnotta.bitcoin.raw.BitcoinCommand;
import org.gmagnotta.bitcoin.raw.NetworkAddress;

/**
 * This class represents Bitcoin Version Message
 * 
 * When a node creates an outgoing connection, it will immediately advertise its version. The remote node will
 * respond with its version. No further communication is possible until both peers have exchanged their version.
 */
public class BitcoinVersionMessage implements BitcoinMessage {

	private long protocolVersion;
	private BigInteger nodeServices;
	private BigInteger nodeTimestamp;
	private NetworkAddress addressReceiving;
	private NetworkAddress addressEmitting;
	private BigInteger nonce;
	private String userAgent;
	private long blockStartHeight;
	private boolean relay;

	public BitcoinVersionMessage(long protocolVersion, BigInteger nodeServices, BigInteger nodeTimestamp,
		NetworkAddress addressReceiving, NetworkAddress addressEmitting, BigInteger nonce, String userAgent, long blockStartHeight, boolean relay) {

		this.protocolVersion = protocolVersion;
		this.nodeServices = nodeServices;
		this.nodeTimestamp = nodeTimestamp;
		this.addressReceiving = addressReceiving;
		this.addressEmitting = addressEmitting;
		this.nonce = nonce;
		this.userAgent = userAgent;
		this.blockStartHeight = blockStartHeight;
		this.relay = relay;

	}

	public long getVersion() {
		return protocolVersion;
	}

	public BigInteger getServices() {
		return nodeServices;
	}

	public BigInteger getTimestamp() {
		return nodeTimestamp;
	}
	
	public NetworkAddress getAddressReceiving() {
		return addressReceiving;
	}

	public NetworkAddress getAddressEmitting() {
		return addressEmitting;
	}

	public BigInteger getNonce() {
		return nonce;
	}

	public String getUserAgent() {

		return userAgent;
	}

	public long getStartHeight() {

		return blockStartHeight;

	}
	
	public boolean getRelay() {
		return relay;
	}

	@Override
	public BitcoinCommand getCommand() {
		return BitcoinCommand.VERSION;
	}
	
	@Override
	public boolean equals(final Object object) {
		
		if (!(object instanceof BitcoinVersionMessage))
			return false;
		
		if (this == object)
			return true;
		
		final BitcoinVersionMessage other = (BitcoinVersionMessage) object;
		
		return Objects.equals(protocolVersion, other.protocolVersion) &&
				Objects.equals(nodeServices, other.nodeServices) &&
				Objects.equals(nodeTimestamp, other.nodeTimestamp) &&
				Objects.equals(addressReceiving, other.addressReceiving) &&
				Objects.equals(addressEmitting, other.addressEmitting) &&
				Objects.equals(nonce, other.nonce) &&
				Objects.equals(userAgent, other.userAgent) &&
				Objects.equals(blockStartHeight, other.blockStartHeight) &&
				Objects.equals(relay, other.relay);
		
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(protocolVersion, nodeServices, nodeTimestamp, addressReceiving,
				addressEmitting, nonce, userAgent, blockStartHeight, relay);
		
	}
	
	@Override
	public String toString() {
		return String.format("%s: protocolVersion %d, nodeServices %d, nodeTimestamp %d, addressReceiving %s, addressEmitting %s, nonce %s, userAgent %s, blockStartHeight %d, relay %b",
				BitcoinCommand.VERSION, protocolVersion, nodeServices, nodeTimestamp, addressReceiving, addressEmitting, nonce, userAgent, blockStartHeight, relay);
	}

}
