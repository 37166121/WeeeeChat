package com.aliyunm.weeeechat.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class ChecksumUtils {
    private ChecksumUtils() {
        super();
    }

    /**
     * SHA1，即Secure Hash Algorithm 1，生成一个160位的散列值
     */
    public static String sha1(byte[] data) {
        return calculate(data, "SHA-1");
    }

    /**
     * SHA256，SHA2之一，即Secure Hash Algorithm 2，生成一个256位的散列值
     */
    public static String sha256(byte[] data) {
        return calculate(data, "SHA-256");
    }

    /**
     * SHA512，SHA2之一，即Secure Hash Algorithm 2，生成一个512位的散列值
     */
    public static String sha512(byte[] data) {
        return calculate(data, "SHA-512");
    }

    /**
     * MD5，即Message-Digest Algorithm 5，生成一个32位的散列值
     */
    public static String md5(byte[] data) {
        return md5(data, false);
    }

    /**
     * MD5，可以只生成一个16位的散列值
     */
    public static String md5(byte[] data, boolean length16) {
        String temp = calculate(data, "MD5");
        // MD5值一般分16位和32位，默认为32位，16位实际上是从32位字符串中去掉前8位和后8位
        if (length16) {
            return temp.substring(8, 24);
        }
        return temp;
    }

    /**
     * 计算哈希值，算法可以是MD2、MD5、SHA-1、SHA-224、SHA-256、SHA-512等。
     * 支持的算法见 https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest
     */
    public static String calculate(byte[] data, String algorithm) {
        if (data == null || data.length == 0) {
            return "";
        }
        try {
            byte[] bytes = MessageDigest.getInstance(algorithm).digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(String.format("%02x", aByte));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    /**
     * CRC32，即Cyclic Redundancy Check 32
     */
    public static String crc32(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return Long.toHexString(crc32.getValue());
    }
}
