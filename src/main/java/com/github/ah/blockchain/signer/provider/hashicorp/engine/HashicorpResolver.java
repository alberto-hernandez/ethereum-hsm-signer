package com.github.ah.blockchain.signer.provider.hashicorp.engine;

import com.github.ah.blockchain.signer.provider.hashicorp.secrets.SecretContent;
import com.github.ah.blockchain.signer.provider.hashicorp.secrets.SecretId;
import com.github.ah.blockchain.signer.provider.hashicorp.secrets.SecretValue;

public interface HashicorpResolver {

  SecretValue fetchSecretValue(final SecretId key);

  SecretContent fetchSecret(final SecretId key);

}
