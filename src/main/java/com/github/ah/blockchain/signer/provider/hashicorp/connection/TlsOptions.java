package com.github.ah.blockchain.signer.provider.hashicorp.connection;

import com.github.ah.blockchain.signer.provider.hashicorp.HashicorpException;
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
    private String trustStorePath;
    private String trustStorePassword;
    private TlsTypes tlsType;

    public TlsOptionsBuilder verifyHost(final boolean verifyHost) {
      this.verifyHost = verifyHost;
      return this;
    }

    public TlsOptionsBuilder tlsType (final TlsTypes tlsType) {
      this.tlsType = tlsType;
      return this;
    }

    public TlsOptionsBuilder trustStorePath (final String trustStorePath) {
      this.trustStorePath = trustStorePath;
      return this;
    }

    public TlsOptionsBuilder trustStorePassword (final String trustStorePassword) {
      this.trustStorePassword = trustStorePassword;
      return this;
    }

    public TlsOptions build () {
      switch (tlsType) {
        case JKS: return buildwithJks();
        case PKCS12: return buildwithPkcs12();
        case PEM: return buildwithPem();
      }

      throw new HashicorpException("TlsOption not defined");
    }


    private TlsOptions buildwithJks() {
      JksOptions jksOptions = new JksOptions();
      jksOptions.setPath(trustStorePath);
      jksOptions.setPassword(trustStorePassword);
      return new TlsOptions(jksOptions, verifyHost);
    }

    private TlsOptions buildwithPkcs12() {
      PfxOptions pfxOptions = new PfxOptions();
      pfxOptions.setPath(trustStorePath);
      pfxOptions.setPassword(trustStorePassword);
      return new TlsOptions(pfxOptions, verifyHost);
    }

    private TlsOptions buildwithPem() {
      PemTrustOptions pemTrustOptions = new PemTrustOptions();
      pemTrustOptions.addCertPath(trustStorePath);
      return new TlsOptions(pemTrustOptions, verifyHost);
    }
  }
}
