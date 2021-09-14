package com.github.ah.blockchain.signer.test.provider.hashicorp;

import com.github.ah.blockchain.signer.Signer;
import com.github.ah.blockchain.signer.provider.hashicorp.HashicorpMemorySignerProvider;
import com.github.ah.blockchain.signer.provider.hashicorp.engine.kv.HashicorpKvResolver;
import com.github.ah.blockchain.signer.provider.hashicorp.secrets.SecretContent;
import com.github.ah.blockchain.signer.provider.hashicorp.secrets.SecretId;
import com.github.ah.blockchain.signer.provider.hashicorp.secrets.SecretValue;
import java.math.BigInteger;
import java.util.Optional;
import org.apache.tuweni.eth.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class HashicorpMemorySignerBuilderTest {

  @Test
  public void shouldRetrieveSecret() {
    SecretId secretId =
        new SecretId("/v1/blockchain/data/network-admin/privatekey", Optional.of("privatekey"));

    SecretContent secretContent = new SecretContent(secretId);
    secretContent.addSecret(
        new SecretValue(
            secretId,
            "privatekey",
            "a392604efc2fad9c0b3da43b5f698a2e3f270f170d859912be0d54742275c5f6"));

    HashicorpKvResolver kvResolver = mock(HashicorpKvResolver.class);
    when(kvResolver.fetchSecret(any())).thenReturn(secretContent);
    when(kvResolver.fetchSecretValue(any())).thenCallRealMethod();

    HashicorpMemorySignerProvider hashicorpMemorySignerBuilder =
        new HashicorpMemorySignerProvider(kvResolver, secretId);

    Signer signer = hashicorpMemorySignerBuilder.build();
    Assertions.assertNotNull(signer);
    Assertions.assertEquals(signer.getAddress(), Address.fromHexString("0xef678007d18427e6022059dbc264f27507cd1ffc"));
  }
}
