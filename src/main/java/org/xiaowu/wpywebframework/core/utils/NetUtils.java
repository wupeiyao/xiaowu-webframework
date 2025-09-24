package org.xiaowu.wpywebframework.core.utils;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetUtils {
    private static final Logger logger = LoggerFactory.getLogger(NetUtils.class);
    public static final String LOCALHOST = "127.0.0.1";
    public static final String ANY_HOST = "0.0.0.0";
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
    private static volatile InetAddress LOCAL_ADDRESS = null;

    public NetUtils() {
    }

    public static boolean check(String address) {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();

            while(en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface)en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();

                while(enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress)enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        String ip = inetAddress.getHostAddress();
                        if (!ip.contains("::") && !ip.contains("0:0:") && !"127.0.0.1".equals(ip) && ip.equals(address)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception var6) {
            Exception e = var6;
            logger.error("获取ip异常：" + e.getMessage(), e);
        }

        return false;
    }

    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        } else {
            InetAddress localAddress = getLocalAddress0();
            LOCAL_ADDRESS = localAddress;
            return localAddress;
        }
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;

        Throwable e;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable var6) {
            e = var6;
            logger.warn("Failed to retriving ip address, {}", e.getMessage(), e);
        }

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while(interfaces.hasMoreElements()) {
                try {
                    NetworkInterface network = (NetworkInterface)interfaces.nextElement();
                    Enumeration<InetAddress> addresses = network.getInetAddresses();

                    while(addresses.hasMoreElements()) {
                        try {
                            InetAddress address = (InetAddress)addresses.nextElement();
                            if (isValidAddress(address)) {
                                return address;
                            }
                        } catch (Throwable var5) {
                            logger.warn("Failed to retriving ip address, {}",var5.getMessage());
                        }
                    }
                } catch (Throwable var7) {
                    logger.warn("Failed to retriving ip address, {}", var7.getMessage());
                }
            }
        } catch (Throwable var8) {
            e = var8;
            logger.warn("Failed to retriving ip address, {}", e.getMessage(), e);
        }

        logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address != null && !address.isLoopbackAddress()) {
            String name = address.getHostAddress();
            return name != null && !"0.0.0.0".equals(name) && !"127.0.0.1".equals(name) && IP_PATTERN.matcher(name).matches();
        } else {
            return false;
        }
    }
}
