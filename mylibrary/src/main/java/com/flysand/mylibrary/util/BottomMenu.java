//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.util;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.flysand.mylibrary.R;

public class BottomMenu {
    private Activity context;
    private LayoutInflater inflater;
    private View mPopupWindow;
    private float alpha = 0.5F;
    private int backgroundColor = 1140850688;
    private PopupWindow popupWindow;

    public BottomMenu(Activity var1) {
        this.context = var1;
        this.inflater = LayoutInflater.from(var1);
    }

    public BottomMenu setShowBackgroundAlpha(float var1) {
        this.alpha = var1;
        return this;
    }

    private void setBackgroundAlpha(float var1, float var2) {
        ValueAnimator var3 = ValueAnimator.ofFloat(new float[]{var1, var2});
        var3.setDuration(200L);
        var3.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator var1) {
                LayoutParams var2 = BottomMenu.this.context.getWindow().getAttributes();
                var2.alpha = ((Float)var1.getAnimatedValue()).floatValue();
                BottomMenu.this.context.getWindow().setAttributes(var2);
            }
        });
        var3.start();
    }

    public BottomMenu setBackgroundColor(int var1) {
        this.backgroundColor = var1;
        return this;
    }

    public void afterShow() {
    }

    public View findViewById(int var1) {
        return var1 < 0?null:this.mPopupWindow.findViewById(var1);
    }

    public BottomMenu showPopupWindow(int var1) {
        try {
            this.mPopupWindow = this.inflater.inflate(var1, (ViewGroup)null);
            this.popupWindow = new PopupWindow((View)null, -1, -2, true);
            this.popupWindow.setOnDismissListener(new OnDismissListener() {
                public void onDismiss() {
                    BottomMenu.this.setBackgroundAlpha(BottomMenu.this.alpha, 1.0F);
                }
            });
            this.popupWindow.setContentView(this.mPopupWindow);
            this.popupWindow.setFocusable(true);
            this.popupWindow.setAnimationStyle(R.style.PopupAnimation);
            ColorDrawable var2 = new ColorDrawable(this.backgroundColor);
            this.popupWindow.setBackgroundDrawable(var2);
            View var3 = ((ViewGroup)this.context.findViewById(android.R.id.content)).getChildAt(0);
            this.popupWindow.showAtLocation(var3, 81, 0, 0);
            this.setBackgroundAlpha(1.0F, this.alpha);
            this.afterShow();
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return this;
    }

    public void dismiss() {
        if(this.popupWindow != null) {
            this.popupWindow.dismiss();
        }

    }
}
