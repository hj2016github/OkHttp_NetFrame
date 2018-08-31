package com.gehj.okhttp_netframe;

import android.app.Application;

import com.gehj.okhttp_netframe.http.HttpManger;
import com.gehj.okhttp_netframe.utils.FileStorageManger;



public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FileStorageManger.getInstance().init(this);//初始化文件管理;
        HttpManger.getInstance().init(this);
    }
}
