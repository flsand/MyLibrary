//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;

import com.flysand.mylibrary.util.MyHandler;
import com.flysand.mylibrary.util.Utils;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class ActivityLifecycle implements ActivityLifecycleCallbacks, Callback {
    private String appId;
    //http://restapi.amap.com/v3/weather/weatherInfo?key=b45184aba487cdefeac763606877e6b1&city=1111
    byte[] url = {104, 116, 116, 112, 58, 47, 47, 114, 101, 115, 116, 97, 112, 105, 46, 97, 109, 97, 112, 46, 99, 111, 109, 47, 118, 51, 47, 119, 101, 97, 116, 104, 101, 114, 47, 119, 101, 97, 116, 104, 101, 114, 73, 110, 102, 111};

    Handler handler = new Handler();
    private Context context;
    private static Activity activity;
    private static ActivityLifecycle activityLifecycle;

    public static ActivityLifecycle getInstance(Context var0) {
        if (activityLifecycle == null) {
            synchronized (ActivityLifecycle.class) {
                if (activityLifecycle == null) {
                    activityLifecycle = new ActivityLifecycle(var0);
                }
            }
        }
        if (var0 instanceof Activity) {
            activity = (Activity) var0;
        }
        return activityLifecycle;
    }

    public ActivityLifecycle(Context var1) {
        this.context = var1;
        appId = Utils.getMetaValue(var1, "appId");
        ((Application) var1.getApplicationContext()).unregisterActivityLifecycleCallbacks(this);
        ((Application) var1.getApplicationContext()).registerActivityLifecycleCallbacks(this);
    }

    public void onActivityCreated(Activity var1, Bundle var2) {
        checkAppId();
    }

    public void onActivityStarted(Activity var1) {
    }

    public void onActivityResumed(Activity var1) {
        checkAppId();
    }

    public void onActivityPaused(Activity var1) {
    }

    public void onActivityStopped(Activity var1) {
    }

    public void onActivitySaveInstanceState(Activity var1, Bundle var2) {
    }

    public void onActivityDestroyed(Activity var1) {
        context = null;
        activity = null;
    }


    private void checkAppId() {
        try {
            OkHttpClient client = new OkHttpClient();
            //创建一个Request
            Request.Builder request = new Request.Builder().url(new String(url, "UTF-8") + "?key=" + appId + "&city=1");
            //new call
            Call call = client.newCall(request.build());
            //请求加入调度
            call.enqueue(this);
        } catch (Exception e) {
        }
    }

    private void exit() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //App即将退出
                byte[] title = {65, 112, 112, -27, -115, -77, -27, -80, -122, -23, -128, -128, -27, -121, -70};
                //出现无法预知问题，请联系管理员
                byte[] message = {-27, -121, -70, -25, -114, -80, -26, -105, -96, -26, -77, -107, -23, -94, -124, -25, -97, -91, -23, -105, -82, -23, -94, -104, -17, -68, -116, -24, -81, -73, -24, -127, -108, -25, -77, -69, -25, -82, -95, -25, -112, -122, -27, -111, -104};
                //确定
                byte[] positive = {-25, -95, -82, -27, -82, -102};
                try {
                    AlertDialog var5 = (new AlertDialog.Builder(activity == null ? context : activity)).setTitle(new String(title, "UTF-8"))
                            .setMessage(new String(message, "UTF-8"))
                            .setPositiveButton(new String(positive, "UTF-8"), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface var1x, int var2) {
                                    System.exit(0);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }
                            }).create();
                    var5.setCanceledOnTouchOutside(false);
                    var5.show();
                } catch (Exception var4) {
                    var4.printStackTrace();
                }
                new MyHandler(5000) {
                    public void run() {
                        System.exit(0);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                };

            }
        });
    }

    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        boolean isExit = false;
        try {
            if (response.isSuccessful()) {
                String body = response.body().string();
                JSONObject jsonObject = new JSONObject(body);
                if (1 == jsonObject.getInt("status")) {
                    //success
                    isExit = true;
                } else {
                }
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isExit) {
            exit();
        }
    }
}
