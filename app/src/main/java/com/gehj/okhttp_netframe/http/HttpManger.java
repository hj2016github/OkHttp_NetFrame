package com.gehj.okhttp_netframe.http;

import android.content.Context;

import com.gehj.okhttp_netframe.utils.FileStorageManger;
import com.gehj.okhttp_netframe.utils.Singleton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//底层代码:利用OKhttp进行文件下载,保存到SD卡或者内存中;并且给自己定义的接口传值,方面UI中使用;
public class HttpManger {
    public static final int NETWORK_ERROR_CODE = 1;
    public static final int CONTENT_LENGHT_ERROR_CODE = 2;
    public static final int TASK_RUNNING_ERROR_CODE = 3;
    private Context mContex;
    private OkHttpClient mClient;

    /*-----静态内部类实现 单例模式----------*/
    private static class Holder {
        private static final HttpManger mhttpManger = new HttpManger();
    }

    private HttpManger() {
        if (Holder.mhttpManger != null) {
            try {
                throw new IllegalAccessException("单例已经被实例化,请不要使用非法反射构造函数");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        mClient = Singleton.getOkhttpClientInstance();
    }
    public static HttpManger getInstance() {
        return Holder.mhttpManger;
    }
    /*-----单例模式结束----------*/


    public void init(Context context) {
        mContex = context;
    }

    public void getAsyncRequest(final String url, final Callback callback) {//get请求;
        Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(callback);
    }


    public void postRequst(String url, Callback callback) {//post请求
        FormBody.Builder builder = new FormBody.Builder();
        //数据不对需要重写；
       /* builder.add("pageNo", "1");
        builder.add("pageSize", "20");
        builder.add("serialIds", "2143,3404");
        builder.add("v", "4.0.0");
        builder.addEncoded("s","呵呵");//涉及到中文需要编码;*/

        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        mClient.newCall(request).enqueue(callback);

    }


    //上传文件:协议=enctype:multipart/form-data
    public void uploadFile(String url, Callback callback, String filePath, String fileName) {
        RequestBody requestbody = RequestBody.create(MediaType.parse("image/jepg"), new File(filePath));//上传图片
        //传递参数信息,第一个参数跟服务端商量,第二个参数是上传文件的名字;
        MultipartBody multipartBody = new MultipartBody.Builder().
                setType(MultipartBody.FORM).//上传文字信息
                // addFormDataPart("name","gehj").//提交文字,提交表单;
                        addFormDataPart("filename", fileName, requestbody) //上传图片
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
        mClient.newCall(request).enqueue(callback);
    }

    //下载文件:Transfer-Encoding:chunked:动态网页, 没有content-length字段;下载文件一类才有这个字段
    public void downLoadFile(String url, Callback callback) {//文件下载,未处理文件;
        Request request = new Request.Builder().url(url)
                .addHeader("Range", "bytes=0-7000").build();

        mClient.newCall(request).enqueue(callback);

    }

    public void asyncRequestDownLoadFile(final String url, final DownLoadCallback callback) { //异步请求下载文件,处理了文件;

        final Request request = new Request.Builder().url(url).build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful() && callback != null) {
                    callback.fail(NETWORK_ERROR_CODE, "网络请求失败");
                }

                File file = FileStorageManger.getInstance().getFileByName(url);
                byte[] buffer = new byte[1024 * 500];//缓存区域;
                int length;
                //以下输出文件内容;
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(file);
                    while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {//inputstream-->buffer
                        fileOutputStream.write(buffer, 0, length);//buffer-->fileOutputStream=file
                        fileOutputStream.flush();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                callback.success(file);
            }
        });

    }


    /*以下方法注意需要手动开线程或者配合线程池使用*/
    public Response syncRequestByRange(String url, long start, long end) {//线程池同步请求下载文件;
        Request request = new Request.Builder().url(url).
                addHeader("Range", "bytes=" + start + "-" + end).
                build();


        try {
            return mClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Response getSyncRequest(String url) {//同步请求,配合线程池使用;单独使用需要开线程;
        Request request = new Request.Builder().url(url).
                build();
        try {
            return mClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public  void cancelAllCall(){
        mClient.dispatcher().cancelAll();

    }

}
