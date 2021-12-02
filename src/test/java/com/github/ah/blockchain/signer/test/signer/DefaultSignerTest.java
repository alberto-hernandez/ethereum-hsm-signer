package com.github.ah.blockchain.signer.test.signer;

import com.github.ah.blockchain.signer.signers.DefaultSigner;
import java.math.BigInteger;
import java.util.logging.Logger;
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
}
