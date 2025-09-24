//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.xiaowu.wpywebframework.core.cipher;

import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AsymmetricSM2Encryptor implements AsymmetricEncryptor {
    private final Logger logger = LoggerFactory.getLogger(AsymmetricSM2Encryptor.class);
    public final String CRYPTO_NAME_SM2 = "sm2p256v1";
    public int CIPHER_MODE_BC = 0;
    public int CIPHER_MODE_NORM = 1;

    public AsymmetricSM2Encryptor() {
    }

    public KeyPairOfString getKeyPair() throws Exception {
        return this.getKeyPair(false);
    }

    public KeyPairOfString getKeyPair(boolean compressed) {
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(), sm2ECParameters.getG(), sm2ECParameters.getN());
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();

        try {
            keyPairGenerator.init(new ECKeyGenerationParameters(domainParameters, SecureRandom.getInstance("SHA1PRNG")));
        } catch (NoSuchAlgorithmException var12) {
            NoSuchAlgorithmException e = var12;
            this.logger.error("生成公私钥对时出现异常:", e);
        }

        AsymmetricCipherKeyPair asymmetricCipherKeyPair = keyPairGenerator.generateKeyPair();
        ECPublicKeyParameters publicKeyParameters = (ECPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        ECPoint ecPoint = publicKeyParameters.getQ();
        String publicKey = Hex.toHexString(ecPoint.getEncoded(compressed));
        ECPrivateKeyParameters privateKeyParameters = (ECPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        BigInteger intPrivateKey = privateKeyParameters.getD();
        String privateKey = intPrivateKey.toString(16);
        this.logger.debug("\npublicKey：{}\nprivateKey：{}", publicKey, privateKey);
        return new KeyPairOfString(publicKey, privateKey);
    }

    public String encrypt(String publicKey, String data) {
        return this.encrypt(publicKey, data, this.CIPHER_MODE_BC);
    }

    public String encrypt(String publicKey, String data, int cipherMode) {
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(), sm2ECParameters.getG(), sm2ECParameters.getN());
        ECPoint pukPoint = sm2ECParameters.getCurve().decodePoint(Hex.decode(publicKey));
        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(pukPoint, domainParameters);
        EngineExtend sm2Engine = new EngineExtend();
        sm2Engine.init(true, cipherMode, new ParametersWithRandom(publicKeyParameters, new SecureRandom()));
        byte[] arrayOfBytes = null;

        try {
            byte[] in = data.getBytes();
            arrayOfBytes = sm2Engine.processBlock(in, 0, in.length);
        } catch (Exception var11) {
            Exception e = var11;
            this.logger.error("SM2加密时出现异常:{}", e.getMessage(), e);
        }

        return Hex.toHexString(arrayOfBytes);
    }

    public String decrypt(String privateKey, String encrypted) {
        return this.decrypt(privateKey, encrypted, this.CIPHER_MODE_BC);
    }

    public String decrypt(String privateKey, String encrypted, int cipherMode) {
        if (!encrypted.startsWith("04")) {
            encrypted = "04" + encrypted;
        }

        byte[] cipherDataByte = Hex.decode(encrypted);
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(), sm2ECParameters.getG(), sm2ECParameters.getN());
        BigInteger privateKeyD = new BigInteger(privateKey, 16);
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKeyD, domainParameters);
        EngineExtend sm2Engine = new EngineExtend();
        sm2Engine.init(false, cipherMode, privateKeyParameters);
        String result = "";

        try {
            byte[] arrayOfBytes = sm2Engine.processBlock(cipherDataByte, 0, cipherDataByte.length);
            return new String(arrayOfBytes);
        } catch (Exception var12) {
            Exception e = var12;
            this.logger.error("SM2解密时出现异常:{}", e.getMessage(), e);
            return result;
        }
    }

    public class EngineExtend {
        private final Digest digest;
        private boolean forEncryption;
        private ECKeyParameters ecKey;
        private ECDomainParameters ecParams;
        private int curveLength;
        private SecureRandom random;
        private int cipherMode;

        public EngineExtend() {
            this(new SM3Digest());
        }

        public EngineExtend(Digest digest) {
            this.digest = digest;
        }

        public void setCipherMode(int cipherMode) {
            this.cipherMode = cipherMode;
        }

        public void init(boolean forEncryption, CipherParameters param) {
            this.init(forEncryption, AsymmetricSM2Encryptor.this.CIPHER_MODE_NORM, param);
        }

        public void init(boolean forEncryption, int cipherMode, CipherParameters param) {
            this.forEncryption = forEncryption;
            this.cipherMode = cipherMode;
            if (forEncryption) {
                ParametersWithRandom rParam = (ParametersWithRandom)param;
                this.ecKey = (ECKeyParameters)rParam.getParameters();
                this.ecParams = this.ecKey.getParameters();
                ECPoint s = ((ECPublicKeyParameters)this.ecKey).getQ().multiply(this.ecParams.getH());
                if (s.isInfinity()) {
                    throw new IllegalArgumentException("invalid key: [h]Q at infinity");
                }

                this.random = rParam.getRandom();
            } else {
                this.ecKey = (ECKeyParameters)param;
                this.ecParams = this.ecKey.getParameters();
            }

            this.curveLength = (this.ecParams.getCurve().getFieldSize() + 7) / 8;
        }

        public byte[] processBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
            return this.forEncryption ? this.encrypt(in, inOff, inLen) : this.decrypt(in, inOff, inLen);
        }

        private byte[] encrypt(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
            byte[] c2 = new byte[inLen];
            System.arraycopy(in, inOff, c2, 0, c2.length);

            byte[] c1;
            ECPoint kPB;
            do {
                BigInteger k = this.nextK();
                ECPoint c1P = this.ecParams.getG().multiply(k).normalize();
                c1 = c1P.getEncoded(false);
                kPB = ((ECPublicKeyParameters)this.ecKey).getQ().multiply(k).normalize();
                this.kdf(this.digest, kPB, c2);
            } while(this.notEncrypted(c2, in, inOff));

            byte[] c3 = new byte[this.digest.getDigestSize()];
            this.addFieldElement(this.digest, kPB.getAffineXCoord());
            this.digest.update(in, inOff, inLen);
            this.addFieldElement(this.digest, kPB.getAffineYCoord());
            this.digest.doFinal(c3, 0);
            return this.cipherMode == AsymmetricSM2Encryptor.this.CIPHER_MODE_NORM ? Arrays.concatenate(c1, c3, c2) : Arrays.concatenate(c1, c2, c3);
        }

        private byte[] decrypt(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
            byte[] c1 = new byte[this.curveLength * 2 + 1];
            System.arraycopy(in, inOff, c1, 0, c1.length);
            ECPoint c1P = this.ecParams.getCurve().decodePoint(c1);
            ECPoint s = c1P.multiply(this.ecParams.getH());
            if (s.isInfinity()) {
                throw new InvalidCipherTextException("[h]C1 at infinity");
            } else {
                c1P = c1P.multiply(((ECPrivateKeyParameters)this.ecKey).getD()).normalize();
                byte[] c2 = new byte[inLen - c1.length - this.digest.getDigestSize()];
                if (this.cipherMode == AsymmetricSM2Encryptor.this.CIPHER_MODE_BC) {
                    System.arraycopy(in, inOff + c1.length, c2, 0, c2.length);
                } else {
                    System.arraycopy(in, inOff + c1.length + this.digest.getDigestSize(), c2, 0, c2.length);
                }

                this.kdf(this.digest, c1P, c2);
                byte[] c3 = new byte[this.digest.getDigestSize()];
                this.addFieldElement(this.digest, c1P.getAffineXCoord());
                this.digest.update(c2, 0, c2.length);
                this.addFieldElement(this.digest, c1P.getAffineYCoord());
                this.digest.doFinal(c3, 0);
                int check = 0;
                int i;
                if (this.cipherMode == AsymmetricSM2Encryptor.this.CIPHER_MODE_BC) {
                    for(i = 0; i != c3.length; ++i) {
                        check |= c3[i] ^ in[c1.length + c2.length + i];
                    }
                } else {
                    for(i = 0; i != c3.length; ++i) {
                        check |= c3[i] ^ in[c1.length + i];
                    }
                }

                this.clearBlock(c1);
                this.clearBlock(c3);
                if (check != 0) {
                    this.clearBlock(c2);
                    throw new InvalidCipherTextException("invalid cipher text");
                } else {
                    return c2;
                }
            }
        }

        private boolean notEncrypted(byte[] encData, byte[] in, int inOff) {
            for(int i = 0; i != encData.length; ++i) {
                if (encData[i] != in[inOff]) {
                    return false;
                }
            }

            return true;
        }

        private void kdf(Digest digest, ECPoint c1, byte[] encData) {
            int ct = 1;
            int v = digest.getDigestSize();
            byte[] buf = new byte[digest.getDigestSize()];
            int off = 0;

            for(int i = 1; i <= (encData.length + v - 1) / v; ++i) {
                this.addFieldElement(digest, c1.getAffineXCoord());
                this.addFieldElement(digest, c1.getAffineYCoord());
                digest.update((byte)(ct >> 24));
                digest.update((byte)(ct >> 16));
                digest.update((byte)(ct >> 8));
                digest.update((byte)ct);
                digest.doFinal(buf, 0);
                if (off + buf.length < encData.length) {
                    this.xor(encData, buf, off, buf.length);
                } else {
                    this.xor(encData, buf, off, encData.length - off);
                }

                off += buf.length;
                ++ct;
            }

        }

        private void xor(byte[] data, byte[] kdfOut, int dOff, int dRemaining) {
            for(int i = 0; i != dRemaining; ++i) {
                data[dOff + i] ^= kdfOut[i];
            }

        }

        private BigInteger nextK() {
            int qBitLength = this.ecParams.getN().bitLength();

            BigInteger k;
            do {
                do {
                    k = new BigInteger(qBitLength, this.random);
                } while(k.equals(ECConstants.ZERO));
            } while(k.compareTo(this.ecParams.getN()) >= 0);

            return k;
        }

        private void addFieldElement(Digest digest, ECFieldElement v) {
            byte[] p = BigIntegers.asUnsignedByteArray(this.curveLength, v.toBigInteger());
            digest.update(p, 0, p.length);
        }

        private void clearBlock(byte[] block) {
            for(int i = 0; i != block.length; ++i) {
                block[i] = 0;
            }

        }
    }
}
