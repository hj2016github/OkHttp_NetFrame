package com.gehj.okhttp_netframe.http;

import android.support.annotation.NonNull;
import android.util.Log;

import com.gehj.okhttp_netframe.db.DownloadEntity;
import com.gehj.okhttp_netframe.utils.FileStorageManger;
import com.gehj.okhttp_netframe.utils.Singleton;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class OkhttpDownloader {
    public static final int NETWORK_ERROR_CODE = 1;
    private int curProgress;// 进度条的当前刻度值
    private Call call;
    private OkHttpClient mClient;
    private static class Holder {
        private static final OkhttpDownloader ourInstance = new OkhttpDownloader();
    }



    private OkhttpDownloader() {
        if (Holder.ourInstance != null) {
            try {
                throw new IllegalAccessException("The singleton has been instantiated, so don't use illegal reflection constructors");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        mClient = Singleton.getOkhttpClientInstance();
    }

    public static OkhttpDownloader getInstance() {
        return Holder.ourInstance;
    }



    public void asyncRequestDownLoadFile(final String url, final long startPoints, final DownLoadCallback callback) { //异步请求下载文件,处理了文件;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Range", "bytes=" + startPoints + "-")//断点续传要用到的，指示下载的区间
                .build();
        this.call = Singleton.getOkhttpClientInstance().newCall(request);
        this.call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful() && callback != null) {
                    callback.fail(NETWORK_ERROR_CODE, "request failed");
                }
                Log.e(TAG, "onResponse: "+response.headers().toString() );
                downLoadToFile(response, startPoints,url, callback);

            }
        });

    }





    private void downLoadToFile(Response response, long startPoints,String url, DownLoadCallback callback) {//输入流到文件;
        DownloadEntity downloadEntity = getDownloadEntity(url);//单线程下载,只有一个实体类;

        File file = FileStorageManger.getInstance().getFileByName(url);
        RandomAccessFile randomAccessFile;

        long fileLength = response.body().contentLength();

        downloadEntity.setEnd_pos(fileLength);


        byte[] buffer = new byte[1024];//缓存区域;
        int length;
        long readedLength = 0l;//进度读取条;
        //以下输出文件内容;
        InputStream inputStream = response.body().byteStream();
        //FileOutputStream fileOutputStream = null;

        try {
            randomAccessFile = new RandomAccessFile(file,"rwd");
            randomAccessFile.seek(startPoints);

            while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {//inputstream-->buffer
                randomAccessFile.write(buffer,0,length);//buffer-->randomAccessFile=file
                readedLength += length;
                curProgress = (int) (((float)readedLength/fileLength)*100);
                callback.progress(curProgress);
            }
            downloadEntity.setProgress_pos(readedLength);//记录暂停前的数据;
            if (readedLength==fileLength)  downloadEntity.setSuccess(true);
            downloadEntity.update(downloadEntity.getId());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cancelAllCall();
        }
        callback.success(file);
    }

    @NonNull
    private DownloadEntity getDownloadEntity(String url) {
        DownloadEntity downloadEntity = LitePal.find(DownloadEntity.class,Singleton.getDownloadEntityInstance().getId());
        downloadEntity.setStart_pos(0l);
        downloadEntity.setDownload_url(url);
        return downloadEntity;
    }

    public  void cancelAllCall(){
        mClient.dispatcher().cancelAll();

    }

    public void pause() {
        if (call != null) {
            call.cancel();
        }
    }
}