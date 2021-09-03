package com.github.ah.blockchain.signer;

import com.github.ah.blockchain.signer.exception.SignerException;

public interface SecuredSignerProvider {
  Signer build() throws SignerException;
}
