//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.implement;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.flysand.mylibrary.listener.DBOperListener;
import com.flysand.mylibrary.sqlhelper.DBVO;
import com.flysand.mylibrary.util.ReflectUtil;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressLint({"DefaultLocale"})
public class a implements DBOperListener {
    public a() {
    }

    private List<String> a(SQLiteDatabase var1, String var2) {
        try {
            String var3 = "select * from " + var2 + " limit 1";
            Cursor var4 = var1.rawQuery(var3, (String[])null);
            ArrayList var5 = new ArrayList();
            if(var4.moveToNext()) {
                int var6 = var4.getColumnCount();

                for(int var7 = 0; var7 < var6; ++var7) {
                    var5.add(var4.getColumnName(var7));
                }

                var5.remove("_id");
            }

            var4.close();
            return var5;
        } catch (Exception var8) {
            var8.printStackTrace();
            return null;
        }
    }

    private <T extends DBVO> List<String> a(SQLiteDatabase var1, Class<T> var2) {
        ReflectUtil var3 = new ReflectUtil();
        String var4 = this.a(var3.getClass(var2));
        List var5 = var3.obtainGetMethods(var2);
        int var6 = var5.size();
        if(var6 == 0) {
            return null;
        } else {
            StringBuffer var7 = new StringBuffer();
            var7.append("CREATE TABLE IF NOT EXISTS ").append(var4);
            var7.append(" (_id INTEGER PRIMARY KEY,");
            ArrayList var8 = new ArrayList();

            for(int var9 = 0; var9 < var6; ++var9) {
                Method var10 = (Method)var5.get(var9);
                String var11 = var10.getName();
                String var12 = var10.getReturnType().getName();
                if(var11.startsWith("get")) {
                    var7.append(var11.substring(3).toLowerCase());
                    var8.add(var11.substring(3).toLowerCase());
                    if(var12.contains("String")) {
                        var7.append(" TEXT,");
                    } else {
                        var7.append(" INT,");
                    }
                } else {
                    var7.append(var11.substring(2).toLowerCase());
                    var8.add(var11.substring(2).toLowerCase());
                    var7.append(" INT,");
                }
            }

            var7.deleteCharAt(var7.length() - 1);
            var7.append(")");
            var1.execSQL(var7.toString());
            return var8;
        }
    }

    private <T extends DBVO> void b(SQLiteDatabase var1, Class<T> var2) {
        ReflectUtil var3 = new ReflectUtil();
        String var4 = this.a(var3.getClass(var2));
        List var5 = this.a(var1, var4);
        if(var5 != null) {
            if(var5.size() == 0) {
                var1.execSQL("DROP TABLE IF EXISTS " + var4);
            } else {
                List var6 = this.a(var1, var2);
                if(var6 != null) {
                    StringBuffer var7 = new StringBuffer();

                    for(int var8 = 0; var8 < var6.size(); ++var8) {
                        if(!var5.contains(var6.get(var8))) {
                            var7.append((String)var6.get(var8)).append(",");
                        }
                    }

                    if(var7.length() > 0) {
                        var7.deleteCharAt(var7.length() - 1);
                        StringBuffer var9 = new StringBuffer();
                        var9.append("alter table ").append(var4).append(" add column ");
                        var9.append(var7);
                        var1.execSQL(var9.toString());
                    }

                }
            }
        }
    }

    public <T extends DBVO> long insert(SQLiteDatabase var1, T var2) {
        if(var2 == null) {
            return 0L;
        } else {
            ReflectUtil var3 = new ReflectUtil(var2);
            Class var4 = var3.getClass(var2.getClass());
            this.b(var1, var4);
            String var5 = this.a(var4);
            List var6 = var3.obtainGetMethods(var4);
            int var7 = var6.size();
            if((new com.flysand.mylibrary.util.a()).b(var1, var5) != null && this.a(var1, var4) == null) {
                return 0L;
            } else {
                ContentValues var8 = new ContentValues();

                for(int var9 = 0; var9 < var7; ++var9) {
                    Method var10 = (Method)var6.get(var9);
                    Class var11 = var10.getReturnType();
                    String var12 = var10.getName();
                    String var13 = var12.substring(3).toLowerCase();
                    Class[] var14 = var10.getParameterTypes();
                    Object var15 = var3.getter(var12, var14);

                    try {
                        if(var11.getName().contains("boolean")) {
                            var8.put(var12.substring(2).toLowerCase(), (Boolean)var15);
                        } else if(var11.getName().contains("int")) {
                            var8.put(var13, (Integer)var15);
                        } else if(var11.getName().contains("float")) {
                            var8.put(var13, (Float)var15);
                        } else if(var11.getName().contains("double")) {
                            var8.put(var13, (Double)var15);
                        } else {
                            var8.put(var13, (String)var15);
                        }
                    } catch (Exception var19) {
                        try {
                            var8.put(var13, (new Gson()).toJson(var15));
                        } catch (Exception var18) {
                            var18.printStackTrace();
                        }
                    }
                }

                return var1.insert(var5, (String)null, var8);
            }
        }
    }

    public <T extends DBVO> List<T> query(SQLiteDatabase var1, Class<T> var2, int var3, String... var4) {
        if(var4 != null && var4.length % 2 != 0) {
            return null;
        } else {
            ReflectUtil var5 = new ReflectUtil();
            var2 = var5.getClass(var2);
            String var6 = this.a(var2);
            ArrayList var7 = new ArrayList();
            if((new com.flysand.mylibrary.util.a()).b(var1, var6) == null) {
                List var8 = var5.obtainSetMethods(var2);
                Gson var9 = new Gson();
                StringBuffer var10 = new StringBuffer();
                var10.append("select * from ").append(var6).append(this.a(0, var4));
                if(var3 > 0) {
                    var10.append(" order by _id desc").append(" limit ").append(var3);
                }

                Cursor var11 = var1.rawQuery(var10.toString(), (String[])null);
                int var12 = var8.size();

                while(var11.moveToNext()) {
                    var5 = new ReflectUtil(var2);

                    for(int var13 = 0; var13 < var12; ++var13) {
                        Method var14 = (Method)var8.get(var13);
                        String var15 = var14.getName();
                        Class[] var16 = var14.getParameterTypes();
                        Class var17 = var16[0];
                        Field var18 = null;
                        String var19 = var15.substring(3);
                        if(var17.getName().contains("boolean")) {
                            var19 = var15.substring(2);
                        }

                        try {
                            var18 = var2.getDeclaredField(var19.substring(0, 1).toLowerCase() + var19.substring(1));
                        } catch (Exception var25) {
                            try {
                                if(var18 == null && var17.getName().contains("boolean")) {
                                    var18 = var2.getDeclaredField("is" + var19);
                                }
                            } catch (Exception var24) {
                                ;
                            }
                        }

                        try {
                            if(var18 != null) {
                                if(var17.getName().contains("int")) {
                                    int var20 = var11.getInt(var11.getColumnIndex(var15.toLowerCase().substring(3)));
                                    var5.setter(var15, Integer.valueOf(var20), var16);
                                } else {
                                    String var26;
                                    if(var17.getName().contains("boolean")) {
                                        var26 = var11.getString(var11.getColumnIndex(var15.toLowerCase().substring(3)));
                                        boolean var21 = !var26.equals("0");
                                        var5.setter(var15, Boolean.valueOf(var21), var16);
                                    } else if(var17.getName().contains("float")) {
                                        float var27 = var11.getFloat(var11.getColumnIndex(var15.toLowerCase().substring(3)));
                                        var5.setter(var15, Float.valueOf(var27), var16);
                                    } else if(var17.getName().contains("double")) {
                                        double var28 = var11.getDouble(var11.getColumnIndex(var15.toLowerCase().substring(3)));
                                        var5.setter(var15, Double.valueOf(var28), var16);
                                    } else if(var17.getName().contains("String")) {
                                        var26 = var11.getString(var11.getColumnIndex(var15.toLowerCase().substring(3)));
                                        var5.setter(var15, var26, var16);
                                    } else {
                                        var26 = var11.getString(var11.getColumnIndex(var15.toLowerCase().substring(3)));
                                        if(var26 != null) {
                                            var26 = var26.trim();
                                            JsonReader var29 = new JsonReader(new StringReader(var26));
                                            var29.setLenient(true);
                                            Object var22 = var9.fromJson(var29, var18.getGenericType());
                                            var5.setter(var15, var22, var16);
                                        }
                                    }
                                }
                            }
                        } catch (Exception var23) {
                            var23.printStackTrace();
                        }
                    }

                    var7.add((DBVO)var5.getObj());
                }
            }

            return var7;
        }
    }

    public <T extends DBVO> int delete(SQLiteDatabase var1, Class<T> var2, String... var3) {
        ReflectUtil var4 = new ReflectUtil();
        var2 = var4.getClass(var2);
        String var5 = this.a(var2);
        if(var3 != null && var3.length % 2 != 0) {
            return 0;
        } else if((new com.flysand.mylibrary.util.a()).b(var1, var5) == null) {
            StringBuffer var6 = new StringBuffer();
            var6.append("delete from ");
            var6.append(var5);
            if(var3 != null && var3.length > 0) {
                var6.append(this.a(0, var3));
            }

            var1.execSQL(var6.toString());
            return 1;
        } else {
            return 0;
        }
    }

    public int update(SQLiteDatabase var1, DBVO var2, int var3, String... var4) {
        if(var4 != null && var4.length != 0) {
            if(var4.length <= var3) {
                return 0;
            } else if(var2 == null) {
                return 0;
            } else {
                ReflectUtil var5 = new ReflectUtil(var2);
                String var6 = this.a(var5.getClass(var2.getClass()));
                if((new com.flysand.mylibrary.util.a()).b(var1, var6) != null) {
                    return 0;
                } else {
                    StringBuffer var7 = new StringBuffer();
                    var7.append("update ").append(var6).append(" set ");
                    if(var3 == 0) {
                        List var8 = var5.obtainGetMethods(var2.getClass());
                        int var9 = var8.size();
                        if(var9 == 0) {
                            return 0;
                        }

                        for(int var10 = 0; var10 < var9; ++var10) {
                            Method var11 = (Method)var8.get(var10);
                            Class var12 = var11.getReturnType();
                            Object var13 = null;

                            try {
                                var13 = var11.invoke(var2, new Object[0]);
                                if(var13 == null) {
                                    continue;
                                }
                            } catch (Exception var22) {
                                var22.printStackTrace();
                                return 0;
                            }

                            String var14 = var11.getName();
                            String var15 = var14.substring(3);
                            if(var12.getName().contains("boolean")) {
                                var15 = var14.substring(2);
                            }

                            Field var16 = null;
                            if(var12.getName().contains("boolean")) {
                                try {
                                    var16 = var5.getClass(var2.getClass()).getDeclaredField("is" + var15);
                                } catch (Exception var21) {
                                    try {
                                        var16 = var5.getClass(var2.getClass()).getDeclaredField(var15.substring(0, 1).toLowerCase() + var15.substring(1));
                                    } catch (Exception var20) {
                                        var20.printStackTrace();
                                        return 0;
                                    }
                                }

                                boolean var17 = ((Boolean)var13).booleanValue();
                                var7.append(var16.getName().toLowerCase()).append(" = ").append(var17?1:0);
                            } else {
                                try {
                                    var16 = var5.getClass(var2.getClass()).getDeclaredField(var15.substring(0, 1).toLowerCase() + var15.substring(1));
                                    if(var12.getName().contains("String")) {
                                        String var23 = (String)var13;
                                        var7.append(var16.getName().toLowerCase()).append(" = \'").append(var23).append("\'");
                                    } else {
                                        var7.append(var16.getName().toLowerCase()).append(" = ").append(var13);
                                    }
                                } catch (Exception var19) {
                                    var19.printStackTrace();
                                    return 0;
                                }
                            }

                            var7.append(",");
                        }

                        var7.deleteCharAt(var7.length() - 1);
                    } else {
                        var7.append(this.a(var5, "set", var2, 0, var3 - 1, var4));
                    }

                    var7.append(" where ");
                    var7.append(this.a(var5, "where", var2, var3, var4.length - 1, var4));
                    var1.execSQL(var7.toString());
                    return 1;
                }
            }
        } else {
            return 0;
        }
    }

    private String a(ReflectUtil var1, String var2, DBVO var3, int var4, int var5, String... var6) {
        if(var6 != null && var6.length != 0) {
            if(var6.length <= var5) {
                return "";
            } else {
                StringBuffer var7 = new StringBuffer();
                Field var8 = null;

                for(int var9 = var4; var9 <= var5; var9 += 2) {
                    try {
                        var7.append(var6[var9].toLowerCase());
                        var8 = var1.getClass(var3.getClass()).getDeclaredField(var6[var9]);
                        Class var10 = var8.getType();
                        Method var11 = null;
                        if(var10.getName().contains("boolean")) {
                            var11 = var1.getClass(var3.getClass()).getDeclaredMethod("is" + var6[var9].substring(0, 1).toUpperCase() + var6[var9].substring(1), new Class[0]);
                        } else {
                            var11 = var1.getClass(var3.getClass()).getDeclaredMethod("get" + var6[var9].substring(0, 1).toUpperCase() + var6[var9].substring(1), new Class[0]);
                        }

                        Object var12 = var11.invoke(var3, new Object[0]);
                        if(var10.getName().contains("boolean")) {
                            boolean var13 = ((Boolean)var12).booleanValue();
                            var7.append(" = ").append(var13?1:0);
                        } else if(var10.getName().contains("String")) {
                            String var15 = (String)var12;
                            var7.append(" = \'").append(var15).append("\'");
                        } else {
                            var7.append(" = ").append(var12);
                        }

                        if(var2.equals("set")) {
                            var7.append(",");
                        } else {
                            var7.append(" and ");
                        }
                    } catch (Exception var14) {
                        var14.printStackTrace();
                        return "";
                    }
                }

                if(var2.equals("set")) {
                    return var7.substring(0, var7.length() - 1);
                } else {
                    return var7.substring(0, var7.length() - 5);
                }
            }
        } else {
            return "";
        }
    }

    private String a(Class<?> var1) {
        String var2 = var1.getName();
        if(var2.contains(".")) {
            var2 = var2.substring(var2.lastIndexOf(".") + 1);
        }

        return var2;
    }

    private StringBuffer a(int var1, String... var2) {
        StringBuffer var3 = new StringBuffer();
        if(var2 != null && var2.length > 0) {
            var3.append(" where ");

            for(; var1 < var2.length; var1 += 2) {
                var3.append(var2[var1]);
                if(!var2[var1 + 1].contains(",")) {
                    var3.append(" = \'");
                    var3.append(var2[var1 + 1]);
                    var3.append("\'");
                } else {
                    String[] var4 = var2[var1 + 1].split(",");
                    var3.append(" in (");

                    for(int var5 = 0; var5 < var4.length; ++var5) {
                        var3.append("\'");
                        var3.append(var4[var5]);
                        var3.append("\',");
                    }

                    var3.deleteCharAt(var3.length() - 1);
                    var3.append(")");
                }

                if(var1 < var2.length - 2) {
                    var3.append(" and ");
                }
            }
        }

        return var3;
    }
}
