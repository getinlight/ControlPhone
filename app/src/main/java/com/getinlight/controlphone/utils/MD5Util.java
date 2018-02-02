package com.getinlight.controlphone.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by getinlight on 2018/2/2.
 */

public class MD5Util {

    public static String encode(String str) {
        try {
            //1.指定加密算法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //2.哈希过程
            byte[] digest = md5.digest(str.getBytes());
            //3.拼接字符串
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2) {
                    hexString = "0"+hexString;
                }
                sb.append(hexString);
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

}
