package com.github.ah.blockchain.signer.signature;

import java.math.BigInteger;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.apache.tuweni.bytes.MutableBytes;
import org.hyperledger.besu.crypto.AbstractSECP256;
import org.hyperledger.besu.crypto.Hash;
import org.hyperledger.besu.crypto.KeyPair;
import org.hyperledger.besu.crypto.SECP256K1;
import org.hyperledger.besu.crypto.SECPPrivateKey;
import org.hyperledger.besu.crypto.SECPPublicKey;
import org.hyperledger.besu.crypto.SECPSignature;

public class ECSASigner {
  public static final byte V_BASE = (byte)27;
  public static final int V_ARRAY_INDEX = 64;

  private final AbstractSECP256 signatureAlgorithm;

  public ECSASigner(final AbstractSECP256 signatureAlgorithm) {
    this.signatureAlgorithm = signatureAlgorithm;
  }

  /**
   * Generates a signature and returned in Bytes in the order (r,s,v)
   * @param rawBytes Bytes of the whole message
   * @param keyPair KeyPair Used to Sign
   * @return Return the Bytes of the Signature
   */
  public Bytes sign (final Bytes rawBytes, final KeyPair keyPair) {
    final Bytes32 hash = Hash.keccak256(rawBytes);
    final SECPSignature signature = signatureAlgorithm.sign(hash, keyPair);
    return normalizeSignature(signature, keyPair.getPublicKey(), hash);
  }

  /**
   * Verify the Signaturre provided for a message corresponds to a the associated public key
   * @param message Original Message Signed
   * @param signature Signature of the Algorithm
   * @param publicKey Public Key Algorithm
   * @return Returns true if the signature matches with the public key argument
   */
  public boolean verify (final Bytes message, final Bytes signature,  final SECPPublicKey publicKey) {
    SECPSignature secpSignature = toSECPSignature(signature);
    final Bytes32 hash = Hash.keccak256(message);
    return signatureAlgorithm.verify(hash, secpSignature, publicKey);
  }

  private Bytes normalizeSignature(final SECPSignature signature, final SECPPublicKey publicKey, final Bytes32 hash) {
    final SECPSignature nSignature = signatureAlgorithm.normaliseSignature(signature.getR(), signature.getS(), publicKey, hash);
    MutableBytes bytes = (MutableBytes)nSignature.encodedBytes();
    // Update The V value
    bytes.set(V_ARRAY_INDEX, (byte)(bytes.get(V_ARRAY_INDEX) + V_BASE));
    return bytes;
  }

  private SECPSignature toSECPSignature (final Bytes signature) {
    MutableBytes modified = signature.mutableCopy();
    modified.set(V_ARRAY_INDEX, (byte)(modified.get(V_ARRAY_INDEX) - V_BASE));

    return signatureAlgorithm.decodeSignature(modified);
  }

  public KeyPair keyPair (final BigInteger privateKey) {
    final SECPPrivateKey secpPrivatekey = signatureAlgorithm.createPrivateKey(privateKey);
    return new KeyPair (secpPrivatekey, signatureAlgorithm.createPublicKey(secpPrivatekey));
  }

  public static ECSASigner withSECP256K1() {
    return new ECSASigner(new SECP256K1());
  }

}
