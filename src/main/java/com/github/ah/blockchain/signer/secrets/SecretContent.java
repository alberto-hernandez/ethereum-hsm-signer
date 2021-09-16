package com.github.ah.blockchain.signer.secrets;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SecretContent {
  private final SecretId secretId;
  private final Map<String, Object> secrets;
}
