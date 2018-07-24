//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.flysand.mylibrary.R;

@SuppressLint("AppCompatCustomView")
public class CircleImageView extends ImageView {
    private static final Config BITMAP_CONFIG;
    private static final int COLORDRAWABLE_DIMENSION = 2;
    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = -16777216;
    private static final int DEFAULT_FILL_COLOR = 0;
    private static final boolean DEFAULT_BORDER_OVERLAY = false;
    private final RectF mDrawableRect;
    private final RectF mBorderRect;
    private final Matrix mShaderMatrix;
    private final Paint mBitmapPaint;
    private final Paint mBorderPaint;
    private final Paint mFillPaint;
    private int mBorderColor;
    private int mBorderWidth;
    private int mFillColor;
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private float mDrawableRadius;
    private float mBorderRadius;
    private ColorFilter mColorFilter;
    private boolean mReady;
    private boolean mSetupPending;
    private boolean mBorderOverlay;

    public CircleImageView(Context var1) {
        super(var1);
        this.mDrawableRect = new RectF();
        this.mBorderRect = new RectF();
        this.mShaderMatrix = new Matrix();
        this.mBitmapPaint = new Paint();
        this.mBorderPaint = new Paint();
        this.mFillPaint = new Paint();
        this.mBorderColor = -16777216;
        this.mBorderWidth = 0;
        this.mFillColor = 0;
        this.init();
    }

    public CircleImageView(Context var1, AttributeSet var2) {
        this(var1, var2, 0);
    }

    public CircleImageView(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        this.mDrawableRect = new RectF();
        this.mBorderRect = new RectF();
        this.mShaderMatrix = new Matrix();
        this.mBitmapPaint = new Paint();
        this.mBorderPaint = new Paint();
        this.mFillPaint = new Paint();
        TypedArray var4 = var1.obtainStyledAttributes(var2, R.styleable.CircleImageView, var3, 0);
        this.mBorderWidth = var4.getDimensionPixelSize(R.styleable.CircleImageView_civ_border_width, 10);
        this.mBorderColor = var4.getColor(R.styleable.CircleImageView_civ_border_color, Color.parseColor("#d3d3d3"));
        this.mBorderOverlay = var4.getBoolean(R.styleable.CircleImageView_civ_border_overlay, false);
        this.mFillColor = var4.getColor(R.styleable.CircleImageView_civ_fill_color, Color.parseColor("#00000000"));
        var4.recycle();
        this.init();
    }

    private void init() {
        this.mReady = true;
        if(this.mSetupPending) {
            this.setup();
            this.mSetupPending = false;
        }

    }

    public void setAdjustViewBounds(boolean var1) {
        if(var1) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    protected void onDraw(Canvas var1) {
        if(this.mBitmap != null) {
            if(this.mFillColor != 0) {
                var1.drawCircle((float)this.getWidth() / 2.0F, (float)this.getHeight() / 2.0F, this.mDrawableRadius, this.mFillPaint);
            }

            var1.drawCircle((float)this.getWidth() / 2.0F, (float)this.getHeight() / 2.0F, this.mDrawableRadius, this.mBitmapPaint);
            if(this.mBorderWidth != 0) {
                var1.drawCircle((float)this.getWidth() / 2.0F, (float)this.getHeight() / 2.0F, this.mBorderRadius, this.mBorderPaint);
            }

        }
    }

    protected void onSizeChanged(int var1, int var2, int var3, int var4) {
        super.onSizeChanged(var1, var2, var3, var4);
        this.setup();
    }

    public int getBorderColor() {
        return this.mBorderColor;
    }

    public void setBorderColor(@ColorInt int var1) {
        if(var1 != this.mBorderColor) {
            this.mBorderColor = var1;
            this.mBorderPaint.setColor(this.mBorderColor);
            this.invalidate();
        }
    }

    public void setBorderColorResource(@ColorRes int var1) {
        this.setBorderColor(this.getContext().getResources().getColor(var1));
    }

    public int getFillColor() {
        return this.mFillColor;
    }

    public void setFillColor(@ColorInt int var1) {
        if(var1 != this.mFillColor) {
            this.mFillColor = var1;
            this.mFillPaint.setColor(var1);
            this.invalidate();
        }
    }

    public void setFillColorResource(@ColorRes int var1) {
        this.setFillColor(this.getContext().getResources().getColor(var1));
    }

    public int getBorderWidth() {
        return this.mBorderWidth;
    }

    public void setBorderWidth(int var1) {
        if(var1 != this.mBorderWidth) {
            this.mBorderWidth = var1;
            this.setup();
        }
    }

    public boolean isBorderOverlay() {
        return this.mBorderOverlay;
    }

    public void setBorderOverlay(boolean var1) {
        if(var1 != this.mBorderOverlay) {
            this.mBorderOverlay = var1;
            this.setup();
        }
    }

    public void setImageBitmap(Bitmap var1) {
        super.setImageBitmap(var1);
        this.mBitmap = var1;
        this.setup();
    }

    public void setImageDrawable(Drawable var1) {
        super.setImageDrawable(var1);
        this.mBitmap = this.getBitmapFromDrawable(var1);
        this.setup();
    }

    public void setImageResource(@DrawableRes int var1) {
        super.setImageResource(var1);
        this.mBitmap = this.getBitmapFromDrawable(this.getDrawable());
        this.setup();
    }

    public void setImageURI(Uri var1) {
        super.setImageURI(var1);
        this.mBitmap = var1 != null?this.getBitmapFromDrawable(this.getDrawable()):null;
        this.setup();
    }

    public void setColorFilter(ColorFilter var1) {
        if(var1 != this.mColorFilter) {
            this.mColorFilter = var1;
            this.mBitmapPaint.setColorFilter(this.mColorFilter);
            this.invalidate();
        }
    }

    private Bitmap getBitmapFromDrawable(Drawable var1) {
        if(var1 == null) {
            return null;
        } else if(var1 instanceof BitmapDrawable) {
            return ((BitmapDrawable)var1).getBitmap();
        } else {
            try {
                Bitmap var2;
                if(var1 instanceof ColorDrawable) {
                    var2 = Bitmap.createBitmap(2, 2, BITMAP_CONFIG);
                } else {
                    var2 = Bitmap.createBitmap(var1.getIntrinsicWidth(), var1.getIntrinsicHeight(), BITMAP_CONFIG);
                }

                Canvas var3 = new Canvas(var2);
                var1.setBounds(0, 0, var3.getWidth(), var3.getHeight());
                var1.draw(var3);
                return var2;
            } catch (Exception var4) {
                var4.printStackTrace();
                return null;
            }
        }
    }

    private void setup() {
        if(!this.mReady) {
            this.mSetupPending = true;
        } else if(this.getWidth() != 0 || this.getHeight() != 0) {
            if(this.mBitmap == null) {
                this.invalidate();
            } else {
                this.mBitmapShader = new BitmapShader(this.mBitmap, TileMode.CLAMP, TileMode.CLAMP);
                this.mBitmapPaint.setAntiAlias(true);
                this.mBitmapPaint.setShader(this.mBitmapShader);
                this.mBorderPaint.setStyle(Style.STROKE);
                this.mBorderPaint.setAntiAlias(true);
                this.mBorderPaint.setColor(this.mBorderColor);
                this.mBorderPaint.setStrokeWidth((float)this.mBorderWidth);
                this.mFillPaint.setStyle(Style.FILL);
                this.mFillPaint.setAntiAlias(true);
                this.mFillPaint.setColor(this.mFillColor);
                this.mBitmapHeight = this.mBitmap.getHeight();
                this.mBitmapWidth = this.mBitmap.getWidth();
                this.mBorderRect.set(0.0F, 0.0F, (float)this.getWidth(), (float)this.getHeight());
                this.mBorderRadius = Math.min((this.mBorderRect.height() - (float)this.mBorderWidth) / 2.0F, (this.mBorderRect.width() - (float)this.mBorderWidth) / 2.0F);
                this.mDrawableRect.set(this.mBorderRect);
                if(!this.mBorderOverlay) {
                    this.mDrawableRect.inset((float)this.mBorderWidth, (float)this.mBorderWidth);
                }

                this.mDrawableRadius = Math.min(this.mDrawableRect.height() / 2.0F, this.mDrawableRect.width() / 2.0F);
                this.updateShaderMatrix();
                this.invalidate();
            }
        }
    }

    private void updateShaderMatrix() {
        float var2 = 0.0F;
        float var3 = 0.0F;
        this.mShaderMatrix.set((Matrix)null);
        float var1;
        if((float)this.mBitmapWidth * this.mDrawableRect.height() > this.mDrawableRect.width() * (float)this.mBitmapHeight) {
            var1 = this.mDrawableRect.height() / (float)this.mBitmapHeight;
            var2 = (this.mDrawableRect.width() - (float)this.mBitmapWidth * var1) * 0.5F;
        } else {
            var1 = this.mDrawableRect.width() / (float)this.mBitmapWidth;
            var3 = (this.mDrawableRect.height() - (float)this.mBitmapHeight * var1) * 0.5F;
        }

        this.mShaderMatrix.setScale(var1, var1);
        this.mShaderMatrix.postTranslate((float)((int)(var2 + 0.5F)) + this.mDrawableRect.left, (float)((int)(var3 + 0.5F)) + this.mDrawableRect.top);
        this.mBitmapShader.setLocalMatrix(this.mShaderMatrix);
    }

    static {
        BITMAP_CONFIG = Config.ARGB_8888;
    }
}
