package com.flysand.mylibrary.http;

import android.text.TextUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by FlySand on 2017\7\24 0024.
 */

public class RequestParams implements Serializable {


    private ConcurrentHashMap<String, String> params = new ConcurrentHashMap();
    private List<FileInput> files = new ArrayList<>();


    private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");
    private MediaType mediaType;


    public RequestParams() {
        this((Map) null);
    }

    public RequestParams(Map<String, String> var1) {
        if (var1 != null) {
            Iterator var2 = var1.entrySet().iterator();
            while (var2.hasNext()) {
                Map.Entry var3 = (Map.Entry) var2.next();
                this.put((String) var3.getKey(), (String) var3.getValue());
            }
        }
    }

    public RequestParams(final String var1, final String var2) {
        this.put(var1, var2);
    }

    public RequestParams(Object... var1) {
        int var2 = var1.length;
        if (var2 % 2 != 0) {
            throw new IllegalArgumentException("Supplied arguments must be even");
        } else {
            for (int var3 = 0; var3 < var2; var3 += 2) {
                String var4 = String.valueOf(var1[var3]);
                String var5 = String.valueOf(var1[var3 + 1]);
                this.put(var4, var5);
            }
        }
    }

    public void put(String key, File var2) {
        this.put(key, var2.getName(), (File) var2);
    }


    public void put(String key, String filename, File file) {
        if (file == null || !file.exists()) {
            throw new RuntimeException("FileNotFoundException");
        } else if (TextUtils.isEmpty(key)) {
            throw new RuntimeException("KeyIsEmpty");
        } else {
            files.add(new FileInput(key, filename, file));
        }

    }

    public void put(String key, File[] var2) {
        for (File file : var2) {
            this.put(key, file);
        }

    }
    public void put(String key, List<File> var2) {
        for (File file : var2) {
            this.put(key, file);
        }
    }

    public void put(String key, String val) {
        if (key != null && val != null) {
            params.put(key, val);
        }
    }

    public void put(String var1, Object var2) {
        if (var1 != null && var2 != null) {
            this.put(var1, var2.toString());
        }

    }

    public void put(String var1, int var2) {
        if (var1 != null) {
            this.put(var1, String.valueOf(var2));
        }

    }

    public void put(String var1, long var2) {
        if (var1 != null) {
            this.put(var1, String.valueOf(var2));
        }

    }

    public void remove(String var1) {
        this.params.remove(var1);
    }

    public ConcurrentHashMap<String, String> getParams() {
        return params;
    }

    public List<FileInput> getFiles() {
        return files;
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder();
        Iterator var2 = this.params.entrySet().iterator();

        Map.Entry var3;
        while (var2.hasNext()) {
            var3 = (Map.Entry) var2.next();
            if (var1.length() > 0) {
                var1.append("&");
            }

            var1.append((String) var3.getKey());
            var1.append("=");
            var1.append((String) var3.getValue());
        }

        if (files.size() > 0) {
            if (var1.length() > 0) {
                var1.append("&");
            }
            var1.append("FILE");
            var1.append("=");
            var1.append(String.valueOf(files.size()));
        }

        return var1.toString();
    }

    public RequestBody generateRequest() {
        return new PostFormRequest(this).buildRequestBody();
    }


    public static class FileInput {
        public String key;
        public String filename;
        public File file;

        public FileInput(String key, String filename, File file) {
            this.key = key;
            this.filename = filename;
            this.file = file;
        }

        @Override
        public String toString() {
            return "FileInput{" +
                    "key='" + key + '\'' +
                    ", filename='" + filename + '\'' +
                    ", file=" + file +
                    '}';
        }
    }

}
