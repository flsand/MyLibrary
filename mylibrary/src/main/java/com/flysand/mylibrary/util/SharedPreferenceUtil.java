//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

public class SharedPreferenceUtil {
    private String xmlFileName = "";
    private static SharedPreferenceUtil sharedPreferenceUtil;
    private Context context;

    public static SharedPreferenceUtil getInstance(Context var0) {
        if (sharedPreferenceUtil == null) {
            synchronized (SharedPreferenceUtil.class) {
                if (sharedPreferenceUtil == null) {
                    sharedPreferenceUtil = new SharedPreferenceUtil(var0);
                }
            }
        }

        return sharedPreferenceUtil;
    }

    private SharedPreferenceUtil(Context var1) {
        this.context = var1;
        this.xmlFileName = var1.getPackageName();
    }

    private SharedPreferences getPreference(String var1) {
        SharedPreferences var2 = null;
        if (var1 == null) {
            var2 = this.context.getSharedPreferences(this.xmlFileName, 0);
        } else {
            var2 = this.context.getSharedPreferences(var1, 0);
        }

        return var2;
    }

    public Map<String, ?> getAllShareKey(String var1) {
        try {
            return this.getPreference(var1).getAll();
        } catch (Exception var3) {
            return null;
        }
    }

    public void removeParam(String var1) {
        this.getPreference(var1).edit().clear().commit();
    }

    public void removeParam(String var1, String var2) {
        Editor var3 = this.getPreference(var1).edit();
        var3.remove(var2);
        var3.commit();
    }

    public boolean saveParam(String var1, Object var2) {
        return this.saveParam(this.xmlFileName, var1, var2);
    }

    public boolean saveParam(String var1, String var2, Object var3) {
        if (TextUtils.isEmpty(var2)) {
            return false;
        } else {
            SharedPreferences var4 = this.getPreference(var1);
            Editor var5 = var4.edit();
            if (!(var3 instanceof Integer) && !(var3 instanceof Boolean) && !(var3 instanceof Float) && !(var3 instanceof Double) && !(var3 instanceof Long) && !(var3 instanceof String)) {
                ByteArrayOutputStream var6 = new ByteArrayOutputStream();
                ObjectOutputStream var7 = null;
                try {
                    var7 = new ObjectOutputStream(var6);
                    var7.writeObject(var3);
                    String var8 = new String(var6.toByteArray());
                    Utils.print("保存的是什么 var8 = " + var8);
                    Utils.print("保存的是什么 length = " + var8.length());
                    var5.putString(var2, var8);
                    var5.commit();
                    return true;
                } catch (Exception var9) {
                    var9.printStackTrace();
                    return false;
                } catch (OutOfMemoryError var10) {
                    var10.printStackTrace();
                    Utils.print("catch (OutOfMemoryError var10  = " + var10.toString());
                    return false;
                } finally {
                    if (var7 != null) {
                        try {
                            var7.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (var6 != null) {
                        try {
                            var6.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                var5.putString(var2, "" + var3);
                var5.commit();
                return true;
            }
        }
    }

    public <T> T readObj(Class<T> var1, String var2) {
        return this.readObj(this.xmlFileName, var2);
    }

    public <T> T readObj(String var1, String var2) {
        if (TextUtils.isEmpty(var2)) {
            return null;
        } else {
            SharedPreferences var3 = this.getPreference(var1);
            Editor var4 = var3.edit();
            String var5 = var3.getString(var2, "");
            byte[] var6 = var5.getBytes();
            ByteArrayInputStream var7 = new ByteArrayInputStream(var6);

            try {
                ObjectInputStream var8 = new ObjectInputStream(var7);
                return (T) var8.readObject();
            } catch (Exception var9) {
                var9.printStackTrace();
                return null;
            }
        }
    }

    public <T> List<T> readList(Class<T> var1, String var2) {
        return this.readList(var1, this.xmlFileName, var2);
    }

    public <T> List<T> readList(Class<T> var1, String var2, String var3) {
        if (TextUtils.isEmpty(var3)) {
            return null;
        } else {
            SharedPreferences var4 = this.getPreference(var2);
            Editor var5 = var4.edit();
            String var6 = var4.getString(var3, "");
            byte[] var7 = var6.getBytes();
            ByteArrayInputStream var8 = new ByteArrayInputStream(var7);

            try {
                ObjectInputStream var9 = new ObjectInputStream(var8);
                return (List) var9.readObject();
            } catch (Exception var10) {
                var10.printStackTrace();
                return null;
            }
        }
    }

    public String readStrParam(String var1, String var2) {
        return this.readStrParam(this.xmlFileName, var1, var2);
    }

    public String readStrParam(String var1, String var2, String var3) {
        SharedPreferences var4 = this.getPreference(var1);
        return var4.getString(var2, var3);
    }

    public int readIntParam(String var1, int var2) {
        return this.readIntParam(this.xmlFileName, var1, var2);
    }

    public int readIntParam(String var1, String var2, int var3) {
        SharedPreferences var4 = this.getPreference(var1);
        String var5 = var4.getString(var2, var3 + "");
        return TextUtils.isEmpty(var5) ? var3 : Integer.parseInt(var5);
    }

    public boolean readBooleanParam(String var1, boolean var2) {
        return this.readBooleanParam(this.xmlFileName, var1, var2);
    }

    public boolean readBooleanParam(String var1, String var2, boolean var3) {
        SharedPreferences var4 = this.getPreference(var1);
        String var5 = var4.getString(var2, var3 + "");
        return TextUtils.isEmpty(var5) ? var3 : Boolean.parseBoolean(var5);
    }

    public float readFloatParam(String var1, float var2) {
        return this.readFloatParam(this.xmlFileName, var1, var2);
    }

    public float readFloatParam(String var1, String var2, float var3) {
        SharedPreferences var4 = this.getPreference(var1);
        String var5 = var4.getString(var2, var3 + "");
        return TextUtils.isEmpty(var5) ? var3 : Float.parseFloat(var5);
    }

    public long readLongParam(String var1, long var2) {
        return this.readLongParam(this.xmlFileName, var1, var2);
    }

    public long readLongParam(String var1, String var2, long var3) {
        SharedPreferences var5 = this.getPreference(var1);
        String var6 = var5.getString(var2, var3 + "");
        return TextUtils.isEmpty(var6) ? var3 : Long.parseLong(var6);
    }
}
