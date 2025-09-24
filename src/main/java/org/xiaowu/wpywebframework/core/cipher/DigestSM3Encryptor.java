package org.xiaowu.wpywebframework.core.cipher;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;

public class DigestSM3Encryptor implements DigestEncryptor {
    public DigestSM3Encryptor() {
    }

    public String digest(byte[] data) {
        SM3Digest digest = new SM3Digest();
        digest.update(data, 0, data.length);
        byte[] result = new byte[digest.getDigestSize()];
        digest.doFinal(result, 0);
        return Hex.toHexString(result);
    }

    public String digest(byte[] key, byte[] data) {
        KeyParameter keyParameter = new KeyParameter(key);
        SM3Digest digest = new SM3Digest();
        HMac mac = new HMac(digest);
        mac.init(keyParameter);
        mac.update(data, 0, data.length);
        byte[] result = new byte[mac.getMacSize()];
        mac.doFinal(result, 0);
        return Hex.toHexString(result);
    }

    public String digest(String data) {
        return this.digest(data.getBytes());
    }

    public String digest(String key, String data) {
        return this.digest(key.getBytes(StandardCharsets.UTF_8), data.getBytes(StandardCharsets.UTF_8));
    }
}
