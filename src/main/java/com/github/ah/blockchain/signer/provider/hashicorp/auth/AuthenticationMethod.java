package com.github.ah.blockchain.signer.provider.hashicorp.auth;

import com.github.ah.blockchain.signer.provider.hashicorp.HashicorpException;
import java.util.Optional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AuthenticationMethod {
  public static final String VAULT_TOKEN_HEADER = "X-Vault-Token";
  public static final String VAULT_NAMESPACE_HEADER = "X-Vault-Namespace";

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
