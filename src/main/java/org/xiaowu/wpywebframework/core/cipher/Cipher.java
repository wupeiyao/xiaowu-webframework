package org.xiaowu.wpywebframework.core.cipher;

public interface Cipher {
    AsymmetricEncryptor getAsymmetricEncryptor();

    SymmetricEncryptor getSymmetricEncryptor();

    DigestEncryptor getDigestEncryptor();
}
