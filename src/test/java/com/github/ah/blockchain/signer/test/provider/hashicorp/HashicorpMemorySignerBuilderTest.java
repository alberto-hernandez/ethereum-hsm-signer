package com.github.ah.blockchain.signer.test.provider.hashicorp;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.ah.blockchain.signer.Signer;
import com.github.ah.blockchain.signer.provider.hashicorp.HashicorpMemorySignerProvider;
import com.github.ah.blockchain.signer.provider.hashicorp.engine.HashicorpKvResolver;
import com.github.ah.blockchain.signer.secrets.SecretContent;
import com.github.ah.blockchain.signer.secrets.SecretId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.tuweni.eth.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HashicorpMemorySignerBuilderTest {

  @Test
  public void shouldRetrieveSecret() {
    SecretId secretId =
        new SecretId("/v1/blockchain/data/blockchain-node/privatekey", Optional.of("privatekey"));
    Map<String, Object> secrets = new HashMap<>();
    secrets.put("privatekey", "a392604efc2fad9c0b3da43b5f698a2e3f270f170d859912be0d54742275c5f6");
    SecretContent expected = SecretContent.builder().secretId(secretId).secrets(secrets).build();

    HashicorpKvResolver kvResolver = mock(HashicorpKvResolver.class);
    when(kvResolver.fetchSecret(any())).thenReturn(expected);
    when(kvResolver.fetchSecretValue(any())).thenCallRealMethod();

    HashicorpMemorySignerProvider hashicorpMemorySignerBuilder =
        new HashicorpMemorySignerProvider(kvResolver, secretId);

    Signer signer =
        hashicorpMemorySignerBuilder.get(
            Address.fromHexString("0xef678007d18427e6022059dbc264f27507cd1ffc"));
    Assertions.assertNotNull(signer);
    Assertions.assertEquals(
        signer.getAddress(), Address.fromHexString("0xef678007d18427e6022059dbc264f27507cd1ffc"));
  }
}
