//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtil {
    public JSONUtil() {
    }

    public static <T> T toJavaBean(Class<T> var0, String var1) {
        return (new Gson()).fromJson(var1, var0);
    }

    public static <T> T toJavaBean(Class<T> var0, JSONObject var1) {
        return toJavaBean(var0, var1.toString());
    }

    public static String toJsonString(Object var0) {
        return (new Gson()).toJson(var0);
    }

    public static <T> ArrayList<T> toJavaBeanList(Class<T> var0, String var1) {
        JsonParser var2 = new JsonParser();
        JsonElement var3 = var2.parse(var1);
        JsonArray var4 = null;
        if (var3.isJsonArray()) {
            var4 = var3.getAsJsonArray();
        }

        ArrayList var5 = new ArrayList();
        int var6 = var4.size();

        for (int var7 = 0; var7 < var6; ++var7) {
            Object var8 = toJavaBean(var0, var4.get(var7).toString());
            var5.add(var8);
        }

        return var5;
    }

    public static <T> ArrayList<T> toJavaBeanList(Class<T> var0, JSONArray var1) {
        ArrayList var2 = new ArrayList();
        int var3 = var1.length();
        try {
            for (int var4 = 0; var4 < var3; ++var4) {
                Object var5 = toJavaBean(var0, var1.getJSONObject(var4));
                var2.add(var5);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return var2;
    }
}
