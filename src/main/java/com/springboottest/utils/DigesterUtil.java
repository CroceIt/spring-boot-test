package com.springboottest.utils;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * @ClassName: DigesterUtil
 * @Description: 基本加密
 * @author: hujunzheng
 * @date: 2017年4月27日 上午11:47:43
 */
public class DigesterUtil {
    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA-1";
    public static final String SHA256 = "SHA-256";
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f'};

    public static String encode(String algorithm, String str) {
        if (str == null)
            return null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(str.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);

        for (int j = 0; j < len; ++j) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4 & 0xF)]);
            buf.append(HEX_DIGITS[(bytes[j] & 0xF)]);
        }
        return Base64.getEncoder().encodeToString(buf.toString().getBytes());
    }

    public static void main(String[] args) {
        System.out.println(DigesterUtil.encode(MD5, "hjzgg"));
    }
}