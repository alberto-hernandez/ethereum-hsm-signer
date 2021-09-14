package com.github.ah.blockchain.signer;

import java.math.BigInteger;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.eth.Address;

public interface Signer {

  Bytes sign(final Bytes transaction);

  Address getAddress();

  BigInteger getPublicKey();
}
