package com.github.ah.blockchain.signer.signature;

import java.math.BigInteger;
import org.apache.commons.lang3.StringUtils;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.eth.Address;
import org.hyperledger.besu.crypto.Hash;
import org.hyperledger.besu.crypto.SECPPublicKey;
import org.hyperledger.besu.crypto.SECPSignature;

public class SignatureUtils {
  private static final int SIGNATURE_V_ENTRY_LENGHT = 1;
  private static final int SIGNATURE_ENTRY_LENGHT = 32;

  private static final int PUBLIC_KEY_LENGHT_BYTES = 64;
  private static final int PUBLIC_KEY_LENGHT_HEX = PUBLIC_KEY_LENGHT_BYTES << 1;

  public static final int ADDRESS_SIZE = 160;
  public static final int ADDRESS_LENGTH_IN_BYTES = 20;


  public static SECPSignature toSECPSignature (final Bytes signature, final BigInteger curveOrder) {
    final BigInteger r = new BigInteger (signature.slice(SIGNATURE_V_ENTRY_LENGHT, SIGNATURE_ENTRY_LENGHT).toArray());
    final BigInteger s = new BigInteger (signature.slice(SIGNATURE_V_ENTRY_LENGHT + SIGNATURE_ENTRY_LENGHT, SIGNATURE_ENTRY_LENGHT).toArray());
    return SECPSignature.create(r, s, signature.get(0), curveOrder);
  }

  public static Bytes toBytes (final SECPSignature signature) {
    return Bytes.concatenate(
        Bytes.of(signature.getRecId()),
        Bytes.of(signature.getR().toByteArray()),
        Bytes.of(signature.getS().toByteArray()));
  }

  public static Address toAddress(final BigInteger publicKey) {
    String publicKeyHex = StringUtils.leftPad(publicKey.toString(16), PUBLIC_KEY_LENGHT_HEX, "0");
    Bytes hash = Hash.keccak256(Bytes.fromHexString(publicKeyHex));
    return Address.fromBytes(hash.slice(hash.size() - ADDRESS_LENGTH_IN_BYTES));
  }

  public static Address toAddress (final SECPPublicKey publicKey) {
    return toAddress(toBigInteger(publicKey));
  }

  public static BigInteger toBigInteger (final SECPPublicKey publicKey) {
    return new BigInteger (publicKey.getEncoded());
  }

}
