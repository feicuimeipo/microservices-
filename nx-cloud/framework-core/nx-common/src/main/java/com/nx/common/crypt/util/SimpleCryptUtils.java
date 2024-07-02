package com.nx.common.crypt.util;



import com.nx.common.crypt.Base64;
import com.nx.common.crypt.DES;

import java.nio.charset.StandardCharsets;




/**
 * 简易加密工具
 */
public class SimpleCryptUtils {

    public static final String GLOBAL_CRYPT_KEY;
    static {
        String env =  "default" ;//ResourceContextUtils.getProperty("env");
        String base = SimpleCryptUtils.class.getName() ;//ResourceContextUtils.getProperty("nx.global.crypto.cryptKey", SimpleCryptUtils.class.getName());
        GLOBAL_CRYPT_KEY = SimpleDigestUtils.md5Short(env + base) + SimpleDigestUtils.md5(base).substring(0, 2);
    }

    public static String encrypt(String data) {
        String encode = DES.encrypt(GLOBAL_CRYPT_KEY, data);
        byte[] bytes = Base64.encodeBase64Byte(encode);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String decrypt(String data) {
        byte[] bytes = Base64.decodeBase64Bytes(data);
        data = new String(bytes, StandardCharsets.UTF_8);
        return DES.decrypt(GLOBAL_CRYPT_KEY, data);
    }

    public static String encrypt(String cryptKey, String data) {
        String encode = DES.encrypt(cryptKey, data);
        byte[] bytes = Base64.encodeBase64Byte(encode);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String decrypt(String cryptKey, String data) {
        byte[] bytes = Base64.decodeBase64Bytes(data);
        data = new String(bytes, StandardCharsets.UTF_8);
        return DES.decrypt(cryptKey, data);
    }

}
