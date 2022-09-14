package com.github.ah.blockchain.signer.provider.hashicorp.auth.method;

import java.util.Optional;

public class AppRole implements AuthMethod {
  private final String roleId;
  private final String secretId;
  private final Optional<String> namespace;

  public AppRole(final String roleId, final String secretId) {
    this (roleId, secretId, Optional.empty());
  }

  public AppRole(final String roleId, final String secretId, final Optional<String> namespace) {
    this.roleId = roleId;
    this.secretId = secretId;
    this.namespace = namespace;
  }

  public String getRoleId() {
    return roleId;
  }

  public String getSecretId() {
    return secretId;
  }

  @Override
  public Optional<String> getNamespace() {
    return namespace;
  }
}
