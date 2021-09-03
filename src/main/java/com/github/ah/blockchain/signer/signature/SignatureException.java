package com.github.ah.blockchain.signer.signature;

import com.github.ah.blockchain.signer.exception.SignerException;

public class SignatureException extends SignerException {

  public SignatureException(final String message) {
    super(message);
  }

  public SignatureException(final Throwable cause) {
    super(cause);
  }
}
