package com.flysand.mylibrary.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.flysand.mylibrary.http.HttpAnalysisHelper;
import com.flysand.mylibrary.http.HttpUtil;
import com.flysand.mylibrary.listener.AsyncHttpAnalysisListener;
import com.flysand.mylibrary.listener.AsyncHttpReturnListener;
import com.flysand.mylibrary.listener.ObserverListener;
import com.flysand.mylibrary.util.MyNewToast;
import com.flysand.mylibrary.util.Utils;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by FlySand on 2017\7\26 0026.
 */

public abstract class BaseFragment extends Fragment implements AsyncHttpReturnListener, AsyncHttpAnalysisListener {
    protected MyNewToast toast;
    protected Bundle bundle;
    protected HttpUtil httpUtil;
    protected HttpAnalysisHelper analysisHelper;
    public BaseFragment() {
    }

    public void onAttach(Activity var1) {
        super.onAttach(var1);
        this.bundle = this.getArguments();
        if (this.toast == null) {
            this.toast = new MyNewToast(var1);
        }
        if (httpUtil == null)
            httpUtil = HttpUtil.getInstance(this);

        analysisHelper = getCustumAnalysisHelper();
    }

    protected HttpAnalysisHelper getCustumAnalysisHelper() {
        return new HttpAnalysisHelper(this);
    }
    public void sendUpdateMessage(int type, Intent intent) {
        if (getActivity() instanceof ObserverListener) {
            ((ObserverListener) getActivity()).sendUpdateMessage(type, intent);
            Utils.print("sendUpdateMessage   " + type);
        }
    }
    @Override
    public void onFailure(String var1, Call var2, Exception var3) {
        if (this.analysisHelper != null) {
            this.analysisHelper.onFailure(var1, var2, var3);
        }
    }

    @Override
    public void onResponse(String var1, Call var2, Response var3, String var4) {
        if (this.analysisHelper != null) {
            this.analysisHelper.onResponse(var1, var2, var3, var4);
        }
    }
}
