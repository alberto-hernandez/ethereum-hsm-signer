package com.github.ah.blockchain.signer.hashicorp;

import com.github.ah.blockchain.signer.exception.SignerException;

public class HashicorpException extends SignerException {

  public HashicorpException(final String message) {
    super(message);
  }

  public HashicorpException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
