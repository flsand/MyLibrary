//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.listener;

import android.database.sqlite.SQLiteDatabase;

import com.flysand.mylibrary.sqlhelper.DBVO;

import java.util.List;

public interface DBOperListener {
    <T extends DBVO> long insert(SQLiteDatabase var1, T var2);

    <T extends DBVO> List<T> query(SQLiteDatabase var1, Class<T> var2, int var3, String... var4);

    <T extends DBVO> int delete(SQLiteDatabase var1, Class<T> var2, String... var3);

    <T extends DBVO> int update(SQLiteDatabase var1, T var2, int var3, String... var4);
}
