package com.github.ah.blockchain.signer.signature;

import java.math.BigInteger;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.eth.Address;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class SECP256K1KeyPair {
  private static final int PUBLIC_KEY_LENGHT_BYTES = 64;
  private static final int PUBLIC_KEY_LENGHT_HEX = PUBLIC_KEY_LENGHT_BYTES << 1;

  public static final int ADDRESS_SIZE = 160;
  public static final int ADDRESS_LENGTH_IN_BYTES = 20;

  private final BigInteger secretKey;
  private final BigInteger publicKey;

  public SECP256K1KeyPair(final BigInteger secretKey, final BigInteger publicKey) {
    this.secretKey = secretKey;
    this.publicKey = publicKey;
  }

  public static SECP256K1KeyPair fromSecretKey(BigInteger secretKey) {
    return new SECP256K1KeyPair(secretKey, publicKeyFromPrivate(secretKey));
  }

  public static BigInteger publicKeyFromPrivate(final BigInteger privateKey) {
    BigInteger privKey = privateKey;
    if (privKey.bitLength() > ECDSASignature.CURVE.getN().bitLength()) {
      privKey = privKey.mod(ECDSASignature.CURVE.getN());
    }

    ECPoint point = new FixedPointCombMultiplier().multiply(ECDSASignature.CURVE.getG(), privKey);
    byte[] encoded = point.getEncoded(false);
    return new BigInteger(1, Arrays.copyOfRange(encoded, 1, encoded.length)); // remove prefix
  }

  public BigInteger getSecretKey() {
    return secretKey;
  }

  public BigInteger getPublicKey() {
    return publicKey;
  }

  public Address toAddress() {
    return toAddress(publicKey);
  }

  public static Address toAddress(final BigInteger publicKey) {
    String publicKeyHex = StringUtils.leftPad(publicKey.toString(16), PUBLIC_KEY_LENGHT_HEX, "0");
    byte[] hash = Hash.sha3(Bytes.fromHexString(publicKeyHex).toArray());
    return Address.fromBytes(Bytes.wrap(hash).slice(hash.length - ADDRESS_LENGTH_IN_BYTES));
  }
}
