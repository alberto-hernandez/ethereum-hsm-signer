package com.github.ah.blockchain.signer.provider.hashicorp.connection;

import com.github.ah.blockchain.signer.provider.hashicorp.HashicorpException;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import java.util.Optional;
import lombok.Builder;

@Builder
public class HashicorpConnectionBuilder {
  private Vertx vertx;
  private BasicParameters basicParameters;
  private Optional<TlsOptions> tlsOptions;

  public static final Long DEFAULT_TIMEOUT_MILLISECONDS = 10000L;

  public WebClient buildConnection() {
    try {
      WebClientOptions webClientOptions = new WebClientOptions();

      basicParameters.apply(webClientOptions);
      tlsOptions.ifPresent(tls -> tls.apply(webClientOptions));

      return WebClient.create(this.vertx, webClientOptions);
    } catch (Exception ex) {
      throw new HashicorpException("Unable to initialise connection to hashicorp vault.", ex);
    }
  }
}
