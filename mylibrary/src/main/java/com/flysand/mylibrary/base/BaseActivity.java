package com.flysand.mylibrary.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.flysand.mylibrary.http.HttpAnalysisHelper;
import com.flysand.mylibrary.listener.AsyncHttpAnalysisListener;
import com.flysand.mylibrary.listener.AsyncHttpReturnListener;
import com.flysand.mylibrary.listener.ObserverListener;
import com.flysand.mylibrary.util.MyNewToast;
import com.flysand.mylibrary.view.HttpProgressDialog;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/23.
 */

public abstract class BaseActivity extends Activity
        implements AsyncHttpReturnListener, AsyncHttpAnalysisListener, ObserverListener, ObserverListener.Subject {

    protected MyNewToast toast;
    protected HttpProgressDialog httpProgressDialog;
    private final static List<ObserverListener> observers = new ArrayList<>();

    protected HttpAnalysisHelper analysisHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLifecycle.getInstance(this);

        if (httpProgressDialog == null) {
            httpProgressDialog = new HttpProgressDialog(this);
        }
        if (toast == null) {
            toast = new MyNewToast(this);
        }
        registerObserver(this);
        analysisHelper = getCustumAnalysisHelper();
    }

    protected HttpAnalysisHelper getCustumAnalysisHelper() {
        return new HttpAnalysisHelper(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeObserver(this);
    }

    @Override
    public void onUpdate(int type, Intent intent) {
    }

    @Override
    public void sendUpdateMessage(int type, Intent intent) {
        notifyObservers(type, intent);
    }

    @Override
    public void registerObserver(ObserverListener o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(ObserverListener o) {
        if (observers.indexOf(o) >= 0) {
            observers.remove(o);
        }
    }

    @Override
    public void notifyObservers(int type, Intent intent) {
        for (final ObserverListener o : observers) {
            o.onUpdate(type, intent);
        }
    }

    public void onFailure(String type, Call call, Exception e) {
        if (analysisHelper != null)
            analysisHelper.onFailure(type, call, e);
    }

    public void onResponse(String type, Call call, Response response, String body) {
        if (analysisHelper != null)
            analysisHelper.onResponse(type, call, response, body);
    }
}
