package com.github.ah.blockchain.signer.signature;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.eth.Address;
import org.hyperledger.besu.crypto.SECPPublicKey;

public final class SignatureUtils {

  public static final int COMPONENTS_LENGTH = 32;
  public static final int V_LENGTH = 1;

  private SignatureUtils() {}

  public static Address toAddress(final SECPPublicKey publicKey) {
    org.hyperledger.besu.datatypes.Address address =
        org.hyperledger.besu.datatypes.Address.extract(publicKey);
    return Address.fromHexString(address.toHexString());
  }

  public static Bytes getV (final Bytes signature) {
    return signature.slice(64, V_LENGTH);
  }

  public static Bytes getR (final Bytes signature) {
    return signature.slice(0, COMPONENTS_LENGTH);
  }

  public static Bytes getS (final Bytes signature) {
    return signature.slice(COMPONENTS_LENGTH, COMPONENTS_LENGTH);
  }

}
