package com.github.ah.blockchain.signer.secrets;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SecretValue {
  private final SecretId secretId;
  private final String entry;
  private final String value;
}
