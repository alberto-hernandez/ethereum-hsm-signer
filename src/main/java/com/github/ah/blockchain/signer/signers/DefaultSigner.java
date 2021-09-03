package com.github.ah.blockchain.signer.signers;

import com.github.ah.blockchain.signer.Signer;
import com.github.ah.blockchain.signer.signature.ECDSASignature;
import com.github.ah.blockchain.signer.signature.Hash;
import com.github.ah.blockchain.signer.signature.SECP256K1KeyPair;
import java.math.BigInteger;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.eth.Address;

public class DefaultSigner implements Signer {
  private final SECP256K1KeyPair keyPair;
  private final ECDSASignature ecdsaSignature;

  public DefaultSigner(final SECP256K1KeyPair keyPair) {
    this.keyPair = keyPair;
    ecdsaSignature = ECDSASignature.fromKeyPair(keyPair);
  }

  @Override
  public Bytes sign(final Bytes transaction) {
    return ecdsaSignature.sign(transaction);
  }

  @Override
  public Address getAddress() {
    Bytes value = Bytes.wrap(Hash.sha3(keyPair.getPublicKey().toByteArray()));
    return Address.fromBytes(value.slice(12));
  }

  @Override
  public BigInteger getPublicKey() {
    return keyPair.getPublicKey();
  }

  public static DefaultSigner fromKeyPair(final SECP256K1KeyPair keyPair) {
    return new DefaultSigner(keyPair);
  }
}
