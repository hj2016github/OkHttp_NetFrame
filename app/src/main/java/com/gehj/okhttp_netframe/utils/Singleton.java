package com.gehj.okhttp_netframe.utils;

import com.gehj.okhttp_netframe.db.DownloadEntity;

import okhttp3.OkHttpClient;

public  class Singleton {
    private static OkHttpClient client = null;
    private  static DownloadEntity downloadEntity = null;
    public static OkHttpClient getOkhttpClientInstance() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

    public  static  DownloadEntity getDownloadEntityInstance() {
        if (downloadEntity == null) {
            downloadEntity = new DownloadEntity();
        }
        return  downloadEntity;
    }


}
