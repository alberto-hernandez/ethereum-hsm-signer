package com.github.ah.blockchain.signer.test.hashicorp;

import com.github.ah.blockchain.signer.hashicorp.connection.HashicorpConnectionBuilder;
import com.github.ah.blockchain.signer.hashicorp.HashicorpMemorySignerBuilder;
import com.github.ah.blockchain.signer.hashicorp.auth.AppRoleAuthentication;
import com.github.ah.blockchain.signer.hashicorp.auth.AuthenticationMethod;
import com.github.ah.blockchain.signer.hashicorp.auth.method.AppRole;
import com.github.ah.blockchain.signer.hashicorp.connection.BasicParameters;
import com.github.ah.blockchain.signer.hashicorp.engine.kv.HashicorpKvResolver;
import com.github.ah.blockchain.signer.hashicorp.connection.TlsOptions;
import com.github.ah.blockchain.signer.hashicorp.secrets.SecretId;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.signer.Signer;

class HashicorpMemorySignerBuilderTest {

  @Test
  public void shouldRetrieveSecret() {
    WebClient webClient =
        HashicorpConnectionBuilder.builder()
            .basicParameters(new BasicParameters())
            .tlsOptions(
                Optional.of (TlsOptions.builder().buildwithJks("src/test/resources/sample.jks", "changeit")))
            .vertx(Vertx.vertx())
            .build()
            .buildConnection();

    AppRole appRole= new AppRole("1a8f8e22-ce03-4ec2-1095-09d0bdf038df", "e4a0c14f-32db-3c94-0ad3-a4c68970a9ee");

    AuthenticationMethod authenticationMethod = new AppRoleAuthentication(webClient, appRole);
    HashicorpKvResolver kvResolver = new HashicorpKvResolver(webClient, authenticationMethod);

    SecretId secretId =
        new SecretId("/v1/blockchain/data/network-admin/privatekey", Optional.of("privatekey"));

    HashicorpMemorySignerBuilder hashicorpMemorySignerBuilder =
        HashicorpMemorySignerBuilder.builder()
            .hashicorpKvResolver(kvResolver)
            .secretId(secretId);

    Signer signer = hashicorpMemorySignerBuilder.build();
    Assertions.assertNotNull(signer);
  }
}
