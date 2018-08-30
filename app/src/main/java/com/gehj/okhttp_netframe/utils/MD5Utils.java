package com.gehj.okhttp_netframe.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    public  static String generateCode(@NonNull String url) {
        if (TextUtils.isEmpty(url))  return  null;
        StringBuffer stringBuffer =  null;
        try {
            stringBuffer =  new StringBuffer();
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            messageDigest.update(url.getBytes());
            byte[]  cipher = messageDigest.digest();//加密的字节;
            //以下加密的字节转化成16进制字符串;

            for (byte b : cipher) {
                String hexString = Integer.toHexString(b & 0xff);
               hexString= hexString.length()==1?"0"+hexString:hexString;//如果少于1位用0补齐;
                stringBuffer.append(hexString);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return  stringBuffer.toString();
    }

}
