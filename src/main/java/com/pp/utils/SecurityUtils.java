package com.pp.utils;



import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 密码工具类
 */
public class SecurityUtils {
    /**
     * 字符转md5
     */
    public static String strToMd5(String str){
        return new String(DigestUtils.md5DigestAsHex(str.getBytes(StandardCharsets.UTF_8)));
    }


    /**
     * 原始字符串是否和md5字符串相等
     * @param str
     * @param md5Str
     * @return
     */
    public static Boolean strEqualsMd5Str(String str,String md5Str){
        return strToMd5(str).equals(md5Str);
    }

    /**
     * base64编码
     */
    public static String stringToBase64(String encodeString){

        String base64EncodeString = Base64.getEncoder().encodeToString(encodeString.getBytes(StandardCharsets.UTF_8));
        return base64EncodeString;

    }

    public static String Base64ToString(String decodeString){
        try {
            byte[] base64DecodeString = Base64.getDecoder().decode(decodeString);
            String decodeStringData = new String(base64DecodeString, "utf-8");

            return decodeStringData;
        }catch (Exception e){
            e.printStackTrace();
           return  e.toString();
        }
    }


}
