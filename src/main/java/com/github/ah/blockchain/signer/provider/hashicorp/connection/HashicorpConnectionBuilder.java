package com.github.ah.blockchain.signer.provider.hashicorp.connection;

import com.github.ah.blockchain.signer.provider.hashicorp.HashicorpException;
import io.vertx.core.Vertx;
import io.vertx.core.net.ProxyOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import java.util.Optional;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

@Builder
public class HashicorpConnectionBuilder {
  private Vertx vertx;
  private BasicParameters basicParameters;
  @Builder.Default
  private Optional<TlsOptions> tlsOptions = Optional.empty();

  public static final Long DEFAULT_TIMEOUT_MILLISECONDS = 10000L;

  public WebClient buildConnection() {
    try {
      WebClientOptions webClientOptions = new WebClientOptions();

      basicParameters.apply(webClientOptions);
      tlsOptions.ifPresent(tls -> tls.apply(webClientOptions));

      // Apply Proxy Settings
      applySystemProxySettings(webClientOptions, tlsOptions.isPresent());

      return WebClient.create(this.vertx, webClientOptions);
    } catch (Exception ex) {
      throw new HashicorpException("Unable to initialise connection to hashicorp vault.", ex);
    }
  }

  /**
   * Apply Proxy Settings to WebClients Options
   * @param webClientOptions
   */
  private void applySystemProxySettings (final WebClientOptions webClientOptions, final boolean https) {
    String proxyHost;
    Integer proxyPort;

    if (https) {
      proxyHost = System.getProperty("https.proxyHost", "");
      proxyPort = Integer.valueOf(System.getProperty("https.proxyPort", "80"));
    } else {
      proxyHost = System.getProperty("http.proxyHost", "");
      proxyPort = Integer.valueOf(System.getProperty("http.proxyPort", "80"));
    }

    if (!StringUtils.isEmpty(proxyHost)) {
      ProxyOptions proxyOptions = new ProxyOptions();
      proxyOptions.setHost(proxyHost);
      proxyOptions.setPort(proxyPort);
      webClientOptions.setProxyOptions(proxyOptions);
    }
  }
}
