package com.github.ah.blockchain.signer.test.provider.hashicorp;

import com.github.ah.blockchain.signer.provider.hashicorp.auth.AppRoleAuthentication;
import com.github.ah.blockchain.signer.provider.hashicorp.auth.Token;
import com.github.ah.blockchain.signer.provider.hashicorp.auth.method.AppRole;
import com.github.ah.blockchain.signer.provider.hashicorp.connection.BasicParameters;
import com.github.ah.blockchain.signer.provider.hashicorp.connection.HashicorpConnectionBuilder;
import com.github.ah.blockchain.signer.provider.hashicorp.engine.HashicorpKvResolver;
import com.github.ah.blockchain.signer.secrets.SecretContent;
import com.github.ah.blockchain.signer.secrets.SecretId;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class HashicorpKVResolverTest {
  public static MockWebServer server;
  public static Vertx vertx;

  private static String JSON_PRIVATE_KEY;
  private static String JSON_CONTENT;

  static {
    try {
      JSON_PRIVATE_KEY =
          new String(
              IOUtils.toByteArray(
                  HashicorpKVResolverTest.class
                      .getClassLoader()
                      .getResourceAsStream("hashicorpRS-privatekey.json")));

      JSON_CONTENT =
          new String(
              IOUtils.toByteArray(
                  HashicorpKVResolverTest.class
                      .getClassLoader()
                      .getResourceAsStream("hashicorpRS-genericContent.json")));
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @BeforeAll
  static void setUp() throws IOException {
    server = new MockWebServer();

    server.start(8200);
    vertx = Vertx.vertx();
  }

  @AfterAll
  static void teardown() throws IOException {
    server.close();
  }

  @Test
  public void shouldRetrievePrivateKey() {
    server.enqueue(new MockResponse().setBody(JSON_PRIVATE_KEY).setResponseCode(200));

    SecretId secretId =
        new SecretId("/v1/blockchain/data/blockchain-node/privatekey", Optional.of("privatekey"));
    Map<String, Object> secrets = new HashMap<>();
    secrets.put("privatekey", "a392604efc2fad9c0b3da43b5f698a2e3f270f170d859912be0d54742275c5f6");
    SecretContent expected = SecretContent.builder().secretId(secretId).secrets(secrets).build();

    Token token =
        Token.builder().value("s.abc").zonedDateTime(ZonedDateTime.now().plusDays(1)).build();

    WebClient webclient =
        HashicorpConnectionBuilder.builder()
            .basicParameters(new BasicParameters())
            .tlsOptions(Optional.empty())
            .vertx(vertx)
            .build()
            .buildConnection();

    HashicorpKvResolver kvResolver =
        new HashicorpKvResolver(
            webclient,
            new AppRoleAuthentication(Optional.of(token), webclient, new AppRole("", "")));

    SecretContent secretContent = kvResolver.fetchSecret(secretId);
    Assertions.assertNotNull(secretContent);
    Assertions.assertTrue(secretContent.getSecrets().size() == 1);
    Assertions.assertEquals(
        secretContent.getSecrets().get("privatekey"), expected.getSecrets().get("privatekey"));
  }

  @Test
  public void shouldRetrieveGenericContent() {
    server.enqueue(new MockResponse().setBody(JSON_CONTENT).setResponseCode(200));
    SecretId secretId =
        new SecretId("/v1/blockchain/data/blockchain-node/privatekey", Optional.of("privatekey"));

    Token token =
        Token.builder().value("s.abc").zonedDateTime(ZonedDateTime.now().plusDays(1)).build();

    WebClient webclient =
        HashicorpConnectionBuilder.builder()
            .basicParameters(new BasicParameters())
            .tlsOptions(Optional.empty())
            .vertx(vertx)
            .build()
            .buildConnection();

    HashicorpKvResolver kvResolver =
        new HashicorpKvResolver(
            webclient,
            new AppRoleAuthentication(Optional.of(token), webclient, new AppRole("", "")));

    SecretContent secretContent = kvResolver.fetchSecret(secretId);
    Assertions.assertNotNull(secretContent);
    Assertions.assertTrue(secretContent.getSecrets().size() == 2);
  }
}
