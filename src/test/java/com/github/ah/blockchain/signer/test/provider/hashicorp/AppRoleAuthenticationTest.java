package com.github.ah.blockchain.signer.test.provider.hashicorp;

import com.github.ah.blockchain.signer.provider.hashicorp.connection.HashicorpConnectionBuilder;
import com.github.ah.blockchain.signer.provider.hashicorp.auth.AppRoleAuthentication;
import com.github.ah.blockchain.signer.provider.hashicorp.auth.AuthenticationMethod;
import com.github.ah.blockchain.signer.provider.hashicorp.auth.Token;
import com.github.ah.blockchain.signer.provider.hashicorp.auth.method.AppRole;
import com.github.ah.blockchain.signer.provider.hashicorp.connection.BasicParameters;
import com.github.ah.blockchain.signer.provider.hashicorp.connection.TlsOptions;
import com.github.ah.blockchain.signer.provider.hashicorp.engine.HashicorpKvResolver;
import com.github.ah.blockchain.signer.secrets.SecretContent;
import com.github.ah.blockchain.signer.secrets.SecretId;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class AppRoleAuthenticationTest {

  @Test
  @Disabled
  public void shouldAuthenticateOnOpenVersion() {
    WebClient webClient =
        HashicorpConnectionBuilder.builder()
            .basicParameters(new BasicParameters())
            .tlsOptions(Optional.empty())
            .vertx(Vertx.vertx())
            .build()
            .buildConnection();

    AppRole appRole= new AppRole("a2f0df44-243d-ce3f-91be-0da225e67096", "dc0ac280-c827-853b-c17d-f6251533bd8c");

    AuthenticationMethod authenticationMethod = new AppRoleAuthentication(webClient, appRole);
    Optional<Token> optionalToken = authenticationMethod.authenticate();

    Assertions.assertTrue(optionalToken.isPresent());
    Assertions.assertTrue(optionalToken.get().getValue().length() > 0, "Token value returned is empty");
    Assertions.assertTrue(!optionalToken.get().isExpired(), "Token is expired");
  }

  @Test
  @Disabled
  public void shouldAuthenticateOnHCP() {

    final String hcpHost = "";
    WebClient webClient =
        HashicorpConnectionBuilder.builder()
            .basicParameters(new BasicParameters(hcpHost, 8200))
            .tlsOptions(Optional.of(TlsOptions.builder().verifyHost(true).build()))
            .vertx(Vertx.vertx())
            .build()
            .buildConnection();

    AppRole appRole= new AppRole("55455e76-02f9-0e4e-f0c3-394ab5e34510", "c0ddbc29-af25-0fa4-acab-0f90e45baf2b", Optional.of("admin/blockchain-dev"));

    AuthenticationMethod authenticationMethod = new AppRoleAuthentication(webClient, appRole);
    Optional<Token> optionalToken = authenticationMethod.authenticate();

    Assertions.assertTrue(optionalToken.isPresent());
    Assertions.assertTrue(optionalToken.get().getValue().length() > 0, "Token value returned is empty");
    Assertions.assertTrue(!optionalToken.get().isExpired(), "Token is expired");
  }

  @Test
  @Disabled
  public void shouldRetrieveOnHCP() {

    final String hcpHost = "";
    WebClient webClient =
        HashicorpConnectionBuilder.builder()
            .basicParameters(new BasicParameters(hcpHost, 8200))
            .tlsOptions(Optional.of(TlsOptions.builder().verifyHost(true).build()))
            .vertx(Vertx.vertx())
            .build()
            .buildConnection();

    AppRole appRole= new AppRole("", "", Optional.of("admin/test-namespace"));

    AuthenticationMethod authenticationMethod = new AppRoleAuthentication(webClient, appRole);
    Optional<Token> optionalToken = authenticationMethod.authenticate();

    HashicorpKvResolver kvResolver =
        new HashicorpKvResolver(
            webClient,
            new AppRoleAuthentication(optionalToken,
                webClient,
                appRole));

    SecretId secretId =
        new SecretId("/v1/engine-name/data/privacy-plugin/keys/0x5219bcef87f90ba5eda48a104106f18c2833ce1c2db2bd989e517e595b5dcf02/encryption/0xf979b393b8a26fca67801bb1803bb03afcda6d07", Optional.of("privatekey"));
    Map<String, Object> secrets = new HashMap<>();
    secrets.put("privatekey", "a392604efc2fad9c0b3da43b5f698a2e3f270f170d859912be0d54742275c5f6");
    SecretContent expected = SecretContent.builder().secretId(secretId).secrets(secrets).build();

    SecretContent secretContent = kvResolver.fetchSecret(secretId);
    Assertions.assertNotNull(secretContent);
    Assertions.assertTrue(secretContent.getSecrets().size() == 1);
    Assertions.assertEquals(
        secretContent.getSecrets().get("privateKey"), expected.getSecrets().get("privateKey"));

  }


}
