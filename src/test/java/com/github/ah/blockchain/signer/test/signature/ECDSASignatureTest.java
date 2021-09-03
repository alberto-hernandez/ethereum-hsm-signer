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
}
