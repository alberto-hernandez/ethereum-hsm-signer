package com.github.ah.blockchain.signer.test.signature;

import com.github.ah.blockchain.signer.signature.SignatureUtils;
import java.math.BigInteger;
import org.apache.tuweni.eth.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SignatureUtilsTest {

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
    Address address = SignatureUtils.toAddress(publicKey);
    Assertions.assertEquals(address, Address.fromHexString(addressHex));
  }
}
