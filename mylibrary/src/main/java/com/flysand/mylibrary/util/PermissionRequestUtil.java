//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.util;

import android.app.Activity;
import android.os.Build.VERSION;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PermissionRequestUtil {
    public static final int ACCESS_COARSE_LOCATION = 0;
    public static final int ACCESS_FINE_LOCATION = 1;
    public static final int WRITE_CONTACTS = 2;
    public static final int GET_ACCOUNTS = 3;
    public static final int READ_CONTACTS = 4;
    public static final int READ_CALL_LOG = 5;
    public static final int READ_PHONE_STATE = 6;
    public static final int CALL_PHONE = 7;
    public static final int WRITE_CALL_LOG = 8;
    public static final int USE_SIP = 9;
    public static final int PROCESS_OUTGOING_CALLS = 10;
    public static final int ADD_VOICEMAIL = 11;
    public static final int READ_CALENDAR = 12;
    public static final int WRITE_CALENDAR = 13;
    public static final int CAMERA = 14;
    public static final int BODY_SENSORS = 15;
    public static final int READ_EXTERNAL_STORAGE = 16;
    public static final int WRITE_EXTERNAL_STORAGE = 17;
    public static final int RECORD_AUDIO = 18;
    public static final int READ_SMS = 19;
    public static final int RECEIVE_WAP_PUSH = 20;
    public static final int RECEIVE_MMS = 21;
    public static final int SEND_SMS = 22;
    public static final int READ_CELL_BROADCASTS = 23;
    private static Map<Integer, String> permissionMap = new HashMap();
    private static PermissionRequestUtil instance;

    public static PermissionRequestUtil getInstance() {
        if(instance == null) {
            Class var0 = PermissionRequestUtil.class;
            synchronized(PermissionRequestUtil.class) {
                if(instance == null) {
                    instance = new PermissionRequestUtil();
                }
            }
        }

        return instance;
    }

    private PermissionRequestUtil() {
        permissionMap.put(Integer.valueOf(0), "android.permission.ACCESS_COARSE_LOCATION");
        permissionMap.put(Integer.valueOf(1), "android.permission.ACCESS_FINE_LOCATION");
        permissionMap.put(Integer.valueOf(2), "android.permission.WRITE_CONTACTS");
        permissionMap.put(Integer.valueOf(3), "android.permission.GET_ACCOUNTS");
        permissionMap.put(Integer.valueOf(4), "android.permission.READ_CONTACTS");
        permissionMap.put(Integer.valueOf(5), "android.permission.READ_CALL_LOG");
        permissionMap.put(Integer.valueOf(6), "android.permission.READ_PHONE_STATE");
        permissionMap.put(Integer.valueOf(7), "android.permission.CALL_PHONE");
        permissionMap.put(Integer.valueOf(8), "android.permission.WRITE_CALL_LOG");
        permissionMap.put(Integer.valueOf(9), "android.permission.USE_SIP");
        permissionMap.put(Integer.valueOf(10), "android.permission.PROCESS_OUTGOING_CALLS");
        permissionMap.put(Integer.valueOf(11), "com.android.voicemail.permission.ADD_VOICEMAIL");
        permissionMap.put(Integer.valueOf(12), "android.permission.READ_CALENDAR");
        permissionMap.put(Integer.valueOf(13), "android.permission.WRITE_CALENDAR");
        permissionMap.put(Integer.valueOf(14), "android.permission.CAMERA");
        permissionMap.put(Integer.valueOf(15), "android.permission.BODY_SENSORS");
        permissionMap.put(Integer.valueOf(16), "android.permission.READ_EXTERNAL_STORAGE");
        permissionMap.put(Integer.valueOf(17), "android.permission.WRITE_EXTERNAL_STORAGE");
        permissionMap.put(Integer.valueOf(18), "android.permission.RECORD_AUDIO");
        permissionMap.put(Integer.valueOf(19), "android.permission.READ_SMS");
        permissionMap.put(Integer.valueOf(20), "android.permission.RECEIVE_WAP_PUSH");
        permissionMap.put(Integer.valueOf(21), "android.permission.RECEIVE_MMS");
        permissionMap.put(Integer.valueOf(22), "android.permission.SEND_SMS");
    }

    public boolean requestPermission(Activity var1, int... var2) {
        if(VERSION.SDK_INT < 23) {
            return true;
        } else {
            int var3 = var2.length;
            if(var3 == 0) {
                return false;
            } else {
                ArrayList var4 = new ArrayList();

                for(int var5 = 0; var5 < var3; ++var5) {
                    String var6 = (String)permissionMap.get(Integer.valueOf(var2[var5]));
                    if(TextUtils.isEmpty(var6)) {
                        return false;
                    }

                    if(ContextCompat.checkSelfPermission(var1, var6) != 0) {
                        var4.add(var6);
                    }
                }

                if(var4.size() == 0) {
                    return true;
                } else {
                    var3 = var4.size();
                    String[] var7 = new String[var3];

                    for(int var8 = 0; var8 < var3; ++var8) {
                        var7[var8] = (String)var4.get(var8);
                    }

                    ActivityCompat.requestPermissions(var1, var7, var2[0]);
                    return false;
                }
            }
        }
    }
}
