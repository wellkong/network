package com.willkong.network.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network.utils
 * @Author: willkong
 * @CreateDate: 2020/5/12 16:50
 * @Description: java类作用描述
 */
public class TecentUtil {
    public static final String secretId = "AKIDnjz8xpfrnajD4jttiwh7z4b7bo52D0ok69js";
    public static final String secretKey = "7ftsdyjglbd4ug2MgauW1Doa1KvrD1wH5s9Tm6u";
    private static final String CONTENT_CHARSET = "UTF-8";
    private static final String HMAC_ALGORITHM = "HmacSHA1";

    private TecentUtil() {

    }

    public static String sign(String secret, String timeStr) {
        String signStr = "date:" + timeStr + "\n" + "source:" + "source";
        try {
            Mac macl = Mac.getInstance(HMAC_ALGORITHM);
            byte[] hash;
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(CONTENT_CHARSET), macl.getAlgorithm());
            macl.init(secretKey);
            hash = macl.doFinal(signStr.getBytes(CONTENT_CHARSET));
            String sig = new String(Base64.encode(hash));
            System.out.println("signValue===>" + sig);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAuthorization(String timeStr) {
        String sig = sign(secretKey,timeStr);
        return "hmac id=\"" + secretId + "\",algorithm=\"hmac-shal\",headers=\"date source\",signature=\"" + sig + "\"";
    }

    public static String getTimeStr() {
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE,dd MM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(cd.getTime());
    }
}
