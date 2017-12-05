package org.gmagnotta.bitcoin.message;

import java.util.Objects;

import org.gmagnotta.bitcoin.wire.BitcoinCommand;

/**
 * This class represents Bitcoin Version Message
 * 
 * When a node creates an outgoing connection, it will immediately advertise its version. The remote node will
 * respond with its version. No further communication is possible until both peers have exchanged their version.
 */
public class BitcoinRejectMessage implements BitcoinMessage {

	private String message;
	private byte ccode;
	private String reason;
	private byte[] data;
	
	public BitcoinRejectMessage(String message, byte ccode, String reason, byte[] data) {

		this.message = message;
		this.ccode = ccode;
		this.reason = reason;
		this.data = data;

	}
	
	public String getMessage() {
		return message;
	}

	public byte getCcode() {
		return ccode;
	}

	public String getReason() {
		return reason;
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public BitcoinCommand getCommand() {
		return BitcoinCommand.REJECT;
	}
	
	@Override
	public boolean equals(final Object object) {
		
		if (!(object instanceof BitcoinRejectMessage))
			return false;
		
		if (this == object)
			return true;
		
		final BitcoinRejectMessage other = (BitcoinRejectMessage) object;
		
		return Objects.equals(message, other.message) &&
				Objects.equals(ccode, other.ccode) &&
				Objects.equals(reason, other.reason) &&
				Objects.equals(data, other.data);
		
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(message, ccode, reason, data);
		
	}
	
	@Override
	public String toString() {
		return String.format("%s:",
				BitcoinCommand.REJECT);
	}

}
