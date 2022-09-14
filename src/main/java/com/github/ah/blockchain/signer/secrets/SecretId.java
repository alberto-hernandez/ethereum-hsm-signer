package com.github.ah.blockchain.signer.secrets;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class SecretId<S> {
  private final String keyPath;
  private final Optional<String> keyName;
}
