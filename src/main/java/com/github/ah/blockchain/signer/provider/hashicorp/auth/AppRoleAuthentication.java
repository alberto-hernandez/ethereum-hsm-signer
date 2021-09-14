package com.github.ah.blockchain.signer.provider.hashicorp.auth;

import com.github.ah.blockchain.signer.provider.hashicorp.HashicorpException;
import com.github.ah.blockchain.signer.provider.hashicorp.auth.method.AppRole;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AppRoleAuthentication extends AuthenticationMethod {

  private static final String AUTH_PATH = "/v1/auth/approle/login";
  public static final String ROLE_ID_KEY = "role_id";
  public static final String SECRET_ID_KEY = "secret_id";
  public static final String AUTH = "auth";
  public static final String CLIENT_TOKEN = "client_token";
  public static final String ACCESSOR = "accessor";
  public static final String LEASE_DURATION = "lease_duration";

  private final WebClient webClient;
  private final AppRole appRole;

  public AppRoleAuthentication(
      final Optional<Token> token, final WebClient webClient, final AppRole appRole) {
    super(token);
    this.webClient = webClient;
    this.appRole = appRole;
  }

  public AppRoleAuthentication(final WebClient webClient, final AppRole appRole) {
    super(Optional.empty());
    this.webClient = webClient;
    this.appRole = appRole;
  }

  @Override
  public Optional<Token> auth() throws HashicorpException {

    try {
      CompletableFuture<Token> future = new CompletableFuture<>();

      this.webClient
          .post(AUTH_PATH)
          .timeout(10000)
          .expect(ResponsePredicate.SC_SUCCESS)
          .sendJsonObject(
              new JsonObject()
                  .put(ROLE_ID_KEY, appRole.getRoleId())
                  .put(SECRET_ID_KEY, appRole.getSecretId()),
              (response) -> {
                if (response.failed()) {
                  future.completeExceptionally(
                      new HashicorpException(
                          "Waiting for Hashicorp response was terminated unexpectedly ",
                          response.cause()));
                  return;
                }
                if (response.succeeded()) {
                  Map<String, Object> auth = extractResponseContent(response.result().body(), AUTH);

                  Token token =
                      Token.builder()
                          .value((String) auth.get(CLIENT_TOKEN))
                          .accessor((String) auth.get(ACCESSOR))
                          .zonedDateTime(
                              ZonedDateTime.now().plusSeconds((Integer) auth.get(LEASE_DURATION)))
                          .build();

                  future.complete(token);
                }
              });

      return Optional.of(future.get());
    } catch (Exception ex) {
      throw new HashicorpException("Error login to the Vault", ex);
    }
  }

  private static Map<String, Object> extractResponseContent(
      final Buffer buffer, final String contentKey) {
    Map<String, Object> body = Json.decodeValue(buffer, Map.class);

    return (Map<String, Object>) body.get(contentKey);
  }
}
