package com.github.ah.blockchain.signer.provider.hashicorp.connection;

import com.github.ah.blockchain.signer.provider.hashicorp.HashicorpException;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.core.net.TrustOptions;
import io.vertx.ext.web.client.WebClientOptions;

public class TlsOptions implements ConnectionModifier {
  private final TrustOptions trustOptions;
  private final boolean verifyHost;

  private TlsOptions(final TrustOptions trustOptions, final boolean verifyHost) {
    this.trustOptions = trustOptions;
    this.verifyHost = verifyHost;
  }

  @Override
  public void apply(final WebClientOptions webClientOptions) {
    webClientOptions.setSsl(true);
    webClientOptions.setVerifyHost(verifyHost);

    if (trustOptions == null) {
      return;
    }

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
    private TlsTrustType tlsTrustType = TlsTrustType.NO_TRUST;

    public TlsOptionsBuilder verifyHost(final boolean verifyHost) {
      this.verifyHost = verifyHost;
      return this;
    }

    public TlsOptionsBuilder tlsType (final TlsTrustType tlsType) {
      this.tlsTrustType = tlsType;
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
      switch (tlsTrustType) {
        case JKS: return buildwithJks();
        case PKCS12: return buildwithPkcs12();
        case PEM: return buildwithPem();
        case NO_TRUST: return new TlsOptions(null, verifyHost);
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
