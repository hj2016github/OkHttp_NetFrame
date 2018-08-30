package com.gehj.okhttp_netframe.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class FileStorageManger {//Android7.0以上需要申请动态权限;
    /*--------单例模式--------*/
    private FileStorageManger(){
        if (Holder.fileStorageManger!=null){
            try {
                throw new IllegalAccessException("单例已经被实例化,请不要使用非法反射构造函数");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    private static class Holder{
        private  static  final FileStorageManger fileStorageManger = new FileStorageManger();
    }


    public static FileStorageManger getInstance(){
        return Holder.fileStorageManger;
    }
    /*--------单例模式--------*/
    private Context mContext;


    public void  init(Context context) {
        this.mContext = context;
    }

    public File  getFileByName(String url) {
        File parent;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//是否有sd卡
                parent = mContext.getExternalCacheDir();
        }else {
                parent = mContext.getCacheDir();
        }

        String fileName = MD5Utils.generateCode(url);
        File file = new File(parent,fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  file;
    }
}
