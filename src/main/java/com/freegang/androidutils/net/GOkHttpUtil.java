package com.freegang.androidutils.net;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.freegang.androidutils.log.GLog;

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

public class GOkHttpUtil {
    public final static int READ_TIMEOUT = 100;
    public final static int CONNECT_TIMEOUT = 60;
    public final static int WRITE_TIMEOUT = 60;
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_IMG_PNG = MediaType.parse("image/png");
    private static final byte[] LOCKER = new byte[0];
    private static GOkHttpUtil mInstance;
    private final OkHttpClient mOkHttpClient;

    final static UIHandler uiHandler = new UIHandler(Looper.getMainLooper());

    /// UI Handler
    private static class UIHandler extends Handler {
        private static final int RESPONSE_SUCCESS_CODE = 1000; //请求成功返回
        private static final int RESPONSE_FAILED_CODE = 1001; //请求失败返回
        private static final int UPLOAD_FILE_CODE = 1002; //文件上传返回code
        //普通回调
        private RespCall respCall;
        private Call call;
        private Response response;
        private IOException ioException;

        // 上传
        private UploadProgressListener uploadListener;
        private long uploadSpeed;
        private long uploadTotal;
        private boolean uploadDone;

        public UIHandler(Looper looper) {
            super(looper);
        }

        /// 设置普通回调
        public void setRespCall(RespCall respCall, Call call, Response response, IOException ioException) {
            this.respCall = respCall;
            this.call = call;
            this.response = response;
            this.ioException = ioException;
        }

        /// 设置上传进度
        public void setUploadProgress(UploadProgressListener listener, long speed, long total, boolean done) {
            this.uploadListener = listener;
            this.uploadSpeed = speed;
            this.uploadTotal = total;
            this.uploadDone = done;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case RESPONSE_SUCCESS_CODE:
                    try {
                        respCall.success(call, response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case RESPONSE_FAILED_CODE:
                    respCall.failed(call, ioException);
                    break;
                case UPLOAD_FILE_CODE:
                    uploadListener.onProgress(uploadSpeed, uploadTotal, uploadDone);
                    break;
            }
        }
    }

    /// 网络回调接口
    public interface RespCall {
        void success(Call call, Response response) throws IOException;

        void failed(Call call, IOException e);
    }

    // =================== 上传监听 ====================== //
    /// 上传进度监听接口
    public interface UploadProgressListener {
        void onProgress(long speed, long total, boolean done);
    }

    /// 上传响应, 重新包装
    private class ProgressResponseBody extends ResponseBody {

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
         * @throws IOException 异常
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
         * @throws IOException
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
                public long read(Buffer sink, long byteCount) throws IOException {
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

    /// 上传拦截器, 在这里重写响应 [ProgressResponseBody]
    private class ProgressResponseInterceptor implements Interceptor {
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

    /**
     * 单例模式获取OkHttpUtil
     *
     * @return
     */
    public static GOkHttpUtil getInstance() {
        if (mInstance == null) {
            synchronized (LOCKER) {
                if (mInstance == null) {
                    mInstance = new GOkHttpUtil();
                }
            }
        }
        return mInstance;
    }

    private GOkHttpUtil() {
        okhttp3.OkHttpClient.Builder ClientBuilder = new okhttp3.OkHttpClient.Builder();
        ClientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);//读取超时
        ClientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);//连接超时
        ClientBuilder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);//写入超时
        //支持HTTPS请求，跳过证书验证
        ClientBuilder.sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts());
        ClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        mOkHttpClient = ClientBuilder.build();
    }

    // ============== 同步请求 ============== //

    /**
     * get请求，同步方式，获取网络数据
     *
     * @param url
     * @return
     */
    public Response getData(String url) {
        return execute(url, "GET", null);
    }

    /**
     * post请求，同步方式，提交数据
     *
     * @param url
     * @param bodyParams
     * @return
     */
    public Response postData(String url, Map<String, String> bodyParams) {
        return execute(url, "POST", buildFormBody(bodyParams));
    }

    /**
     * post请求，同步方式，提交JSON数据
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
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
     * @param url
     * @param method
     * @param headers
     * @param requestBody
     * @return
     */
    public Response execute(String url, String method, Headers headers, RequestBody requestBody) {
        Request request = new Request.Builder()
                .headers(headers)
                .method(method, requestBody)
                .url(url)
                .build();
        //3 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //4 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


    // ============== 异步请求 ============== //

    /**
     * get请求，异步方式，获取网络数据
     *
     * @param url
     * @param respCall
     * @return
     */
    public void getDataAsync(String url, final RespCall respCall) {
        executeAsync(url, "GET", null, respCall);
    }

    /**
     * post请求，异步方式，提交数据
     *
     * @param url
     * @param bodyParams
     * @param respCall
     */
    public void postDataAsync(String url, Map<String, String> bodyParams, final RespCall respCall) {
        executeAsync(url, "POST", buildFormBody(bodyParams), respCall);
    }

    /**
     * post请求，异步方式，提交JSON格式数据
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public void postJsonAsync(String url, String json, final RespCall respCall) {
        executeAsync(url, "POST", buildJsonBody(json), respCall);
    }

    public void executeAsync(String url, String method, RequestBody requestBody, RespCall respCall) {
        executeAsync(url, method, Headers.of(), requestBody, respCall);
    }

    /**
     * 异步请求方式, 通过 respCall进行请求回调, 非UI线程
     *
     * @param url
     * @param method
     * @param headers
     * @param requestBody
     * @param respCall
     */
    public void executeAsync(String url, String method, Headers headers, RequestBody requestBody, RespCall respCall) {
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
            public void onFailure(Call call, IOException e) {
                respCall.failed(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                respCall.success(call, response);
            }
        });
    }


    // ============== 异步请求(UI) ============== //

    /**
     * get请求，异步方式(UI)，获取网络数据
     *
     * @param url
     * @param respCall
     * @return
     */
    public void getDataAsyncUI(String url, final RespCall respCall) {
        executeAsyncUI(url, "GET", null, respCall);
    }

    /**
     * post请求，异步方式(UI)，提交数据
     *
     * @param url
     * @param bodyParams
     * @param respCall
     */
    public void postDataAsyncUI(String url, Map<String, String> bodyParams, final RespCall respCall) {
        executeAsyncUI(url, "POST", buildFormBody(bodyParams), respCall);
    }

    /**
     * post请求，异步方式(UI)，提交JSON格式数据
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public void postJsonAsyncUI(String url, String json, final RespCall respCall) {
        executeAsyncUI(url, "POST", buildJsonBody(json), respCall);
    }

    public void executeAsyncUI(String url, String method, RequestBody requestBody, RespCall respCall) {
        executeAsyncUI(url, method, Headers.of(), requestBody, respCall);
    }

    /**
     * 异步请求方式, UI线程
     *
     * @param url
     * @param method
     * @param headers
     * @param requestBody
     * @param respCall
     */
    public void executeAsyncUI(String url, String method, Headers headers, RequestBody requestBody, RespCall respCall) {
        //使用Handler代理用户传过来的RespCall
        final RespCall mRespCall = new RespCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                uiHandler.setRespCall(respCall, call, response, null); //设置成功回调信息
                uiHandler.sendEmptyMessage(UIHandler.RESPONSE_SUCCESS_CODE); //发送UI更新消息
            }

            @Override
            public void failed(Call call, IOException e) {
                uiHandler.setRespCall(respCall, call, null, e); //设置失败回调信息
                uiHandler.sendEmptyMessage(UIHandler.RESPONSE_FAILED_CODE); //发送UI更新消息
            }
        };
        executeAsync(url, method, headers, requestBody, mRespCall);
    }


    // ============== 文件上传 ============== //
    public void uploadFile(String url, String method, MediaType[] types, File[] files, UploadProgressListener listener) {
        uploadFile(url, method, types, files, null, listener);
    }

    /**
     * 文件上传, 非UI线程
     *
     * @param url
     * @param method
     * @param types
     * @param files
     * @param bodyParams
     * @param listener
     */
    public void uploadFile(String url, String method, MediaType[] types, File[] files, Map<String, String> bodyParams, UploadProgressListener listener) {
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
        Response response = null;
        try {
            response = client.newCall(request).execute();
            boolean successful = response.isSuccessful();
            //todo 其他操作
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ============== 文件上传(UI) ============== //
    public void uploadFileUI(String url, String method, MediaType[] types, File[] files, UploadProgressListener listener) {
        uploadFileUI(url, method, types, files, null, listener);
    }

    /**
     * 文件上传, UI线程
     *
     * @param url
     * @param method
     * @param types
     * @param files
     * @param bodyParams
     * @param listener
     */
    public void uploadFileUI(String url, String method, MediaType[] types, File[] files, Map<String, String> bodyParams, UploadProgressListener listener) {
        //使用Handler代理用户传过来的Listener
        final UploadProgressListener mListener = new UploadProgressListener() {
            @Override
            public void onProgress(long speed, long total, boolean done) {
                uiHandler.setUploadProgress(listener, speed, total, done); //设置进度
                uiHandler.sendEmptyMessage(UIHandler.UPLOAD_FILE_CODE); //发送UI更新消息
            }
        };
        //调用文件上传
        uploadFile(url, method, types, files, bodyParams, mListener);
    }


    // ============== 构建各种请求 ============== //

    /**
     * Form表单格式, 请求参数, 构造RequestBody
     *
     * @param bodyParams 参数组合
     * @return
     */
    private RequestBody buildFormBody(Map<String, String> bodyParams) {
        RequestBody body = null;
        FormBody.Builder formEncodingBuilder = new FormBody.Builder();
        if (bodyParams != null) {
            Iterator<String> iterator = bodyParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next();
                formEncodingBuilder.add(key, bodyParams.get(key));
                GLog.d("bodyParams===" + key + "====" + bodyParams.get(key));
            }
        }
        body = formEncodingBuilder.build();
        return body;
    }

    /**
     * JSON文本格式, 请求参数, 构造RequestBody
     *
     * @param json 参数json
     * @return
     */
    private RequestBody buildJsonBody(String json) {
        //return RequestBody.create(MEDIA_TYPE_JSON, json);
        return RequestBody.create(json, MEDIA_TYPE_JSON); //okhttp3推荐这样写, 如有需要可切换上面一行
    }

    /**
     * 文件上传格式, 请求参数, 构造RequestBody
     *
     * @param types
     * @param files
     * @return
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
            //RequestBody fileBody = MultipartBody.create(type, json);
            RequestBody fileBody = MultipartBody.create(file, type); //okhttp3推荐这样写, 如有需要可切换上面一行
            builder.addFormDataPart("file_" + i, file.getName(), fileBody);
            GLog.d("uploadFile===" + file.getName());
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
                GLog.d("bodyParams===" + key + "====" + bodyParams.get(key));
            }
        }

        return builder.build();
    }


    // ============== 证书 =========== //

    /**
     * 生成安全套接字工厂，用于https请求的证书跳过
     *
     * @return
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
}