package com.gehj.okhttp_netframe;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gehj.okhttp_netframe.http.DownLoadCallback;
import com.gehj.okhttp_netframe.http.HttpManger;
import com.gehj.okhttp_netframe.http.TransVal;
import com.gehj.okhttp_netframe.utils.GlobeUrl;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Data";
    private ImageView imageView;
    private int count;
    private ProgressBar progressBar;
    private TextView textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getServiceData();
        //File file = FileStorageManger.getInstance().getFileByName("https://www.baidu.com");
        //下载:
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textView);
        //download_1();//下载显示图片;
        //getData();//普通的get请求
        download_2();//下载apk;


    }

    private void download_2(){
        HttpManger.getInstance().asyncRequestDownLoadFile(GlobeUrl.apkUrl, new DownLoadCallback() {
            @Override
            public void success(File file) {
                Log.e(TAG, "success: "+file.getName()+": "+file.length() );
                Log.e(TAG, "success: "+file.getAbsolutePath() );
            }

            @Override
            public void fail(int errorCode, String errorMessage) {
                Log.e(TAG, "fail: "+errorMessage );
            }

            @Override
            public void progress(final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
            }
        });
    }

    private void download_1() {
        HttpManger.getInstance().asyncRequestDownLoadFile(GlobeUrl.picUrl, new DownLoadCallback() {
            @Override
            public void success(File file) {
                final Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }

            @Override
            public void fail(int errorCode, String errorMessage) {
                System.out.println(errorCode + ":" + errorMessage);

            }

            @Override
            public void progress(int progress) {

            }
        });
    }

    private void getData() {
            HttpManger.getInstance().getAsyncRequest(GlobeUrl.url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                        final String text = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(text);
                            }
                        });

                }
            });



    }


}