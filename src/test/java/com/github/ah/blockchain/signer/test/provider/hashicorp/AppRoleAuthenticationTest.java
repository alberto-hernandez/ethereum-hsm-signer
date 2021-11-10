package com.github.ah.blockchain.signer.test.provider.hashicorp;

import com.github.ah.blockchain.signer.provider.hashicorp.connection.HashicorpConnectionBuilder;
import com.github.ah.blockchain.signer.provider.hashicorp.auth.AppRoleAuthentication;
import com.github.ah.blockchain.signer.provider.hashicorp.auth.AuthenticationMethod;
import com.github.ah.blockchain.signer.provider.hashicorp.auth.Token;
import com.github.ah.blockchain.signer.provider.hashicorp.auth.method.AppRole;
import com.github.ah.blockchain.signer.provider.hashicorp.connection.BasicParameters;
import com.github.ah.blockchain.signer.provider.hashicorp.connection.TlsOptions;
import com.github.ah.blockchain.signer.provider.hashicorp.connection.TlsTypes;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class AppRoleAuthenticationTest {

  @Test
  @Disabled
  public void shouldRetrieveSecret() {
    WebClient webClient =
        HashicorpConnectionBuilder.builder()
            .basicParameters(new BasicParameters())
            .tlsOptions(
                Optional.of (TlsOptions.builder()
                                .tlsType(TlsTypes.JKS)
                                .trustStorePath("src/test/resources/sample.jks")
                                .trustStorePassword("changeit")
                                .build()))
            .vertx(Vertx.vertx())
            .build()
            .buildConnection();

    AppRole appRole= new AppRole("1a8f8e22-ce03-4ec2-1095-09d0bdf038df", "e4a0c14f-32db-3c94-0ad3-a4c68970a9ee");

    AuthenticationMethod authenticationMethod = new AppRoleAuthentication(webClient, appRole);
    Optional<Token> optionalToken = authenticationMethod.authenticate();

    Assertions.assertTrue(optionalToken.isPresent());
    Assertions.assertTrue(optionalToken.get().getValue().length() > 0, "Token value returned is empty");
    Assertions.assertTrue(!optionalToken.get().isExpired(), "Token is expired");
  }
}
