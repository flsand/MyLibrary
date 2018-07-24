//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Build.VERSION;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@SuppressLint({"DefaultLocale"})
public final class FileUtils {
    public static final long GB = 1073741824L;
    public static final long MB = 1048576L;
    public static final long KB = 1024L;
    public static final int ICON_TYPE_ROOT = 1;
    public static final int ICON_TYPE_FOLDER = 2;
    public static final int ICON_TYPE_MP3 = 3;
    public static final int ICON_TYPE_MTV = 4;
    public static final int ICON_TYPE_JPG = 5;
    public static final int ICON_TYPE_FILE = 6;
    public static final String MTV_REG = "^.*\\.(mp4|3gp)$";
    public static final String MP3_REG = "^.*\\.(mp3|wav)$";
    public static final String JPG_REG = "^.*\\.(gif|jpg|png)$";
    private static final String FILENAME_REGIX = "^[^\\/?\"*:<>\\\u0005]{1,255}$";
    public static final String FILE_EXTENSION_SEPARATOR = "";

    private FileUtils() {
        throw new Error("Do not need instantiate!");
    }

    public static boolean deleteFile(File var0) {
        if(var0 != null && var0.exists()) {
            if(var0.isFile()) {
                return var0.delete();
            } else if(!var0.isDirectory()) {
                return false;
            } else {
                File[] var1 = var0.listFiles();
                if(var1 != null && var1.length != 0) {
                    File[] var2 = var1;
                    int var3 = var1.length;

                    for(int var4 = 0; var4 < var3; ++var4) {
                        File var5 = var2[var4];
                        deleteFile(var5);
                    }

                    return var0.delete();
                } else {
                    return var0.delete();
                }
            }
        } else {
            return false;
        }
    }

    public static boolean renameFile(File var0, String var1) {
        if(var1.matches("^[^\\/?\"*:<>\\\u0005]{1,255}$")) {
            File var2 = null;
            if(var0.isDirectory()) {
                var2 = new File(var0.getParentFile(), var1);
            } else {
                String var3 = var1 + var0.getName().substring(var0.getName().lastIndexOf(46));
                var2 = new File(var0.getParentFile(), var3);
            }

            if(var0.renameTo(var2)) {
                return true;
            }
        }

        return false;
    }

    public static String getFileSize(File var0) {
        FileInputStream var1 = null;

        String var3;
        try {
            var1 = new FileInputStream(var0);
            int var2 = var1.available();
            if((long)var2 >= 1073741824L) {
                var3 = String.format("%.2f GB", new Object[]{Double.valueOf((double)var2 * 1.0D / 1.073741824E9D)});
                return var3;
            }

            if((long)var2 >= 1048576L) {
                var3 = String.format("%.2f MB", new Object[]{Double.valueOf((double)var2 * 1.0D / 1048576.0D)});
                return var3;
            }

            var3 = String.format("%.2f KB", new Object[]{Double.valueOf((double)var2 * 1.0D / 1024.0D)});
        } catch (Exception var15) {
            var15.printStackTrace();
            return "未知";
        } finally {
            try {
                var1.close();
            } catch (IOException var14) {
                var14.printStackTrace();
            }

        }

        return var3;
    }

    public static void openFile(Activity var0, File var1) {
        Intent var2 = new Intent();
        var2.setFlags(268435456);
        var2.setAction("android.intent.action.VIEW");
        var2.setDataAndType(Uri.fromFile(var1), getMimeType(var1, var0));
        var0.startActivity(var2);
    }

    public static String getMimeType(File var0, Activity var1) {
        String var2 = var0.getName().substring(var0.getName().lastIndexOf(46) + 1).toLowerCase();
        int var3 = var1.getResources().getIdentifier(var1.getPackageName() + ":string/" + var2, (String)null, (String)null);
        if("class".equals(var2)) {
            return "application/octet-stream";
        } else if("3gp".equals(var2)) {
            return "video/3gpp";
        } else if("nokia-op-logo".equals(var2)) {
            return "image/vnd.nok-oplogo-color";
        } else if(var3 == 0) {
            throw new RuntimeException("未找到分享该格式的应用");
        } else {
            return var1.getString(var3);
        }
    }

    public static List<HashMap<String, Object>> recursionFolder(File var0, FileFilter var1) {
        ArrayList var2 = new ArrayList();
        File[] var3 = var0.listFiles();
        if(var1 != null) {
            var3 = var0.listFiles(var1);
        }

        if(var3 != null) {
            for(int var4 = 0; var4 < var3.length; ++var4) {
                File var5 = var3[var4];
                if(var5.isDirectory()) {
                    var2.addAll(recursionFolder(var5, var1));
                } else {
                    HashMap var6 = new HashMap();
                    var6.put("file", var5);
                    if(var5.getAbsolutePath().toLowerCase().matches("^.*\\.(mp3|wav)$")) {
                        var6.put("iconType", Integer.valueOf(3));
                    } else if(var5.getAbsolutePath().toLowerCase().matches("^.*\\.(mp4|3gp)$")) {
                        var6.put("iconType", Integer.valueOf(4));
                    } else if(var5.getAbsolutePath().toLowerCase().matches("^.*\\.(gif|jpg|png)$")) {
                        var6.put("iconType", Integer.valueOf(5));
                    } else {
                        var6.put("iconType", Integer.valueOf(6));
                    }

                    var2.add(var6);
                }
            }
        }

        return var2;
    }

    public static List<HashMap<String, Object>> unrecursionFolder(File var0, FileFilter var1) {
        ArrayList var2 = new ArrayList();
        if(!var0.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            HashMap var3 = new HashMap();
            var3.put("file", var0.getParentFile());
            var3.put("iconType", Integer.valueOf(1));
            var2.add(var3);
        }

        File[] var9 = var0.listFiles();
        if(var1 != null) {
            var9 = var0.listFiles(var1);
        }

        if(var9 != null && var9.length > 0) {
            File[] var4 = var9;
            int var5 = var9.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                File var7 = var4[var6];
                HashMap var8 = new HashMap();
                var8.put("file", var7);
                if(var7.isDirectory()) {
                    var8.put("iconType", Integer.valueOf(2));
                } else if(var7.getAbsolutePath().toLowerCase().matches("^.*\\.(mp3|wav)$")) {
                    var8.put("iconType", Integer.valueOf(3));
                } else if(var7.getAbsolutePath().toLowerCase().matches("^.*\\.(mp4|3gp)$")) {
                    var8.put("iconType", Integer.valueOf(4));
                } else if(var7.getAbsolutePath().toLowerCase().matches("^.*\\.(gif|jpg|png)$")) {
                    var8.put("iconType", Integer.valueOf(5));
                } else {
                    var8.put("iconType", Integer.valueOf(6));
                }

                var2.add(var8);
            }
        }

        return var2;
    }

    public static FileFilter getFileFilter(final String var0, boolean var1) {
        return var1?new FileFilter() {
            public boolean accept(File var1) {
                return var1.getAbsolutePath().toLowerCase().matches(var0) || var1.isDirectory();
            }
        }:new FileFilter() {
            public boolean accept(File var1) {
                return var1.getAbsolutePath().toLowerCase().matches(var0) && var1.isFile();
            }
        };
    }

    public static StringBuilder readFile(String var0, String var1) {
        File var2 = new File(var0);
        StringBuilder var3 = new StringBuilder("");
        if(!var2.isFile()) {
            return null;
        } else {
            BufferedReader var4 = null;

            StringBuilder var7;
            try {
                InputStreamReader var5 = new InputStreamReader(new FileInputStream(var2), var1);

                String var6;
                for(var4 = new BufferedReader(var5); (var6 = var4.readLine()) != null; var3.append(var6)) {
                    if(!var3.toString().equals("")) {
                        var3.append("\r\n");
                    }
                }

                var4.close();
                var7 = var3;
            } catch (IOException var16) {
                throw new RuntimeException("IOException occurred. ", var16);
            } finally {
                if(var4 != null) {
                    try {
                        var4.close();
                    } catch (IOException var15) {
                        throw new RuntimeException("IOException occurred. ", var15);
                    }
                }

            }

            return var7;
        }
    }

    public static boolean writeFile(String var0, String var1, boolean var2) {
        if(TextUtils.isEmpty(var1)) {
            return false;
        } else {
            FileWriter var3 = null;

            boolean var4;
            try {
                makeDirs(var0);
                var3 = new FileWriter(var0, var2);
                var3.write(var1);
                var3.close();
                var4 = true;
            } catch (IOException var13) {
                throw new RuntimeException("IOException occurred. ", var13);
            } finally {
                if(var3 != null) {
                    try {
                        var3.close();
                    } catch (IOException var12) {
                        throw new RuntimeException("IOException occurred. ", var12);
                    }
                }

            }

            return var4;
        }
    }

    public static boolean writeFile(String var0, List<String> var1, boolean var2) {
        if(var1 != null && var1.size() >= 1) {
            FileWriter var3 = null;

            boolean var16;
            try {
                makeDirs(var0);
                var3 = new FileWriter(var0, var2);
                int var4 = 0;

                String var6;
                for(Iterator var5 = var1.iterator(); var5.hasNext(); var3.write(var6)) {
                    var6 = (String)var5.next();
                    if(var4++ > 0) {
                        var3.write("\r\n");
                    }
                }

                var3.close();
                var16 = true;
            } catch (IOException var14) {
                throw new RuntimeException("IOException occurred. ", var14);
            } finally {
                if(var3 != null) {
                    try {
                        var3.close();
                    } catch (IOException var13) {
                        throw new RuntimeException("IOException occurred. ", var13);
                    }
                }

            }

            return var16;
        } else {
            return false;
        }
    }

    public static boolean writeFile(String var0, String var1) {
        return writeFile(var0, var1, false);
    }

    public static boolean writeFile(String var0, List<String> var1) {
        return writeFile(var0, var1, false);
    }

    public static boolean writeFile(String var0, InputStream var1) {
        return writeFile(var0, var1, false);
    }

    public static boolean writeFile(String var0, InputStream var1, boolean var2) {
        return writeFile(var0 != null?new File(var0):null, var1, var2);
    }

    public static boolean writeFile(File var0, InputStream var1) {
        return writeFile(var0, var1, false);
    }

    public static boolean writeFile(File var0, InputStream var1, boolean var2) {
        FileOutputStream var3 = null;

        try {
            makeDirs(var0.getAbsolutePath());
            var3 = new FileOutputStream(var0, var2);
            byte[] var4 = new byte[1024];
            boolean var5 = true;

            int var19;
            while((var19 = var1.read(var4)) != -1) {
                var3.write(var4, 0, var19);
            }

            var3.flush();
            boolean var6 = true;
            return var6;
        } catch (FileNotFoundException var16) {
            throw new RuntimeException("FileNotFoundException occurred. ", var16);
        } catch (IOException var17) {
            throw new RuntimeException("IOException occurred. ", var17);
        } finally {
            if(var3 != null) {
                try {
                    var3.close();
                    var1.close();
                } catch (IOException var15) {
                    throw new RuntimeException("IOException occurred. ", var15);
                }
            }

        }
    }

    public static boolean copyFile(String var0, String var1) {
        FileInputStream var2 = null;

        try {
            var2 = new FileInputStream(var0);
        } catch (FileNotFoundException var4) {
            throw new RuntimeException("FileNotFoundException occurred. ", var4);
        }

        return writeFile((String)var1, (InputStream)var2);
    }

    public static final byte[] input2byte(InputStream var0) {
        if(var0 == null) {
            return null;
        } else {
            ByteArrayOutputStream var1 = new ByteArrayOutputStream();
            byte[] var2 = new byte[100];
            boolean var3 = false;

            int var6;
            try {
                while((var6 = var0.read(var2, 0, 100)) > 0) {
                    var1.write(var2, 0, var6);
                }
            } catch (IOException var5) {
                var5.printStackTrace();
            }

            return var1.toByteArray();
        }
    }

    public static List<String> readFileToList(String var0, String var1) {
        File var2 = new File(var0);
        ArrayList var3 = new ArrayList();
        if(!var2.isFile()) {
            return null;
        } else {
            BufferedReader var4 = null;

            try {
                InputStreamReader var5 = new InputStreamReader(new FileInputStream(var2), var1);
                var4 = new BufferedReader(var5);
                String var6 = null;

                while((var6 = var4.readLine()) != null) {
                    var3.add(var6);
                }

                var4.close();
                ArrayList var7 = var3;
                return var7;
            } catch (IOException var16) {
                throw new RuntimeException("IOException occurred. ", var16);
            } finally {
                if(var4 != null) {
                    try {
                        var4.close();
                    } catch (IOException var15) {
                        throw new RuntimeException("IOException occurred. ", var15);
                    }
                }

            }
        }
    }

    public static String getFileNameWithoutExtension(String var0) {
        if(TextUtils.isEmpty(var0)) {
            return var0;
        } else {
            int var1 = var0.lastIndexOf("");
            int var2 = var0.lastIndexOf(File.separator);
            return var2 == -1?(var1 == -1?var0:var0.substring(0, var1)):(var1 == -1?var0.substring(var2 + 1):(var2 < var1?var0.substring(var2 + 1, var1):var0.substring(var2 + 1)));
        }
    }

    public static String getFileName(String var0) {
        if(TextUtils.isEmpty(var0)) {
            return var0;
        } else {
            int var1 = var0.lastIndexOf(File.separator);
            return var1 == -1?var0:var0.substring(var1 + 1);
        }
    }

    public static String getFolderName(String var0) {
        if(TextUtils.isEmpty(var0)) {
            return var0;
        } else {
            int var1 = var0.lastIndexOf(File.separator);
            return var1 == -1?"":var0.substring(0, var1);
        }
    }

    public static String getFileExtension(String var0) {
        if(TextUtils.isEmpty(var0)) {
            return var0;
        } else {
            int var1 = var0.lastIndexOf("");
            int var2 = var0.lastIndexOf(File.separator);
            return var1 == -1?"":(var2 >= var1?"":var0.substring(var1 + 1));
        }
    }

    public static boolean makeDirs(String var0) {
        String var1 = getFolderName(var0);
        if(TextUtils.isEmpty(var1)) {
            return false;
        } else {
            File var2 = new File(var1);
            return var2.exists() && var2.isDirectory() || var2.mkdirs();
        }
    }

    public static boolean makeFolders(String var0) {
        return makeDirs(var0);
    }

    public static boolean isFileExist(String var0) {
        if(TextUtils.isEmpty(var0)) {
            return false;
        } else {
            File var1 = new File(var0);
            return var1.exists() && var1.isFile();
        }
    }

    public static boolean isFolderExist(String var0) {
        if(TextUtils.isEmpty(var0)) {
            return false;
        } else {
            File var1 = new File(var0);
            return var1.exists() && var1.isDirectory();
        }
    }

    public static boolean deleteFile(String var0) {
        if(TextUtils.isEmpty(var0)) {
            return true;
        } else {
            File var1 = new File(var0);
            if(!var1.exists()) {
                return true;
            } else if(var1.isFile()) {
                return var1.delete();
            } else if(!var1.isDirectory()) {
                return false;
            } else {
                File[] var2 = var1.listFiles();
                int var3 = var2.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    File var5 = var2[var4];
                    if(var5.isFile()) {
                        var5.delete();
                    } else if(var5.isDirectory()) {
                        deleteFile(var5.getAbsolutePath());
                    }
                }

                return var1.delete();
            }
        }
    }

    public static long getFileSize(String var0) {
        if(TextUtils.isEmpty(var0)) {
            return -1L;
        } else {
            File var1 = new File(var0);
            return var1.exists() && var1.isFile()?var1.length():-1L;
        }
    }

    public static File uri2File(Activity var0, Uri var1) {
        String[] var2;
        if(VERSION.SDK_INT < 11) {
            var2 = new String[]{"_data"};
            Cursor var6 = var0.managedQuery(var1, var2, (String)null, (String[])null, (String)null);
            int var7 = var6.getColumnIndexOrThrow("_data");
            var6.moveToFirst();
            String var8 = var6.getString(var7);
            return new File(var8);
        } else {
            var2 = new String[]{"_data"};
            CursorLoader var3 = new CursorLoader(var0, var1, var2, (String)null, (String[])null, (String)null);
            Cursor var4 = var3.loadInBackground();
            int var5 = var4.getColumnIndexOrThrow("_data");
            var4.moveToFirst();
            return new File(var4.getString(var5));
        }
    }
}
