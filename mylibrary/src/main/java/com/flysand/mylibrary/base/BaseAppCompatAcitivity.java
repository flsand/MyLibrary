package com.flysand.mylibrary.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.flysand.mylibrary.customView.MyRadioGroup;
import com.flysand.mylibrary.http.HttpAnalysisHelper;
import com.flysand.mylibrary.http.HttpUtil;
import com.flysand.mylibrary.listener.AsyncHttpAnalysisListener;
import com.flysand.mylibrary.listener.AsyncHttpReturnListener;
import com.flysand.mylibrary.listener.ObserverListener;
import com.flysand.mylibrary.util.MyNewToast;
import com.flysand.mylibrary.util.SharedPreferenceUtil;
import com.flysand.mylibrary.util.Utils;
import com.flysand.mylibrary.view.HttpProgressDialog;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by FlySand on 2017/9/1.
 */

public abstract class BaseAppCompatAcitivity extends AppCompatActivity
        implements AsyncHttpAnalysisListener, AsyncHttpReturnListener, ObserverListener, ObserverListener.Subject {

    protected MyNewToast toast;
    protected HttpProgressDialog httpProgressDialog;
    private final static List<ObserverListener> observers = new ArrayList<>();
    protected Bundle savedInstanceState;
    protected HttpAnalysisHelper analysisHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLifecycle.getInstance(this);

        if (httpProgressDialog == null) {
            httpProgressDialog = new HttpProgressDialog(this);
        }
        if (toast == null) {
            toast = new MyNewToast(getApplicationContext());
        }
        registerObserver(this);
        this.savedInstanceState = savedInstanceState;
        analysisHelper = getCustumAnalysisHelper();
    }

    protected HttpAnalysisHelper getCustumAnalysisHelper() {
        return new HttpAnalysisHelper(this);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        if (toast == null) {
            toast = new MyNewToast(getApplicationContext());
        }
    }

    private void a(View var1, Bundle var2) {
        if (var1 != null && var1 instanceof ViewGroup) {
            ViewGroup var3 = (ViewGroup) var1;
            int var4 = var3.getChildCount();

            for (int var5 = 0; var5 < var4; ++var5) {
                View var6 = var3.getChildAt(var5);
                if (!(var6 instanceof ViewGroup)) {
                    if (var6 instanceof View) {
                        this.b(var6, var2);
                    }
                } else {
                    if (var6 instanceof com.flysand.mylibrary.customView.MyRadioGroup || var6 instanceof RadioGroup) {
                        var2.putString(var6.getId() + "", "");
                    }

                    this.a(var6, var2);
                }
            }
        } else {
            this.b(var1, var2);
        }

    }


    protected void onSaveInstanceState(Bundle var1) {
        this.a(this.getRootView(), var1);
        this.a(var1);
        super.onSaveInstanceState(var1);
    }

    protected void onRestoreInstanceState(Bundle var1) {
        Map var2 = this.b(var1);
        super.onRestoreInstanceState(var1);
        this.a(var2);
        this.c(var1);
        this.afterRestoreInstanceState(var1);
    }

    protected abstract void afterRestoreInstanceState(Bundle var1);

    private void a(Bundle var1) {
        try {
            Class var2;
            for (var2 = this.getClass(); var2.getName().contains("_"); var2 = var2.getSuperclass()) {
                ;
            }

            Field[] var3 = var2.getDeclaredFields();
            ActivityLifecycle.getInstance(this.getApplicationContext());
            boolean var4 = false;
            Field[] var5 = var3;
            int var6 = var3.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                Field var8 = var5[var7];
                var2 = var8.getType();
                if (!var2.getName().contains("android.widget") && !var2.getName().contains("android.support")) {
                    var8.setAccessible(true);
                    if (!(var8.get(this) instanceof View) && !(var8.get(this) instanceof ViewGroup)) {
                        Serializable var9 = null;
                        if (var2 instanceof Serializable) {
                            try {
                                Class.forName(var2.getName() + "_");
                                Object var10 = var2.newInstance();
                                Method[] var27 = var2.getDeclaredMethods();
                                Method[] var12 = var27;
                                int var13 = var27.length;

                                for (int var14 = 0; var14 < var13; ++var14) {
                                    Method var15 = var12[var14];
                                    String var16 = var15.getName();
                                    if (var16.startsWith("get") || var16.startsWith("is")) {
                                        try {
                                            var16 = "set" + var16.substring(var16.startsWith("get") ? 3 : 2);
                                            var2.getMethod(var16, new Class[]{var15.getReturnType()}).invoke(var10, new Object[]{var15.invoke(var8.get(this), new Object[0])});
                                        } catch (Exception var23) {
                                            var23.printStackTrace();
                                        }
                                    }
                                }

                                var9 = (Serializable) var10;
                            } catch (ClassNotFoundException var24) {
                                if ((var8.getModifiers() & 16) != 16) {
                                    Object var11 = var8.get(this);
                                    if (var11 != null) {
                                        if (!(var11 instanceof List) && !(var11 instanceof Map)) {
                                            if (!var11.getClass().isPrimitive() && !(var11 instanceof String)) {
                                                if (!(var11 instanceof ParameterizedType) && var11 instanceof Serializable) {
                                                    var9 = (Serializable) var11;
                                                }
                                            } else {
                                                var9 = (Serializable) var11;
                                            }
                                        } else {
                                            SharedPreferenceUtil.getInstance(this).saveParam("sharefilenameforsaveinstance", this.getClass().getName() + var8.getName(), var11);
                                            var4 = true;
                                        }
                                    }
                                }
                            } finally {
                                if (var9 != null) {
                                    var1.putSerializable(var8.getName(), var9);
                                }

                                var8.setAccessible(false);
                            }
                        }
                    } else {
                        var8.setAccessible(false);
                    }
                }
            }

            if (var4) {
                var1.putSerializable("sharefilenameforsaveinstance", "");
            }
        } catch (Exception var26) {
            var26.printStackTrace();
        } catch (OutOfMemoryError var10) {
            var10.printStackTrace();
            Utils.print("catch (OutOfMemoryError var10  = " + var10.toString());
        }

    }

    private boolean a(Object var1) {
        try {
            if (var1 == null) {
                return false;
            } else if (var1 instanceof List) {
                List var7 = (List) var1;
                int var8 = var7.size();
                if (var8 <= 0) {
                    return false;
                } else {
                    boolean var9 = true;

                    while (var8-- > 0) {
                        var9 &= this.a(var7.get(var8));
                        if (!var9) {
                            break;
                        }
                    }

                    return var9;
                }
            } else if (!(var1 instanceof Map)) {
                return !var1.getClass().isPrimitive() && !(var1 instanceof String) ? !(var1 instanceof ParameterizedType) && var1 instanceof Serializable : true;
            } else {
                Map var2 = (Map) var1;
                if (var2.size() <= 0) {
                    return false;
                } else {
                    boolean var3 = true;
                    Iterator var4 = var2.keySet().iterator();

                    while (var4.hasNext()) {
                        Object var5 = var4.next();
                        var3 &= this.a(var5);
                        if (!var3) {
                            break;
                        }

                        var3 &= this.a(var2.get(var5));
                        if (!var3) {
                            break;
                        }
                    }

                    return var3;
                }
            }
        } catch (Exception var6) {
            var6.printStackTrace();
            return false;
        }
    }

    private Map<View, Object> b(Bundle var1) {
        HashMap var2 = new HashMap();
        Iterator var3 = var1.keySet().iterator();

        while (var3.hasNext()) {
            String var4 = (String) var3.next();

            try {
                View var5 = this.findViewById(Integer.valueOf(var4).intValue());
                if (var5 != null) {
                    if (var5 instanceof RadioGroup) {
                        this.a(var5, RadioGroup.class.getDeclaredField("mOnCheckedChangeListener"), var2);
                    } else if (var5 instanceof MyRadioGroup) {
                        this.a(var5, MyRadioGroup.class.getDeclaredField("mOnCheckedChangeListener"), var2);
                    } else if (var5 instanceof CheckBox) {
                        this.a(var5, CompoundButton.class.getDeclaredField("mOnCheckedChangeListener"), var2);
                    } else if (var5 instanceof EditText) {
                        this.a(var5, TextView.class.getDeclaredField("mListeners"), var2);
                    } else if (var5 instanceof TextView) {
                        this.a(var5, TextView.class.getDeclaredField("mListeners"), var2);
                    }
                }
            } catch (NumberFormatException var6) {
                ;
            } catch (Exception var7) {
                var7.printStackTrace();
            }
        }

        return var2;
    }

    private void c(Bundle var1) {
        Class var2;
        for (var2 = this.getClass(); var2.getName().contains("_"); var2 = var2.getSuperclass()) {
            ;
        }

        Iterator var3 = var1.keySet().iterator();

        while (var3.hasNext()) {
            String var4 = (String) var3.next();

            try {
                if (var4.equals("sharefilenameforsaveinstance")) {
                    Map var13 = SharedPreferenceUtil.getInstance(this).getAllShareKey("sharefilenameforsaveinstance");
                    if (var13 != null) {
                        Iterator var6 = var13.keySet().iterator();

                        while (var6.hasNext()) {
                            String var7 = (String) var6.next();

                            try {
                                Field var8 = var2.getDeclaredField(var7.replace(this.getClass().getName(), ""));
                                var8.setAccessible(true);
                                var8.set(this, SharedPreferenceUtil.getInstance(this).readObj("sharefilenameforsaveinstance", var7));
                                var8.setAccessible(false);
                                SharedPreferenceUtil.getInstance(this).removeParam("sharefilenameforsaveinstance", var7);
                            } catch (Exception var10) {
                                ;
                            }
                        }
                    }
                } else {
                    try {
                        Long.parseLong(var4);
                    } catch (Exception var9) {
                        Field var5 = var2.getDeclaredField(var4);
                        var5.setAccessible(true);
                        var5.set(this, var1.getSerializable(var4));
                        var5.setAccessible(false);
                    }
                }
            } catch (NoSuchFieldException var11) {
                ;
            } catch (IllegalAccessException var12) {
                ;
            }
        }

    }

    private void a(View var1, Field var2, Map<View, Object> var3) {
        var2.setAccessible(true);
        try {
            var3.put(var1, var2.get(var1));
            var2.set(var1, (Object) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void a(Map<View, Object> var1) {
        Iterator var2 = var1.keySet().iterator();

        while (true) {
            while (var2.hasNext()) {
                View var3 = (View) var2.next();
                if (var3 instanceof CheckBox) {
                    ((CheckBox) var3).setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) var1.get(var3));
                } else {
                    ArrayList var4;
                    Iterator var5;
                    TextWatcher var6;
                    if (var3 instanceof EditText) {
                        var4 = (ArrayList) var1.get(var3);
                        if (var4 != null) {
                            var5 = var4.iterator();

                            while (var5.hasNext()) {
                                var6 = (TextWatcher) var5.next();
                                ((EditText) var3).addTextChangedListener(var6);
                            }
                        }
                    } else if (var3 instanceof TextView) {
                        var4 = (ArrayList) var1.get(var3);
                        if (var4 != null) {
                            var5 = var4.iterator();

                            while (var5.hasNext()) {
                                var6 = (TextWatcher) var5.next();
                                ((TextView) var3).addTextChangedListener(var6);
                            }
                        }
                    } else if (var3 instanceof RadioGroup) {
                        ((RadioGroup) var3).setOnCheckedChangeListener((android.widget.RadioGroup.OnCheckedChangeListener) var1.get(var3));
                    } else if (var3 instanceof MyRadioGroup) {
                        ((MyRadioGroup) var3).setOnCheckedChangeListener((MyRadioGroup.OnCheckedChangeListener) var1.get(var3));
                    }
                }
            }

            return;
        }
    }

    protected View getRootView() {
        ActivityLifecycle.getInstance(getApplication());
        return ((ViewGroup) this.getWindow().getDecorView().getRootView().findViewById(android.R.id.content)).getChildAt(0);
    }


    private void b(View var1, Bundle var2) {
        if (var1 instanceof TextView && var1.getId() > 0) {
            var2.putString(var1.getId() + "", "");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeObserver(this);
        HttpUtil.removeContextMap(this);
        if (httpProgressDialog != null) {
            httpProgressDialog.dismiss();
        }
    }

    @Override
    public void onUpdate(int type, Intent intent) {
    }

    @Override
    public void sendUpdateMessage(int type, Intent intent) {
        notifyObservers(type, intent);
    }

    @Override
    public void registerObserver(ObserverListener o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(ObserverListener o) {
        if (observers.indexOf(o) >= 0) {
            observers.remove(o);
        }
    }

    @Override
    public void notifyObservers(int type, Intent intent) {
        for (final ObserverListener o : observers) {
            try {
                o.onUpdate(type, intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(String var1, Call var2, Exception var3) {
        if (this.analysisHelper != null) {
            this.analysisHelper.onFailure(var1, var2, var3);
        }

    }

    @Override
    public void onResponse(String var1, Call var2, Response var3, String var4) {
        if (this.analysisHelper != null) {
            this.analysisHelper.onResponse(var1, var2, var3, var4);
        }

    }

}
