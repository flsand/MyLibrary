package com.flysand.mylibrary.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RadioButton;

import com.flysand.mylibrary.R;
import com.flysand.mylibrary.util.Utils;


/**
 * Created by Administrator on 2016/5/7.
 */
public class DrawableCenterRadioButton extends RadioButton {

    // drawable 的宽度
    private int drawableWidth;
    // drawable 的高度
    private int drawableHeight;
    // 是否已经根据drawable的大小重新计算drawablepadding
    int isCalulate = 0;
    // drawable 和 文字是否居中
    private boolean isCenter;

    private int padding = -1;

    public DrawableCenterRadioButton(Context context) {
        this(context, null);
    }


    public DrawableCenterRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomDrawableTextView);
        drawableWidth = (int) a.getDimension(R.styleable.CustomDrawableTextView_drawableWidth, 30);
        drawableHeight = (int) a.getDimension(R.styleable.CustomDrawableTextView_drawableHeight, 30);
        isCenter = a.getBoolean(R.styleable.CustomDrawableTextView_drawableTextCenter, true);
        setSingleLine();
        a.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (isCenter && isCalulate < 2) {
            isCalulate++;
            if (padding < 0) {
                padding = getCompoundDrawablePadding();
            }
            Drawable[] drawables = getCompoundDrawables();
            Drawable drawableLeft = drawables[0];
            Drawable drawableTop = drawables[1];
            Drawable drawableRight = drawables[2];
            Drawable drawableBottom = drawables[3];
            if (drawableLeft != null) {
                setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                setPadding((int) (getWidth() - getBodyWidth(padding)) / 2, 0, 0, 0);
                int offsetHeight = (drawableHeight - drawableLeft.getIntrinsicHeight()) / 2;
                drawableLeft.setBounds(0, -offsetHeight, drawableWidth, drawableHeight - offsetHeight);
                setCompoundDrawablePadding(padding + drawableWidth - drawableLeft.getIntrinsicWidth());
            } else if (drawableTop != null) {
                setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                setPadding(0, (int) (getHeight() - getBodyHeight(padding)) / 2, 0, 0);
                int offsetWidth = (drawableWidth - drawableTop.getIntrinsicWidth()) / 2;
                drawableTop.setBounds(-offsetWidth, 0, drawableWidth - offsetWidth, drawableHeight);
                setCompoundDrawablePadding(padding + drawableHeight - drawableTop.getIntrinsicHeight());
            } else if (drawableRight != null) {
                setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                setPadding(0, 0, (int) (getWidth() - getBodyWidth(padding)) / 2, 0);
                int offsetHeight = (drawableHeight - drawableRight.getIntrinsicHeight()) / 2;
                drawableRight.setBounds(drawableWidth, -offsetHeight, drawableWidth + drawableWidth, drawableHeight - offsetHeight);
                setCompoundDrawablePadding(padding + drawableWidth - drawableRight.getIntrinsicWidth());
            } else if (drawableBottom != null) {
                setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                setPadding(0, 0, 0, (int) (getHeight() - getBodyHeight(padding)) / 2);
                int offsetWidth = (drawableWidth - drawableBottom.getIntrinsicWidth()) / 2;
                int offsetHeight = (drawableHeight - drawableBottom.getIntrinsicHeight()) / 2;
                drawableBottom.setBounds(-offsetWidth, (int) (getHeight() - getBodyHeight(padding)) / 2, drawableWidth - offsetWidth, (int) (getHeight() - getBodyHeight(padding)) / 2 + drawableHeight);
                setCompoundDrawablePadding(padding + drawableHeight - drawableBottom.getIntrinsicHeight());
            }
        }
        super.onDraw(canvas);
    }

    private float getBodyWidth(int drawablePadding) {
        float textWidth = measureWidth();
        return textWidth + this.drawableWidth + drawablePadding;
    }

    private float getBodyHeight(int drawablePadding) {
        float textHeight = -getPaint().getFontMetrics().ascent;
        return textHeight + this.drawableHeight + drawablePadding;
    }

    private float measureWidth() {
        Paint paint = new Paint();
        paint.setTextSize(getTextSize());
        if (!TextUtils.isEmpty(getText())) {
            return paint.measureText(getText().toString());
        } else {
            return 0;
        }
    }

}
