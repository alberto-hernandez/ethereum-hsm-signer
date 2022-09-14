package com.github.ah.blockchain.signer.provider.hashicorp.auth.method;

import java.util.Optional;

public interface AuthMethod {

  Optional<String> getNamespace();

}
