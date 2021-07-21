package com.github.ah.blockchain.signer.hashicorp.secrets;

public class SecretValue {
  private final SecretId secretId;
  private final String entry;
  private final String value;


  public SecretValue(final SecretId secretId, final String entry, final String value) {
    this.secretId = secretId;
    this.entry = entry;
    this.value = value;
  }

  public SecretId getKeyId() {
    return secretId;
  }

  public String getValue() {
    return value;
  }

  public String getEntry() {
    return entry;
  }
}
