package com.gehj.okhttp_netframe.http;

import java.io.File;

public interface DownLoadCallback {//处理网络请求成功后的回调;
        void success(File file);
        void fail(int errorCode, String errorMessage);
        void progress(int progress);
}
