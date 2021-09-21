package com.github.ah.blockchain.signer;

import com.github.ah.blockchain.signer.exception.SignerException;
import com.github.ah.blockchain.signer.secrets.SecretId;
import org.apache.tuweni.eth.Address;

public interface SecuredSignerProvider {

  Signer get(final Address address) throws SignerException;

  Signer get(final Address address, final SecretId secretId) throws SignerException;

}
