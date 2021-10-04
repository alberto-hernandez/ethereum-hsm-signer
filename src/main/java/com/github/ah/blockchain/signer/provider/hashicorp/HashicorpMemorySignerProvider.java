package com.github.ah.blockchain.signer.provider.hashicorp;

import com.github.ah.blockchain.signer.SecuredSignerProvider;
import com.github.ah.blockchain.signer.Signer;
import com.github.ah.blockchain.signer.exception.SignerException;
import com.github.ah.blockchain.signer.exception.SignerProviderException;
import com.github.ah.blockchain.signer.secrets.SecretId;
import com.github.ah.blockchain.signer.secrets.SecretValue;
import com.github.ah.blockchain.signer.signers.DefaultSigner;
import java.math.BigInteger;
import java.util.Optional;
import org.apache.tuweni.eth.Address;


public class HashicorpMemorySignerProvider implements SecuredSignerProvider {
  private final HashicorpResolver hashicorpResolver;
  private final Optional<SecretId> secretId;

  private HashicorpMemorySignerProvider(final HashicorpResolver hashicorpResolver,
      final Optional<SecretId> secretId) {
    this.hashicorpResolver = hashicorpResolver;
    this.secretId = secretId;
  }

  public HashicorpMemorySignerProvider(
      final HashicorpResolver hashicorpResolver,
      final SecretId secretId) {
    this (hashicorpResolver, Optional.of(secretId));
  }

  public HashicorpMemorySignerProvider(
      final HashicorpResolver hashicorpResolver) {
    this (hashicorpResolver, Optional.empty());
  }

  @Override
  public Signer get() throws SignerException {
    return get(Optional.empty(), secretId);
  }

  @Override
  public Signer get(final Address address) throws SignerException {
    return get(Optional.of(address), secretId);
  }

  @Override
  public Signer get(final Address address, final SecretId secretId) throws SignerException {
    return get(Optional.of(address), Optional.of(secretId));
  }


  private Signer get(final Optional<Address> address, final Optional<SecretId> secretId) throws SignerException {
    try {
      secretId.orElseThrow(() -> new SignerProviderException("Secret key to Retrieve not defined"));
      SecretValue secretValue = hashicorpResolver.fetchSecretValue(secretId.get());
      BigInteger privatekey = new BigInteger(secretValue.getValue(), 16);

      Signer signer = new DefaultSigner(privatekey);

      // If the Address is empty we skip the verification of the Signer Retrieved
      if (address.isPresent() && !signer.getAddress().equals(address.get())) {
          throw new SignerProviderException(String.format("Private Key Retrieved does not match with expected, requested:{}, retrieved:{}", address.get(), signer.getAddress()));
      }

      return signer;

    } catch (Exception e) {
      throw new SignerProviderException("Unexpected Exception Providing Signer", e);
    }
  }
}
