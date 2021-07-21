package com.github.ah.blockchain.signer.hashicorp.connection;

import io.vertx.core.net.JksOptions;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.core.net.TrustOptions;
import io.vertx.ext.web.client.WebClientOptions;

public class TlsOptions implements ConnectionModifier {
  private TrustOptions trustOptions;
  private boolean verifyHost;

  private TlsOptions(final TrustOptions trustOptions, final boolean verifyHost) {
    this.trustOptions = trustOptions;
    this.verifyHost = verifyHost;
  }

  @Override
  public void apply(final WebClientOptions webClientOptions) {
    if (trustOptions == null) {
      return;
    }

    webClientOptions.setSsl(true);
    webClientOptions.setVerifyHost(verifyHost);

    if (trustOptions instanceof JksOptions) {
      webClientOptions.setTrustStoreOptions((JksOptions) trustOptions);
    } else {
      if (trustOptions instanceof PfxOptions) {
        webClientOptions.setPfxTrustOptions((PfxOptions) trustOptions);
      } else {
        if (trustOptions instanceof PemTrustOptions) {
          webClientOptions.setPemTrustOptions((PemTrustOptions) trustOptions);
        } else {
          webClientOptions.setTrustOptions(trustOptions);
        }
      }
    }
  }

  public static TlsOptionsBuilder builder() {
    return new TlsOptionsBuilder();
  }

  public static class TlsOptionsBuilder {
    private boolean verifyHost;

    public TlsOptionsBuilder verifyHost(final boolean verifyHost) {
      this.verifyHost = verifyHost;
      return this;
    }

    public TlsOptions buildwithJks(final String trustStorePath, final String password) {
      JksOptions jksOptions = new JksOptions();
      jksOptions.setPath(trustStorePath);
      jksOptions.setPassword(password);
      return new TlsOptions(jksOptions, verifyHost);
    }

    public TlsOptions buildwithPkcs12(final String trustStorePath, final String password) {
      PfxOptions pfxOptions = new PfxOptions();
      pfxOptions.setPath(trustStorePath);
      pfxOptions.setPassword(password);
      return new TlsOptions(pfxOptions, verifyHost);
    }

    public TlsOptions buildwithPem(final String certPath) {
      PemTrustOptions pemTrustOptions = new PemTrustOptions();
      pemTrustOptions.addCertPath(certPath);
      return new TlsOptions(pemTrustOptions, verifyHost);
    }
  }
}
