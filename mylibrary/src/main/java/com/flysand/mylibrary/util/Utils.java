package com.flysand.mylibrary.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.pm.PackageManager.GET_META_DATA;
import static android.util.Log.e;

/**
 * Created by Administrator on 2017/3/6.
 */

public class Utils {

    private static int viewId = 10000;
    private static long lastClickTime;
    private static int timeOffset = 300;
    private static boolean DEBUG = false;
    private static boolean ISWriteFile = false;
    private static final String FILE_NAME = "share_system_utils_date";

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @SuppressLint("LongLogTag")
    public static void print(String str) {
        if (DEBUG) {
            try {
                StackTraceElement[] var3 = Thread.currentThread().getStackTrace();
                StackTraceElement ste = var3[3];
                String tag = var3[4].getClassName().substring(var3[4].getClassName().lastIndexOf(".")) + " --->" + ste.getClassName().substring(ste.getClassName().lastIndexOf(".")) + "---> " + ste.getMethodName() + "() LineNum= " + ste.getLineNumber() + " -->  ";
                e(tag, str);
                if (ISWriteFile)
                    writeFile("    " + tag + "   log  =   " + str + "\r\n");
            } catch (Exception var31) {
                e("---printException--->>", "str = " + str);
            }
        }
    }

    public static boolean isApkDebugable(Context context, String packageName) {
        try {

            PackageInfo pkginfo = context.getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_ACTIVITIES);
            if (pkginfo != null) {
                ApplicationInfo info = pkginfo.applicationInfo;
                DEBUG = ((info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
            }
        } catch (Exception e) {

        }
        return DEBUG;
    }

    public static void printLine() {
        if (DEBUG) {
            try {
                StackTraceElement[] var3 = Thread.currentThread().getStackTrace();
                StackTraceElement ste = var3[3];
                String tag = var3[4].getClassName().substring(var3[4].getClassName().lastIndexOf(".")) + " --->" + ste.getClassName().substring(ste.getClassName().lastIndexOf(".")) + "---> " + ste.getMethodName() + "()";
                e(tag, "---- >  LineNumber = " + ste.getLineNumber());
                if (ISWriteFile)
                    writeFile("    " + tag + " ---- >  LineNumber = " + ste.getLineNumber() + " \r\n");
            } catch (Exception var31) {
                e("---printException--->>", "行号打印错误");
            }
        }
    }

    public static void printLine(String log) {
        if (DEBUG) {
            try {
                StackTraceElement[] var3 = Thread.currentThread().getStackTrace();
                StackTraceElement ste = var3[3];
                String tag = var3[4].getClassName().substring(var3[4].getClassName().lastIndexOf(".")) + " --->" + ste.getClassName().substring(ste.getClassName().lastIndexOf(".")) + "---> " + ste.getMethodName() + "()";
                e(tag, "---- >  LineNumber = " + ste.getLineNumber() + "     " + log);
                if (ISWriteFile)
                    writeFile("    " + tag + " ---- >  LineNumber = " + ste.getLineNumber() + "    log = " + log + " \r\n");
            } catch (Exception var31) {
                e("---printException--->>", "行号打印错误");
            }
        }
    }

    public static void printDisplayMetrics(Context context) {
        Utils.print("屏幕高度 = " + context.getResources().getDisplayMetrics().heightPixels);
        Utils.print("屏幕宽度 = " + context.getResources().getDisplayMetrics().widthPixels);
        Utils.print("屏幕密度 = " + context.getResources().getDisplayMetrics().density);
        Utils.print("屏幕密度DPI = " + context.getResources().getDisplayMetrics().densityDpi);//320
    }

    public static String getFilePath(Context var0, String var1) {
        try {
            String var2 = var0.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            File var3 = new File(var2 + File.separator + var1);
            if (!var3.exists()) {
                var3.mkdirs();
            }
            return var3.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String filePath;


    public static void initWiteFile(Context context) {
        try {
            SimpleDateFormat var11 = new SimpleDateFormat("yyyy-MM-dd");
            String var12 = var11.format(new Date(System.currentTimeMillis())) + ".mog";
            String path = getFilePath(context, "Log") + File.separator;
            File var13 = new File(path);
            if (!var13.exists()) {
                var13.mkdirs();
            }
            File var14 = new File(path + File.separator + var12);
            if (!var14.exists()) {
                var14.createNewFile();
            }
            filePath = var14.getAbsolutePath();

            //删除7天前的Log
            long time = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000l);
            File[] files = var13.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    print("文件夹:" + file.getAbsolutePath());
                } else {
                    print("文件:" + file.getAbsolutePath());
                    String fileName = file.getName().replace(".mog", "");
                    print("文件:" + fileName);
                    long curTime = var11.parse(fileName).getTime();
                    if (curTime < time) {
                        file.delete();
                        print("删除文件：" + fileName);
                    }
                }
            }
            ISWriteFile = true;

        } catch (Exception var21) {
            var21.printStackTrace();
        }
    }

    private static void writeFile(String str) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(filePath, true);
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    Utils.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
        }
    }

    /**
     * 去掉小数后面无用的零
     *
     * @param num
     * @return
     */
    public static String replaceDoubleZero(String num) {
        try {
            if (num.indexOf(".") > 0) {
                //正则表达
                num = num.replaceAll("0+?$", "");//去掉后面无用的零
                num = num.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
            }
            return num;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;

    }

    /**
     * 将阿拉伯数字转为汉字
     *
     * @param string
     * @return
     */
    public static String numberToChinese(String string) {
        String[] s1 = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] s2 = {"十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};

        String result = "";

        int n = string.length();
        for (int i = 0; i < n; i++) {

            int num = string.charAt(i) - '0';

            if (i != n - 1 && num != 0) {
                result += s1[num] + s2[n - 2 - i];
            } else {
                result += s1[num];
            }
        }
        if (result.indexOf("零") > 0) {
            //正则表达
            result = result.replaceAll("零+?$", "");
        }
        if (result.startsWith("一") && result.length() > 1) {
            result = result.substring(1, result.length());
        }
        return result;
    }

    public static String getSDPath() {
        File var0 = null;
        boolean var1 = Environment.getExternalStorageState().equals("mounted");
        if (var1) {
            var0 = Environment.getExternalStorageDirectory();
        }

        return var0.toString();
    }

    public static boolean isEmpty(String str) {
        if (TextUtils.isEmpty(str) || str.equals("null"))
            return true;
        return false;
    }

    public static void printSystemTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        print(df.format(new Date(System.currentTimeMillis())));
    }

    public static void printError(String message) {
        if (DEBUG) {
            String msg = message + getTraceElement();

            try {
                e("--error--->>>", msg);
            } catch (Exception var3) {
                System.err.println("--error--->>>" + msg);
            }
        }

    }

    public static void closeDebug() {
        DEBUG = false;
    }

    public static void openDebug() {
        DEBUG = true;
    }

    /**
     * 关闭输入法,返回输入法状态，true为开，即关闭失败
     *
     * @param context
     * @return
     */
    public static boolean hideInputMethod(Activity context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(context
                            .getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            return imm.isActive();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 获取相应的图片存储路径
     *
     * @param type
     * @return
     */
    public static String getPicPath(Context context, String type) {
        String basePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        File file = new File(basePath + File.separator + type);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    //隐藏虚拟键盘
    public static boolean closeKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void saveData(Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences("share_system_utils_date", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, encode("" + object));
        editor.commit();
    }

    public static void saveSerializableBean(Context context, Object var0) {
        SharedPreferences sp = context.getSharedPreferences("share_system_utils_date", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(var0.getClass().getName(), new Gson().toJson(var0));
        editor.commit();
    }

    public static <T> T getSaveSerializableBean(Context context, Class<T> var0) {
        SharedPreferences sp = context.getSharedPreferences("share_system_utils_date", 0);
        String result = sp.getString(var0.getName(), "{}");
        return JSONUtil.toJavaBean(var0, result);
    }

    public static <T> void saveSerializableBeanList(Context context, List<T> var0) {
        if (var0 != null && var0.size() > 0) {
            SharedPreferences sp = context.getSharedPreferences("share_system_utils_date", 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(var0.get(0).getClass().getName() + "List", new Gson().toJson(var0));
            editor.commit();
        } else {
            Utils.print("Parameter Exception");
        }
    }

    public static <T> void delSerializableBeanList(Context context, List<T> var0) {
        if (var0 != null && var0.size() > 0) {
            SharedPreferences sp = context.getSharedPreferences("share_system_utils_date", 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(var0.get(0).getClass().getName() + "List", "[]");
            editor.commit();
        } else {
            Utils.print("Parameter Exception");
        }
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static <T extends Parcelable> void saveParcelBean(Context context, T object) {
        try {
            SharedPreferences sp = context.getSharedPreferences("share_system_utils_date", 0);
            SharedPreferences.Editor editor = sp.edit();

            Parcel parcel = Parcel.obtain();
            List<T> list = new ArrayList<>();
            list.add(object);
            parcel.writeInt(list.size());
            parcel.writeTypedList(list);
            String saveData = new String(parcel.marshall());
            Utils.print("saveData = " + saveData);
            editor.putString(object.getClass().getName(), saveData);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static <T extends Parcelable> T getSaveParcelBean(Context context, T var0) {
        try {
            SharedPreferences sp = context.getSharedPreferences("share_system_utils_date", 0);
            String result = sp.getString(var0.getClass().getName(), "");

            Utils.print("result = " + result);
            byte[] bytes = result.getBytes();
            Parcel parcel = Parcel.obtain();
            parcel.unmarshall(bytes, 0, bytes.length);
            parcel.setDataPosition(0);
            int size = parcel.readInt();
            List<T> list = new ArrayList<>(size);
            parcel.readTypedList(list, null);
//            Bundle bundle = new Bundle();
//            bundle.putParcelable("book", book);
            return list.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return var0;
    }


    public static <T> ArrayList<T> getSaveBeanList(Context context, Class<T> var0) {
        SharedPreferences sp = context.getSharedPreferences("share_system_utils_date", 0);
        String result = sp.getString(var0.getName() + "List", "[]");
        return JSONUtil.toJavaBeanList(var0, result);
    }


    public static String getSaveStringData(Context context, String key, String defaultObject) {
        SharedPreferences sp = context.getSharedPreferences("share_system_utils_date", 0);
        String result = sp.getString(key, defaultObject);
        if (!defaultObject.equals(result)) {
            result = decode(result);
        }

        return result;
    }

    public static Integer getSaveIntegerData(Context context, String key, Integer defaultObject) {
        try {
            SharedPreferences var5 = context.getSharedPreferences("share_system_utils_date", 0);
            String result = var5.getString(key, "" + defaultObject);
            return result.equals("" + defaultObject) ? defaultObject : Integer.valueOf(decode(result));
        } catch (Exception var51) {
            printError(var51.toString());
            return defaultObject;
        }
    }

    public static Boolean getSaveBooleanData(Context context, String key, Boolean defaultObject) {
        SharedPreferences sp = context.getSharedPreferences("share_system_utils_date", 0);
        String result = sp.getString(key, "" + defaultObject);
        if (result.equals("" + defaultObject)) {
            return defaultObject;
        } else {
            result = decode(result);
            if (!result.equals("true") && !result.equals("false")) {
                throw new RuntimeException("SaveData Not Boolean  Result = " + result);
            } else {
                return Boolean.valueOf(result);
            }
        }
    }

    public static Float getSaveFloatData(Context context, String key, Float defaultObject) {
        try {
            SharedPreferences var5 = context.getSharedPreferences("share_system_utils_date", 0);
            String result = var5.getString(key, "" + defaultObject);
            return result.equals("" + defaultObject) ? defaultObject : Float.valueOf(decode(result));
        } catch (Exception var51) {
            printError(var51.toString());
            return defaultObject;
        }
    }

    public static Long getSaveLongData(Context context, String key, Long defaultObject) {
        try {
            SharedPreferences var5 = context.getSharedPreferences("share_system_utils_date", 0);
            String result = var5.getString(key, "" + defaultObject);
            return result.equals("" + defaultObject) ? defaultObject : Long.valueOf(decode(result));
        } catch (Exception var51) {
            printError(var51.toString());
            return defaultObject;
        }
    }

    public static Double getSaveDoubleData(Context context, String key, Double defaultObject) {
        try {
            SharedPreferences var5 = context.getSharedPreferences("share_system_utils_date", 0);
            String result = var5.getString(key, "" + defaultObject);
            return result.equals("" + defaultObject) ? defaultObject : Double.valueOf(decode(result));
        } catch (Exception var51) {
            printError(var51.toString());
            return defaultObject;
        }
    }

    private static String getTraceElement() {
        try {
            int var7 = 2;
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            int stackOffset = _getStackOffset(trace);
            if (var7 + stackOffset > trace.length) {
                var7 = trace.length - stackOffset - 1;
            }

            String level = "    ";
            StringBuilder builder = new StringBuilder();

            for (int i = var7; i > 0; --i) {
                int stackIndex = i + stackOffset;
                if (stackIndex < trace.length) {
                    builder.append("\n").append(level).append(_getSimpleClassName(trace[stackIndex].getClassName())).append(".").append(trace[stackIndex].getMethodName()).append(" ").append("(").append(trace[stackIndex].getFileName()).append(":").append(trace[stackIndex].getLineNumber()).append(")");
                    level = level + "    ";
                }
            }

            return builder.toString();
        } catch (Exception var71) {
            return "";
        }
    }

    /**
     * public static void loadImage(Context context, File file, ImageView view) {
     * try {
     * Picasso.with(context).load(file).placeholder(android.R.drawable.ic_popup_sync).into(view);
     * } catch (Exception e) {
     * <p>
     * }
     * }
     * <p>
     * public static void loadImage(Context context, String url, ImageView view) {
     * try {
     * Picasso.with(context).load(utf8Togb2312(url)).placeholder(android.R.drawable.ic_popup_sync).into(view);
     * } catch (Exception e) {
     * <p>
     * }
     * }
     **/
    public static String utf8Togb2312(String str) {
        if (TextUtils.isEmpty(str))
            return "";
        String data = "";
        try {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c + "".getBytes().length > 1 && c != ':' && c != '/' && c != '×') {
                    data = data + java.net.URLEncoder.encode(c + "", "utf-8");
                } else {
                    data = data + c;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
        }
        return data;
    }

    private static int _getStackOffset(StackTraceElement[] trace) {
        for (int i = 3; i < trace.length; ++i) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(Utils.class.getName())) {
                --i;
                return i;
            }
        }

        return -1;
    }

    private static String _getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    @TargetApi(11)
    @SuppressLint({"NewApi"})
    public static void showNotification(Context context, String title, String msg, Class<?> c, int icon) {
        int messageNotificationID = (int) Math.random() * 1000;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = null;
        if (c != null) {
            pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, c), 0);
        } else {
            pendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
        }

        Notification notify2 = (new Notification.Builder(context)).setSmallIcon(icon).setTicker(msg).setContentTitle(title).setContentText(msg).setContentIntent(pendingIntent).setDefaults(3).getNotification();
        notify2.flags |= 16;
        manager.notify(messageNotificationID, notify2);
    }

    public static boolean isFastDoubleClick() {
        long var0 = System.currentTimeMillis();
        long var2 = var0 - lastClickTime;
        if (0L < var2 && var2 < (long) getTimeOffset()) {
            return true;
        } else {
            lastClickTime = var0;
            return false;
        }
    }

    private static long lastHttpTime;
    private static int timeHttpOffset = 1000;

    public static boolean isShowHttpDialog() {
        long var0 = System.currentTimeMillis();
        long var2 = var0 - lastHttpTime;
        if (0L < var2 && var2 < timeHttpOffset) {
            return false;
        } else {
            lastHttpTime = var0;
            return true;
        }
    }

    public static int getTimeOffset() {
        return timeOffset;
    }

    public static void setTimeOffset(int var0) {
        timeOffset = var0;
    }


    public static void setDatePickerDividerColor(LinearLayout datePicker, Drawable drawable) {

        LinearLayout datePickerLayout = (LinearLayout) datePicker.getChildAt(0);
        for (int i = 0; i < datePickerLayout.getChildCount(); i++) {
            NumberPicker picker = (NumberPicker) datePickerLayout.getChildAt(i);
            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    try {
                        pf.set(picker, drawable);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            // 分割线高度
            for (Field pf2 : pickerFields) {
                if (pf2.getName().equals("mSelectionDividerHeight")) {
                    pf2.setAccessible(true);
                    try {
                        int result = 1;
                        pf2.set(picker, result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }


    public static int dpToPx(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int spToPx(Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }


    public static <T> ArrayList<T> getTestList(int count, T obj) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(obj);
        }
        return (ArrayList<T>) list;
    }

    public static <T> String toListString(List<T> list) {
        String str = "\n size =  " + list.size() + "    \n ";
        for (int i = 0; i < list.size(); i++) {
            str += " -- [" + i + "] =" + list.get(i).toString() + "\n";
        }
        print(str);
        return str;
    }

    /**
     * 加密
     *
     * @param code
     * @return
     */
    public static String encode(String code) {

        return code;
    }

    /**
     * 解密
     *
     * @param code
     * @return
     */
    public static String decode(String code) {

        return code;
    }


    /**
     * 获取屏幕内容高度
     *
     * @param activity
     * @return
     */
    public static int getScreenHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int result = 0;
        int resourceId = activity.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        int screenHeight = dm.heightPixels - result;
        return screenHeight;
    }

    /**
     * dp转px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static void openNVA(Context context, String endAddr, double startLat, double startLng, double endlat, double endlng) {
        Utils.print("  跳转导航！！！");
        try {
            Intent intent = Intent.getIntent("intent://map/direction?origin=latlng:" +
                    startLat + "," +
                    startLng +
                    "|name:" + "我当前的位置" +
                    "&destination=latlng:"
                    + endlat + "," +
                    endlng + "|name:" + endAddr +
                    "&mode=driving&thirdapp.navi.yourCompanyName.yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            if (isInstallByread("com.baidu.BaiduMap")) {
                context.startActivity(intent); //启动调用
                Utils.print("百度地图客户端已经安装");
            } else {
                Utils.print("没有安装百度地图客户端");
                openGaoDeMap(context, endAddr, endlat, endlng);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    public static void openGaoDeMap(Context context, String endAddr, double lat, double lng) {
        try {
            Intent intent = Intent.getIntent("androidamap://navi?" +
                    "sourceApplication=com.autonavi.minimap" +
                    "&poiname=" + endAddr +
                    "&"
                    +
                    "lat="
                    + lat +
                    "&lon=" +
                    lng +
                    "&dev=1&style=2");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAppVersion(Context var0) {
        PackageManager var1 = var0.getPackageManager();

        try {
            PackageInfo var2 = var1.getPackageInfo(var0.getPackageName(), 0);
            return var2.versionName;
        } catch (Exception var4) {
            var4.printStackTrace();
            return "";
        }
    }

    public static String getApplicationName(Context var0) {
        PackageManager var1 = null;
        ApplicationInfo var2 = null;

        try {
            var1 = var0.getPackageManager();
            var2 = var1.getApplicationInfo(var0.getPackageName(), 0);
        } catch (Exception var4) {
            var2 = null;
            var4.printStackTrace();
        }

        String var3 = (String) var1.getApplicationLabel(var2);
        return var3;
    }

    public static String getMetaValue(Context var0, String var1) {
        Bundle var2 = null;
        String var3 = null;
        if (var0 != null && var1 != null) {
            try {
                ApplicationInfo var4 = var0.getPackageManager().getApplicationInfo(var0.getPackageName(), GET_META_DATA);
                if (null != var4) {
                    var2 = var4.metaData;
                }
                if (null != var2) {
                    var3 = var2.getString(var1);
                }
            } catch (PackageManager.NameNotFoundException var5) {
                var5.printStackTrace();
            }
            return var3;
        } else {
            return null;
        }
    }

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(1, dpVal, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(2, spVal, context.getResources().getDisplayMetrics());
    }

    public static float px2dp(Context context, float pxVal) {
        float scale = context.getResources().getDisplayMetrics().density;
        return pxVal / scale;
    }

    public static float px2sp(Context context, float pxVal) {
        return pxVal / context.getResources().getDisplayMetrics().scaledDensity;
    }

    public static synchronized int getViewAutoId() {
        return viewId++ % 10000 + 10000;
    }

    public static String getNetStyle(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        return info != null ? (info.getType() == 1 ? "wifi" : (info.getType() == 0 ? "mobile" : "other")) : "no net";
    }

    public static String getDeviceInfo() {
        HashMap infos = new HashMap();

        try {
            Field[] fields = Build.class.getDeclaredFields();
            Field[] var2 = fields;
            int var3 = fields.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                Field field = var2[var4];
                field.setAccessible(true);
                infos.put(field.getName(), field.get((Object) null).toString());
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        String info = (String) infos.get("BRAND") + " " + (String) infos.get("MODEL");
        return info;
    }
}
