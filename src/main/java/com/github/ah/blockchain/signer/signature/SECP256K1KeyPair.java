package com.github.ah.blockchain.signer.signature;

import java.math.BigInteger;
import java.util.Arrays;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class SECP256K1KeyPair {
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
}
