package com.gehj.okhttp_netframe.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lenovo on 2017/3/8.
 * 主要为判断是否含有中文，以及对中文进行转码与解码
 */

public class UrlUtils {

    //判断网址中是否包含中文
    public static boolean isContainsChinese(String str) {
        boolean flag = false;
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            flag = true;
        }
        return flag;
    }

    //url中文转码encode，加密；
    public static String getURLEncoderString(String str) {
        String ENCODE = "UTF-8";
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = URLEncoder.encode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    //url中文解码decode
    public static String getURLDecoderString(String str) {
        String ENCODE = "UTF-8";
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = URLDecoder.decode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据fileName截取文件名
     * @param url
     * @return
     */
    public static String encodeUrl(String url) {
        String encodeUrl = null;
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("fileName") && isContainsChinese(url)) {
                int startIndex = url.lastIndexOf("=");
                int endIndex = url.lastIndexOf(".");
                String perfix_url = url.substring(0, startIndex + 1);//fileName前面的url,需要截“=”；
                Log.i("prefix_url",perfix_url);
                String chiness = url.substring(startIndex + 1, endIndex);//substring是闭区间,不截“.”；
                Log.i("chiness",chiness);//中文文件名
                String suffix_url = url.substring(endIndex);//"."+后缀；
                String encodeChiness = getURLEncoderString(chiness);
                encodeUrl = perfix_url + encodeChiness + suffix_url;
                return encodeUrl;
            } else {
                return url;
            }
        } else {
            return  "";
        }

    }

}
