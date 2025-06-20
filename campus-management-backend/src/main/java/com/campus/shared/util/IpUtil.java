package com.campus.shared.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * IP工具类
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
public class IpUtil {

    /**
     * IP地址正则表达式
     */
    private static final Pattern IP_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );

    /**
     * IPv6地址正则表达式
     */
    private static final Pattern IPV6_PATTERN = Pattern.compile(
        "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|^::1$|^::$"
    );

    /**
     * 验证IP地址格式
     * 
     * @param ip IP地址
     * @return 是否有效
     */
    public static boolean isValidIp(String ip) {
        if (!StringUtils.hasText(ip)) {
            return false;
        }
        return IP_PATTERN.matcher(ip).matches() || IPV6_PATTERN.matcher(ip).matches();
    }

    /**
     * 验证IPv4地址格式
     * 
     * @param ip IP地址
     * @return 是否有效
     */
    public static boolean isValidIpv4(String ip) {
        if (!StringUtils.hasText(ip)) {
            return false;
        }
        return IP_PATTERN.matcher(ip).matches();
    }

    /**
     * 验证IPv6地址格式
     * 
     * @param ip IP地址
     * @return 是否有效
     */
    public static boolean isValidIpv6(String ip) {
        if (!StringUtils.hasText(ip)) {
            return false;
        }
        return IPV6_PATTERN.matcher(ip).matches();
    }

    /**
     * 将IP地址转换为长整型
     * 
     * @param ip IP地址
     * @return 长整型值
     */
    public static long ipToLong(String ip) {
        if (!isValidIpv4(ip)) {
            throw new IllegalArgumentException("Invalid IP address: " + ip);
        }

        String[] parts = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = result * 256 + Integer.parseInt(parts[i]);
        }
        return result;
    }

    /**
     * 将长整型转换为IP地址
     * 
     * @param ip 长整型IP
     * @return IP地址字符串
     */
    public static String longToIp(long ip) {
        return ((ip >> 24) & 0xFF) + "." +
               ((ip >> 16) & 0xFF) + "." +
               ((ip >> 8) & 0xFF) + "." +
               (ip & 0xFF);
    }

    /**
     * 检查IP是否在指定范围内
     * 
     * @param ip 要检查的IP
     * @param startIp 起始IP
     * @param endIp 结束IP
     * @return 是否在范围内
     */
    public static boolean isIpInRange(String ip, String startIp, String endIp) {
        try {
            if (!isValidIpv4(ip) || !isValidIpv4(startIp) || !isValidIpv4(endIp)) {
                return false;
            }

            long ipLong = ipToLong(ip);
            long startLong = ipToLong(startIp);
            long endLong = ipToLong(endIp);

            return ipLong >= startLong && ipLong <= endLong;
        } catch (Exception e) {
            log.warn("检查IP范围失败: ip={}, startIp={}, endIp={}", ip, startIp, endIp, e);
            return false;
        }
    }

    /**
     * 检查IP是否在子网内
     * 
     * @param ip 要检查的IP
     * @param subnet 子网地址
     * @param subnetMask 子网掩码
     * @return 是否在子网内
     */
    public static boolean isIpInSubnet(String ip, String subnet, String subnetMask) {
        try {
            if (!isValidIpv4(ip) || !isValidIpv4(subnet) || !isValidIpv4(subnetMask)) {
                return false;
            }

            long ipLong = ipToLong(ip);
            long subnetLong = ipToLong(subnet);
            long maskLong = ipToLong(subnetMask);

            return (ipLong & maskLong) == (subnetLong & maskLong);
        } catch (Exception e) {
            log.warn("检查IP子网失败: ip={}, subnet={}, mask={}", ip, subnet, subnetMask, e);
            return false;
        }
    }

    /**
     * 检查IP是否在CIDR网段内
     * 
     * @param ip 要检查的IP
     * @param cidr CIDR表示法的网段（如：192.168.1.0/24）
     * @return 是否在网段内
     */
    public static boolean isIpInCidr(String ip, String cidr) {
        try {
            if (!StringUtils.hasText(cidr) || !cidr.contains("/")) {
                return false;
            }

            String[] parts = cidr.split("/");
            if (parts.length != 2) {
                return false;
            }

            String networkIp = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);

            if (!isValidIpv4(ip) || !isValidIpv4(networkIp) || prefixLength < 0 || prefixLength > 32) {
                return false;
            }

            long ipLong = ipToLong(ip);
            long networkLong = ipToLong(networkIp);
            long maskLong = (0xFFFFFFFFL << (32 - prefixLength)) & 0xFFFFFFFFL;

            return (ipLong & maskLong) == (networkLong & maskLong);
        } catch (Exception e) {
            log.warn("检查IP CIDR失败: ip={}, cidr={}", ip, cidr, e);
            return false;
        }
    }

    /**
     * 获取子网掩码对应的CIDR前缀长度
     * 
     * @param subnetMask 子网掩码
     * @return CIDR前缀长度
     */
    public static int subnetMaskToCidr(String subnetMask) {
        if (!isValidIpv4(subnetMask)) {
            throw new IllegalArgumentException("Invalid subnet mask: " + subnetMask);
        }

        long maskLong = ipToLong(subnetMask);
        return Integer.bitCount((int) maskLong);
    }

    /**
     * 将CIDR前缀长度转换为子网掩码
     * 
     * @param prefixLength CIDR前缀长度
     * @return 子网掩码
     */
    public static String cidrToSubnetMask(int prefixLength) {
        if (prefixLength < 0 || prefixLength > 32) {
            throw new IllegalArgumentException("Invalid prefix length: " + prefixLength);
        }

        long maskLong = (0xFFFFFFFFL << (32 - prefixLength)) & 0xFFFFFFFFL;
        return longToIp(maskLong);
    }

    /**
     * 检查是否为内网IP
     * 
     * @param ip IP地址
     * @return 是否为内网IP
     */
    public static boolean isInternalIp(String ip) {
        if (!isValidIpv4(ip)) {
            return false;
        }

        // 10.0.0.0/8
        if (isIpInCidr(ip, "10.0.0.0/8")) {
            return true;
        }

        // 172.16.0.0/12
        if (isIpInCidr(ip, "172.16.0.0/12")) {
            return true;
        }

        // 192.168.0.0/16
        if (isIpInCidr(ip, "192.168.0.0/16")) {
            return true;
        }

        // 127.0.0.0/8 (localhost)
        if (isIpInCidr(ip, "127.0.0.0/8")) {
            return true;
        }

        return false;
    }

    /**
     * 获取IP地址的地理位置信息（需要集成第三方服务）
     * 
     * @param ip IP地址
     * @return 地理位置信息
     */
    public static String getIpLocation(String ip) {
        // 这里可以集成第三方IP地理位置服务
        // 如：高德地图、百度地图、IP2Location等
        if (isInternalIp(ip)) {
            return "内网";
        }
        return "未知";
    }

    /**
     * 解析域名为IP地址
     * 
     * @param hostname 域名
     * @return IP地址
     */
    public static String resolveHostname(String hostname) {
        try {
            InetAddress address = InetAddress.getByName(hostname);
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("解析域名失败: {}", hostname, e);
            return null;
        }
    }

    /**
     * 反向解析IP地址为域名
     * 
     * @param ip IP地址
     * @return 域名
     */
    public static String reverseResolveIp(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.getHostName();
        } catch (UnknownHostException e) {
            log.warn("反向解析IP失败: {}", ip, e);
            return null;
        }
    }
}
