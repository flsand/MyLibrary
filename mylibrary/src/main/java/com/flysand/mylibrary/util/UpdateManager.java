//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.AttributeSet;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;

import com.flysand.mylibrary.listener.APPUpdateListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressLint({"HandlerLeak"})
public class UpdateManager {
    private Context context;
    private String apkUrl;
    private Dialog downloadDialog;
    private String saveFilePath;
    private ProgressBar mProgress;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private int progress;
    private Thread downLoadThread;
    private boolean interceptFlag = false;
    private APPUpdateListener listener;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message var1) {
            switch (var1.what) {
                case 1:
                    UpdateManager.this.mProgress.setProgress(UpdateManager.this.progress);
                    break;
                case 2:
                    UpdateManager.this.installApk();
            }

        }
    };

    public String getApplicationName(Context var0) {
        PackageManager var1 = null;
        ApplicationInfo var2 = null;

        try {
            var1 = var0.getPackageManager();
            var2 = var1.getApplicationInfo(var0.getPackageName(), 0);
        } catch (Exception var4) {
            var2 = null;
        }

        String var3 = (String) var1.getApplicationLabel(var2);
        return var3;
    }

    private Runnable mdownApkRunnable = new Runnable() {
        public void run() {
            try {
                URL var1 = new URL(UpdateManager.this.apkUrl);
                HttpURLConnection var2 = (HttpURLConnection) var1.openConnection();
                var2.connect();
                int var3 = var2.getContentLength();
                InputStream var4 = var2.getInputStream();
                File var5 = new File(UpdateManager.this.saveFilePath);
                if (!var5.exists()) {
                    var5.mkdirs();
                }

                String var6 = getApplicationName(UpdateManager.this.context) + ".apk";
                File var7 = new File(UpdateManager.this.saveFilePath + File.separator + var6);
                FileOutputStream var8 = new FileOutputStream(var7);
                int var9 = 0;
                byte[] var10 = new byte[1024];

                do {
                    int var11 = var4.read(var10);
                    var9 += var11;
                    UpdateManager.this.progress = (int) ((float) var9 / (float) var3 * 100.0F);
                    UpdateManager.this.mHandler.sendEmptyMessage(1);
                    if (var11 <= 0) {
                        UpdateManager.this.mHandler.sendEmptyMessage(2);
                        break;
                    }

                    var8.write(var10, 0, var11);
                } while (!UpdateManager.this.interceptFlag);

                var8.close();
                var4.close();
            } catch (Exception var12) {
                var12.printStackTrace();
            }

        }
    };

    @TargetApi(19)
    public String getFilePath(Context var0, String var1) {
        String var2 = var0.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        File var3 = new File(var2 + File.separator + var1);
        if (!var3.exists()) {
            var3.mkdirs();
        }

        return var3.getAbsolutePath();
    }

    public UpdateManager(Context var1, String var2, APPUpdateListener var3) {
        this.context = var1;
        this.listener = var3;
        if (VERSION.SDK_INT > 19) {
            this.saveFilePath = getFilePath(var1, "apk");
        } else {
            this.saveFilePath = getPicPath(var1, "apk");
        }

        this.apkUrl = var2;
    }

    public static String getPicPath(Context var0, String var1) {
        String var2 = var0.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        File var3 = new File(var2 + File.separator + var1);
        if (!var3.exists()) {
            var3.mkdirs();
        }

        return var3.getAbsolutePath();
    }

    public void showDefaultDownloadDialog() {
        Builder var1 = new Builder(this.context);
        var1.setTitle("软件版本更新");
        this.mProgress = new ProgressBar(this.context, (AttributeSet) null, 16842872);
        LayoutParams var2 = new LayoutParams(-1, -2);
        this.mProgress.setLayoutParams(var2);
        var1.setView(this.mProgress);
        var1.setNegativeButton("取消", new OnClickListener() {
            public void onClick(DialogInterface var1, int var2) {
                var1.dismiss();
                UpdateManager.this.interceptFlag = true;
                UpdateManager.this.deleteApk();
                if (UpdateManager.this.listener != null) {
                    UpdateManager.this.listener.downloadCancel();
                }

            }
        });
        this.downloadDialog = var1.create();
        this.downloadDialog.setCancelable(false);
        this.downloadDialog.show();
        this.downloadApk();
    }

    private void downloadApk() {
        this.downLoadThread = new Thread(this.mdownApkRunnable);
        this.downLoadThread.start();
    }

    protected void renameFile(String var1, String var2, String var3) {
        if (!var2.equals(var3)) {
            File var4 = new File(var1 + "/" + var2);
            File var5 = new File(var1 + "/" + var3);
            if (!var4.exists()) {
                return;
            }

            if (var5.exists()) {
                System.out.println(var3 + "已经存在！");
            } else {
                var4.renameTo(var5);
            }
        } else {
            System.out.println("新文件名和旧文件名相同...");
        }

    }

    private void installApk() {
        if (this.listener != null) {
            this.listener.successDownload();
        }

        this.downloadDialog.dismiss();
        File var1 = new File(this.saveFilePath + File.separator + getApplicationName(this.context) + ".apk");
        if (var1.exists()) {
            Intent var2 = new Intent("android.intent.action.VIEW");
            var2.addFlags(268435456);
            if (VERSION.SDK_INT > 23) {
                Uri var3 = FileProvider.getUriForFile(this.context, this.context.getPackageName() + ".fileprovider", var1);
                var2.addFlags(1);
                var2.setDataAndType(var3, "application/vnd.android.package-archive");
            } else {
                var2.setDataAndType(Uri.parse("file://" + var1.toString()), "application/vnd.android.package-archive");
            }

            this.context.startActivity(var2);
        }
    }

    public void deleteApk() {
        FileUtils.deleteFile(new File(this.saveFilePath));
    }
}
