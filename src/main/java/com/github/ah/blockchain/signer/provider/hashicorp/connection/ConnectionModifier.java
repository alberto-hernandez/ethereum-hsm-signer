package com.github.ah.blockchain.signer.provider.hashicorp.connection;

import io.vertx.ext.web.client.WebClientOptions;

public interface ConnectionModifier {

  void apply (WebClientOptions webClientOptions);
}
