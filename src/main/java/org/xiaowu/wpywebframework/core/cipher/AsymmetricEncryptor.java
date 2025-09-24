package org.xiaowu.wpywebframework.core.cipher;

public interface AsymmetricEncryptor {
    KeyPairOfString getKeyPair() throws Exception;

    String decrypt(String privateKey, String encrypted) throws Exception;

    String encrypt(String publicKey, String data) throws Exception;
}
