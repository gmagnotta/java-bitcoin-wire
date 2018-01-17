import java.math.BigInteger;

import org.gmagnotta.bitcoin.wire.Utils;
import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {
	
	private static byte[] array16 = new byte[] { (byte) 0xfe, (byte) 0xb9 };
	
	private static byte[] array32 = new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xfe, (byte) 0xb9 };
	
	private static byte[] array64 = new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xfe, (byte) 0xb9 };
	
	@Test
	public void testReadUint16LE() {
		Assert.assertEquals(47614, Utils.readUint16LE(array16, 0));
	}

	@Test
	public void testReadUint16BE() {
		Assert.assertEquals(65209, Utils.readUint16BE(array16, 0));
	}
	
	@Test
	public void testReadSint16LE() {
		Assert.assertEquals(-17922, Utils.readSint16LE(array16, 0));
	}

	@Test
	public void testReadSint16BE() {
		Assert.assertEquals(-327, Utils.readSint16BE(array16, 0));
	}

	@Test
	public void testReadUint32LE() {
		Assert.assertEquals(3120496639L, Utils.readUint32LE(array32, 0));
	}

	@Test
	public void testReadUint32BE() {
		Assert.assertEquals(4294966969L, Utils.readUint32BE(array32, 0));
	}

	@Test
	public void testReadSint32LE() {
		Assert.assertEquals(-1174470657L, Utils.readSint32LE(array32, 0));
	}

	@Test
	public void testReadSint32BE() {
		Assert.assertEquals(-327, Utils.readSint32BE(array32, 0));
	}

	@Test
	public void testReadSint64LE() {
		Assert.assertEquals(-5044313057631666177L, Utils.readSint64LE(array64, 0));
	}

	@Test
	public void testReadSint64BE() {
		Assert.assertEquals(-327, Utils.readSint64BE(array64, 0));
	}

	@Test
	public void testReadUint64LE() {
		Assert.assertEquals(new BigInteger("13402431016077885439"), Utils.readUint64LE(array64, 0));
	}

	@Test
	public void testReadUint64BE() {
		Assert.assertEquals(new BigInteger("18446744073709551289"), Utils.readUint64BE(array64, 0));
	}

	@Test
	public void testWriteInt16LE() {
		Assert.assertArrayEquals(array16, Utils.writeInt16LE(47614));
	}

	@Test
	public void testWriteInt16BE() {
		Assert.assertArrayEquals(array16, Utils.writeInt16BE(65209));
	}
	
	@Test
	public void testWriteInt32LE() {
		Assert.assertArrayEquals(array32, Utils.writeInt32LE(3120496639L));
	}

	@Test
	public void testWriteInt32BE() {
		Assert.assertArrayEquals(array32, Utils.writeInt32BE(4294966969L));
	}

	@Test
	public void testWriteInt64LE() {
		Assert.assertArrayEquals(array64, Utils.writeInt64LE(new BigInteger("13402431016077885439").longValue()));
	}

	@Test
	public void testWriteInt64BE() {
		Assert.assertArrayEquals(array64, Utils.writeInt64BE(new BigInteger("18446744073709551289").longValue()));
	}
	
	@Test
	public void testDifficulty() {
		
		Assert.assertTrue(org.gmagnotta.bitcoin.utils.Utils.isShaMatchesTarget("839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048", 0x1d00ffff));

		Assert.assertFalse(org.gmagnotta.bitcoin.utils.Utils.isShaMatchesTarget("839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048", 0x177e578c));

		Assert.assertTrue(org.gmagnotta.bitcoin.utils.Utils.isShaMatchesTarget("7b2b98bf2ab895d585601704439bbd68681442c97940b0", 0x177e578c));
		
		Assert.assertTrue(org.gmagnotta.bitcoin.utils.Utils.isShaMatchesTarget("7b2b98bf2ab895d585601704439bbd68681442c97940b0", 0x1d00ffff));
		
	}

	@Test
	public void testCompact() {
		
		BigInteger bigInteger =  new BigInteger("680733321990486529407107157001552378184394215934016880640");
		
		Assert.assertEquals(bigInteger, org.gmagnotta.bitcoin.utils.Utils.uncompact(0x181bc330));
		
		BigInteger bigInteger2 = new BigInteger("22791060871177364286867400663010583169263383106957897897309909286912");
		
		Assert.assertEquals(bigInteger2, org.gmagnotta.bitcoin.utils.Utils.uncompact(0x1d00d86a));
		
		Assert.assertEquals(0x181bc330, org.gmagnotta.bitcoin.utils.Utils.compact(bigInteger));
		
		Assert.assertEquals(0x1d00d86a, org.gmagnotta.bitcoin.utils.Utils.compact(bigInteger2));
		
	}

}
