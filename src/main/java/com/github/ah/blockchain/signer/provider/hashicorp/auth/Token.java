package com.github.ah.blockchain.signer.provider.hashicorp.auth;

import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Token {
  private final String value;
  private final String accessor;
  private final ZonedDateTime zonedDateTime;

  @Builder.Default
  private final Optional<String> namespace = Optional.empty();

  public boolean isExpired() {
    return ZonedDateTime.now().isAfter(zonedDateTime);
  }
}
