package org.xiaowu.wpywebframework.core.cipher;


public interface DigestEncryptor {
    String digest(byte[] data);

    String digest(byte[] key, byte[] data);

    String digest(String data);

    String digest(String key, String data);
}
