package com.flysand.mylibrary.http;

import com.flysand.mylibrary.listener.AsyncHttpAnalysisListener;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by FlySand on 2018/4/4.
 */

public class HttpAnalysisHelper {

    protected AsyncHttpAnalysisListener listener;

    public HttpAnalysisHelper(AsyncHttpAnalysisListener listener) {
        this.listener = listener;
    }

    public void onFailure(String type, Call call, Exception e) {

    }

    public void onResponse(String type, Call call, Response response, String body) {

    }
}
