package com.github.ah.blockchain.signer.signature;

import org.apache.tuweni.eth.Address;
import org.hyperledger.besu.crypto.SECPPublicKey;

public class SignatureUtils {

  public static Address toAddress(final SECPPublicKey publicKey) {
    org.hyperledger.besu.ethereum.core.Address address =
        org.hyperledger.besu.ethereum.core.Address.extract(publicKey);
    return Address.fromHexString(address.toHexString());
  }
}
