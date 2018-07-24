package com.flysand.mylibrary.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.Toast;

import com.flysand.mylibrary.util.Utils;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.Recorder;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


/**
 * 作用:
 * 1.收集错误信息
 * 2.保存错误信息
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler sInstance = null;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    // 保存手机信息和异常信息
    private Map<String, String> mMessage = new HashMap<>();
    private static boolean isDispose = false;
    private static boolean isUpLoadFile = false;
    private static String updateFileName;
    private static String filePath;

    public static CrashHandler getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CrashHandler.class) {
                if (sInstance == null) {
                    synchronized (CrashHandler.class) {
                        sInstance = new CrashHandler(context);
                    }
                }
            }
        }
        return sInstance;
    }

    private void initHandlerMap() {
        this.mMessage.put("innerAppStyle", "Android");
        this.mMessage.put("innerAppName", Utils.getApplicationName(mContext));
        this.mMessage.put("innerAppId", mContext.getPackageName());
        this.mMessage.put("innerAppVersion", Utils.getAppVersion(mContext));
        this.mMessage.put("innerAppNet", Utils.getNetStyle(mContext));
        this.mMessage.put("innerAppHardware", Utils.getDeviceInfo());
        this.mMessage.put("innerAppHardwareVersion", Build.VERSION.RELEASE);
        this.mMessage.put("appId", Utils.getMetaValue(mContext, "appBaseId"));
    }

    public void putParams(String key, String val) {
        mMessage.put(key, val);
    }

    private CrashHandler() {
    }

    /**
     * 初始化默认异常捕获
     *
     * @param context context
     */
    private CrashHandler(Context context) {
        mContext = context;
        // 获取默认异常处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 将此类设为默认异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        initHandlerMap();
    }

    @Override
    public void uncaughtException(final Thread t, Throwable e) {
        Utils.print("uncaughtException() isUpLoadFile = " + isUpLoadFile);
        try {
            if (!handleException(e)) {
                mDefaultHandler.uncaughtException(t, e);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 是否人为捕获异常
     *
     * @param e Throwable
     * @return true:已处理 false:未处理
     */
    private boolean handleException(final Throwable e) {
        if (e == null) {// 异常是否为空
            return false;
        }
        if (!isDispose) {
            isDispose = true;
            new Thread() {// 在主线程中弹出提示
                @Override
                public void run() {
                    Looper.prepare();
                    Utils.print("捕获到异常");
                    Toast.makeText(mContext, "捕获到异常", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }.start();
            new Thread() {
                @Override
                public void run() {
                    try {
                        Looper.prepare();
                        collectErrorMessages();
                        saveErrorMessages(e);
                    } catch (Exception e1) {

                    }
                }
            }.start();
            SystemClock.sleep(3000);
            return true;
        }


        return false;
    }

    /**
     * 1.收集错误信息
     */
    private void collectErrorMessages() throws Exception {
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = TextUtils.isEmpty(pi.versionName) ? "null" : pi.versionName;
                String versionCode = "" + pi.versionCode;
                mMessage.put("versionName", versionName);
                mMessage.put("versionCode", versionCode);
            }
            // 通过反射拿到错误信息
            Field[] fields = Build.class.getFields();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    try {
                        mMessage.put(field.getName(), field.get(null).toString());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            Iterator<Map.Entry<String, String>> it = mMessage.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                String key = entry.getKey();
                String value = entry.getValue();
                Utils.print("key = " + key + "; value = " + value);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2.保存错误信息
     *
     * @param e Throwable
     */
    private void saveErrorMessages(Throwable e) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : mMessage.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        e.printStackTrace();
        e.printStackTrace(pw);
        Throwable cause = e.getCause();
        // 循环取出Cause
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = e.getCause();
            Utils.print("-------------");
            cause.printStackTrace();
            cause = null;
        }
        pw.close();
        String result = writer.toString();
        Utils.print("=========crash==result======= " + result);
        addCausedBy(result);
        sb.append(result);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
        String fileName = "crash-" + time + "-" + System.currentTimeMillis() + ".mog";
        // 有无SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getPath() + "/crash/";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            final File file = new File(path + fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            String err = e.toString();
            int index = err.length();
            if (err.indexOf(":") > 0) {
                index = err.indexOf(":");
            }
            updateFileName = mContext.getPackageName() + "*" + mMessage.get("versionName") + "*" + mMessage.get("DEVICE")
                    + "*" + err.substring(0, index) + "*" + time;
            filePath = file.getAbsolutePath();
            updateFile(updateFileName, filePath);

//            String url = "http://221.0.91.34:12080/ApplicationListenerManager/bugController/appBugUpload";
//            RequestParams requestParams = new RequestParams(mMessage);
//            requestParams.put("file", file);
//            Utils.print("url = " + url);
//            HttpUtil.getInstance(new AsyncHttpReturnListener() {
//                @Override
//                public void onFailure(String type, Call call, Exception e) {
//                    Utils.print("onFailure");
//                }
//
//                @Override
//                public void onResponse(String type, Call call, Response response, String body) {
//                    Utils.print("onResponse    ");
//                    Utils.print("onResponse   body = " + body);
//                }
//            }).httpPost(requestParams, url);
            Utils.print("Thread post end");
        }

        SystemClock.sleep(3000);
        System.exit(0);

    }

    private void updateFile(final String updateFileName, final String filePath) {
        UploadManager uploadManager = new UploadManager(new Recorder() {
            @Override
            public void set(String key, byte[] data) {
            }

            @Override
            public byte[] get(String key) {
                return new byte[0];
            }

            @Override
            public void del(String key) {
            }
        });
//...生成上传凭证，然后准备上传
        String accessKey = "-tdYOfKj1Mz7-G9fB_mxtlrQ6GaIFsGbaKBX3pXe";
        String secretKey = "Dwas3vLmdSRwaH51Yseb3pJ7m6uGs5aw9fXwom5B";
        String bucket = "buglistener";
//默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = updateFileName + ".txt";
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            com.qiniu.http.Response response = uploadManager.put(filePath, key, upToken);
            String result = response.bodyString();
            Utils.print("Result = " + result);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(result, DefaultPutRet.class);
            Utils.print(putRet.key);
            Utils.print(putRet.hash);
            isUpLoadFile = true;
        } catch (QiniuException ex) {
            Utils.print("catch (QiniuException ex)");
            ex.printStackTrace();
            com.qiniu.http.Response r = ex.response;
            Utils.print(ex.toString());
            try {
                Utils.print(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }

    }

    private void addCausedBy(String result) {

        int index = result.indexOf("Caused by:");
        String tempExceptionString = result.substring(Math.max(index, 0));
        String statusCode = "";
        if (tempExceptionString.contains("NullPointerException")) {
            statusCode = "1";
        } else if (tempExceptionString.contains("IndexOutOfBoundsException")) {
            statusCode = "2";
        } else if (tempExceptionString.contains("RuntimeException")) {
            statusCode = "5";
        } else if (tempExceptionString.contains("ArithmeticException")) {
            statusCode = "3";
        } else if (tempExceptionString.contains("ClassCastException")) {
            statusCode = "4";
        } else if (tempExceptionString.contains("OutOfMemoryError")) {
            statusCode = "6";
        } else if (tempExceptionString.contains("IllegalArgumentException")) {
            statusCode = "7";
        } else {
            statusCode = "-1";
        }

        mMessage.put("innerAppNet", Utils.getNetStyle(mContext));
        mMessage.put("innerAppTime", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
        mMessage.put("innerBugStyle", statusCode);
    }
}