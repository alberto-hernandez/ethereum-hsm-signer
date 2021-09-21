package com.github.ah.blockchain.signer.provider.hashicorp;

import com.github.ah.blockchain.signer.secrets.SecretContent;
import com.github.ah.blockchain.signer.secrets.SecretId;
import com.github.ah.blockchain.signer.secrets.SecretValue;

public interface HashicorpResolver {

  SecretValue fetchSecretValue(final SecretId key);

  SecretContent fetchSecret(final SecretId key);

}
