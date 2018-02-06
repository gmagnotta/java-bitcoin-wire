package org.gmagnotta.bitcoin.wire.serializer;
import java.math.BigInteger;
import java.net.InetAddress;

import org.gmagnotta.bitcoin.message.impl.BitcoinAddrMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinInvMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPongMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinVersionMessage;
import org.gmagnotta.bitcoin.message.impl.InventoryVector;
import org.gmagnotta.bitcoin.message.impl.InventoryVector.Type;
import org.gmagnotta.bitcoin.message.impl.NetworkAddress;
import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.gmagnotta.bitcoin.wire.BitcoinFrame;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

public class BitcoinFrameTest {
	
	private static byte[] version;
	private static byte[] verack;
	private static byte[] ping;
	private static byte[] pong;
	private static byte[] getAddr;
	private static byte[] addr;
	private static byte[] inv;
	private static byte[] getHeaders;
	private static byte[] headers;
	
	static {
		version = Hex.decode("fabfb5da76657273696f6e000000000073000000078454dc711101000000000000000000ddf3ed5900000000000000000000000000000000000000000000ffff7f0000014a38000000000000000000000000000000000000ffff7f0000014a3800000000000000001d2f626974636f696e6a3a302e31342e352f554e49515549443a302e312f0000000001");
		verack = Hex.decode("fabfb5da76657261636b000000000000000000005df6e0e2");
		ping = Hex.decode("fabfb5da70696e670000000000000000080000007ff7de1902246433af0feb3c");
		pong = Hex.decode("fabfb5da706f6e670000000000000000080000007ff7de1902246433af0feb3c");
		getAddr = Hex.decode("fabfb5da676574616464720000000000000000005df6e0e2");
		addr = Hex.decode("fabfb5da6164647200000000000000001f0000007070d6a201eaae275a0d0000000000000000000000000000000000ffff34f32409479d");
		inv = Hex.decode("fabfb5da696e7600000000000000000025000000582f6f8c010100000060a310a6de373d91449f0c373908ab871db3cec38087b4c2474629c8795988df");
		getHeaders = Hex.decode("fabfb5da67657468656164657273000025040000ea3e084b7c11010020d9624dab5a36d2441df2ab125d5d89f507d6f3ca6be12faf01ee62db000000000748af8916926b4a0cb18871bc2a0f0c29e0c90abec79494eb04000000000000916109d1e39f5e027dd729fbdf881ddf1c3200bad45f05fcae0b000000000000eaf697ce87063d16996e98c14cf5983ebdd901ffb0bcda3c3d0a00000000000083657e9e06aa4d01e58f437128b3df57e377f7f9ea50f51a250c000000000000397a0e03481b958f8f6b0580a0ca8a62a450a7e04d7fb2c30802000000000000867f027653075754f852303edfd9e617c045ce6cfc4f00dea200000000000000f0c7c361765d3c8b2a9f9be9dc62d68d77decf771cfd25ffb40200000000000066f985b5e50f404a54d4c9f4a0fc79f64997348ffc298d7bde06000000000000e783418b4327e0f659a2b1a7b43867ef2321887291ab8372ee0e000000000000b59f59b78c73093bb35cd500a93c2dca547cd31a0918a2ca5e050000000000002f2d3dc67e3fdcf1f2bbeb9f780f13b8f352e530c6597831500e000000000000d4146993ccbf2c4299f6f29a3e7344816e567cc5f0781da5e006000000000000ae37166ec1f9a62a5e532962a547974743f52a0d869f85070c020000000000007df096614ae4ad71e1917cb8961ebf079521a2e36fbb8be3c70000000000000040a22b0865151e0bd0df83aa88dbb5dc82227552e4d7ef6d6f0a0000000000001dae903157326a6013494d3e2a0ad027619bbf71b8588e59a10f000000000000bd48c0ad5c73a83c15f9a86f623480ebdcfc97a15e178fc56c0f000000000000cffc7eebc4cf92e1ba67bdbeb6fb849fbfea08c1c10a5ffe5f0f00000000000004e512fd86222fbfe0b97ffb46ddb7ed30868ce800a9c11ad4070000000000004050fc6df3c49fa7f0f21c53c372302874cba43906729a5d650b0000000000006782e12e50539efc8b3d138fa5a78650d76c4029ece5655776330000000000001ce0af0be46e3374ae292b9f7b8707b2887471d7712b2f769d08000000000000f8a9c9b1efdfc0649d972479f2431098d16a60e13d4d250eca050200000000006bece9087e5045832e9f0de5d2534c929056fdd7cfdacb7c3289b90a0000000084e31413a04de0e6a0e6ba1434917743dbb36a7ad0eee468d3b46c00000000000c346a533b18d025f9d7946e549f353492f4454583cfa416079539030000000061f716c16875e50fe6aa8e2b1a1e77f26995fd74c68fbfe33401000000000000305b825f685b86ed2f11d9a93402f453a4c32f046f8296d342884b0000000000fd59259d5a4837587550ac3adb23389e6d80f6f31070b33f1ab00c5400000000bfb57ba9548e96c060610f99b6eb79f907f907258f5c91c459cb95820000000043497fd7f826957108f4a30fd9cec3aeba79972084e90ead01ea3309000000000000000000000000000000000000000000000000000000000000000000000000");
		headers = Hex.decode("fabfb5da686561646572730000000000a30000002e5cce1f020100000043497fd7f826957108f4a30fd9cec3aeba79972084e90ead01ea330900000000bac8b0fa927c0ac8234287e33c5f74d38d354820e24756ad709d7038fc5f31f020e7494dffff001d03e4b672000100000006128e87be8b1b4dea47a7247d5528d2702c96826c7a648497e773b800000000e241352e3bec0a95a6217e10c3abb54adfa05abb12c126695595580fb92e222032e7494dffff001d00d2353400");
	}

	@Test
	public void testFromBytes() throws Exception {
		
		// TEST VERSION
		BitcoinFrame frameVersion = BitcoinFrame.deserialize(version, 0);
		Assert.assertEquals(frameVersion.getMagic(), MagicVersion.REGTEST);
		Assert.assertEquals(frameVersion.getCommand(), BitcoinCommand.VERSION);
		Assert.assertEquals(frameVersion.getLenght(), 115L);
		Assert.assertEquals(frameVersion.getChecksum(), 126112988L);
		
		BitcoinVersionMessage versionMessage = (BitcoinVersionMessage) frameVersion.getPayload();
		
		Assert.assertEquals(70001, versionMessage.getVersion());
		Assert.assertEquals(new BigInteger("0"), versionMessage.getServices());
		Assert.assertEquals(new BigInteger("1508766685"), versionMessage.getTimestamp());
		Assert.assertEquals(new BigInteger("0"), versionMessage.getNonce());
		Assert.assertEquals("/bitcoinj:0.14.5/UNIQUID:0.1/", versionMessage.getUserAgent());
		Assert.assertEquals(0, versionMessage.getStartHeight());
		Assert.assertEquals(true, versionMessage.getRelay());
		Assert.assertEquals(new NetworkAddress(0, new BigInteger("0"), InetAddress.getByAddress(new byte[] { (byte) 0x7f, (byte) 0x0, (byte) 0x0, (byte) 0x1 }), 19000), versionMessage.getAddressReceiving());
		Assert.assertEquals(new NetworkAddress(0, new BigInteger("0"), InetAddress.getByAddress(new byte[] { (byte) 0x7f, (byte) 0x0, (byte) 0x0, (byte) 0x1 }), 19000), versionMessage.getAddressEmitting());
		Assert.assertArrayEquals(version, BitcoinFrame.serialize(frameVersion));
		
		// TEST VERACK
		BitcoinFrame frameVerack = BitcoinFrame.deserialize(verack, 0);
		Assert.assertEquals(frameVerack.getMagic(), MagicVersion.REGTEST);
		Assert.assertEquals(frameVerack.getCommand(), BitcoinCommand.VERACK);
		Assert.assertEquals(frameVerack.getLenght(), 0);
		Assert.assertEquals(frameVerack.getChecksum(), 1576460514L);
		Assert.assertArrayEquals(verack, BitcoinFrame.serialize(frameVerack));
		
		// TEST PING
		BitcoinFrame framePing = BitcoinFrame.deserialize(ping, 0);
		Assert.assertEquals(framePing.getMagic(), MagicVersion.REGTEST);
		Assert.assertEquals(framePing.getCommand(), BitcoinCommand.PING);
		Assert.assertEquals(framePing.getLenght(), 8);
		Assert.assertEquals(framePing.getChecksum(), 2146950681L);
		
		BitcoinPingMessage pingMessage = (BitcoinPingMessage) framePing.getPayload();
		Assert.assertEquals(new BigInteger("4389619506958574594"), pingMessage.getNonce());
		Assert.assertArrayEquals(ping, BitcoinFrame.serialize(framePing));
		
		// TEST PONG
		BitcoinFrame framePong = BitcoinFrame.deserialize(pong, 0);
		Assert.assertEquals(framePong.getMagic(), MagicVersion.REGTEST);
		Assert.assertEquals(framePong.getCommand(), BitcoinCommand.PONG);
		Assert.assertEquals(framePong.getLenght(), 8);
		Assert.assertEquals(framePong.getChecksum(), 2146950681L);
		
		BitcoinPongMessage pongMessage = (BitcoinPongMessage) framePong.getPayload();
		Assert.assertEquals(new BigInteger("4389619506958574594"), pongMessage.getNonce());
		Assert.assertArrayEquals(pong, BitcoinFrame.serialize(framePong));
		
		// TEST GETADDR
		BitcoinFrame frameGetAddr = BitcoinFrame.deserialize(getAddr, 0);
		Assert.assertEquals(frameGetAddr.getMagic(), MagicVersion.REGTEST);
		Assert.assertEquals(frameGetAddr.getCommand(), BitcoinCommand.GETADDR);
		Assert.assertEquals(frameGetAddr.getLenght(), 0);
		Assert.assertEquals(frameGetAddr.getChecksum(), 1576460514L);
		
		Assert.assertArrayEquals(getAddr, BitcoinFrame.serialize(frameGetAddr));
		
		
		// TEST ADDR
		BitcoinFrame frameAddr = BitcoinFrame.deserialize(addr, 0);
		Assert.assertEquals(frameAddr.getMagic(), MagicVersion.REGTEST);
		Assert.assertEquals(frameAddr.getCommand(), BitcoinCommand.ADDR);
		Assert.assertEquals(frameAddr.getLenght(), 31);
		Assert.assertEquals(frameAddr.getChecksum(), 1886443170L);
		
		BitcoinAddrMessage addrMessage = (BitcoinAddrMessage) frameAddr.getPayload();
		Assert.assertEquals(1, addrMessage.getNetworkAddress().size());
		Assert.assertEquals(new NetworkAddress(1512550122, new BigInteger("13"), InetAddress.getByAddress(new byte[] { (byte) 52, (byte) 243, (byte) 36, (byte) 9 }), 18333), addrMessage.getNetworkAddress().get(0));
		Assert.assertArrayEquals(addr, BitcoinFrame.serialize(frameAddr));
		
		// TEST INV
		BitcoinFrame frameInv = BitcoinFrame.deserialize(inv, 0);
		Assert.assertEquals(frameInv.getMagic(), MagicVersion.REGTEST);
		Assert.assertEquals(frameInv.getCommand(), BitcoinCommand.INV);
		Assert.assertEquals(frameInv.getLenght(), 37);
		Assert.assertEquals(frameInv.getChecksum(), 1479503756L);
		
		BitcoinInvMessage bitcoinInvMessage = (BitcoinInvMessage) frameInv.getPayload();
		Assert.assertEquals(1, bitcoinInvMessage.getInventoryVectors().size());
		Assert.assertEquals(new InventoryVector(Type.MSG_TX, Sha256Hash.wrap("60a310a6de373d91449f0c373908ab871db3cec38087b4c2474629c8795988df")), bitcoinInvMessage.getInventoryVectors().get(0));
		Assert.assertArrayEquals(inv, BitcoinFrame.serialize(frameInv));
		
		// TEST GETHEADERS
		BitcoinFrame frameGetHeaders = BitcoinFrame.deserialize(getHeaders, 0);
		Assert.assertEquals(frameGetHeaders.getMagic(), MagicVersion.REGTEST);
		Assert.assertEquals(frameGetHeaders.getCommand(), BitcoinCommand.GETHEADERS);
		Assert.assertEquals(frameGetHeaders.getLenght(), 1061);
		Assert.assertEquals(frameGetHeaders.getChecksum(), 3929933899L);
		
		BitcoinGetHeadersMessage bitcoinGetHeadersMessage = (BitcoinGetHeadersMessage) frameGetHeaders.getPayload();
		Assert.assertEquals(33, bitcoinGetHeadersMessage.getHash().size());
		Assert.assertEquals(Sha256Hash.wrap("00000000db62ee01af2fe16bcaf3d607f5895d5d12abf21d44d2365aab4d62d9"), bitcoinGetHeadersMessage.getHash().get(0));
		Assert.assertEquals(Sha256Hash.wrap("000000000000055ecaa218091ad37c54ca2d3ca900d55cb33b09738cb7599fb5"), bitcoinGetHeadersMessage.getHash().get(10));
		
		// Remove last 0000
		bitcoinGetHeadersMessage.getHash().remove(32);
		
		Assert.assertArrayEquals(getHeaders, BitcoinFrame.serialize(frameGetHeaders));
		
		// TEST HEADERS
		BitcoinFrame frameHeaders = BitcoinFrame.deserialize(headers, 0);
		Assert.assertEquals(frameHeaders.getMagic(), MagicVersion.REGTEST);
		Assert.assertEquals(frameHeaders.getCommand(), BitcoinCommand.HEADERS);
		Assert.assertEquals(frameHeaders.getLenght(), 163);
		Assert.assertEquals(frameHeaders.getChecksum(), 777834015L);
		
		BitcoinHeadersMessage headersMessage = (BitcoinHeadersMessage) frameHeaders.getPayload();
		
		Assert.assertEquals(2, headersMessage.getHeaders().size());
		Assert.assertEquals(Sha256Hash.wrap("000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943"), headersMessage.getHeaders().get(0).getPrevBlock());
		Assert.assertEquals(Sha256Hash.wrap("00000000b873e79784647a6c82962c70d228557d24a747ea4d1b8bbe878e1206"), headersMessage.getHeaders().get(1).getPrevBlock());
		
		Assert.assertArrayEquals(headers, BitcoinFrame.serialize(frameHeaders));
	}

}
