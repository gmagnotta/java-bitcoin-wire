package org.gmagnotta.bitcoin.wire;

import java.math.BigInteger;

/**
 * Utility class to work with deserialization/serialization of data from/to byte arrays
 */
public class Utils {

	/**
	 * Parse 2 bytes from the byte array (starting at the offset) as unsigned
	 * 16-bit integer in little endian format.
	 */
	public static int readUint16LE(byte[] bytes, int offset) {
		return (int) ((bytes[offset] & 0xffl) | ((bytes[offset + 1] & 0xffl) << 8));
	}

	/**
	 * Parse 2 bytes from the byte array (starting at the offset) as unsigned
	 * 16-bit integer in big endian format.
	 */
	public static int readUint16BE(byte[] bytes, int offset) {
		return (int) (((bytes[offset] & 0xffl) << 8) | ((bytes[offset + 1] & 0xffl)));
	}
	
	/**
	 * Parse 2 bytes from the byte array (starting at the offset) as signed
	 * 16-bit integer in little endian format.
	 */
	public static short readSint16LE(byte[] bytes, int offset) {
		return (short) readUint16LE(bytes, offset);
	}

	/**
	 * Parse 2 bytes from the byte array (starting at the offset) as signed
	 * 16-bit integer in big endian format.
	 */
	public static short readSint16BE(byte[] bytes, int offset) {
		return (short) readUint16BE(bytes, offset);
	}

	/**
	 * Parse 4 bytes from the byte array (starting at the offset) as unsigned
	 * 32-bit integer in little endian format.
	 */
	public static long readUint32LE(byte[] bytes, int offset) {
		return (bytes[offset] & 0xffl) | ((bytes[offset + 1] & 0xffl) << 8) | ((bytes[offset + 2] & 0xffl) << 16) | ((bytes[offset + 3] & 0xffl) << 24);
	}

	/**
	 * Parse 4 bytes from the byte array (starting at the offset) as unsigned
	 * 32-bit integer in big endian format.
	 */
	public static long readUint32BE(byte[] bytes, int offset) {
		return ((bytes[offset] & 0xffl) << 24) | ((bytes[offset + 1] & 0xffl) << 16)
				| ((bytes[offset + 2] & 0xffl) << 8) | (bytes[offset + 3] & 0xffl);
	}

	/**
	 * Parse 4 bytes from the byte array (starting at the offset) as signed
	 * 32-bit integer in little endian format.
	 */
	public static int readSint32LE(byte[] bytes, int offset) {
		return (int) readUint32LE(bytes, offset);
	}

	/**
	 * Parse 4 bytes from the byte array (starting at the offset) as signed
	 * 32-bit integer in big endian format.
	 */
	public static int readSint32BE(byte[] bytes, int offset) {
		return (int) readUint32BE(bytes, offset);
	}

	/**
	 * Parse 8 bytes from the byte array (starting at the offset) as signed
	 * 64-bit integer in little endian format.
	 */
	public static long readSint64LE(byte[] bytes, int offset) {
		
		return (bytes[offset] & 0xffl) | ((bytes[offset + 1] & 0xffl) << 8) | ((bytes[offset + 2] & 0xffl) << 16)
				| ((bytes[offset + 3] & 0xffl) << 24) | ((bytes[offset + 4] & 0xffl) << 32)
				| ((bytes[offset + 5] & 0xffl) << 40) | ((bytes[offset + 6] & 0xffl) << 48)
				| ((bytes[offset + 7] & 0xffl) << 56);
		
	}
	
	/**
	 * Parse 8 bytes from the byte array (starting at the offset) as signed
	 * 64-bit integer in big endian format.
	 */
	public static long readSint64BE(byte[] bytes, int offset) {
		
		return ((bytes[offset] & 0xffl) << 56) | ((bytes[offset + 1] & 0xffl) << 48) | ((bytes[offset + 2] & 0xffl) << 40)
				| ((bytes[offset + 3] & 0xffl) << 32) | ((bytes[offset + 4] & 0xffl) << 24)
				| ((bytes[offset + 5] & 0xffl) << 16) | ((bytes[offset + 6] & 0xffl) << 8)
				| (bytes[offset + 7] & 0xffl);
		
	}
	
	/**
	 * Parse 8 bytes from the byte array (starting at the offset) as unsigned
	 * 64-bit integer in little endian format.
	 */
	public static BigInteger readUint64LE(byte[] bytes, int offset) {
		
		long l = readSint64LE(bytes, offset);
		
		BigInteger b = BigInteger.valueOf(l);
		
		if (b.compareTo(BigInteger.ZERO) < 0)
			b = b.add(BigInteger.ONE.shiftLeft(64));
		
		return b;
		
	}

	/**
	 * Parse 8 bytes from the byte array (starting at the offset) as unsigned
	 * 64-bit integer in big endian format.
	 */
	public static BigInteger readUint64BE(byte[] bytes, int offset) {
		
		long l = readSint64BE(bytes, offset);
		
		BigInteger b = BigInteger.valueOf(l);
		
		if (b.compareTo(BigInteger.ZERO) < 0)
			b = b.add(BigInteger.ONE.shiftLeft(64));
		
		return b;
	}
	
	///
	///
	///
	
	public static final byte[] writeInt32BE(long value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
	}
	
	public static final byte[] writeInt32LE(long value) {
		return new byte[] { (byte) value, (byte) (value >>> 8), (byte) (value >>> 16), (byte) (value >>> 24) };
	}
	
	public static final byte[] writeInt64BE(long value) {
		return new byte[] { (byte) (value >>> 56), (byte) (value >>> 48), (byte) (value >>> 40), (byte) (value >>> 32), (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
	}
	
	public static final byte[] writeInt64LE(long value) {
		return new byte[] { (byte) value, (byte) (value >>> 8), (byte) (value >>> 16), (byte) (value >>> 24), (byte) (value >>> 32), (byte) (value >>> 40), (byte) (value >>> 48), (byte) (value >>> 56) };
	}
	
	public static final byte[] writeInt16BE(int value) {
		return new byte[] { (byte) (value >>> 8), (byte) value };
	}
	
	public static final byte[] writeInt16LE(int value) {
		return new byte[] { (byte) value, (byte) (value >>> 8) };
	}

}
