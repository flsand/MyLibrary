package com.flysand.mylibrary.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import com.flysand.mylibrary.base.BaseApplication;
import com.flysand.mylibrary.listener.AsyncHttpReturnListener;
import com.flysand.mylibrary.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by FlySand on 2017\7\24 0024.
 */

public class HttpUtil {

    private static OkHttpClient client;
    private static OkHttpClient.Builder builder;

    private static Map<AsyncHttpReturnListener, HttpUtil> as = new ConcurrentHashMap();
    private static Map<String, String> headers = new ConcurrentHashMap();

    private AsyncHttpCallbackImpl asyncHttpCallback;

    public static HttpUtil getInstance(AsyncHttpReturnListener var0) {
        synchronized (HttpUtil.class) {
            if (!as.containsKey(var0)) {
                as.put(var0, new HttpUtil());
            }
            initClient();
        }
        return (HttpUtil) as.get(var0);
    }

    private static void initClient() {
        if (builder == null) {
            builder = new OkHttpClient.Builder();
            //设置超时时间
            builder.connectTimeout(30, TimeUnit.SECONDS);
            //设置读取超时时间
            builder.readTimeout(30, TimeUnit.SECONDS);
            //设置写入超时时间
            builder.writeTimeout(30, TimeUnit.SECONDS);
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.cookieJar(new LocalCookieJar());   //为OkHttp设置自动携带Cookie的功能
//            builder.followRedirects(true);  //OkHttp的重定向操作，
//            builder.followSslRedirects(true);//https的重定向也自己处理
            builder.hostnameVerifier(new DefaultHostnameVerifier());
        }
        if (client == null) {
            client = builder.build();
        }
    }

    /**
     * 设置头
     */
    public static void addInterceptor() {
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request orignaRequest = chain.request();
                Request request = orignaRequest.newBuilder()
                        .header("AppType", "TPOS")
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .method(orignaRequest.method(), orignaRequest.body())
                        .build();
                return chain.proceed(request);
            }
        };
        builder.addInterceptor(headerInterceptor);
        client = builder.build();
    }

    /**
     * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
     * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
     * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
     */
    public static class DefaultHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            Utils.print("verify  hostname = "+hostname + "  session = "+session );
            return true;
        }
    }

    /**
     * 初始化cookie
     * 自动管理cookie
     */
    public void initCookieStore(Context context) {
//        builder.cookieJar(new CookieJar() {
//                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
//                    @Override
//                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//                        cookieStore.put(url.host(), cookies);
//                    }
//                    @Override
//                    public List<Cookie> loadForRequest(HttpUrl url) {
//                        List<Cookie> cookies = cookieStore.get(url.host());
//                        return cookies != null ? cookies : new ArrayList<Cookie>();
//                    }
//                });
        builder.cookieJar(new CookiesManager(context));
        client = builder.build();
    }

    /**
     * 清除cookie
     *
     * @param context
     */
    public static void clearCookie(Context context) {
        client = null;
        builder = null;
        initClient();
        SharedPreferences sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

    }


    public void httpGet(RequestParams requestParams, String url) {
        this.httpGet((String) null, requestParams, url);
    }

    public void httpGet(String type, RequestParams requestParams, String url) {
        synchronized (BaseApplication.class) {


            Utils.print("as  = "+as.size());

            if (TextUtils.isEmpty(type)) {
                type = "NO_TYPE";
            }
            Iterator var5 = as.keySet().iterator();
            while (var5.hasNext()) {
                AsyncHttpReturnListener var6 = (AsyncHttpReturnListener) var5.next();
                if (as.get(var6) == this) {
                    Utils.print("type=" + type + " : url = " + url + "?" + requestParams.toString());
                    //创建一个Request
                    Request.Builder request = addHeaders().url(url + "?" + requestParams.toString());
                    //new call
                    Call call = client.newCall(request.build());
                    asyncHttpCallback = new AsyncHttpCallbackImpl(type, var6);
                    //请求加入调度
                    call.enqueue(asyncHttpCallback);

                    break;
                }
            }

        }
    }


    public void httpPost(RequestParams var1, String url) {
        this.httpPost((String) null, var1, url);

    }

    public void httpPost(String type, RequestParams requestParams, String url) {


        synchronized (BaseApplication.class) {

            Utils.print("as  = "+as.size());

            if (TextUtils.isEmpty(type)) {
                type = "NO_TYPE";
            }
            Iterator var5 = as.keySet().iterator();
            while (var5.hasNext()) {
                AsyncHttpReturnListener var6 = (AsyncHttpReturnListener) var5.next();
                if (as.get(var6) == this) {
                    Utils.print("type=" + type + " : url = " + url + "?" + requestParams.toString());
                    //创建一个Request
                    Request.Builder request = addHeaders()
                            .url(url)
                            .post(requestParams.generateRequest());
                    //new call
                    Call call = client.newCall(request.build());
                    asyncHttpCallback = new AsyncHttpCallbackImpl(type, var6);
                    //请求加入调度
                    call.enqueue(asyncHttpCallback);

                    Utils.print("client  = " + client);
                    break;
                }
            }

        }
    }

    public HttpUtil addHeader(String key, String val) {
        headers.put(key, val);
        return this;
    }


    /**
     * 取消请求回调
     */
    public void cancelRequst(String type) {
        if (asyncHttpCallback != null)
            asyncHttpCallback.cancelRequest(type);
    }

    /**
     * 统一为请求添加头信息
     *
     * @return
     */
    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("platform", "2")
                .addHeader("phoneModel", Build.MODEL)
                .addHeader("systemVersion", Build.VERSION.RELEASE)
                .addHeader("appVersion", "1.0.0");
        Iterator entries = headers.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            builder.addHeader(key, value);
        }
        return builder;
    }

    public static void clearContextMap() {
        as.clear();
    }
    public static void removeContextMap(AsyncHttpReturnListener listener) {
        as.remove(listener);
    }

    private HttpUtil() {
    }

    //CookieJar是用于保存Cookie的
    static class LocalCookieJar implements CookieJar {
        List<Cookie> cookies;

        @Override
        public List<Cookie> loadForRequest(HttpUrl arg0) {
            if (cookies != null)
                return cookies;
            return new ArrayList<>();
        }

        @Override
        public void saveFromResponse(HttpUrl arg0, List<Cookie> cookies) {
            this.cookies = cookies;
            for (Cookie c : cookies) {
                Utils.print("c.domain() = " + c.domain());
            }

            Utils.toListString(cookies);
        }

    }
}
