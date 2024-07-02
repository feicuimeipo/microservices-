package com.nx.boot.launch.listener;

import com.nx.boot.launch.NxLaunchTools;
import com.nx.common.crypt.util.SimpleCryptUtils;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.extension.protocol.StringProtocol;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;
import org.smartboot.socket.transport.WriteBuffer;
import org.springframework.util.ObjectUtils;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

class ApplicationEventManager {

    public enum HeartBeatType{
        readySuccess,readyFail,started,closed,log,destory;
    }

    private static String PublicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIE5ga1nzNfIoj0kFX3wjALSYqxBkAu0eI41GZj8ME9DQ4XPyZYN9vpnxVZEm6hoZdgnjMhSKoofkG6nMsKqFWMCAwEAAQ==";
    private static String encryptLicenseServer ="RfhQjB+Ssc/NODPWnfFTOwYTvRyT4ly1sxdvS4Q6/+ehTYPMEahiK15ZRoHTWJxbYaeirqhjJeQxHSOxFgBrYg==";//rpc.915zb.com;
    private static String encryptPort = "cNWzKsnBpoXGkm8vdGO4VjVp6tCiX7mHYro7oWBzeJhvnVHyE83X0DHnr0iI34DOk+J9coVqXE+MwZjzmje4dQ==";//11203

    private static volatile AioQuickClient aioQuickClient = null;
    private static AioSession session = null;
    private static MessageProcessor<String> stringMessageProcessor =null;
    static {
        initStringMessageProcessor();
    }
    private static void initStringMessageProcessor(){
        stringMessageProcessor = new MessageProcessor<String>() {
            @Override
            public void process(AioSession session, String msg) {
                try{
                    String decryptSocketFlag = ApplicationEventManager.decrypt(msg);
                    if (NxLaunchTools.isNumeric(decryptSocketFlag)){
                        //
                    }
                }catch (Exception e){}
            }
        };
    }

    private static String decrypt(String text) throws Exception {
        return SimpleCryptUtils.decrypt(PublicKey, text);
    }

    private static String getServer() throws Exception {
        return SimpleCryptUtils.decrypt(PublicKey, encryptLicenseServer);

    }

    private static int  generatorPort() throws Exception {
        return Integer.parseInt(SimpleCryptUtils.decrypt(PublicKey, encryptPort));
    }


    private static AioQuickClient getAioQuickClient(MessageProcessor<String> processor) throws Exception {
        if (aioQuickClient==null){
            String server = ApplicationEventManager.getServer();
            int port = ApplicationEventManager.generatorPort();
            aioQuickClient = new AioQuickClient(server, port, new StringProtocol(),processor);;
            getAioQuickClient(processor).start();
        }
        return aioQuickClient;
    }

    private static AioSession getAioSession() throws Exception {
      if (session==null)   {
            session = getAioQuickClient(stringMessageProcessor).getSession();
      }else if (session.isInvalid()){
          getAioQuickClient(stringMessageProcessor).start();
          session = getAioQuickClient(stringMessageProcessor).getSession();
      }
      return session;
    }

    public static void releasebeat(){
        try {
            Thread.currentThread().sleep(100);
        } catch (InterruptedException e) {
        }
        try {
            if (aioQuickClient!=null) {
                aioQuickClient.shutdown();
            }
        }catch (Exception e){}
    }

    private static void heartbeat(byte[] data){
        try {
            AioQuickClient client = getAioQuickClient(stringMessageProcessor);
            session = client.start();
            WriteBuffer writeBuffer = session.writeBuffer();
            writeBuffer.writeInt(data.length);
            writeBuffer.write(data);
            writeBuffer.flush();
        }catch (Exception e){

        }
    }

    public static void  heartbeat(HeartBeatType act){
        try {
            Optional<Inet4Address> localIp = IpUtil.getLocalIp4Address();
            Optional<String> macAddresss = localIp.isPresent() ? IpUtil.getMACAddress(localIp.get()) : Optional.empty();
            StringBuffer sb = new StringBuffer();
            sb.append(macAddresss).append(",").append(localIp).append(act);
            byte[] data = sb.toString().getBytes();
            ApplicationEventManager.heartbeat(data);
        }catch (Exception e){}
    }


    /**
     * 获取本机IP 地址
     *
     * @author nianxiaoling
     * 2021.04.28 11:49
     */
    public static class IpUtil {
        public static final String LOCAL_HOST = "127.0.0.1";

        /**
         * 获取 服务器 HostIp
         *
         * @return HostIp
         */
        public static String getHostIp() {
            String hostAddress;
            try {
                InetAddress address = getLocalHostLANAddress();
                // force a best effort reverse DNS lookup
                hostAddress = address.getHostAddress();
                if (hostAddress == null || "".equals(hostAddress)) {
                    hostAddress = address.toString();
                }
            } catch (UnknownHostException ignore) {
                hostAddress = LOCAL_HOST;
            }
            return hostAddress;
        }

        public static InetAddress getLocalHostLANAddress() throws UnknownHostException {
            try {
                InetAddress candidateAddress = null;
                for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                    NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                    for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                        InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                        if (!inetAddr.isLoopbackAddress()) {

                            if (inetAddr.isSiteLocalAddress()) {
                                return inetAddr;
                            } else if (candidateAddress == null) {
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
        /*
         * 获取本机所有网卡信息   得到所有IP信息
         * @return Inet4Address>
         */
        public static List<Inet4Address> getLocalIp4AddressFromNetworkInterface() throws SocketException {
            List<Inet4Address> addresses = new ArrayList<>(1);

            // 所有网络接口信息
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (ObjectUtils.isEmpty(networkInterfaces)) {
                return addresses;
            }
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                //滤回环网卡、点对点网卡、非活动网卡、虚拟网卡并要求网卡名字是eth或ens开头
                if (!isValidInterface(networkInterface)) {
                    continue;
                }

                // 所有网络接口的IP地址信息
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    // 判断是否是IPv4，并且内网地址并过滤回环地址.
                    if (isValidAddress(inetAddress)) {
                        addresses.add((Inet4Address) inetAddress);
                    }
                }
            }
            return addresses;
        }

        /**
         * 过滤回环网卡、点对点网卡、非活动网卡、虚拟网卡并要求网卡名字是eth或ens开头
         *
         * @param ni 网卡
         * @return 如果满足要求则true，否则false
         */
        private static boolean isValidInterface(NetworkInterface ni) throws SocketException {
            return !ni.isLoopback() && !ni.isPointToPoint() && ni.isUp() && !ni.isVirtual()
                    && (ni.getName().startsWith("eth") || ni.getName().startsWith("ens"));
        }

        /**
         * 判断是否是IPv4，并且内网地址并过滤回环地址.
         */
        private static boolean isValidAddress(InetAddress address) {
            return address instanceof Inet4Address && address.isSiteLocalAddress() && !address.isLoopbackAddress();
        }

        /*
         * 通过Socket 唯一确定一个IP
         * 当有多个网卡的时候，使用这种方式一般都可以得到想要的IP。甚至不要求外网地址8.8.8.8是可连通的
         * @return Inet4Address>
         */
        private static Optional<Inet4Address> getIpBySocket() throws SocketException {
            try (final DatagramSocket socket = new DatagramSocket()) {
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                if (socket.getLocalAddress() instanceof Inet4Address) {
                    return Optional.of((Inet4Address) socket.getLocalAddress());
                }
            } catch (UnknownHostException networkInterfaces) {
                throw new RuntimeException(networkInterfaces);
            }
            return Optional.empty();
        }

        /*
         * 获取本地IPv4地址
         * @return Inet4Address>
         */
        public static Optional<Inet4Address> getLocalIp4Address() throws SocketException {
            final List<Inet4Address> inet4Addresses = getLocalIp4AddressFromNetworkInterface();
            if (inet4Addresses.size() != 1) {
                final Optional<Inet4Address> ipBySocketOpt = getIpBySocket();
                if (ipBySocketOpt.isPresent()) {
                    return ipBySocketOpt;
                } else {
                    return inet4Addresses.isEmpty() ? Optional.empty() : Optional.of(inet4Addresses.get(0));
                }
            }
            return Optional.of(inet4Addresses.get(0));
        }

        /**
         * @Title: getMACAddress
         * @Description: 通过InetAddress对象获取MAC地址
         * @param inetAddress
         * @return
         * @throws Exception String
         * @author: wangyk
         * @date: 2020年11月23日 上午10:24:42
         * @version: 2.0.1
         */
        public static Optional<String> getMACAddress(InetAddress inetAddress) throws Exception {
            // 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
            byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
            // 下面代码是把mac地址拼装成String
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                // mac[i] & 0xFF 是为了把byte转化为正整数
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
            // 把字符串所有小写字母改为大写成为正规的mac地址并返回
            return Optional.of(sb.toString().toUpperCase());
        }


        /**
         * 尝试端口时候被占用
         *
         * @param port 端口号
         * @return 没有被占用：true,被占用：false
         */
        public static boolean tryPort(int port) {
            try (ServerSocket ignore = new ServerSocket(port)) {
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * 获取 服务器 hostname
         *
         * @return hostname
         */
        public static String getHostName() {
            String hostname;
            try {
                InetAddress address = InetAddress.getLocalHost();
                // force a best effort reverse DNS lookup
                hostname = address.getHostName();
                if (hostname == null || "".equals(hostname)) {
                    hostname = address.toString();
                }
            } catch (UnknownHostException ignore) {
                hostname = LOCAL_HOST;
            }
            return hostname;
        }

    }


}
