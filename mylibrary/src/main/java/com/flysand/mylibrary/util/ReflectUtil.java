//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.util;

import com.flysand.mylibrary.sqlhelper.DBVO;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectUtil<T extends DBVO> {
    Object obj = null;

    public Class<?> getClass(Class<T> var1) {
        if(var1.getName().equals(DBVO.class.getName())) {
            throw new RuntimeException("请使用DBVO的子类");
        } else {
            while(!var1.getSuperclass().getName().equals(DBVO.class.getName())) {
                var1 = (Class<T>) var1.getSuperclass();
            }

            return var1;
        }
    }

    public ReflectUtil(Class<T> var1) {
        try {
            this.obj = this.getClass(var1).newInstance();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public ReflectUtil() {
    }

    public ReflectUtil(DBVO var1) {
        this.obj = var1;
    }

    public static List<String> getFieldsName(Class<?> var0) {
        ArrayList var1 = new ArrayList();
        Field[] var2 = var0.getDeclaredFields();
        Field[] var3 = var2;
        int var4 = var2.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Field var6 = var3[var5];
            var1.add(var6.getName());
        }

        return var1;
    }

    public static List<Field> getFields(Class<?> var0) {
        Field[] var1 = var0.getDeclaredFields();
        return Arrays.asList(var1);
    }

    public List<Method> obtainSetMethods(Class<T> var1) {
        ArrayList var2 = null;

        try {
            Method[] var3 = this.getClass(var1).getDeclaredMethods();
            int var4 = var3.length;
            var2 = new ArrayList();

            for(int var5 = 0; var5 < var4; ++var5) {
                String var6 = var3[var5].getName();
                if(var6.startsWith("set")) {
                    var2.add(var3[var5]);
                }
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return var2;
    }

    public List<Method> obtainGetMethods(Class<T> var1) {
        ArrayList var2 = null;

        try {
            Method[] var3 = this.getClass(var1).getDeclaredMethods();
            int var4 = var3.length;
            var2 = new ArrayList();

            for(int var5 = 0; var5 < var4; ++var5) {
                String var6 = var3[var5].getName();
                if(var6.startsWith("get") || var6.startsWith("is")) {
                    var2.add(var3[var5]);
                }
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return var2;
    }

    public void setter(String var1, Object var2, Class... var3) {
        try {
            Method var4 = this.obj.getClass().getMethod(var1, var3);
            var4.invoke(this.obj, new Object[]{var2});
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public Object getter(String var1, Class... var2) {
        Object var3 = null;

        try {
            Method var4 = this.obj.getClass().getMethod(var1, var2);
            var3 = var4.invoke(this.obj, new Object[0]);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return var3;
    }

    public Object getObj() {
        return this.obj;
    }
}
