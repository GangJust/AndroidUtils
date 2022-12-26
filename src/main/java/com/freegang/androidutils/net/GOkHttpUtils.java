package com.freegang.androidutils.net;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by fyc on 2017/7/12.
 * 需要 OkHttp库支持, 如果依赖冲突, 可直接复制该类使用
 */

public class GOkHttpUtils {
    private static final String TAG = "GOkHttpUtil";

    public final static int READ_TIMEOUT = 100;
    public final static int CONNECT_TIMEOUT = 60;
    public final static int WRITE_TIMEOUT = 60;
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_IMG_PNG = MediaType.parse("image/png");
    private static final byte[] LOCKER = new byte[0];
    private static GOkHttpUtils mInstance;
    private final OkHttpClient mOkHttpClient;

    /**
     * 单例模式获取OkHttpUtil
     */
    public static GOkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (LOCKER) {
                if (mInstance == null) {
                    mInstance = new GOkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    private GOkHttpUtils() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        //读取超时
        clientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        //连接超时
        clientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        //写入超时
        clientBuilder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        //支持HTTPS请求，跳过证书验证
        clientBuilder.sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts());
        clientBuilder.hostnameVerifier((hostname, session) -> true);
        
        mOkHttpClient = clientBuilder.build();
    }

    /**
     * get请求，同步方式，获取网络数据
     *
     * @param url url
     * @return return
     */
    public Response getData(String url) {
        return execute(url, "GET", null);
    }

    /**
     * post请求，同步方式，提交数据获取响应
     *
     * @param url        url
     * @param bodyParams Request Body
     * @return return
     */
    public Response postData(String url, Map<String, String> bodyParams) {
        return postData(url, buildFormBody(bodyParams));
    }

    /**
     * post请求，同步方式，提交数据获取响应
     *
     * @param url  url
     * @param body body
     * @return return
     */
    public Response postData(String url, RequestBody body) {
        return execute(url, "POST", body);
    }

    /**
     * post请求，同步方式，提交JSON数据获取响应
     *
     * @param url  url
     * @param json Request Json
     * @return return
     */
    public Response postJson(String url, String json) {
        return execute(url, "POST", buildJsonBody(json));
    }

    public Response execute(String url, String method, RequestBody requestBody) {
        return execute(url, method, Headers.of(), requestBody);
    }

    /**
     * 同步请求方式, 直接请求网络, 需要手动创建子线程执行.
     *
     * @param url         url
     * @param method      Method
     * @param headers     Request Headers
     * @param requestBody Request Body
     * @return return
     */
    public Response execute(String url, String method, Headers headers, RequestBody requestBody) {
        Request request = new Request.Builder()
                .headers(headers)
                .method(method, requestBody)
                .url(url)
                .build();
        //将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //执行Call，得到response
        try (Response response = call.execute()) {
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    ////

    /**
     * get请求，异步方式，获取响应
     *
     * @param url      url
     * @param respCall Response
     */
    public void getDataAsync(String url, final RespCall respCall) {
        executeAsync(url, "GET", null, respCall);
    }

    /**
     * post请求，异步方式，提交数据获取响应
     *
     * @param url        url
     * @param bodyParams Request Body
     * @param respCall   Response
     */
    public void postDataAsync(String url, Map<String, String> bodyParams, final RespCall respCall) {
        postDataAsync(url, buildFormBody(bodyParams), respCall);
    }

    /**
     * post请求，异步方式，提交数据获取响应
     *
     * @param url         url
     * @param requestBody Request Body
     * @param respCall    Response
     */
    public void postDataAsync(String url, RequestBody requestBody, final RespCall respCall) {
        executeAsync(url, "POST", requestBody, respCall);
    }

    /**
     * post请求，异步方式，提交JSON格式数据获取响应
     *
     * @param url  url
     * @param json Request Json
     */
    public void postJsonAsync(String url, String json, RespCall respCall) {
        executeAsync(url, "POST", buildJsonBody(json), respCall);
    }

    /**
     * 异步请求, 通过 respCall进行请求回调, 非UI线程
     *
     * @param url         url
     * @param method      Method
     * @param requestBody Request Body
     * @param respCall    Response
     */
    public void executeAsync(String url, String method, RequestBody requestBody, final RespCall respCall) {
        executeAsync(url, method, Headers.of(), requestBody, respCall);
    }

    /**
     * 异步请求方式, 通过 respCall进行请求回调, 非UI线程
     *
     * @param url         url
     * @param method      Method
     * @param headers     Request Headers
     * @param requestBody Request Body
     * @param respCall    Response
     */
    public void executeAsync(String url, String method, Headers headers, RequestBody requestBody, final RespCall respCall) {
        //构造Request
        Request request = new Request.Builder()
                .headers(headers)
                .method(method, requestBody)
                .url(url)
                .build();
        //将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                respCall.failed(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                respCall.success(call, response);
            }
        });
    }

    ////

    /**
     * get请求, 异步方式, 获取响应体内容
     *
     * @param url          url
     * @param respCallBody Response
     */
    public void getDataAsync(String url, RespCallBody respCallBody) {
        executeAsync(url, "GET", null, respCallBody);
    }

    /**
     * post请求, 异步方式, 获取响应体内容
     *
     * @param url          url
     * @param bodyParams   Request Body
     * @param respCallBody Response
     */
    public void postDataAsync(String url, Map<String, String> bodyParams, final RespCallBody respCallBody) {
        postDataAsync(url, buildFormBody(bodyParams), respCallBody);
    }

    /**
     * post请求, 异步方式, 获取响应体内容
     *
     * @param url          url
     * @param requestBody  Request Body
     * @param respCallBody Response
     */
    public void postDataAsync(String url, RequestBody requestBody, final RespCallBody respCallBody) {
        executeAsync(url, "POST", requestBody, respCallBody);
    }

    /**
     * post请求，异步方式，提交JSON格式数据获取响应
     *
     * @param url  url
     * @param json Request Json
     */
    public void postJsonAsync(String url, String json, RespCallBody respCallBody) {
        executeAsync(url, "POST", buildJsonBody(json), respCallBody);
    }

    /**
     * 异步请求
     *
     * @param url          url
     * @param method       Method
     * @param requestBody  Request Body
     * @param respCallBody Response
     */
    public void executeAsync(String url, String method, RequestBody requestBody, final RespCallBody respCallBody) {
        executeAsync(url, method, Headers.of(), requestBody, respCallBody);
    }

    /**
     * 异步请求方式, 通过 respCall进行请求回调, 非UI线程
     *
     * @param url          Request Url
     * @param method       Request Method
     * @param headers      Request Headers
     * @param requestBody  Request Body
     * @param respCallBody Response
     */
    public void executeAsync(String url, String method, Headers headers, RequestBody requestBody, final RespCallBody respCallBody) {
        //构造Request
        Request request = new Request.Builder()
                .headers(headers)
                .method(method, requestBody)
                .url(url)
                .build();
        //将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                respCallBody.onResponseBody(-1, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    respCallBody.onResponseBody(response.code(), responseBody.string());
                } else {
                    respCallBody.onResponseBody(response.code(), "");
                }
                response.close();
            }
        });
    }

    //// 待测试

    /**
     * 文件上传
     *
     * @param url      url
     * @param method   Method
     * @param types    File Types
     * @param files    Files
     * @param listener Listener
     */
    public void uploadFiles(String url, String method, MediaType[] types, File[] files, UploadProgressListener listener) {
        uploadFiles(url, method, types, files, null, listener);
    }

    /**
     * 文件上传
     *
     * @param url        url
     * @param method     Method
     * @param types      File Types
     * @param files      Files
     * @param bodyParams Request Body
     * @param listener   Listener
     */
    public void uploadFiles(String url, String method, MediaType[] types, File[] files, Map<String, String> bodyParams, UploadProgressListener listener) {
        //构建RequestBody
        RequestBody requestBody = buildUploadFileBody(types, files, bodyParams);
        //构建Request
        Request request = new Request
                .Builder()
                .method(method, requestBody)
                .url(url)
                .build();
        //重新构建一个 HttpClient, 并添加上传拦截器
        OkHttpClient client = mOkHttpClient
                .newBuilder()
                .addInterceptor(new ProgressResponseInterceptor(listener))
                .build();
        //执行请求
        try {
            Response response = client.newCall(request).execute();
            boolean successful = response.isSuccessful();
            //todo 其他操作
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ////

    /**
     * Form表单格式, 请求参数, 构造RequestBody
     *
     * @param bodyParams Request Body 参数组合
     * @return return
     */
    public RequestBody buildFormBody(Map<String, String> bodyParams) {
        RequestBody body = null;
        FormBody.Builder formEncodingBuilder = new FormBody.Builder();
        if (bodyParams != null) {
            Iterator<String> iterator = bodyParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next();
                formEncodingBuilder.add(key, bodyParams.get(key));
                Log.d(TAG, "bodyParams===" + key + "====" + bodyParams.get(key));
            }
        }
        body = formEncodingBuilder.build();
        return body;
    }

    /**
     * JSON文本格式, 请求参数, 构造RequestBody
     *
     * @param json Request Json
     * @return return
     */
    public RequestBody buildJsonBody(String json) {
        return RequestBody.create(MEDIA_TYPE_JSON, json);
        //return RequestBody.create(json, MEDIA_TYPE_JSON); //okhttp3推荐这样写, 如有需要可切换上面一行
    }

    /**
     * 文件上传格式, 请求参数, 构造RequestBody
     *
     * @param types File Types
     * @param files Files
     * @return return
     */
    public RequestBody buildUploadFileBody(MediaType[] types, File[] files, Map<String, String> bodyParams) {
        //构建上传表单
        MultipartBody.Builder builder = new MultipartBody
                .Builder()
                .setType(MultipartBody.FORM);

        //添加欲上传的文件
        for (int i = 0; i < files.length; i++) {
            MediaType type = types[i];
            File file = files[i];
            RequestBody fileBody = MultipartBody.create(type, file);
            //RequestBody fileBody = MultipartBody.create(file, type); //okhttp3推荐这样写, 如有需要可切换上面一行
            builder.addFormDataPart("file_" + i, file.getName(), fileBody);
            Log.d(TAG, "uploadFile===" + file.getName());
        }
        //传入批量提交文件的数量
        builder.addFormDataPart("fileCount", String.valueOf(files.length));

        //其他参数, 如果有
        if (bodyParams != null) {
            Iterator<String> iterator = bodyParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next();
                builder.addFormDataPart(key, bodyParams.get(key));
                Log.d(TAG, "bodyParams===" + key + "====" + bodyParams.get(key));
            }
        }

        return builder.build();
    }

    ////

    /**
     * 生成安全套接字工厂，用于https请求的证书跳过
     *
     * @return return
     */
    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }

    /**
     * 用于信任所有证书
     */
    static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    ////

    /**
     * 响应回调接口
     */
    public interface RespCall {
        void failed(@NonNull Call call, IOException e);

        void success(@NonNull Call call, @NonNull Response response) throws IOException;
    }

    /**
     * 响应回调接口, 返回响应体内容
     */
    public interface RespCallBody {
        void onResponseBody(int statusCode, String body);
    }

    /**
     * 上传进度监听接口
     */
    public interface UploadProgressListener {
        void onProgress(long speed, long total, boolean done);
    }

    /**
     * 上传响应, 重新包装
     */
    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody mResponseBody;
        private final UploadProgressListener progressListener;
        private BufferedSource bufferedSource;

        ProgressResponseBody(ResponseBody mResponseBody, UploadProgressListener progressListener) {
            this.mResponseBody = mResponseBody;
            this.progressListener = progressListener;
        }

        /**
         * 重写调用实际的响应体的contentLength
         *
         * @return contentLength
         */
        @Override
        public long contentLength() {
            return mResponseBody.contentLength();
        }

        /**
         * 重写调用实际的响应体的contentType
         *
         * @return MediaType
         */
        @Nullable
        @Override
        public MediaType contentType() {
            return mResponseBody.contentType();
        }

        /**
         * 重写进行包装source
         *
         * @return BufferedSource
         */
        @NonNull
        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(mResponseBody.source()));
            }
            return bufferedSource;
        }

        /**
         * 读取，回调进度接口
         *
         * @param source Source
         * @return Source
         */
        private Source source(Source source) {
            return new ForwardingSource(source) {
                //当前读取字节数
                long totalBytesRead = 0L;

                @Override
                public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    //回调，如果contentLength()长度未知，会返回-1
                    progressListener.onProgress(totalBytesRead, mResponseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }

    /**
     * 上传拦截器, 在这里重写响应 [ProgressResponseBody]
     */
    private static class ProgressResponseInterceptor implements Interceptor {
        private final UploadProgressListener progressListener;

        ProgressResponseInterceptor(UploadProgressListener progressListener) {
            this.progressListener = progressListener;
        }

        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            return response
                    .newBuilder()
                    .code(response.code())
                    .headers(response.headers())
                    .body(new ProgressResponseBody(response.body(), progressListener))
                    .build();
        }
    }
}