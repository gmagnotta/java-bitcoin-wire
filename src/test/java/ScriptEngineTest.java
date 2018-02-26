import java.util.List;

import org.gmagnotta.bitcoin.blockchain.BlockChain;
import org.gmagnotta.bitcoin.blockchain.ValidatedBlockHeader;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.message.impl.BlockMessage;
import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.script.TransactionValidator;
import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.wire.serializer.impl.TransactionDeserializedWrapper;
import org.gmagnotta.bitcoin.wire.serializer.impl.TransactionSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

public class ScriptEngineTest {

	@Test
	public void testEngine() throws Exception {
		
		final TransactionSerializer transactionSerializer = new TransactionSerializer();
		
		// Original tx
		final TransactionDeserializedWrapper transaction0 = transactionSerializer.deserialize(Hex.decode("0100000001b28b0c15ff67e3de8f01b9cc6e06379bfce8cb06e9e07e261256b074222b146d000000006b483045022100d2ab67b795bb2f3653e52482b4bf4abddb4749933aeb913125500ce520abf096022070e5bce199edf772c03d3357661389017c7c667814dd5cd61878269f65e358ec012102b82f8436cdb8ea68699dc60fd12f44473b8a261688c58236e66a662f00c65a0fffffffff0440420f00000000001976a914d6366a27b3e36dbb02b3da99f912294bcee61c7688ac40420f00000000001976a914216c3ba20b4df80814e6e30d31f54c865395587d88ac40420f00000000001976a914b9f5bf6cb6d78082b1b0e145a924740f1554c0af88ac30dc6c3b000000001976a9147c62e7f89f21633f4d5318087cfb06b6904f144388ac00000000"), 0, 0);
		
		// Spending signed
		final TransactionDeserializedWrapper transaction1 = transactionSerializer.deserialize(Hex.decode("010000000152fbf09171e6469e05247aff7848db279e710e0e39ce7fcb19289b6f7df270be010000006a4730440220303184757d2c1132b70d2763c73ca6b3111ed83654fd837fd1fe0bb98570663002201211d62bbd8a553e8334ef754e2756f059b57684233e02b0e3ac6f1ce584ad22012102aa884b5833dc5cca26abed63ebaaf8857f9bfba49d993dc636335ae710662acfffffffff0410270000000000001976a9148f41ad374f35de9799820b1a052916521a864f2f88ac0000000000000000536a4c50000000004000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000030750000000000001976a9148f41ad374f35de9799820b1a052916521a864f2f88acf07e0e00000000001976a91482b08870ae5b57ae5b76ead97a1388f390d7304788ac00000000"), 0, 0);
		
		// Spending unsigned
		final TransactionDeserializedWrapper transaction2 = transactionSerializer.deserialize(Hex.decode("02000000027b2b1ef470a33fb8b2d4c951a94844ef58e0454edd3f43bd7ecc4fd4364f40da1b0000006a473044022079bc2215d132d2acf4963b41c73a814732dcd3c135c854f85f8a733d8b28f53402202886869e2dbe0476b31195bfc2310b312b9bc4f913f561f490de90e12a5072670121032d1f641be1edec04ff68269c0028c8eab2d622eb3745b27c0d5512f9202ffc00feffffff493a3197d2dea15ce14a9615eaa14cb24af9c9c50532ca0657e5ae4e56f6e1bf980000006b483045022100beab9096c6392a3bb335fb51c5456aec1a4292bc3bc6bad7a63f5a56d62307720220289d6a6a7a8c304b1fd199ba2289e388ff0d371ee6f0c628678efa5d4cf4aef30121038dbef7fc7d1829baa9d7a30f04a70ce8ff10a098a92b8825d6405ae6c2e127c0feffffff02406aa901000000001976a9141d2d91090e61c367e6193b9d6160fc420c0751d788ac3cd40b00000000001976a914badfa352f4e943b1106be477847d74871fbc1ac688ac22cc0700"), 0, 0);
		
		final TransactionDeserializedWrapper transaction_2_input_source_0 = transactionSerializer.deserialize(Hex.decode("010000000186b202608fe0949ddd54da14ccbb4f55edde494e3cfd37ffee43b36349760c33010000006b4830450221009bfb45d6bb94677def09afa41e9be03d85dabe44fc36e04f47b4e63ef2cf439402200346cd7828fd8af68c2d7efe273504d1e3f872daaec64bb952bcc6a3427ca1740121027304a6461c9c67ad2593fee281da1666612228b1f1eea53e53b5995859f78c07ffffffff2e80c44a000000000017a9141074ce809d3ae9c4f6751f44916a73cee350aa848780f0fa02000000001976a9140ef855f4eb4d245e1d3fefc162b7a7a50a5eb13688acd85d29000000000017a9146978fa64fbbc836da60cac6a4b19f730ce9f939e87655b4100000000001976a9147463265d21bf5e9b1cc8c61ec25d4a4fd40120bf88accee59d01000000001976a914637642433a65dc411d5f85b59b2e98b1f840ba4088ac603bea0b000000001976a914b3a2d14174dda18b62b6d6921d821b2826aea33088acc0c62d000000000017a9143ac9b308b25ff80acf71e70569c2e229dc55b49587a64c8d00000000001976a9146ee06da6d61520e89b76d03592bddea2429980ae88ac90ce0a00000000001976a91422acb111a3a46c9e030ca42cded38050ab72bf2688ace3baa703000000001976a9149006cb7422dca571a6b7733253bf8ff6c2a254e288ac46e25c10000000001976a914a26870ed6d4576e31499c28af75687b6df221cab88ac0e1b0c00000000001976a914dd4aef1ded2de01b6ab567c911a37e4decc5639288acccc30900000000001976a914101295d66466f0c6ca845250ca5aa607200d427488ac07e23200000000001976a9142b90f30fd520dcfceae24782bee6abb2ad140ca088ace0930400000000001976a9144f3da9a03325635f46839ee18563c7573cad7dd088ace5136900000000001976a914016cc4a52ca99028d43e968e564089f5f16cdb2088ac73139304000000001976a914f998f1857cfd9a34f776a560be55c16be8717c0288ac2aa03a020000000017a91429eb6284e33d556e723b7c405438cb75d34295a387e0fd1c00000000001976a9143ee2da90fb21e92206782027e48a744d9196568b88acb5f60b00000000001976a9140d577eb7dd6b4d3c4ba15da1eadbab3b907a7a7488ac20ed3400000000001976a914b86bff2d461a170f7db59c9aa7c4e722807af8db88ac254d53010000000017a91441115c54cfbaa6ebe9ff0760d31b096d2c54dc7287bfa20a00000000001976a9140d68a6d209642d44c1f160a5eb5fe48c1ea780db88ac01fe1c000000000017a914a2865bfb06766502af9bde68a4bd7b5c338d0cb587c43f3400000000001976a914ddc12f7556ceab80fb776b5ea7b9afff938ca60088acd89940000000000017a914bf6721ed005b910964348942021b8f863252da3287baff1900000000001976a9149afce46584d9df8cff49842cd53be9d8a732ff0c88ac0138b401000000001976a9147b35cdc39a5323683f944f117f05ee9b770b8a6e88ac042f51030000000017a9145a91ce0ea33a15a2f44ec5c965e91e79ac21843b87dd481400000000001976a91470a85cb36d00dd4c7f4305c4e4223f760555557488ac481e12000000000017a9148ca7e66c454851b2c76a0a8a4ca7d59773b985208720402c000000000017a914e2ed6b485a421edf54203380ea4af235bb9658c48744c97000000000001976a914e45c606392bbb786a3c1ce5452c5c95418be9bb088ac2e6c1200000000001976a9143c50faf1e1280e508c6004858a1cf94f18c56b8a88ac54836301000000001976a914a4f467eb107285882faeb3244117108248893e7788ac5db77c01000000001976a914370e73c2f830121ab93601a4ac957e9ba38dad7088ac47520300000000001976a914ed7d3e6ddec742c1f3db1f413021cc150105614088ac161b10000000000017a9142640dce65843ceabd32e977fc97966d7afda6cc58718a9a200000000001976a9146c6b389db0801f8c8a9fe6a2f72496241b37a91588aca0541b04000000001976a914cb76664b4b5f5a29dbaa5507ff221ce8a5174ccd88ac80d54302000000001976a914c4ffc768ad75347f0ef50c9d9fe686f072c0856388ac90181b00000000001976a914294771a9011cd64e5185d9a532e24a1ef713e34088ac5a5c9a00000000001976a9146681290952b692ea295bcef054979bf26237a3a288ac257a0b000000000017a9144eb29d9a21ba4c79bdcd851080e55164bd8f9c9b87302fba00000000001976a9143f11a765e40b72e778d2f28dac42145506e3e06b88acfe472c09000000001976a914e71debe251bb26c7e757d9ae265da6e5d00f31b988ac00000000"), 0, 0);
		
		final TransactionDeserializedWrapper transaction_2_input_source_1 = transactionSerializer.deserialize(Hex.decode("0200000001e1463ff754a92080e6fe3db63c90e0237319353dd5dd5ba468efec14d86fce1e000000006a473044022034f0a3e37e545a7d176a53d67e36394809b808e0cafec0a3de61085d78c196280220514c720b7adaaa6aa7adb8c654da8d4efc3307619a7d21b1f1edbb29af5deb7c0121038836175234670ee4c53943900615248f4a007b002b3c91c84c3edae009b9af3bfeffffffa139490f00000000001976a9141da3707bd744e4c3c24e4258c7a979b55593423488acb3590f00000000001976a914186a832fbe8f706e3f3d5afd3b7834de1c4f172d88acf54f0f000000000017a9145079c053ccbd46f953d337b77e8e10ceb35cb55387d4a81500000000001976a9148e9239c9ebc3f043b2447e8cc78c2bcd3aed40e288aca7b839000000000017a914ed184778b08b2f61875495c31e834ac51a39f25987eac4a300000000001976a9146122b54e490c9070ba5151c8fdb4a77c764c737f88ac1c470f00000000001976a9144f23a1c0bced90be8337d983e7f2a21d4abbc0f388ace87601000000000017a914711dcd673ec82a6ef38b4377120a044e60ff2fd58776510f00000000001976a914a79d43174467ee3be42bf446e132735ca8b474f088ac63a40f000000000017a9143f8037bf774caa7ff131e7daa3d189e07b867c2c87d14e0f000000000017a914ded71ad556c4bf021432005ddb4daaa0c4df19a18723510f00000000001976a91498f7d86c8ac4eb115b309f46d51ca2ee693e3da488ac6bad0f000000000017a914eeea975c7eeed0bf4e60cf35da3d055c468a653f87fc6b0100000000001976a91424f50dc430c337fb5cb85cd034ddcf1263e857be88ac213518000000000017a914528e863fb0ab21933bd5c95136b8164f13460f6987bfc70f00000000001976a91417e892c491fe822eaa955f0101217fe89e10397888ac87d29103000000001976a914ea4da7f0b06abd7c0fae2df07bdc348fd1ba88e188acc64d0f000000000017a91476df960bd14b8d1165a6a0ba25e554d1c085ec3b87db610f00000000001976a914e6a39fbd666643a6460cfb8a2d5e23f6c5c62d3088ac4e2a19060000000017a914b2875991ae15182cc827964aa97c1ac10ca9eb7a87bddb1000000000001976a91478e42288a541d0ae60d8bf56a971b60d3c1d522688ac13060300000000001976a914183e1b5743df1af043fddc136e62182f297c1fa388acb4440f000000000017a914e4d4ff9a9410df28cfcb1326fecfd372565553bb875a6a0100000000001976a9146d533647d3fcdd7d8f469eebc580f8d31d10e8cc88ac11b939010000000017a914245d7779a53c9af343918300b03bdf274ac56f8187e5e30f00000000001976a914250e6216703f6e0346c3e76675875df6266eb65488ac546c3101000000001976a91451d84e5802509266280f715c4a1873b495cf6cb388ac0d810f00000000001976a914422300240a6bb1fc0851efff0799744a1747a59688ace8f311000000000017a914cf06c6afb69fbfdb5d6a4a210a24df189d6a30db876f9e1e00000000001976a914cad82f5a384323b32573fe3ab7fb78ca8da8535888ace441d0010000000017a914b042e5499fb9e87a0d694b937dfab5d065c4cff88732620f00000000001976a914302e86c9a5881733d98a32acae3d73da897a4b9f88ac7e870100000000001976a914ffeef37b3b004a31f36d5a291479a523c581a65c88ac66d310000000000017a91401c707ad96a0ab2004d62eb55c0b700fb9581f5e87a10e10000000000017a91480735fbd87e9a8a023eda719e01a71e278d5822487d8500f00000000001976a91437aa099e5cb38cc521d04691a300ab1a7410cadf88ac42780f00000000001976a9142e997a236c6e8672ef264ea916da079ec140287288ace20d0300000000001976a91472bed28bae07e3fa919bf871eaca3efcd373b87788ac44f10f00000000001976a9147ecc495f1c5d0a7b8926e15155aefd2d525a381b88ac1c4b0f00000000001976a91411e357114324bc567a2f30ef005f20cb16559c6e88acdc8a0200000000001976a914c398dc5e59f66b5160c4bc23720efe79494a216188ac387b0100000000001976a914c33714093343fad624f387ea2399d18d1b84e3d588ac54991e00000000001976a91499ae6ace141c72a71ac6ab8f54c1357bf0b8026088acb4296400000000001976a9141609ce015ea9bd16d500367ad0fc878c6814445888ac37e30600000000001976a91481ef952cc8d073b6012946d7f6be70c9f1f8691e88ac721703000000000017a9143f5931050c3356f836732529a100d879da8b1c8987a2540f00000000001976a91453f13553133811ebcc50e9aa0534d40c10ab0b5888acb7fb0f000000000017a914e681cbcda901b90f00983348a747d3fa935d50ef872aca1500000000001976a9142c030f06b760f645b0d268a7a499f5795243770388ac12f00f000000000017a91492109e9a40ed665072aee8e19a65ff96eeb221e88783630f00000000001976a91469539119ffb63dd583ddc83f3c83e00fe4afe86f88ac07630100000000001976a9147191245473c5c524cad5fe188da843929f7c83e388ace2560f00000000001976a9142658916d01819c9216e254e6d8adf96bf125725588ac60bc1000000000001976a91476d848de534bc9a0ae24893aa51a561e79e8951e88acdd8a0b00000000001976a914f93faeae2ebe796d276f49c151efa4468ba0471688ac8f7c0f00000000001976a914a43fe0964caa94865fbbf5f3353154bcadbf172d88ac86770100000000001976a91473e1ac09c25e28b668ba06fc06007ea3cb1c567688ac8a321000000000001976a914f4ee0a2d049316e562fae5f6fb2def43924cbcf888ac73960f000000000017a9149d58b925b613c0c0ebd6ae1cdc287e47c5faf6e987aed40700000000001976a91450c7bbbdd13594f8be63e15c3a1ce804e150434d88ac4e570f000000000017a914f6c325899a9f23a1c7239b30e3adaf6a0a94027587c25b0f000000000017a914d09794e329bf5e149e7abd81b6000cad33628f4e870b7b0f000000000017a914a52c964c3775266794309d83c1bb575e39e3608a87028e0f00000000001976a9142a0a3490210645c69c3d3965a043785daaaf8a3a88acb9d60f00000000001976a91423c2fd8b6fdd656444a1d05c074fa6af000df3f188ac68ea10000000000017a914c40d36314f538a201260515e148d226bf6e8166987b3e40f00000000001976a9149e614d8a237253819c891e57b956e6ee5b7349be88ac3e209f00000000001976a91427ff69bc5bcc0da59bb7f5db238b92e299b92c3b88acaf760100000000001976a91476e106ae961404b001ae4541b831b2329969c68a88aca7d70f000000000017a91460a4123b6640ab1fe7d38142745313f7beb463bf877a3b1000000000001976a9149869674fb71586537f4c5b5e1ccfd3159c9f306088acf5490f000000000017a914dbc93f6fed1406767da9d70d1b2d181dc7439167875a7286000000000017a914c62afcb8493a26736cfb70f6a7001c2f0231ef9787c2580f00000000001976a9142663213c4471a4615a66b457a7bf9b967c2dc5d188accfbf0100000000001976a914087d66257853598fc8b4459d1d92d1d6501685ca88acc8540f00000000001976a9144a6267d311e9da5169c9f83b57ee26655f1c009a88ac85a31e000000000017a9146d9ba89f4e8164115d36967c50e986830c80c11d87f4011000000000001976a9141921648c7b7afa54a8d3368d6c2fd9dbc265c69d88ac6cd87f01000000001976a91425fa9f601e63a6eebbc5ac1b36f1170911ea7acc88acdd4d0f000000000017a9149ae2f9f2f7d1f2e202c9dab2fe928a8955b4628a8781630100000000001976a9148762a78d9188bff7d865f0a93c2100f8c62e3a0788ac929607000000000017a91442f28a7272443168b2d73abacc8957258d4dfee187d1600f00000000001976a914ef1e0933a12431248a490a1662775545f5f434a388ac32841800000000001976a9144a71c74ae1a4c21757ec3d3f784743d0a05fe94388acb86501000000000017a914c04f7869d2c3ea8193f2efbc9360e71181c83481871e43f1220000000017a914d0efc036af38694634b0261304dabbf90a6853fd874dda1000000000001976a914865ecf4acbe84399a5a18309bbd4058fef5e535b88acf47c0100000000001976a914b0d4330890839351845373b89b2275043e0ef37a88acb3670f00000000001976a914f29a14829aa318f082f9788db9fc85f6f23fdd9588ac50590f00000000001976a9148f61468b00dd10f22a0cbc1590f2e60468b18b9488acea710100000000001976a914a77a89936d028364550cb240c7089139905f118188ac22b61e00000000001976a914f3ab3c02d967029f72744e155f0ec3bc4e51332088ac76700100000000001976a914f6917cf5c64669932f088f9c9cbb8d0c60f9eb7e88acc7be0200000000001976a914bd87352001e88a710dd99a2e2db936c063b3549288ac68650100000000001976a914047db65b6fb268f6477ccf6ddcd85cdbf6ebade488ac56dc1000000000001976a91484f89f5b94cb8d22a9a16d341863ea6c4cf9051b88acc67e0f00000000001976a914a96dcf34601d204039763caac72118f4c597b94688ac20bf0f00000000001976a914b893486f063272db6d40e36aeb5e29e08e68869e88ac8c420f00000000001976a9149fc7c4233975e5957e7de7c2dda1f3f6daebd61e88ac18750f000000000017a914b1f8ffed7dbbf3e2c0c14297f9a38345d9e4879d8730a49800000000001976a9140548e1480d22262ece5ce619fb7d1b3fd882339888ac16820400000000001976a914fce2d218fecb7b1dade01e2de94c06cf7efba35b88ac99610f00000000001976a914d7bd021eb2c5a2c772b458adc165606972b9593d88aceb7d0100000000001976a91492421a3d31e6d8f3f2b141dc7fec11e92e751a5b88ac9d420f00000000001976a9143f994d5e36aa9d39fab7f21970bf3500825b293088ac6de01000000000001976a9144cdd05d6a55c581d80f49be9251696d669114fc588ac87571000000000001976a914acdd53426d13636170f0f0066bf43c612a65926988ac583f0300000000001976a9141f5645426feabce66f4a8495593151ed83c7281b88ac69d511000000000017a914f2fc10361a2b7254fcd9f515295b9c5dc0762682874b9e1e00000000001976a91406a148f21eec2a748bac897c0b828d6924f5315888ac1e7f0f00000000001976a9146c1db166d5b27f1dc81953f77f22e28020b9ff8f88ac074a0f000000000017a914316e31bb90ebf1cab27feb1cca6f1245d3c6ce198748b30601000000001976a91420b74269f0d1462ef983e6af54dbe55cf22e4b6f88acd4c910000000000017a914e2d29f4910d5df2c340a673bc9cffe8c1df9d7fe8709ef0200000000001976a91451d2d401e7c6dc7e7f7d1a0380eed7c3bc59fa3d88ac462d1306000000001976a91457c5ff4afbf682885b431d8abf3aec4791d0f60188ac124624000000000017a9148562f2966f3e979b095719a4dc849121125597098762ab9800000000001976a91464c6c19a9b6f2649ba62c492befbc92d71ac670588acd5c903000000000017a914f3e79b0ed093f1243ebece5a134135e95d9fdcc187801d1300000000001976a914620ae44693576065b4b3d595c1d816ed4935b3be88ac01450f00000000001976a9141efd84d31371664e858f50a4e9f87a6aca8d3e6988ac5fcba200000000001976a914643b24e494bdfe78f14987895122659890be5b5888acc5710f00000000001976a914a7083757dc8d1d406e2673e235fcb862da7570c088ac424a0f000000000017a914f25501b63f69d4e3bc1374d2378ef52deea69ada8707891e00000000001976a91437aa0c7cbf8c04746d25b7dc9fdacc33b5d106bd88ac3cf90f00000000001976a914208f3067abc4fe896e8f0bf134cb4c87e84e4d8e88acfc981f00000000001976a9143fe01123035e10c840701912f8d302e48a31989988ac514d1200000000001976a914e60a6ad0698d1cdf96853d2c686b941e14fb8b7388ac4a181000000000001976a91406b60864cbd9ce7a50ae080ae4267f5f1991eb1588ac548c0100000000001976a91479ee237210a9a2a541a0fa4156e059e6cf5d405d88acace20f000000000017a914c7ede1fd2fa208b20ce99e4dd2b581a19339838787a3030003000000001976a914e1c86e0bb279a7c87e22adc8089023fa64f02e7788ac7d1ca900000000001976a9145191b3e873ec7f0ed36e3dae5a4efcce9ae0e69388acdafa5f00000000001976a914a2f62f9d2ff6937fbd328fde5567f73541d2a47488accb820100000000001976a914d73c87afb7175e9bcaa884bdb4a5252b25909d0188ac115c0f000000000017a914af06cfa6409435fccf715e0888f15301aa310f3d87dd480f000000000017a9142c8559de2237b774ade55d0706b7915ef14cd4438751791000000000001976a9142a2058b801a26592633822a4a1601f2624e3ba7e88ac2c161000000000001976a914d06b3b0976ec3d66cbf8d16f570a611081d6ddfe88acf8bc1200000000001976a9142166161510b6c6c5ca30b5bf4f9df70e69755fbd88ace16d0f00000000001976a9143989b82394c6611da36c203893881434409776e688acf55b0f00000000001976a9149a9c271dda089cd918715413e3d947613fd208c288acfecd2600000000001976a9147ac31d060eccf13a6566dda80070a68b1b82c41888acd5450f000000000017a914bf0c7767921e1ea0f4433ad87b91be09b87d57c1875c8d1e00000000001976a9149c0c034be894cce804ac9d347da6e72546f45dae88ac53500f00000000001976a9143fd68df4d6cb28c7c708f4a3dc2a4b592927a68e88ac76850700000000001976a914e47eac58486410f70ad5125cb8ded50e736ff73b88ac2fef1600000000001976a91478c59f44ab36ff59d6bc0b1dc4d1e338348dc58388acbeb90f00000000001976a9145d5e020e7e531c3f52a6fb1eaacbb4062e73626d88acad9d0100000000001976a914bc9e6bafd658386533f83b8842533ddb41be210d88acd7590f000000000017a91443536ea5c6201d0af34e4a00bee5fd42a330770087c7940800000000001976a9143c6a76ed033475952a5dfce638da7ae6ecd573b388ac81740400000000001976a914b589d2b21ee5b7ec62244be80c1f75045ff1d3da88acdd750f00000000001976a914e4f5217d6c06b2e88709643f2d5507fecaa1fc6e88accaec9800000000001976a9143bb6e45a2b8e3f83ba6a4f85f3ecfbc8de8e9b6788acf65f0100000000001976a91463f285d029eb79f312cb3a723c8cb8f5207ff2a488ace0960700000000001976a9144c61fa2c4235e54badc978ece80a812e2a449ac488ac34f40100000000001976a914a00d34ae3703fed2438e72e18ec5c7350937f48b88ac2da301030000000017a914d04a0ac2322b21a22536ec91b37a4ae9f62cc198878a24f4110000000017a91481ce38620847339bfe55e1f76303bc628837ad6f8736000300000000001976a914f878fb4e5e6fbce722c23e426a39198105d99a2f88aceeb90700"), 0, 0);
		
		final TransactionValidator scriptEngine = new TransactionValidator(new BlockChain() {
			
			@Override
			public Transaction getTransaction(String hash) {
				
				if ("be70f27d6f9b2819cb7fce390e0e719e27db4878ff7a24059e46e67191f0fb52".equals(hash)) {
					return transaction0.getTransaction();
				} else if ("da404f36d44fcc7ebd433fdd4e45e058ef4448a951c9d4b2b83fa370f41e2b7b".equals(hash)) {
					return transaction_2_input_source_0.getTransaction();
				} else if ("bfe1f6564eaee55706ca3205c5c9f94ab24ca1ea15964ae15ca1ded297313a49".equals(hash)) {
					return transaction_2_input_source_1.getTransaction();
				}
				
				return null;
					
			}
			
			@Override
			public List<Sha256Hash> getHashList(long index, long len) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<ValidatedBlockHeader> getBlockHeaders(long index, long len) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ValidatedBlockHeader getBlockHeader(String hash) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ValidatedBlockHeader getBlockHeader(int index) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public long getBestChainLenght() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public boolean addBlockHeader(BlockHeader header) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean addBlock(BlockMessage blockMessage) {
				// TODO Auto-generated method stub
				return false;
			}
		}, new BlockMessage(null, null, null));

		Assert.assertTrue(scriptEngine.isValid(transaction1.getTransaction()));
		
		Assert.assertTrue(scriptEngine.isValid(transaction2.getTransaction()));

	}
}
