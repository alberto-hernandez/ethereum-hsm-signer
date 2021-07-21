package com.github.ah.blockchain.signer;

import com.github.ah.blockchain.signer.exception.SignerException;
import org.web3j.crypto.signer.Signer;

public interface SecuredSignerBuilder {

  Signer build() throws SignerException;

}
