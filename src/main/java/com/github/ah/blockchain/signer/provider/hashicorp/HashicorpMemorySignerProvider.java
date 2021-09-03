package com.github.ah.blockchain.signer.provider.hashicorp;

import com.github.ah.blockchain.signer.SecuredSignerProvider;
import com.github.ah.blockchain.signer.Signer;
import com.github.ah.blockchain.signer.exception.SignerException;
import com.github.ah.blockchain.signer.provider.hashicorp.engine.HashicorpResolver;
import com.github.ah.blockchain.signer.provider.hashicorp.secrets.SecretId;
import com.github.ah.blockchain.signer.provider.hashicorp.secrets.SecretValue;
import com.github.ah.blockchain.signer.signature.SECP256K1KeyPair;
import com.github.ah.blockchain.signer.signers.DefaultSigner;
import java.math.BigInteger;


public class HashicorpMemorySignerProvider implements SecuredSignerProvider {
  private final HashicorpResolver hashicorpResolver;
  private final SecretId secretId;

  public HashicorpMemorySignerProvider(
      final HashicorpResolver hashicorpResolver,
      final SecretId secretId) {
    this.hashicorpResolver = hashicorpResolver;
    this.secretId = secretId;
  }

  @Override
  public Signer build() throws HashicorpException {
    try {
      SecretValue secretValue = hashicorpResolver.fetchSecretValue(secretId);
      BigInteger privatekey = new BigInteger(secretValue.getValue(), 16);

      SECP256K1KeyPair keyPair = SECP256K1KeyPair.fromSecretKey(privatekey);
      return DefaultSigner.fromKeyPair(keyPair);
    } catch (Exception e) {
      throw new SignerException(e);
    }
  }

}
