//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.xiaowu.wpywebframework.core.cipher;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.Security;

public class SymmetricSM4Encryptor implements SymmetricEncryptor {
    private static final String ALGORITHM_NAME = "SM4";
    private static final String ALGORITHM_ECB_PKCS5PADDING = "SM4/ECB/PKCS5Padding";
    private static final int DEFAULT_KEY_SIZE = 128;

    public SymmetricSM4Encryptor() {
    }

    public String getKeyPair() throws Exception {
        return this.getKeyPair(128);
    }

    private String getKeyPair(int keySize) throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("SM4", "BC");
        generator.init(keySize, new SecureRandom());
        byte[] encoded = generator.generateKey().getEncoded();
        return Hex.toHexString(encoded);
    }

    public String decrypt(String key, String text) throws Exception {
        Cipher cipher = Cipher.getInstance("SM4", "BC");
        SecretKeySpec spec = new SecretKeySpec(Hex.decode(key), "SM4");
        cipher.init(2, spec);
        byte[] decrypted = cipher.doFinal(Base64.decodeBase64(text));
        return new String(decrypted);
    }

    public String encrypt(String key, String text) throws Exception {
        Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", "BC");
        SecretKeySpec spec = new SecretKeySpec(Hex.decode(key), "SM4");
        cipher.init(1, spec);
        byte[] encrypted = cipher.doFinal(text.getBytes(Charset.defaultCharset()));
        return Base64.encodeBase64String(encrypted);
    }

    public static void main1(String[] args) throws Exception {
        try {
            SymmetricEncryptor encryptor = new SymmetricSM4Encryptor();
            String key = "JeF8U9wHFOMfs2Y8";
            String data = "109.461989,30.926082";
            String text = encryptor.encrypt(Hex.toHexString(key.getBytes()), data);
            System.out.println(text);
            System.out.println(encryptor.decrypt(key, text));
        } catch (Exception var5) {
            Exception e = var5;
            e.printStackTrace();
        }

    }

    static {
        if (null == Security.getProvider("BC")) {
            Security.addProvider(new BouncyCastleProvider());
        }

    }
}
