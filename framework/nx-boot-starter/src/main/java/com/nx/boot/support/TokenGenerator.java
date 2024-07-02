package com.nx.boot.support;

import com.nx.common.crypt.Base58;
import com.nx.common.crypt.DES;
import com.nx.common.crypt.util.SimpleCryptUtils;
import com.nx.common.crypt.util.SimpleDigestUtils;
import com.nx.common.exception.BaseException;
import org.apache.commons.lang3.StringUtils;
import java.util.Date;
import java.util.UUID;

/**
 * 生成token
 * 
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2017年11月30日
 */
public class TokenGenerator {

    private static final String LINE_THROUGH = "-";
    private static final int EXPIRE = 1000 * 60 * 3;

    public static String generateFrom(String base) {
        String str = SimpleDigestUtils.md5(base);
        return new String(Base58.encode(str.getBytes()));
    }

    public static String generate(String... prefixs) {
        String str = StringUtils.replace(UUID.randomUUID().toString(), LINE_THROUGH, StringUtils.EMPTY);
        if (prefixs != null && prefixs.length > 0 && StringUtils.isNotBlank(prefixs[0])) {
            str = prefixs[0].concat(str);
        }
        return str;
    }

    public static String generateWithSign() {
        return generateWithSign(null);
    }

    /**
     * 生成带签名信息的token
     * 
     * @return
     */
    public static String generateWithSign(String tokenType) {
        String timeString = String.valueOf(System.currentTimeMillis());
        String str = SimpleDigestUtils.md5Short(timeString).concat(timeString);
        if (tokenType == null) {
            return SimpleCryptUtils.encrypt(str);
        }
        String cryptKey = getCryptKey(tokenType);
        return DES.encrypt(cryptKey, str);
    }

    public static void validate(String token, boolean validateExpire) {
        validate(null, token, validateExpire);
    }

    /**
     * 验证带签名信息的token
     */
    public static void validate(String tokenType, String token, boolean validateExpire) {
        long timestamp = 0;
        Date date = new Date();
        try {
            if (tokenType == null) {
                timestamp = Long.parseLong(SimpleCryptUtils.decrypt(token).substring(6));
            } else {
                String cryptKey = getCryptKey(tokenType);
                timestamp = Long.parseLong(DES.decrypt(cryptKey, token).substring(6));
            }
        } catch (Exception e) {
            throw new BaseException(4005, "token格式错误");
        }
        if (validateExpire && date.getTime() - timestamp > EXPIRE) {
            throw new BaseException(4005, "token已过期");
        }
    }

    private static String getCryptKey(String tokenType) {
        if (StringUtils.isNotBlank(tokenType)) {
           String key = AppUtil.getAndValidateProperty(tokenType + ".cryptKey");
           return key;
        } else {
            return SimpleCryptUtils.GLOBAL_CRYPT_KEY;
        }
    }


}
