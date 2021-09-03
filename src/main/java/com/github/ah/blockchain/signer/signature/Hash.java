package com.github.ah.blockchain.signer.signature;

import org.bouncycastle.jcajce.provider.digest.Keccak;

public class Hash {
  public static byte[] sha3(byte[] input) {
    Keccak.DigestKeccak kecc = new Keccak.Digest256();
    kecc.update(input, 0, input.length);
    return kecc.digest();
  }
}
