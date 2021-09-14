package com.github.ah.blockchain.signer.provider.hashicorp.engine.kv;

import com.github.ah.blockchain.signer.provider.hashicorp.HashicorpException;
import com.github.ah.blockchain.signer.provider.hashicorp.auth.AuthenticationMethod;
import com.github.ah.blockchain.signer.provider.hashicorp.auth.Token;
import com.github.ah.blockchain.signer.provider.hashicorp.connection.HashicorpConnectionBuilder;
import com.github.ah.blockchain.signer.provider.hashicorp.engine.HashicorpResolver;
import com.github.ah.blockchain.signer.provider.hashicorp.secrets.SecretContent;
import com.github.ah.blockchain.signer.provider.hashicorp.secrets.SecretId;
import com.github.ah.blockchain.signer.provider.hashicorp.secrets.SecretValue;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HashicorpKvResolver implements HashicorpResolver {
  private static final String VAULT_TOKEN_HEADER = "X-Vault-Token";
  private static final String VAULT_ERROR_RESPONSE_KEY = "errors";
  private static final String VAULT_DATA_KEY = "data";

  private WebClient webClient;
  private long requestTimeoutMs;
  private AuthenticationMethod authenticationMethod;


  public HashicorpKvResolver(final WebClient webClient, final AuthenticationMethod authenticationMethod) {
    this (webClient, HashicorpConnectionBuilder.DEFAULT_TIMEOUT_MILLISECONDS, authenticationMethod);
  }

  @Override
  public SecretValue fetchSecretValue(final SecretId key) {
    key.getKeyName().orElseThrow(() -> new HashicorpException("Requested Secret Value Without key name present"));
    SecretContent secretContent = this.fetchSecret(key);
    Optional<SecretValue> optSecret =
        secretContent.getSecrets().stream()
            .filter(s -> s.getEntry().equals(key.getKeyName().get()))
            .findFirst();

    return optSecret.orElseThrow(() -> new HashicorpException("Requested Secret name does not exist."));
  }

  @Override
  public SecretContent fetchSecret(SecretId secretId) {
    try {
      Optional<Token> optToken = authenticationMethod.authenticate();
      Token token = optToken.orElseThrow(() -> new HashicorpException("Could not get Token with AuthMethod"));

      CompletableFuture<SecretContent> future = new CompletableFuture<>();
      this.webClient
          .get(secretId.getKeyPath())
          .putHeader(VAULT_TOKEN_HEADER, token.getValue())
          .timeout(requestTimeoutMs)
          .expect(ResponsePredicate.SC_SUCCESS)
          .send(
              (response) -> {
                if (response.failed()) {
                  future.completeExceptionally(new HashicorpException(
                      "Waiting for Hashicorp response was terminated unexpectedly ", response.cause()));
                  return;
                }
                if (response.succeeded()) {
                  SecretContent secretContent = new SecretContent(secretId);
                  Buffer body = response.result().body();
                  Map<String, Object> secrets = Json.decodeValue(body, Map.class);

                  if (secrets.containsKey(VAULT_ERROR_RESPONSE_KEY)) {
                    future.completeExceptionally(new HashicorpException(extractError(secrets)));
                    return;
                  }

                  Map<String, Object> secretValues = extractData(secrets);
                  secretValues.entrySet().stream()
                      .map(e -> new SecretValue(secretId, e.getKey(), e.getValue().toString()))
                      .forEach(sv -> secretContent.addSecret(sv));
                  future.complete(secretContent);
                }
              });

      return future.get();
      } catch (Exception ex) {
        throw new HashicorpException("unable to recover the Secret", ex);
      }
  }


  private String extractError (Map<String, Object> secrets) {
    Object error = secrets.get(VAULT_ERROR_RESPONSE_KEY);

    if (error instanceof String) {
      return (String)error;
    }
    if (error instanceof List) {
      return ((List<?>) error).get(0).toString();
    }

    return error.toString();

  }

  private Map<String, Object> extractData (Map<String, Object> secrets) {
    // Obtain the Data from the whole response
    Map<String, Object> data = (Map<String,Object>)secrets.get(VAULT_DATA_KEY);
    // Obtaint the Value of the data
    return (Map<String,Object>)data.get(VAULT_DATA_KEY);
  }
}
