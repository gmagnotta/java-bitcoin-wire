package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.message.impl.TransactionInput;
import org.gmagnotta.bitcoin.message.impl.TransactionOutput;
import org.gmagnotta.bitcoin.wire.Utils;

import com.subgraph.orchid.encoders.Hex;

public class TransactionSerializer {
	
	public TransactionSize deserialize(byte[] payload, int offset, int lenght) {
		
		// read version
		long version = Utils.readSint32LE(payload, offset + 0);
		
		// read how many input we have
		VarInt txIn = new VarInt(payload, offset + 4);
		
		TransactionInputSerializer transactionInputSerializer = new TransactionInputSerializer();
		TransactionOutputSerializer transactionOutputSerializer = new TransactionOutputSerializer();
		
		int lastIndex = offset + 0 + 4 + txIn.getSizeInBytes();
		List<TransactionInput> txInputs = new ArrayList<TransactionInput>();
		for (int i = 0; i < txIn.value; i++) {
		
			TransactionInputSize transactionSize = transactionInputSerializer.deserialize(payload, lastIndex);
			
			lastIndex = (int) transactionSize.getSize();
			
			txInputs.add(transactionSize.getTransactionInput());
		
		}
		
		VarInt txOut = new VarInt(payload, lastIndex);
		
		lastIndex = lastIndex + txOut.getSizeInBytes();
		List<TransactionOutput> txOutputs = new ArrayList<TransactionOutput>();
		for (int i = 0; i < txOut.value; i++) {
		
			TransactionOutputSize transactionOutput = transactionOutputSerializer.deserialize(payload, lastIndex);
			
			lastIndex = (int) transactionOutput.getSize();
			
			txOutputs.add(transactionOutput.getTransactionOutput());
			
		}
		
		long lockTime = Utils.readUint32LE(payload, lastIndex);
		
		Transaction transaction = new Transaction(version, txInputs, txOutputs, lockTime);
		
		return new TransactionSize(lastIndex + 4, transaction);
	}
	
	public static void main(String[] args) {
		
		byte[] b = Hex.decode("010000000188c0a951754b8d6c1fe5350e6fa991df4818f2e5849ea472df16932cc5aefa4d0200000000ffffffff0200286bee000000001976a91449f2a3e7873c7b5e37fb92603ff3a0733bb4d0e688ac20830c0000000000220020701a8d401c84fb13e6baf169d59684e17abd9fa216c8cc5b9fc63d622ff8c58d000000000200000011624d2ac61a5fb350055d0e4520a0af7083385a2874eaab4052297904df1f3a2d140000006b483045022100bdb51b888ff4ae44371e0788c6c1f3fb5db627cd7869f37c9f5b827708e74025022042f6c966f342ec1abadcc44a3a932e1a85b510c523b3c2350b17f0fe50424f29012103d87fc1f25335d077791f97888bdd18cbd2d12f9037383471c92fcb80a3bf4eb6feffffff1d98762a30ec8df56797b2f8b6ff67009ae500c322bf00ca94296f16095ded92010000006a47304402203947c6580071845b5a4b409aef5345d2fc417522be8a5fed4db0fc8e4ba7f60902204d1df9f8382db8edb54b71ed34e89e392247811e");
		
		TransactionSerializer s = new TransactionSerializer();
		
		s.deserialize(b, 0, 0);
		
		b = Hex.decode("0200000011624d2ac61a5fb350055d0e4520a0af7083385a2874eaab4052297904df1f3a2d140000006b483045022100bdb51b888ff4ae44371e0788c6c1f3fb5db627cd7869f37c9f5b827708e74025022042f6c966f342ec1abadcc44a3a932e1a85b510c523b3c2350b17f0fe50424f29012103d87fc1f25335d077791f97888bdd18cbd2d12f9037383471c92fcb80a3bf4eb6feffffff1d98762a30ec8df56797b2f8b6ff67009ae500c322bf00ca94296f16095ded92010000006a47304402203947c6580071845b5a4b409aef5345d2fc417522be8a5fed4db0fc8e4ba7f60902204d1df9f8382db8edb54b71ed34e89e392247811e584f2395da6f786037ac518101210208da4f932fdd14b319d41daef3638d15c45db7a134952655abd5e333e829d0a5feffffffaee66d0a3f0ae469ab2350245508cd99a7c2cf4f58d25ff70380651a7151ef27000000006a47304402202440dcf86847e41af5b887e20f2e881a26339c57d8214822911f2fb72bf64fe50220523fc8a6b1005b0a2af48a397738f4490ee9871d3d9f2d4f5e4c7014f58f410e0121026942351b3d3e545a8fb7688d89bcfeeaf7646d4284d3096a82484582a5c24f33feffffffd6ddbba9699a600885c48afb8e9d96bbbc911ca1dc6e1b7f819ae67e070974a3310000006b483045022100e8b84ac7b307f8719a84722b3d2b4aa88cd148d9fd7c14ca244490deaed5042d02200f3eb5d3cffb09d5f5482e44e6b24ff034a7fe4a5382bdc9adbb7af0768fd741012103322fbf454b118ef72e08fe99e23362a87c3f58f4b681bdaa263daafc71b5dcdefeffffffa034e9b1724c7c96dd35b7ad8d9eff488969788f318ae1efa263a661e3691905030000006a473044022053c0f41ee5f1a6288ca7abf233932a7219afda09670a771e53daf26eae7cefd902204fd8cb1ce3ecc9b97bd96c2d4d879678bcaae8da922d48796a90f9f7e82e19fd01210334b85bab5fabed94f78b2292528885ce2bc5db2413f2ac86b5a876f41bebc65afeffffff67016aaf9015e22748c959df7b0946564b0a0295e92057305c33335f572bf371000000006b4830450221008f6b55e6f70b4aa6d65cc4ac69851ca9d9f26e7163d72eb99b89e2318e59addb02207d8ddda953c0c47a26c8dcfd0b83752d26d22cd52a9d0b76567c8792501b56d0012103da410ea4d66569240cf5e1dd9c7b7e5112cb62be2da8f49897bbc59acc99518efeffffff6b23b0342ccd5d355d3d5afc096402056048e62c6ed99c98df3197d3ccb6ea8f000000006b483045022100b484779cf8bab806b64a0b5506cefdbd6878d073b857cbb9b1f30e3fcbe467e102203f3afde361511396bd0e4c88b9cae67d3de53ef63c65284528300954299d108e0121036a7776dc183ba2ee329bb91b4222c0ae18394dc81346253679c6021208cf0460feffffff43d65aa4060591c7a93499f159d1acf4487c54670c51f64785b448a92d81d50e010000006b483045022100a2db969235ee3ff48944e2c2f5a3e3d70ce449aff57cc6b103c89392112eaf5702202f9a5eb5da4cba386a990b227ed153345944d1c23faba53c2b6f779f329a8cd601210224b89d2dfb324a240d33ad04491626e8d505c3bd27ae8927c5f5127958e80003feffffffc072a21159510d0e66ce77ac81075a20a5ed0940ab68de084f5d5925128527df2f0000006a4730440220745fe01fa4043c754cad5848ac9895aeb7750a1b716aa5389e49a9eecb41cad502206b36e22f281737a33610891596a2c02d5cf3ce7c9c0578c9c92dd871454621ed012102d4db616c07d94755e7d90d51c6350ba22186e9ba2de1fba50480311e310a06a7feffffff884f7da81d2c88724482cbbeb5bd6ce3eee3084c88f27cd8169268b14777d6d6070000006b483045022100a63a36a57c47c3e3447a765bdafb114cfcc99a7c81b4990eba24b83bb6f92daf022023844de984f6582cd2ad46d58e256210f00957f8aca61f615e2ef2bc476be82b0121027d98d52cd1ace8afbe520058f558d26135f4ace96239a5c87b28d72943530d2dfeffffff32c3327a9e06ee5a447c9d50b2ea38a3c9d85c296713cfaac9f7ac1d7a0051b21d0000006a473044022032f30ded5ba6c5bd47e44de482071e1e892324e5efa9cf02c431b2714caa837702206504a7392cdf2b86fd829c3129f0e8a99f904f1a10b59f6612f1755411ccbdd40121031cfde82317b2eca72c89bc2b7955e261328b529339fd29040fed217e08dd562bfeffffffa0aaac50a1088ba2c6740b5f63a192150a4e06ad9d4a88156612acff967c5793040000006a47304402200fe65f5cec04d51e9d426e62b3caf6aa51494f60977dba6e08de7fc1b9d923c102202fafe3ae9c5e55dfe0658e0465659bead28212a72bd6987a90cd5e3eb2689e9b0121025dccf78ea20ec75b390a9e0526df8c7f851199f3ad88e67381da09cb6a41b974feffffff245e2d05d4804f46f877a541e2be20cc0ea6383dcd88632ee7b23620bd51b096000000006b483045022100e2e960a39700336e29d8e5236ca308e95ee7ccc3d3bb4712a9b9b644d68d07eb02207a9758b5fb627c15e6cdef98f6b506b21235e7b3ecdd2fb431d0f059fffafeb20121037c18fa148e9bcd22d06e33d7f1ccce422068a6a8c7ee197ad6fe48010cf5e1dcfeffffff50eb324c6f86689356e1fe92a9ced7b3ee7c3c5c35966d5072258b48d3393f98000000006b4830450221009c2174d72b9af54bc29e367b1fd1f52e4c581d4a45016d9dd7de042ad71fee26022067b96e33b37f96f8fb728a74dded6de89fc5dd28807ab1845760bf5ba2d6fcda01210229935c3950897258d87531d909d3f5b9f50ca053e85ab992ccd2845ccac539b1feffffff30e36eae0e9ef668aeef915e3e4a43c50d47f9150f0ed45dbe0fb651d3806d34000000006a47304402203a1e8d3911c5a3e6870e18596ea557d2b8d97ae1f235e957aff5e8e34984e7ad022052d22003e3629878a7d3e0a1d9817adfc6479206b1089604618b75ccc25d29c3012102f92a888c0e8d42bbca1576bc1ce51796acb375557640c8a58c03e30674477faafeffffffd498d817e9a7b7410bb4ea02a4cf571900091301e13204e03b6a6adb68dea3d70d0000006b483045022100c9113520c688dd05ecc2c1760270efd2cbb3d3252feb970f8969b899a22e27b50220363ea6e7adc0665a37d699a7c491370a884644aedc7043c0798191dcaccc2707012102c3e94249625e96f7621ee946c802331d779771eb585f1f6478fcad6c628db58ffeffffffdc2e6e67bff88f3054f3510a9aa58c4faafe8f6680eeb9218b79c90601a5d6a6010000006b48304502210098c36805cfb40cb4c7a8a1fda735acaeecea97d7b3004302e6659415856aae4502201e0f30a97c41e8ae31bf1b2579aff28e8d028d3779a4cf8a17068441435a3897012103249c487d8cd4fefda8ada8c394bf903af1a2720bedb0ab8c81902b7d3c0fc8a8feffffff02cde71b00000000001976a914f2763fe4b734fb9340deeed63801940364b96a1a88ace0f8ef18000000001976a9147cdd192406ccc34b305419041a2f9fbc2c6f979688aca9c007000100000002e24e297a5e25c65b9d4af3cacbeb14ae464ffbb827a51dd7b6c9feb67d13e5b6080000006b483045022100fd7fc2d5ce60e3af937ae9265dd7f9df323fa59e8d9684a14b87453102b2390f022043cf3bff1e8cfbe4a14a4b047edd076acb6074bc75219f05699614f6e56d3b84012102db24cf1edcb55a224725ed52d00c305d072b8d543a114e60a15a2f5e615333fbffffffff3f8528132e28f5f266570cefa667ed30ce9e5d550839ae3680714a0f46df81f3010000006b483045022100a60ebbed9dd3a06469ce19ebab30497251cbf9d7ef5c880fedba0297f814edf502203fa2af45f0e8cba63dc2232995263b455722e86f96191d4a1da414c70616d021012102b1e5df07522c609866c468731261225b5241556a5e635c8640c6532dacbc54e8ffffffff0187287f010000000017a91473bbe5580daed52c819362da60d0332b97e1945587000000000100000001ee7332adcb7f3f18307c8d5b7c9aa3b3ab3cd64300a7d7e2c88e1d191ab97e6b0100");
		
		s.deserialize(b, 0, 0);
		
		
	}

}
