package com.github.ah.blockchain.signer.signature;

import java.math.BigInteger;
import java.util.Arrays;
import org.apache.tuweni.bytes.Bytes;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;

public class ECDSASignature {
  public static final X9ECParameters CURVE = SECNamedCurves.getByName("secp256k1");
  static final ECDomainParameters EC_DOMAIN_PARAMETERS =
      new ECDomainParameters(CURVE.getCurve(), CURVE.getG(), CURVE.getN(), CURVE.getH());
  static final BigInteger HALF_CURVE_ORDER = CURVE.getN().shiftRight(1);

  private final ECDSASigner signer;
  private final SECP256K1KeyPair keyPair;

  public ECDSASignature(final SECP256K1KeyPair keyPair) {
    this.keyPair = keyPair;

    signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
    signer.init(true, new ECPrivateKeyParameters(keyPair.getSecretKey(), EC_DOMAIN_PARAMETERS));
  }

  public Bytes sign(final Bytes transaction) {
    try {
      byte[] messageHash = Hash.sha3(transaction.toArray());
      // Calculate R & S
      final BigInteger[] signature = signer.generateSignature(messageHash);
      final BigInteger rValue = signature[0];
      final BigInteger sValue = toCanonical(signature[1]);

      byte v = computeV(rValue, sValue, messageHash);

      return Bytes.concatenate(Bytes.of(v), toBytesPadded(rValue, 32), toBytesPadded(sValue, 32));
    } catch (Exception e) {
      throw new SignatureException(e);
    }
  }

  private byte computeV(final BigInteger r, final BigInteger s, final byte[] messageHash) {
    int recId = -1;
    for (int i = 0; i < 4; i++) {
      BigInteger k = recoverFromSignature(signer.getOrder(), i, r, s, messageHash);
      if (k != null && k.equals(keyPair.getPublicKey())) {
        recId = i;
        break;
      }
    }
    if (recId == -1) {
      throw new SignatureException(
          "Could not construct a recoverable key. Are your credentials valid?");
    }

    return Integer.valueOf(recId + 27).byteValue();
  }

  private static Bytes toBytesPadded(BigInteger value, int length) {
    byte[] result = new byte[length];
    byte[] bytes = value.toByteArray();

    int bytesLength;
    int srcOffset;
    if (bytes[0] == 0) {
      bytesLength = bytes.length - 1;
      srcOffset = 1;
    } else {
      bytesLength = bytes.length;
      srcOffset = 0;
    }

    if (bytesLength > length) {
      throw new RuntimeException("Input is too large to put in byte array of size " + length);
    }

    int destOffset = length - bytesLength;
    System.arraycopy(bytes, srcOffset, result, destOffset, bytesLength);
    return Bytes.wrap(result);
  }

  private static BigInteger recoverFromSignature(
      final BigInteger order,
      final int recId,
      final BigInteger r,
      final BigInteger s,
      final byte[] message) {
    // 1.0 For j from 0 to h   (h == recId here and the loop is outside this function)
    //   1.1 Let x = r + jn
    BigInteger i = BigInteger.valueOf((long) recId / 2);
    BigInteger x = r.add(i.multiply(order));
    //   1.2. Convert the integer x to an octet string X of length mlen using the conversion
    //        routine specified in Section 2.3.7, where mlen = ⌈(log2 p)/8⌉ or mlen = ⌈m/8⌉.
    //   1.3. Convert the octet string (16 set binary digits)||X to an elliptic curve point R
    //        using the conversion routine specified in Section 2.3.4. If this conversion
    //        routine outputs "invalid", then do another iteration of Step 1.
    //
    // More concisely, what these points mean is to use X as a compressed public key.
    BigInteger prime = SecP256K1Curve.q;
    if (x.compareTo(prime) >= 0) {
      // Cannot have point co-ordinates larger than this as everything takes place modulo Q.
      return null;
    }
    // Compressed keys require you to know an extra bit of data about the y-coord as there are
    // two possibilities. So it's encoded in the recId.
    ECPoint R = decompressKey(x, (recId & 1) == 1);
    //   1.4. If nR != point at infinity, then do another iteration of Step 1 (callers
    //        responsibility).
    if (!R.multiply(order).isInfinity()) {
      return null;
    }
    //   1.5. Compute e from M using Steps 2 and 3 of ECDSA signature verification.
    BigInteger e = new BigInteger(1, message);
    //   1.6. For k from 1 to 2 do the following.   (loop is outside this function via
    //        iterating recId)
    //   1.6.1. Compute a candidate public key as:
    //               Q = mi(r) * (sR - eG)
    //
    // Where mi(x) is the modular multiplicative inverse. We transform this into the following:
    //               Q = (mi(r) * s ** R) + (mi(r) * -e ** G)
    // Where -e is the modular additive inverse of e, that is z such that z + e = 0 (mod n).
    // In the above equation ** is point multiplication and + is point addition (the EC group
    // operator).
    //
    // We can find the additive inverse by subtracting e from zero then taking the mod. For
    // example the additive inverse of 3 modulo 11 is 8 because 3 + 8 mod 11 = 0, and
    // -3 mod 11 = 8.
    BigInteger eInv = BigInteger.ZERO.subtract(e).mod(order);
    BigInteger rInv = r.modInverse(order);
    BigInteger srInv = rInv.multiply(s).mod(order);
    BigInteger eInvrInv = rInv.multiply(eInv).mod(order);
    ECPoint q = ECAlgorithms.sumOfTwoMultiplies(CURVE.getG(), eInvrInv, R, srInv);

    byte[] qBytes = q.getEncoded(false);
    // We remove the prefix
    return new BigInteger(1, Arrays.copyOfRange(qBytes, 1, qBytes.length));
  }

  private static ECPoint decompressKey(BigInteger xBN, boolean yBit) {
    X9IntegerConverter x9 = new X9IntegerConverter();
    byte[] compEnc = x9.integerToBytes(xBN, 1 + x9.getByteLength(CURVE.getCurve()));
    compEnc[0] = (byte) (yBit ? 0x03 : 0x02);
    return CURVE.getCurve().decodePoint(compEnc);
  }

  private BigInteger toCanonical(BigInteger s) {
    return s.compareTo(HALF_CURVE_ORDER) <= 0 ? s : CURVE.getN().subtract(s);
  }

  public static ECDSASignature fromKeyPair(final SECP256K1KeyPair keyPair) {
    return new ECDSASignature(keyPair);
  }
}
