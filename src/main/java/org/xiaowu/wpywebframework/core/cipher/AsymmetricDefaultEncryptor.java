//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.xiaowu.wpywebframework.core.cipher;



import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class AsymmetricDefaultEncryptor implements AsymmetricEncryptor {
    private static final String ALGORITHM_NAME = "RSA";

    public AsymmetricDefaultEncryptor() {
    }

    public KeyPairOfString getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(512);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
        String publicKeyString = Hex.toHexString(publicKey.getEncoded());
        String privateKeyString = Hex.toHexString(privateKey.getEncoded());
        return new KeyPairOfString(publicKeyString, privateKeyString);
    }

    public String decrypt(String privateKey, String encrypted) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(Hex.decode(privateKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(pkcs8EncodedKeySpec5);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(2, key);
        byte[] decrypted = this.doLongerCipherFinal(2, cipher, Base64.decodeBase64(encrypted));
        return new String(decrypted, Charset.defaultCharset());
    }

    public String encrypt(String publicKey, String data) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec2 = new X509EncodedKeySpec(Hex.decode(publicKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(x509EncodedKeySpec2);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(1, key);
        byte[] encrypted = this.doLongerCipherFinal(1, cipher, data.getBytes());
        return Base64.encodeBase64String(encrypted);
    }

    private byte[] doLongerCipherFinal(int opMode, Cipher cipher, byte[] source) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (opMode == 2) {
            out.write(cipher.doFinal(source));
        } else {
            int offset = 0;

            int size;
            for(int totalSize = source.length; totalSize - offset > 0; offset += size) {
                size = Math.min(cipher.getOutputSize(0) - 11, totalSize - offset);
                out.write(cipher.doFinal(source, offset, size));
            }
        }

        out.close();
        return out.toByteArray();
    }

}
