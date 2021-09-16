package com.github.ah.blockchain.signer.exception;

public class SignerProviderException extends SignerException {

  public SignerProviderException(final String message) {
    super(message);
  }

  public SignerProviderException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
