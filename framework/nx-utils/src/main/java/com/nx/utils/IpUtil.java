package com.nx.utils;


import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import static com.nx.utils.string.StringPool.COLON;

public class IpUtil {

	public static final String HEADER_FROWARDED_FOR = "x-forwarded-for";
	public static final String LOCAL_BACK_IP = "127.0.0.1";
	public static final String LOCAL_HOST = "localhost";
	public static final String UNKNOWN = "unknown";
	private static volatile String localIp;
	private static Pattern ipPattern = Pattern.compile("(\\d{1,3}\\.)+\\d{1,3}");
	private static final String[] lanIpPrefixs = new String[] { "127.", "192.168", "10.", "100.64.", "172." };

	public static boolean isIp(String ipAddr) {
		if (StringUtils.isBlank(ipAddr))
			return false;
		return ipPattern.matcher(ipAddr).matches();
	}

	public static boolean isInnerIp(String ipAddr) {
		if (StringUtils.isBlank(ipAddr) || !isIp(ipAddr))
			return false;
		if (ipAddr.startsWith(lanIpPrefixs[0]))
			return true;
		if (ipAddr.startsWith(lanIpPrefixs[1]))
			return true;
		if (ipAddr.startsWith(lanIpPrefixs[2]))
			return true;
		if (ipAddr.startsWith(lanIpPrefixs[3]))
			return true;
		if (ipAddr.startsWith(lanIpPrefixs[4])) {
			try {
				int secondSub = Integer.parseInt(StringUtils.splitByWholeSeparator(ipAddr, ".")[1]);
				return secondSub >= 16 && secondSub <= 31;
			} catch (Exception e) {
			}
		}
		return false;
	}

	/**
	 * <p>
	 * 获取客户端的IP地址的方法是：request.getRemoteAddr()，这种方法在大部分情况下都是有效的。
	 * 但是在通过了Apache,Squid等反向代理软件就不能获取到客户端的真实IP地址了，如果通过了多级反向代理的话，
	 * X-Forwarded-For的值并不止一个，而是一串IP值， 究竟哪个才是真正的用户端的真实IP呢？
	 * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。 例如：X-Forwarded-For：192.168.1.110,
	 * 192.168.1.120, 192.168.1.130, 192.168.1.100 用户真实IP为： 192.168.1.110
	 * </p>
	 *
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {

		String ip = request.getHeader(HEADER_FROWARDED_FOR);
		if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if (ip.equals(LOCAL_BACK_IP)) {
				/** 根据网卡取本机配置的IP */
				ip = getLocalIpAddr();
			}
		}
		/**
		 * 对于通过多个代理的情况， 第一个IP为客户端真实IP,多个IP按照','分割 x-forwarded-for=192.168.2.22,
		 * 192.168.1.92
		 */
		if (ip != null && ip.length() > 15) {
			String[] ips = StringUtils.split(ip, ",");
			for (String _ip : ips) {
				ip = StringUtils.trimToNull(_ip);
				if (!UNKNOWN.equalsIgnoreCase(ip)) {
					return ip;
				}
			}
		}

		// 0:0:0:0:0:0:0:1
		if (ip != null && ip.contains(COLON)) {
			ip = IpUtil.LOCAL_BACK_IP;
		} else if (ip == null) {
			ip = request.getRemoteHost();
		}

		return ip;
	}

	public static String getLocalIpAddr() {
		if (localIp != null)
			return localIp;
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			String currentIp = null;
			outter: while (en.hasMoreElements()) {
				NetworkInterface i = en.nextElement();

				for (Enumeration<InetAddress> en2 = i.getInetAddresses(); en2.hasMoreElements();) {
					InetAddress addr = en2.nextElement();
					if (!addr.isLoopbackAddress()) {
						if (addr instanceof Inet4Address) {
							currentIp = addr.getHostAddress();
							if (isInnerIp(currentIp)) {
								localIp = currentIp;
								break outter;
							}
						}
					}
				}
			}
			if (localIp == null)
				localIp = currentIp;
		} catch (Exception e) {
		}

		if (localIp == null) {
			InetAddress inet = null;
			try {
				inet = InetAddress.getLocalHost();
				localIp = inet.getHostAddress();
			} catch (UnknownHostException e) {
			}
		}
		return localIp;
	}


//	public static void main(String[] args) {
//	}
}
