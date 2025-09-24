package org.xiaowu.wpywebframework.core.utils;



import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.xiaowu.wpywebframework.core.cipher.*;

import java.io.Closeable;


public class Y {


    public static FastBeanCopier bean = FastBeanCopier.getCopier();

    public static final Cipher cipher = new Cipher() {

        public AsymmetricEncryptor getAsymmetricEncryptor() {
            return new AsymmetricSM2Encryptor();
        }

        public SymmetricEncryptor getSymmetricEncryptor() {
            return new SymmetricSM4Encryptor();
        }

        public DigestEncryptor getDigestEncryptor() {
            return new DigestSM3Encryptor();
        }
    };
    public static Random random = new Random();
    public static ThreadPoolTaskExecutor executor = ServerThreadExecutor.getExecutor();

    public Y() {
    }

    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }

    }

    public static void close(AutoCloseable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }

    }
}
