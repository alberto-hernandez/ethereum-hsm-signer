package com.github.ah.blockchain.signer.provider.hashicorp;

import com.github.ah.blockchain.signer.secrets.SecretContent;
import com.github.ah.blockchain.signer.secrets.SecretId;
import com.github.ah.blockchain.signer.secrets.SecretList;
import com.github.ah.blockchain.signer.secrets.SecretValue;

public interface HashicorpResolver {

  SecretValue fetchSecretValue(final SecretId secretId);

  SecretContent fetchSecret(final SecretId secretId);

  SecretList listSecret (final SecretId secretId);
}
