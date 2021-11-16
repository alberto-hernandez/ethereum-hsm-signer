package com.github.ah.blockchain.signer.provider.hashicorp.connection;

import java.util.Arrays;
import java.util.Optional;

public enum TlsTypes {
  JKS("jks", true),
  PKCS12("pkcs12", true),
  PEM ("pem", false);

  private final String value;
  private final boolean requirePassword;

  TlsTypes(final String value, final boolean requirePassword) {
    this.value = value;
    this.requirePassword = requirePassword;
  }

  public boolean isRequirePassword() {
    return requirePassword;
  }

  public String getValue() {
    return value;
  }

  public static Optional<TlsTypes> fromString (final String strValue) {
    return Arrays.stream(TlsTypes.values()).filter( t -> t.value.equals(strValue)).findFirst();
  }

}
