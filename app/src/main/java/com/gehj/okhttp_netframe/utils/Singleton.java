package com.gehj.okhttp_netframe.utils;

import okhttp3.OkHttpClient;

public  class Singleton {
    private static OkHttpClient client = null;

    public static OkHttpClient getOkhttpClientInstance() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }


}
