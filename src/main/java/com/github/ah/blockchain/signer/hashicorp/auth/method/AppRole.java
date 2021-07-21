package com.github.ah.blockchain.signer.hashicorp.auth.method;

public class AppRole implements AuthMethod {
  private final String roleId;
  private final String secretId;

  public AppRole(final String roleId, final String secretId) {
    this.roleId = roleId;
    this.secretId = secretId;
  }

  public String getRoleId() {
    return roleId;
  }

  public String getSecretId() {
    return secretId;
  }
}
