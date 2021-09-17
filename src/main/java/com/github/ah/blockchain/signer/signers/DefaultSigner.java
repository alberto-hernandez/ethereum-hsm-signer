package com.github.ah.blockchain.signer.signers;

import com.github.ah.blockchain.signer.Signer;
import com.github.ah.blockchain.signer.signature.ECSASigner;
import com.github.ah.blockchain.signer.signature.SignatureUtils;
import java.math.BigInteger;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.eth.Address;
import org.hyperledger.besu.crypto.KeyPair;

public class DefaultSigner implements Signer {
  private final KeyPair keyPair;
  private final ECSASigner ecdsaSignature;

  public DefaultSigner(final BigInteger privateKey) {
    ecdsaSignature = ECSASigner.withSECP256K1();
    this.keyPair = ecdsaSignature.keyPair(privateKey);
  }

  @Override
  public Bytes sign(final Bytes transaction) {
    return ecdsaSignature.sign(transaction, keyPair);
  }

  @Override
  public Address getAddress() {
    return SignatureUtils.toAddress(keyPair.getPublicKey());
  }

  @Override
  public BigInteger getPublicKey() {
    return SignatureUtils.toBigInteger(keyPair.getPublicKey());
  }

}
