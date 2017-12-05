package org.gmagnotta.bitcoin.message;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Objects;

public class NetworkAddress {
	
	private long time;
	private BigInteger services;
	private InetAddress inetAddress;
	private int port;
	
	public NetworkAddress(long time, BigInteger services, InetAddress inetAddress, int port) {
		this.time = time;
		this.services = services;
		this.inetAddress = inetAddress;
		this.port = port;
	}
	
	public long getTime() {
		return time;
	}

	public BigInteger getServices() {
		return services;
	}

	public InetAddress getInetAddress() {
		return inetAddress;
	}

	public int getPort() {
		return port;
	}
	
	@Override
	public boolean equals(final Object object) {
		
		if (!(object instanceof NetworkAddress))
			return false;
		
		if (this == object)
			return true;
		
		final NetworkAddress other = (NetworkAddress) object;
		
		return Objects.equals(time, other.getTime()) &&
				Objects.equals(services, other.getServices()) &&
				Objects.equals(inetAddress, other.getInetAddress()) &&
				Objects.equals(port, other.getPort());
		
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(time, services, inetAddress, port);
		
	}
	
	@Override
	public String toString() {
		
		return String.format("NetworkAddress: %d, %d, %s, %d", time, services, inetAddress, port);
		
	}

}
