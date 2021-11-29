package com.github.ah.blockchain.signer.test.signer;

import com.github.ah.blockchain.signer.signature.SignatureUtils;
import com.github.ah.blockchain.signer.signers.DefaultSigner;
import java.math.BigInteger;
import java.util.logging.Logger;
import org.apache.tuweni.eth.Address;
import org.hyperledger.besu.crypto.KeyPair;
import org.hyperledger.besu.crypto.SECP256K1;
import org.hyperledger.besu.crypto.SECPPrivateKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultSignerTest {
  private static Logger LOG = Logger.getLogger(DefaultSigner.class.getName());
  static String PRIVATE_KEY_STRING =
      "a392604efc2fad9c0b3da43b5f698a2e3f270f170d859912be0d54742275c5f6";

  static final String PUBLIC_KEY_STRING =
      "506bc1dc099358e5137292f4efdd57e400f29ba5132aa5d12b18dac1c1f6aab"
          + "a645c0b7b58158babbfa6c6cd5a48aa7340a8749176b120e8516216787a13dc76";

  @Test
  public void shouldCalculatePublicKeyProperly() {
    final BigInteger PRIVATE_KEY = new BigInteger(PRIVATE_KEY_STRING, 16);
    final BigInteger PUBLIC_KEY = new BigInteger(PUBLIC_KEY_STRING, 16);

    DefaultSigner signer = new DefaultSigner(PRIVATE_KEY);
    BigInteger obtained = signer.getPublicKey();

    Assertions.assertNotNull(obtained);
    Assertions.assertEquals(obtained, PUBLIC_KEY);
  }



  @Test
  public void generateKeysAcholon() {
    SECP256K1 ecsa = new SECP256K1();
    for (int i = 0; i < 0; ) {
      KeyPair keyPair = ecsa.generateKeyPair();
      Address addr = SignatureUtils.toAddress(keyPair.getPublicKey());

      if (addr != null) {
        LOG.info("----------------------------------------------");
        LOG.info("PrivateKey " + keyPair.getPrivateKey().toString() + " - addr=" + addr);
        i++;
      } else {
        LOG.info("*******************************");

      }
    }

    SECPPrivateKey privateKey = ecsa.createPrivateKey(new BigInteger("5621f9b17596910fc7cf2d8202e8ce15908513274a3506ba1f2a49714a091c29", 16));
    KeyPair keyPair = ecsa.createKeyPair(privateKey);
    Address addr = SignatureUtils.toAddress(keyPair.getPublicKey());
    org.hyperledger.besu.datatypes.Address besuCore = org.hyperledger.besu.datatypes.Address.extract(keyPair.getPublicKey());

    LOG.info("PrivateKey " + keyPair.getPrivateKey().toString() + " - addr=" + addr);
    LOG.info("Correct " + keyPair.getPrivateKey().toString() + " - addr=" + besuCore);

    //INFORMACIÓN: PrivateKey 5621f9b17596910fc7cf2d8202e8ce15908513274a3506ba1f2a49714a091c29 - addr=0x1c0691f121d4db669876f28d6439c8606b8bd2f2
  }



}
