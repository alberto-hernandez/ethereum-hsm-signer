package com.github.ah.blockchain.signer.provider.hashicorp.engine;

import com.github.ah.blockchain.signer.provider.hashicorp.HashicorpException;
import com.github.ah.blockchain.signer.provider.hashicorp.HashicorpResolver;
import com.github.ah.blockchain.signer.provider.hashicorp.auth.AuthenticationMethod;
import com.github.ah.blockchain.signer.provider.hashicorp.auth.Token;
import com.github.ah.blockchain.signer.provider.hashicorp.connection.HashicorpConnectionBuilder;
import com.github.ah.blockchain.signer.secrets.SecretContent;
import com.github.ah.blockchain.signer.secrets.SecretId;
import com.github.ah.blockchain.signer.secrets.SecretList;
import com.github.ah.blockchain.signer.secrets.SecretValue;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.impl.headers.HeadersMultiMap;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HashicorpKvResolver implements HashicorpResolver {

  private static final String VAULT_ERROR_RESPONSE_KEY = "errors";
  private static final String VAULT_DATA_KEY = "data";
  private static final String VAULT_LIST_KEY = "keys";
  public static final String LIST_HTTP_METHOD = "LIST";

  private WebClient webClient;
  private long requestTimeoutMs;
  private AuthenticationMethod authenticationMethod;

  public HashicorpKvResolver(
      final WebClient webClient, final AuthenticationMethod authenticationMethod) {
    this(webClient, HashicorpConnectionBuilder.DEFAULT_TIMEOUT_MILLISECONDS, authenticationMethod);
  }

  @Override
  public SecretValue fetchSecretValue(final SecretId secretId) {
    if (secretId.getKeyName().isEmpty()) {
      throw new HashicorpException("Requested Secret Value Without key name present");
    }
    // key.getKeyName().orElseThrow(() -> new HashicorpException("Requested Secret Value Without key
    // name present"));
    SecretContent secretContent = this.fetchSecret(secretId);
    Object secretValue = secretContent.getSecrets().get(secretId.getKeyName().get());

    if (secretValue == null) {
      throw new HashicorpException("Requested Secret name does not exist.");
    }

    return SecretValue.builder().secretId(secretId).value(secretValue.toString()).build();
  }

  @Override
  public SecretContent fetchSecret(final SecretId secretId) {
    try {
      Optional<Token> optToken = authenticationMethod.authenticate();
      Token token =
          optToken.orElseThrow(() -> new HashicorpException("Could not get Token with AuthMethod"));

      // Settings the Authentication Header with namespace in case of applied
      HeadersMultiMap headers = new HeadersMultiMap();
      headers.add(AuthenticationMethod.VAULT_TOKEN_HEADER, token.getValue());
      token.getNamespace().ifPresent(h -> headers.set(AuthenticationMethod.VAULT_NAMESPACE_HEADER, h));

      CompletableFuture<SecretContent> future = new CompletableFuture<>();
      this.webClient
          .get(secretId.getKeyPath())
          .putHeaders(headers)
          .timeout(requestTimeoutMs)
          .expect(ResponsePredicate.SC_SUCCESS)
          .send(
              (response) -> {
                if (response.failed()) {
                  future.completeExceptionally(response.cause());
                  return;
                }
                if (response.succeeded()) {
                  Buffer body = response.result().body();
                  Map<String, Object> secrets = Json.decodeValue(body, Map.class);

                  if (secrets.containsKey(VAULT_ERROR_RESPONSE_KEY)) {
                    future.completeExceptionally(new HashicorpException(extractError(secrets)));
                    return;
                  }

                  Map<String, Object> secretValues = extractData(secrets);
                  future.complete(
                      SecretContent.builder().secretId(secretId).secrets(secretValues).build());
                }
              });

      return future.get();
    } catch (Exception ex) {
      throw new HashicorpException("unable to recover the Secret", ex);
    }
  }

  @Override
  public SecretList listSecret(final SecretId secretId) {
    try {
      Optional<Token> optToken = authenticationMethod.authenticate();
      Token token =
          optToken.orElseThrow(() -> new HashicorpException("Could not get Token with AuthMethod"));

      // Settings the Authentication Header with namespace in case of applied
      HeadersMultiMap headers = new HeadersMultiMap();
      headers.add(AuthenticationMethod.VAULT_TOKEN_HEADER, token.getValue());
      token.getNamespace().ifPresent(h -> headers.set(AuthenticationMethod.VAULT_NAMESPACE_HEADER, h));

      CompletableFuture<SecretList> future = new CompletableFuture<>();
      this.webClient
          .request(HttpMethod.valueOf(LIST_HTTP_METHOD), secretId.getKeyPath())
          .putHeaders(headers)
          .timeout(requestTimeoutMs)
          .expect(ResponsePredicate.SC_SUCCESS)
          .send(
              (response) -> {
                if (response.failed()) {
                  future.completeExceptionally(response.cause());
                  return;
                }
                if (response.succeeded()) {
                  Buffer body = response.result().body();
                  Map<String, Object> secrets = Json.decodeValue(body, Map.class);

                  if (secrets.containsKey(VAULT_ERROR_RESPONSE_KEY)) {
                    future.completeExceptionally(new HashicorpException(extractError(secrets)));
                    return;
                  }

                  List<String> secretValues = extractList(secrets);
                  future.complete(
                      SecretList.builder().secretId(secretId).secrets(secretValues).build());
                }
              });

      return future.get();
    } catch (Exception ex) {
      throw new HashicorpException("Unable to recover the Secret", ex);
    }
  }

  private String extractError(Map<String, Object> secrets) {
    Object error = secrets.get(VAULT_ERROR_RESPONSE_KEY);

    if (error instanceof String) {
      return (String) error;
    }
    if (error instanceof List) {
      return ((List<?>) error).get(0).toString();
    }

    return error.toString();
  }

  private Map<String, Object> extractData(Map<String, Object> secrets) {
    // Obtain the Data from the whole response
    Map<String, Object> data = (Map<String, Object>) secrets.get(VAULT_DATA_KEY);

    if (data == null) {
      return Collections.emptyMap();
    }
    return (Map<String, Object>) data.get(VAULT_DATA_KEY);
  }

  private List<String> extractList(Map<String, Object> secrets) {
    // Obtain the Data from the whole response
    Map<String, Object> data = (Map<String, Object>) secrets.get(VAULT_DATA_KEY);

    if (data == null) {
      return Collections.emptyList();
    }
    return (List<String>) data.get(VAULT_LIST_KEY);
  }
}
