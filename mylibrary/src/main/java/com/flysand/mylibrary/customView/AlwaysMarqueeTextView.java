package com.flysand.mylibrary.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * 项目 BaseActivityApplication
 * Created by SunFangtao on 2016/12/14.
 */

@SuppressLint("AppCompatCustomView")
public class AlwaysMarqueeTextView extends TextView {

    private boolean isCenter = false;

    public AlwaysMarqueeTextView(Context context) {
        this(context, null);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isCenter) {
            setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            // getCompoundDrawables() : Returns drawables for the left, top, right, and bottom borders.
            Drawable[] drawables = getCompoundDrawables();
            // 得到drawableLeft设置的drawable对象
            Drawable rightDrawable = drawables[2];
            if (rightDrawable != null) {
                // 得到rightDrawable的宽度
                int rightDrawableWidth = rightDrawable.getIntrinsicWidth();
                // 得到drawable与text之间的间距
                int drawablePadding = getCompoundDrawablePadding();
                // 得到文本的宽度
                int textWidth = (int) getPaint().measureText(getText().toString().trim());
                int bodyWidth = rightDrawableWidth + drawablePadding + textWidth;
                canvas.save();
                canvas.translate(-(getWidth() - bodyWidth) / 2, 0);
            }
        }
        super.onDraw(canvas);
    }

    public void setCenter(boolean center) {
        isCenter = center;
    }
}
