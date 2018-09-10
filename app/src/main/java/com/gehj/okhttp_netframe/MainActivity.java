package com.gehj.okhttp_netframe;


import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gehj.okhttp_netframe.db.DownloadEntity;
import com.gehj.okhttp_netframe.http.DownLoadCallback;
import com.gehj.okhttp_netframe.http.HttpManger;
import com.gehj.okhttp_netframe.utils.GlobeUrl;
import com.gehj.okhttp_netframe.utils.Singleton;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;

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

        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textView);
        //download_1();//下载显示图片;
        //getData();//普通的get请求

        initDownload();





    }

    private void initDownload() {
        DownloadEntity downloadEntity = Singleton.getDownloadEntityInstance();
        if (!downloadEntity.isSaved())downloadEntity.save();//保存过就不在进行保存;
        boolean success = LitePal.find(DownloadEntity.class,downloadEntity.getId()).isSuccess();
        if (success == false) { //第一次下载
            download_2();//下载apk;
        }else {
            progressBar.setProgress(100);//下载成功后显示进度条;
            //TODO 进行安装等;
        }
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