package org.xiaowu.wpywebframework.core.cipher;

import lombok.Generated;

public class KeyPairOfString {
    private String publicKey;
    private String privateKey;

    public KeyPairOfString(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    @Generated
    public String getPublicKey() {
        return this.publicKey;
    }

    @Generated
    public String getPrivateKey() {
        return this.privateKey;
    }

    @Generated
    public void setPublicKey(final String publicKey) {
        this.publicKey = publicKey;
    }

    @Generated
    public void setPrivateKey(final String privateKey) {
        this.privateKey = privateKey;
    }
}
