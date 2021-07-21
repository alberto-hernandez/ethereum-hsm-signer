package com.github.ah.blockchain.signer.hashicorp.auth;

import com.github.ah.blockchain.signer.hashicorp.HashicorpException;
import java.util.Optional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AuthenticationMethod {

  private Optional<Token> token;

  protected abstract Optional<Token> auth() throws HashicorpException;

  public final Optional<Token> authenticate() throws HashicorpException {
    synchronized (this) {
      if (token.isPresent() && !token.get().isExpired()) {
        return token;
      }

      token = auth();
      return token;
    }
  }
}
