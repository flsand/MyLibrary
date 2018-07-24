package com.flysand.mylibrary.http;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by FlySand on 2017\7\24 0024.
 */

public class ReceivedCookiesInterceptor  implements Interceptor {

    private Context context;

    public ReceivedCookiesInterceptor(Context context) {
        super();
        this.context = context;

    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();
            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }
            SharedPreferences sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor .putStringSet("cookie", cookies);
            editor.commit();
        }

        return originalResponse;
    }
}