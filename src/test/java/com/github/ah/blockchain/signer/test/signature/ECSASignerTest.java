package com.github.ah.blockchain.signer.test.signature;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.github.ah.blockchain.signer.signature.ECSASigner;
import java.math.BigInteger;
import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.crypto.KeyPair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ECSASignerTest {

  private static final String TEST_MESSAGE = "A test message";
  static String PRIVATE_KEY_STRING =
      "a392604efc2fad9c0b3da43b5f698a2e3f270f170d859912be0d54742275c5f6";

  static final BigInteger PRIVATE_KEY = new BigInteger(PRIVATE_KEY_STRING, 16);

  private ECSASigner ecsaSigner;

  @BeforeEach
  public void setUp () {
    ecsaSigner = ecsaSigner.withSECP256K1();
  }

  @Test
  public void shouldCalculateSignature() {
    KeyPair keyPair = ecsaSigner.keyPair(PRIVATE_KEY);

    Bytes expectedSignature =
        Bytes.concatenate(
            Bytes.fromHexString(
                "0x9631f6d21dec448a213585a4a41a28ef3d4337548aa34734478b563036163786"),
            Bytes.fromHexString(
                "0x2ff816ee6bbb82719e983ecd8a33a4b45d32a4b58377ef1381163d75eedc900b"),
            Bytes.of((byte) 27)
        );

    Bytes transactionSerialized = Bytes.wrap(TEST_MESSAGE.getBytes());

    Bytes signature = ecsaSigner.sign(transactionSerialized, keyPair);
    Assertions.assertNotNull(signature);
    Assertions.assertEquals(signature.toHexString(), expectedSignature.toHexString());
  }

  @Test
  public void shouldCalculateSignatureSample2() {
    KeyPair keyPair = ecsaSigner.keyPair(new BigInteger("c85ef7d79691fe79573b1a7064c19c1a9819ebdbd1faaab1a8ec92344438aaf4", 16));

    final Bytes data = Bytes.wrap("This is an example of a signed message.".getBytes(UTF_8));

    Bytes expectedSignature =
        Bytes.concatenate(
            Bytes.fromHexString(
                "d2ce488f4da29e68f22cb05cac1b19b75df170a12b4ad1bdd4531b8e9115c6fb"),
            Bytes.fromHexString(
                "75c1fe50a95e8ccffcbb5482a1e42fbbdd6324131dfe75c3b3b7f9a7c721eccb"),
            Bytes.of((byte) 28)
        );

    Bytes signature = ecsaSigner.sign(data, keyPair);
    Assertions.assertNotNull(signature);
    Assertions.assertEquals(signature.toHexString(), expectedSignature.toHexString());
  }

  @Test
  public void shouldVerifySignature() {
    KeyPair keyPair = ecsaSigner.keyPair(new BigInteger("c85ef7d79691fe79573b1a7064c19c1a9819ebdbd1faaab1a8ec92344438aaf4", 16));

    final Bytes data = Bytes.wrap("This is an example of a signed message.".getBytes(UTF_8));

    Bytes signature =
        Bytes.concatenate(
            Bytes.fromHexString(
                "d2ce488f4da29e68f22cb05cac1b19b75df170a12b4ad1bdd4531b8e9115c6fb"),
            Bytes.fromHexString(
                "75c1fe50a95e8ccffcbb5482a1e42fbbdd6324131dfe75c3b3b7f9a7c721eccb"),
            Bytes.of((byte) 28)
        );

    Assertions.assertTrue(ecsaSigner.verify(data, signature, keyPair.getPublicKey()));
  }

}
