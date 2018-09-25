package com.gehj.okhttp_netframe.utils.OpenFile;

import android.content.Intent;

import java.io.File;

/**TODO 需要进行重构;
 * Created by lenovo on 2017/3/8.
 * 这个文件主要是截取文件的后缀然后判断后再调用Intent;
 */

public class OpenFile {
    public static Intent openFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        /* 取得扩展名 （后缀名）*/
        int begin = file.getName().lastIndexOf(".") + 1; //.后面的第一个字符
        String fileExtention = file.getName().substring(begin);//后缀名；
        fileExtention = fileExtention.toLowerCase();//把后缀转换成小写
        if (fileExtention.equals("m4a") || fileExtention.equals("mp3") || fileExtention.equals("mid") ||
                fileExtention.equals("xmf") || fileExtention.equals("ogg") || fileExtention.equals("wav")) {
            //如果是视频文件
            return AndoridOPenIntent.getAudioFileIntent(filePath);
        } else if (fileExtention.equals("3gp") || fileExtention.equals("mp4")) {
            return AndoridOPenIntent.getAudioFileIntent(filePath);
        } else if (fileExtention.equals("jpg") || fileExtention.equals("gif") || fileExtention.equals("png") ||
                fileExtention.equals("jpeg") || fileExtention.equals("bmp")) {
            return AndoridOPenIntent.getImageFileIntent(filePath);
        } else if (fileExtention.equals("apk")) {
            return AndoridOPenIntent.getApkFileIntent(filePath);
        } else if (fileExtention.equals("ppt") || fileExtention.equals("pptx")) {
            return AndoridOPenIntent.getPptFileIntent(filePath);
        } else if (fileExtention.equals("xls") || fileExtention.equals("xlsx")) {
            return AndoridOPenIntent.getExcelFileIntent(filePath);
        } else if (fileExtention.equals("doc") || fileExtention.equals("docx")) {
            return AndoridOPenIntent.getWordFileIntent(filePath);
        } else if (fileExtention.equals("pdf")) {
            return AndoridOPenIntent.getPdfFileIntent(filePath);
        } else if (fileExtention.equals("chm")) {
            return AndoridOPenIntent.getChmFileIntent(filePath);
        } else if (fileExtention.equals("txt")) {
            return AndoridOPenIntent.getTextFileIntent(filePath, false);
        } else {
            return AndoridOPenIntent.getAllIntent(filePath);
        }
    }
}
