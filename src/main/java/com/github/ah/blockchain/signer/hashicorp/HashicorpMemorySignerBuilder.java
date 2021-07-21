package com.github.ah.blockchain.signer.hashicorp;

import com.github.ah.blockchain.signer.SecuredSignerBuilder;
import com.github.ah.blockchain.signer.hashicorp.engine.kv.HashicorpKvResolver;
import com.github.ah.blockchain.signer.hashicorp.secrets.SecretId;
import com.github.ah.blockchain.signer.hashicorp.secrets.SecretValue;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.signer.DefaultSigner;
import org.web3j.crypto.signer.Signer;


public class HashicorpMemorySignerBuilder implements SecuredSignerBuilder {
  private HashicorpKvResolver hashicorpKvResolver;
  private SecretId secretId;

  private HashicorpMemorySignerBuilder() {
  }

  public HashicorpMemorySignerBuilder hashicorpKvResolver(final HashicorpKvResolver hashicorpKvResolver) {
    this.hashicorpKvResolver = hashicorpKvResolver;
    return this;
  }

  public HashicorpMemorySignerBuilder secretId(final SecretId secretId) {
    this.secretId = secretId;
    return this;
  }

  public static HashicorpMemorySignerBuilder builder () {
    return new HashicorpMemorySignerBuilder();
  }

  @Override
  public Signer build() throws HashicorpException {
    SecretValue secretValue = hashicorpKvResolver.fetchSecretValue(secretId);
    Credentials credentials = Credentials.create(secretValue.getValue());
    return DefaultSigner.fromCredentials(credentials);
  }

}
