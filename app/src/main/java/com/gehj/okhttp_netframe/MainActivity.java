package com.gehj.okhttp_netframe;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gehj.okhttp_netframe.db.DownloadEntity;
import com.gehj.okhttp_netframe.http.DownLoadCallback;
import com.gehj.okhttp_netframe.http.HttpManger;
import com.gehj.okhttp_netframe.http.OkhttpDownloader;
import com.gehj.okhttp_netframe.utils.FileStorageManger;
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
    private ProgressBar progressBar;
    private TextView textView;
    private Button button;
    private DownloadEntity downloadEntity = Singleton.getDownloadEntityInstance();
    ;
    private long id_download;
    private OkhttpDownloader downloader;
    private File apk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getServiceData();
        //File file = FileStorageManger.getInstance().getFileByName("https://www.baidu.com");

        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        //download_1();//下载显示图片;
        //getData();//普通的get请求

        initDownload();//上来就先进行保存,保证查success字段不是空的,而下载的过程中动态的update相应的字段;
        mangeDownload();//下载完成后控制不能第二次下载;只能进行安装;


    }

    private void initDownload() {
        downloader = OkhttpDownloader.getInstance();
        downloader.setContext(this);
        if (!downloadEntity.isSaved()) {//保存过就不在进行保存;
            downloadEntity.setStart_pos(0l);
            downloadEntity.save();
        }
        id_download = downloadEntity.getId();

    }

    private void mangeDownload() {
        boolean success = LitePal.find(DownloadEntity.class, id_download).isSuccess();
        if (!success) { //第一次下载或者关闭app后重新下载;
            FileStorageManger.getInstance().deleteFile(GlobeUrl.apkUrl);//如果存在文件不管完整还是不完整先进行删除;
            download_app(0l);//下载apk;
        } else {
            button.setEnabled(false);//下载完成设置按钮不可点击;
            button.setClickable(false);
            progressBar.setProgress(100);//下载成功后显示进度条;
             //判断是否已经安装需要反编译后拿到包名,所以如果用户取消安装则什么也不做;以后关闭程序后直接进行下载;
            //File file = FileStorageManger.getInstance().getFileByName(GlobeUrl.apkUrl);//success保证只直接有下载完整才能调这个file;
            //if (LitePal.find(DownloadEntity.class,downloadEntity.getId()).isSuccess()) doInstallAuthority(file);

        }
    }


    private void download_app(long startPoint) {
        downloader.asyncRequestBreakPointDownLoadFile(GlobeUrl.apkUrl, startPoint, new DownLoadCallback() {
            @Override
            public void success(File file) {
                Log.e(TAG, "success: " + file.getName() + ": " + file.length());
                Log.e(TAG, "success: " + file.getAbsolutePath());
               if(LitePal.find(DownloadEntity.class,downloadEntity.getId()).isSuccess())
                   doInstallAuthority(file);

            }


            @Override
            public void fail(int errorCode, String errorMessage) {
                Log.e(TAG, "fail: " + errorMessage);
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

    private void doInstallAuthority(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.REQUEST_INSTALL_PACKAGES}, 1);//sdk>26 android8.0以上需要有REQUEST_INSTALL_PACKAGES权限;
            apk = file; //先传完整的file,才进行回调;
        } else {
            installApk(file);
        }
    }

    private void download_1() {
        OkhttpDownloader.getInstance().asyncRequestBreakPointDownLoadFile(GlobeUrl.picUrl, 0, new DownLoadCallback() {
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

    private void getData() {//下载json等;
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


    public void pause(View view) {
        boolean isCancel = LitePal.find(DownloadEntity.class, id_download).isPause();//初始是false;
        if (isCancel) {
            downloadEntity.setToDefault("isPause");//litepal改回默认值用此方法;
            downloadEntity.update(downloadEntity.getId());
            long startPos = LitePal.find(DownloadEntity.class, id_download).getProgress_pos();
            download_app(startPos + 1);//startPos从+1处开始;
        } else {
            downloadEntity.setPause(true);
            downloadEntity.update(downloadEntity.getId());
            downloader.pause();
        }
    }

    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri =null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri  = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".okhttpdownload", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri =  Uri.fromFile(file);
        }
        intent.setDataAndType(uri,"application/vnd.android.package-archive");
        startActivity(intent);
    }


    /*回调运行时权限*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    installApk(apk);//回调总是在传file(完整)之后,所以apk不会空;
                }
                break;
        }
    }
}