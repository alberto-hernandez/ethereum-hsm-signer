package com.github.ah.blockchain.signer.provider.hashicorp.connection;

import io.vertx.ext.web.client.WebClientOptions;

public class BasicParameters implements ConnectionModifier {
  public static final int DEFAULT_SERVER_PORT = 8200;
  public static final String DEFAULT_SERVER_HOST = "localhost";

  private int port;
  private String host;

  public BasicParameters(final String host, final int port) {
    this.port = port;
    this.host = host;
  }

  public BasicParameters() {
    this (DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);
  }


  @Override
  public void apply(final WebClientOptions webClientOptions) {
    webClientOptions
        .setDefaultHost(host)
        .setDefaultPort(port);
  }

}
