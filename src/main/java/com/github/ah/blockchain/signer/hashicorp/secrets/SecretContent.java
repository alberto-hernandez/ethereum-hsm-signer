package com.github.ah.blockchain.signer.hashicorp.secrets;

import java.util.ArrayList;
import java.util.List;

public class SecretContent {
  private final SecretId secretId;
  private final List<SecretValue> secrets;

  public SecretContent(final SecretId secretId, final List<SecretValue> secrets) {
    this.secretId = secretId;
    this.secrets = secrets;
  }

  public SecretContent(final SecretId secretId) {
    this (secretId, new ArrayList<>());
  }


  public SecretId getSecretId() {
    return secretId;
  }

  public List<SecretValue> getSecrets() {
    return secrets;
  }


  public SecretContent addSecret (final SecretValue secretValue) {
    this.secrets.add(secretValue);
    return this;
  }

}
