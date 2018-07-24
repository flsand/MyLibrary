package com.flysand.mylibrary.http;

import android.os.Handler;

import com.flysand.mylibrary.listener.AsyncHttpReturnListener;
import com.flysand.mylibrary.util.Utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by FlySand on 2017\7\24 0024.
 */

public class AsyncHttpCallbackImpl implements Callback {

    private String type;
    private AsyncHttpReturnListener listener;
    private static Handler handler = new Handler();
    private String body = "";

    private String cancelType = "--cancelType--";

    public void cancelRequest(String type) {
        cancelType = type;
    }

    public AsyncHttpCallbackImpl(String type, AsyncHttpReturnListener listener) {
        this.type = type;
        this.listener = listener;
    }

    @Override
    public void onFailure(final Call call, final IOException e) {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onFailure(type, call, e);
                }
            });
        }
    }

    @Override
    public void onResponse(final Call call, final Response response) throws IOException {
        if (listener != null) {

            try {
                body = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
                Utils.print("Exception  = " + e.toString());
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!cancelType.equals(type))
                            listener.onResponse(type, call, response, body);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.print("Exception  = " + e.toString());
                    }
                }
            });
        }
    }
}
