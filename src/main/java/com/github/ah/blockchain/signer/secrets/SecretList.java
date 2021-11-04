package com.github.ah.blockchain.signer.secrets;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SecretList {
  private final SecretId secretId;
  private final List<String> secrets;
}
