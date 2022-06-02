package com.github.ah.blockchain.signer;

import java.math.BigInteger;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.eth.Address;

public interface Signer {
  /**
   * Receive a Transaction or message serialized into Bytes and returns the ECDSA signature
   * with the structure of (R[0..32],S[32,64],V[64])
   * @param transaction Transaction or Message Serialized
   * @return the ECSA signature in the form R,S,V
   */
  Bytes sign(Bytes transaction);

  /**
   * Returns the address derivated from the public key for signing
   * @return
   */
  Address getAddress();

  /**
   * Returns the the public key associated to the private key used for signing
   * @return
   */
  BigInteger getPublicKey();
}
