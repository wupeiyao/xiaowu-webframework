package org.xiaowu.wpywebframework.core.cipher;

public interface SymmetricEncryptor {
    String getKeyPair() throws Exception;

    String decrypt(String key, String text) throws Exception;

    String encrypt(String key, String text) throws Exception;
}
