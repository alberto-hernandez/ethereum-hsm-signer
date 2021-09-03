package com.github.ah.blockchain.signer.provider.hashicorp.secrets;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SecretId {
  private final String keyPath;
  private final Optional<String> keyName;
}
