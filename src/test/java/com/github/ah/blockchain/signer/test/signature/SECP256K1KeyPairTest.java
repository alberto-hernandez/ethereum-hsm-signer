package com.github.ah.blockchain.signer.test.signature;

import com.github.ah.blockchain.signer.signature.SECP256K1KeyPair;
import java.math.BigInteger;
import org.apache.tuweni.eth.Address;
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

  @Test
  public void shouldCalculateAddress() {
    shouldCalculateAddress(
        "a0f14c227b6e17180963f51ace87eabe12b154c8ddf1894bf2c99b31a2527e32f39255d182c6b7da961c8d37a6efbf65a4c95fb14d7e99faddadf7c26b481857",
        "d9aeec4fdbd6975f969ad40d5e1f882bcb136729");

    shouldCalculateAddress(
        "aafc729abd5a8d95b187284d9c7d9b610a55fb349499712cfb39186edd94d58766ca67608137ed9f0b34438f13f7f6b043ff6ef8bff99911746928e8350c0f23",
        "ea6cf034d279a9ca02d60e84985a5c1678267d48");
  }

  private void shouldCalculateAddress(final String publicKeyHex, final String addressHex) {
    final BigInteger publicKey = new BigInteger(publicKeyHex, 16);
    Address address = SECP256K1KeyPair.toAddress(publicKey);
    Assertions.assertEquals(address, Address.fromHexString(addressHex));
  }
}
