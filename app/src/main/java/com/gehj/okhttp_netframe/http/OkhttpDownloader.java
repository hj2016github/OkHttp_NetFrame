package com.gehj.okhttp_netframe.http;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.gehj.okhttp_netframe.MainActivity;
import com.gehj.okhttp_netframe.R;
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



    private Activity context;
    /*静态内部类-->单例,构造传参有问题*/
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


    public void asyncRequestBreakPointDownLoadFile(final String url, final long startPoints, final DownLoadCallback callback) { //异步请求下载文件,处理了文件;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Range", "bytes=" + startPoints + "-")//断点续传要用到的，指示下载的区间
                .build();
        this.call = mClient.newCall(request);
        this.call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response)  {
                if (!response.isSuccessful() && callback != null) {
                    callback.fail(NETWORK_ERROR_CODE, "request failed");
                }
               // Log.e(TAG, "onResponse: " + response.headers().toString());

                DownloadEntity downloadEntity = getDownloadEntity(url);//单线程下载,只有一个实体类;
                long qEnd_pos =  LitePal.find(DownloadEntity.class,downloadEntity.getId()).getEnd_pos();
                if (qEnd_pos == 0){//第一次下载记录文件总长度,避免暂停后response文件长度的变化;
                    if (response!=null)
                    downloadEntity.setEnd_pos(response.body().contentLength());
                    downloadEntity.update(downloadEntity.getId());
                }

                //服务器不支持断点下载或者有防火墙拦截的情况的下则直接下载
                if (response.code() != 206 || (response!=null&&response.body().contentType().type().contains("text"))) {
                    asyncRequestDownLoadFile(url, callback);
                } else {
                    downLoadToFile(response, startPoints, url, downloadEntity, callback);
                }

            }
        });




    }

    /*服务器无法断点续传,则直接下载头不设置range,如没有断点下载的需求直接用此方法简化下载*/
    private void asyncRequestDownLoadFile(final String url, final DownLoadCallback callback) {
        Request request = new Request.Builder().url(url) .build();
        this.call = mClient.newCall(request);
        this.call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful() && callback != null) {
                    callback.fail(NETWORK_ERROR_CODE, "request failed");
                }
                //被拦截后filelength的长度是2048,以下重新设置fileLength的长度;
                DownloadEntity downloadEntity = getDownloadEntity(url);
                if (response!=null)
                downloadEntity.setEnd_pos(response.body().contentLength());
                downloadEntity.update(downloadEntity.getId());
                long startPoints = 0l;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        context.findViewById(R.id.button).setEnabled(false);
                        Toast.makeText(context, "The environment does not support breakpoint downloads",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                downLoadToFile(response, startPoints, url, downloadEntity,callback);


            }
        });
    }


    private void downLoadToFile(Response response, long startPoints, String url,
                                DownloadEntity downloadEntity,
                                DownLoadCallback callback) {//输入流到文件;

        long  fileLength = downloadEntity.getEnd_pos();
        File file = FileStorageManger.getInstance().getFileByName(url);
        RandomAccessFile randomAccessFile;

        byte[] buffer = new byte[1024];//缓存区域;
        int length;
        long readedLength = startPoints;//进度读取条;
        //以下输出文件内容;
        InputStream inputStream = null;
        if (response!=null){
            inputStream= response.body().byteStream();
        }


        try {
            randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(startPoints);
            while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {//inputstream-->buffer
                randomAccessFile.write(buffer, 0, length);//buffer-->randomAccessFile=file
                readedLength += length;
                curProgress = (int) (((float) readedLength / fileLength) * 100);
                callback.progress(curProgress);
                downloadEntity.setProgress_pos(readedLength);//记录暂停前的数据;
                downloadEntity.update(downloadEntity.getId());
            }

            if (readedLength == fileLength) downloadEntity.setSuccess(true);
            downloadEntity.update(downloadEntity.getId());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

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
        DownloadEntity downloadEntity = LitePal.find(DownloadEntity.class, Singleton.getDownloadEntityInstance().getId());
        downloadEntity.setDownload_url(url);
        return downloadEntity;
    }

    public void cancelAllCall() {
        mClient.dispatcher().cancelAll();

    }

    public void pause() {
        if (call != null) {
            call.cancel();
        }
    }

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }
}