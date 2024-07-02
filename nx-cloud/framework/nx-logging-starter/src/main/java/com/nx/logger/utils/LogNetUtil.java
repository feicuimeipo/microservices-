package com.nx.logger.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class LogNetUtil {
    public static final String LOCAL_HOST = "127.0.0.1";

    public static String getHostName() {
        String hostname;
        try {
            InetAddress address = InetAddress.getLocalHost();
            hostname = address.getHostName();
            if (hostname == null || "".equals(hostname))
                hostname = address.toString();
        } catch (UnknownHostException ignore) {
            hostname = "127.0.0.1";
        }
        return hostname;
    }

    public static String getHostIp() {
        String hostAddress;
        try {
            InetAddress address = getLocalHostLANAddress();
            hostAddress = address.getHostAddress();
            if (hostAddress == null || "".equals(hostAddress)) {
                hostAddress = address.toString();
            }
        } catch (UnknownHostException ignore) {
            hostAddress = "127.0.0.1";
        }
        return hostAddress;
    }

    private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = ifaces.nextElement();
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            return inetAddr;
                        }
                        if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }


}
