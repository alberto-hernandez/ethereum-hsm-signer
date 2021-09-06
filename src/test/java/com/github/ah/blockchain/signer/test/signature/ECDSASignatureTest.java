package com.github.ah.blockchain.signer.test.signature;

import com.github.ah.blockchain.signer.signature.ECDSASignature;
import com.github.ah.blockchain.signer.signature.SECP256K1KeyPair;
import java.math.BigInteger;
import org.apache.tuweni.bytes.Bytes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ECDSASignatureTest {

  private static final String TEST_MESSAGE = "A test message";
  static String PRIVATE_KEY_STRING =
      "a392604efc2fad9c0b3da43b5f698a2e3f270f170d859912be0d54742275c5f6";

  @Test
  public void shouldCalculateSignature() {
    SECP256K1KeyPair keyPair =
        SECP256K1KeyPair.fromSecretKey(new BigInteger(PRIVATE_KEY_STRING, 16));
    ECDSASignature ecdsaSignature = new ECDSASignature(keyPair);

    Bytes expectedSignature =
        Bytes.concatenate(
            Bytes.of((byte) 27),
            Bytes.fromHexString(
                "0x9631f6d21dec448a213585a4a41a28ef3d4337548aa34734478b563036163786"),
            Bytes.fromHexString(
                "0x2ff816ee6bbb82719e983ecd8a33a4b45d32a4b58377ef1381163d75eedc900b"));

    Bytes transactionSerialized = Bytes.wrap(TEST_MESSAGE.getBytes());

    Bytes signature = ecdsaSignature.sign(transactionSerialized);
    Assertions.assertNotNull(signature);
    Assertions.assertEquals(signature.toHexString(), expectedSignature.toHexString());
  }

  @Test
  public void transactionSignature() {
    Bytes transaction =
        Bytes.fromHexString(
            "0xf901f08266d2840112a880840112a88094100000000000000000000000000000000000000480b901c47e533b0400000000000000000000000000000000000000000000000000000000000000a0000000000000000000000000000000000000000000000000000000000000014000000000000000000000000000000000000000000000000000000000000001800000000000000000000000000000000000000000000000000000000000001134000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000803166323634656163623837653039636436336335646662643430386661393662373062336134323461373339663634653739633938663335376434343062306166333661346137643834383036316336653164653931346663343766393865396462333761366637346364613762356635663432376332646635613735333964000000000000000000000000000000000000000000000000000000000000000b31302e38352e302e3134320000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000007302e302e302e300000000000000000000000000000000000000000000000000083a98ac78080");

    SECP256K1KeyPair keyPair =
        SECP256K1KeyPair.fromSecretKey(
            new BigInteger("2c5e3dbf5b875a968b7a0f16156ee3b875ea0f3164c6878f6c8c20ea6b42927a", 16));

    Bytes expectedSignature =
        Bytes.concatenate(
            Bytes.of((byte) 27),
            Bytes.fromHexString(
                "0xe3aedf59b87e84d09fff7a4011c4bc88991a65b42439ef2d92d4974bc3e390e6"),
            Bytes.fromHexString(
                "0x14a1cdf8521127c8a77e86e9fb2c7ddb13b77a5e09963bd8cec8fb479c0c2a13"));

    ECDSASignature ecdsaSignature = new ECDSASignature(keyPair);
    Bytes signature = ecdsaSignature.sign(transaction);
    Assertions.assertNotNull(signature);
    Assertions.assertEquals(signature.toHexString(), expectedSignature.toHexString());
  }
}
