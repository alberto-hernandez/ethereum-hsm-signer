package com.github.ah.blockchain.signer.test.signature;

import com.github.ah.blockchain.signer.signature.ECSASigner;
import com.github.ah.blockchain.signer.signature.SignatureUtils;
import java.math.BigInteger;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.eth.Address;
import org.hyperledger.besu.crypto.KeyPair;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SignatureUtilsTest {
  private static Bytes SIGNATURE;

  private static String R = "d2ce488f4da29e68f22cb05cac1b19b75df170a12b4ad1bdd4531b8e9115c6fb";
  private static String S = "75c1fe50a95e8ccffcbb5482a1e42fbbdd6324131dfe75c3b3b7f9a7c721eccb";
  private static byte   V = (byte)28;


  @BeforeAll
  public static void setUp () {
    SIGNATURE =
        Bytes.concatenate(
            Bytes.fromHexString(R),
            Bytes.fromHexString(S),
            Bytes.of(V)
        );
  }

  @Test
  public void shouldExtractR() {
    Bytes r_component = SignatureUtils.getR(SIGNATURE);
    Assert.assertEquals(r_component.size(), 32);
    Assert.assertEquals(r_component.toUnprefixedHexString(), R);
  }

  @Test
  public void shouldExtractS() {
    Bytes s_component = SignatureUtils.getS(SIGNATURE);
    Assert.assertEquals(s_component.size(), 32);
    Assert.assertEquals(s_component.toUnprefixedHexString(), S);
  }

  @Test
  public void shouldExtractV() {
    Bytes v_component = SignatureUtils.getV(SIGNATURE);
    Assert.assertEquals(v_component.size(), 1);
    Assert.assertEquals(v_component.get(0), V);
  }

  @Test
  public void shouldDeriveProperlyAddress() {
    String PRIVATE_KEY = "a392604efc2fad9c0b3da43b5f698a2e3f270f170d859912be0d54742275c5f6";
    KeyPair keyPair = ECSASigner.withSECP256K1().keyPair(new BigInteger(PRIVATE_KEY, 16));

    Address address = SignatureUtils.toAddress(keyPair.getPublicKey());

    Assertions.assertNotNull(address);
    Assertions.assertEquals(address, Address.fromHexString("0xef678007d18427e6022059dbc264f27507cd1ffc"));
  }

}
