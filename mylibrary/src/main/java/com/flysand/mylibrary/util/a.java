//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class a {
    public a() {
    }

    public String b(SQLiteDatabase var1, String var2) {
        if(TextUtils.isEmpty(var2)) {
            return "table name error!";
        } else {
            String var3 = null;
            Cursor var4 = null;

            try {
                String var5 = "select count(*) as c from sqlite_master where type =\'table\' and name =\'" + var2 + "\' ";
                var4 = var1.rawQuery(var5, (String[])null);
                if(var4.moveToNext()) {
                    int var6 = var4.getInt(0);
                    if(var6 <= 0) {
                        var3 = "table not exist";
                    }
                } else {
                    var3 = "table not exist";
                }

                var4.close();
            } catch (Exception var7) {
                var4.close();
                var3 = "exception";
            }

            return var3;
        }
    }
}
