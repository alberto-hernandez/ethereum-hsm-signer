package com.github.ah.blockchain.signer.exception;

public class SignerException extends RuntimeException {

  public SignerException(final String message) {
    super(message);
  }

  public SignerException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
