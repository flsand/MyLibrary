//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.sqlhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.flysand.mylibrary.base.ActivityLifecycle;
import com.flysand.mylibrary.listener.DBOperListener;

import java.lang.reflect.Method;
import java.util.List;

public class DBHelper {
    private static DBHelper aL = null;
    private static String aM = null;
    private static int aN = 1;
    private DBHelper.a aO;
    private DBOperListener aP;

    private DBHelper(Context var1) {
        aM = var1.getPackageName().replace(".", "");
        this.aO = new DBHelper.a(var1);
        this.aP = new com.flysand.mylibrary.implement.a();
        ActivityLifecycle.getInstance(var1.getApplicationContext());
    }

    public static DBHelper getInstance(Context var0) {
        if(aL == null) {
            Class var1 = DBHelper.class;
            synchronized(DBHelper.class) {
                if(aL == null) {
                    aL = new DBHelper(var0);
                }
            }
        }

        return aL;
    }

    public <T extends DBVO> long updateInsert(T var1) {
        String var2 = var1.findKeyForVO();
        if(TextUtils.isEmpty(var2)) {
            return this.insert(var1);
        } else {
            try {
                Method var3 = var1.getClass().getMethod("get" + var2.substring(0, 1).toUpperCase() + var2.substring(1), new Class[0]);
                Object var4 = var3.invoke(var1, new Object[0]);
                int var5 = this.update(var1, 0, new String[]{var2, String.valueOf(var4)});
                return var5 > 0?(long)var5:this.insert(var1);
            } catch (Exception var6) {
                var6.printStackTrace();
                return 0L;
            }
        }
    }

    public <T extends DBVO> long deleteInsert(T var1) {
        String var2 = var1.findKeyForVO();
        if(TextUtils.isEmpty(var2)) {
            return this.insert(var1);
        } else {
            try {
                Method var3 = var1.getClass().getMethod("get" + var2.substring(0, 1).toUpperCase() + var2.substring(1), new Class[0]);
                Object var4 = var3.invoke(var1, new Object[0]);
                this.delete(var1.getClass(), new String[]{var2.toLowerCase(), (String)var4});
                return this.insert(var1);
            } catch (Exception var5) {
                var5.printStackTrace();
                return 0L;
            }
        }
    }

    public long insert(DBVO var1) {
        return this.aP.insert(this.i(), var1);
    }

    public <T extends DBVO> List<T> query(Class<T> var1, String... var2) {
        return this.query(var1, 0, var2);
    }

    public <T extends DBVO> List<T> query(Class<T> var1, int var2, String... var3) {
        return this.aP.query(this.i(), var1, var2, var3);
    }

    public <T extends DBVO> int delete(Class<T> var1, String... var2) {
        return this.aP.delete(this.i(), var1, var2);
    }

    public <T extends DBVO> int update(DBVO var1, int var2, String... var3) {
        return this.aP.update(this.i(), var1, var2, var3);
    }

    private SQLiteDatabase i() {
        return this.aO.getWritableDatabase();
    }

    private class a extends SQLiteOpenHelper {
        public a(Context var2) {
            super(var2, DBHelper.aM, (CursorFactory)null, DBHelper.aN);
        }

        public void onCreate(SQLiteDatabase var1) {
        }

        public void onUpgrade(SQLiteDatabase var1, int var2, int var3) {
        }
    }
}
