//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.flysand.mylibrary.R;

@SuppressLint("AppCompatCustomView")
public class DrawableCenterTextView extends TextView {
    private int drawableWidth;
    private int drawableHeight;
    boolean isCalulate;
    private boolean isCenter;

    public DrawableCenterTextView(Context var1, AttributeSet var2) {
        super(var1, var2);
        this.isCalulate = false;
        TypedArray var3 = var1.obtainStyledAttributes(var2, R.styleable.CustomDrawableTextView);
        this.drawableWidth = (int)var3.getDimension(R.styleable.CustomDrawableTextView_drawableWidth, 30.0F);
        this.drawableHeight = (int)var3.getDimension(R.styleable.CustomDrawableTextView_drawableHeight, 30.0F);
        this.isCenter = var3.getBoolean(R.styleable.CustomDrawableTextView_drawableTextCenter, true);
        this.setSingleLine();
        var3.recycle();
    }

    public DrawableCenterTextView(Context var1) {
        this(var1, (AttributeSet)null);
    }

    protected void onDraw(Canvas var1) {
        if(!this.isCalulate) {
            this.isCalulate = true;
            Drawable[] var2 = this.getCompoundDrawables();
            if(var2 != null) {
                Drawable var3;
                int var4;
                if(var2[0] != null) {
                    var3 = var2[0];
                    this.setGravity(19);
                    if(this.isCenter) {
                        this.setPadding((int)(((float)this.getWidth() - this.getBodyWidth(var3)) / 2.0F), 0, 0, 0);
                    }

                    var4 = (this.drawableHeight - var3.getIntrinsicHeight()) / 2;
                    var3.setBounds(0, -var4, this.drawableWidth, this.drawableHeight - var4);
                    this.setCompoundDrawablePadding(this.drawableWidth - var3.getIntrinsicWidth() + this.getCompoundDrawablePadding());
                } else if(var2[2] != null) {
                    var3 = var2[2];
                    this.setGravity(21);
                    if(this.isCenter) {
                        this.setPadding(0, 0, (int)(((float)this.getWidth() - this.getBodyWidth(var3)) / 2.0F), 0);
                    }

                    var4 = (this.drawableHeight - var3.getIntrinsicHeight()) / 2;
                    var3.setBounds(var3.getIntrinsicWidth() - this.drawableWidth, -var4, var3.getIntrinsicWidth(), this.drawableHeight - var4);
                    this.setCompoundDrawablePadding(this.drawableWidth - var3.getIntrinsicWidth() + this.getCompoundDrawablePadding());
                } else if(var2[1] != null) {
                    var3 = var2[1];
                    this.setGravity(49);
                    if(this.isCenter) {
                        Paint var6 = new Paint();
                        var6.setTextSize(this.getTextSize());
                        this.setPadding(0, (int)(((float)this.getHeight() - this.getBodyHeight(var3) + var6.getFontMetrics().descent) / 2.0F), 0, 0);
                    }

                    var4 = (this.drawableWidth - var3.getIntrinsicWidth()) / 2;
                    int var5 = (this.drawableHeight - var3.getIntrinsicHeight()) / 10;
                    var3.setBounds(-var4, -var5, this.drawableWidth - var4, this.drawableHeight - var5);
                    this.setCompoundDrawablePadding(this.drawableHeight - var3.getIntrinsicHeight() + this.getCompoundDrawablePadding());
                } else if(var2[3] != null) {
                    var3 = var2[3];
                    this.setGravity(81);
                    if(this.isCenter) {
                        this.setPadding(0, 0, 0, (int)(((float)this.getHeight() - this.getBodyHeight(var3)) / 2.0F));
                    }

                    var4 = (this.drawableWidth - var3.getIntrinsicWidth()) / 2;
                    var3.setBounds(-var4, var3.getIntrinsicHeight() - this.drawableHeight, this.drawableWidth - var4, var3.getIntrinsicHeight());
                    this.setCompoundDrawablePadding(this.drawableHeight - var3.getIntrinsicHeight() + this.getCompoundDrawablePadding());
                } else {
                    this.setGravity(17);
                    this.setPadding(0, 0, 0, 0);
                }
            }
        }

        super.onDraw(var1);
    }

    private float getBodyWidth(Drawable var1) {
        float var2 = this.measureWidth();
        int var3 = this.getCompoundDrawablePadding();
        int var4 = var1.getIntrinsicWidth();
        if(var4 < 0) {
            var4 = this.drawableWidth;
        }

        return var2 + (float)var4 + (float)var3;
    }

    private float getBodyHeight(Drawable var1) {
        float var2 = this.measureHeight();
        int var3 = this.getCompoundDrawablePadding();
        int var4 = var1.getIntrinsicHeight();
        if(var4 < 0) {
            var4 = this.drawableHeight;
        }

        return var2 + (float)var4 + (float)var3;
    }

    private float measureWidth() {
        Paint var1 = new Paint();
        var1.setTextSize(this.getTextSize());
        return !TextUtils.isEmpty(this.getText())?var1.measureText(this.getText().toString()):0.0F;
    }

    private float measureHeight() {
        Paint var1 = new Paint();
        var1.setTextSize(this.getTextSize());
        return !TextUtils.isEmpty(this.getText())?var1.measureText(this.getText().toString().substring(0, 1)):0.0F;
    }
}
