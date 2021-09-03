package com.github.ah.blockchain.signer.test.signature;

import com.github.ah.blockchain.signer.signature.SECP256K1KeyPair;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SECP256K1KeyPairTest {
  static String PRIVATE_KEY_STRING =
      "a392604efc2fad9c0b3da43b5f698a2e3f270f170d859912be0d54742275c5f6";

  static final String PUBLIC_KEY_STRING =
      "506bc1dc099358e5137292f4efdd57e400f29ba5132aa5d12b18dac1c1f6aab"
          + "a645c0b7b58158babbfa6c6cd5a48aa7340a8749176b120e8516216787a13dc76";

  @Test
  public void shouldCalculatePublicKeyProperly() {
    final BigInteger PRIVATE_KEY = new BigInteger(PRIVATE_KEY_STRING, 16);
    final BigInteger PUBLIC_KEY = new BigInteger(PUBLIC_KEY_STRING, 16);

    BigInteger obtained = SECP256K1KeyPair.publicKeyFromPrivate(PRIVATE_KEY);

    Assertions.assertNotNull(obtained);
    Assertions.assertEquals(obtained, PUBLIC_KEY);
  }
}
