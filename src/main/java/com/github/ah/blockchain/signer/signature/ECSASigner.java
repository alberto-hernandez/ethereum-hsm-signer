package com.github.ah.blockchain.signer.signature;

import java.math.BigInteger;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.hyperledger.besu.crypto.Hash;
import org.hyperledger.besu.crypto.KeyPair;
import org.hyperledger.besu.crypto.SECP256K1;
import org.hyperledger.besu.crypto.SECPPrivateKey;
import org.hyperledger.besu.crypto.SECPPublicKey;
import org.hyperledger.besu.crypto.SECPSignature;
import org.hyperledger.besu.crypto.SignatureAlgorithm;

public class ECSASigner {
  private final SignatureAlgorithm signatureAlgorithm;

  public ECSASigner(final SignatureAlgorithm signatureAlgorithm) {
    this.signatureAlgorithm = signatureAlgorithm;
  }

  public Bytes sign (final Bytes rawBytes, final KeyPair keyPair) {
    final Bytes32 hash = Hash.keccak256(rawBytes);
    final SECPSignature signature = signatureAlgorithm.sign(hash, keyPair);
    return normalizeSignature(signature, keyPair.getPublicKey(), hash);
  }

  public boolean verify (final Bytes message, final Bytes signature,  final SECPPublicKey publicKey) {
    SECPSignature secpSignature = toSECPSignature(signature);
    final Bytes32 hash = Hash.keccak256(message);
    return signatureAlgorithm.verify(hash, secpSignature, publicKey);
  }

  private Bytes normalizeSignature(final SECPSignature signature, final SECPPublicKey publicKey, final Bytes32 hash) {
    final SECPSignature nSignature = signatureAlgorithm.normaliseSignature(signature.getR(), signature.getS(), publicKey, hash);
    return Bytes.concatenate(
        Bytes.of(nSignature.getRecId() + 27),
        toBytesPadded(nSignature.getR(), 32),
        toBytesPadded(nSignature.getS(), 32));
  }

  private SECPSignature toSECPSignature (final Bytes signature) {
    final BigInteger r = new BigInteger (signature.slice(1, 32).toArray());
    final BigInteger s = new BigInteger (signature.slice(33, 32).toArray());

    return signatureAlgorithm.createSignature(r,s, (byte)(signature.get(0) - 27));
  }

  public KeyPair keyPair (final BigInteger privateKey) {
    final SECPPrivateKey secpPrivatekey = signatureAlgorithm.createPrivateKey(privateKey);
    return new KeyPair (secpPrivatekey, signatureAlgorithm.createPublicKey(secpPrivatekey));
  }

  public static ECSASigner withSECP256K1() {
    return new ECSASigner(new SECP256K1());
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
}
