package com.flysand.mylibrary.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import com.flysand.mylibrary.R;


/**
 * 文件名：ClearEditText
 * 描    述： 可清除EditText中的文字，
 * 作    者：flysand - yjf
 * 时    间：2016/7/25.
 * 版    权：山东贝格新能源科技有限公司
 */
public class ClearEditText extends AppCompatEditText implements TextWatcher {
    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;
    private int deleteIcon;
    ;
    private int drawableWith = 0;
    /**
     * 控件是否有焦点
     */
    private boolean hasFoucs;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        // 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);

    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray var4 = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText, defStyle, 0);
        this.deleteIcon = var4.getResourceId(R.styleable.ClearEditText_deleteIcon, R.mipmap.delete_text_icon);
        this.drawableWith = (int) var4.getDimension(R.styleable.ClearEditText_drawableWith, 40);
        init();
    }

    private void init() {

        // 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2];
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        if (mClearDrawable == null){
            mClearDrawable = getResources().getDrawable(deleteIcon);
        }
        mClearDrawable.setBounds(0, 0, drawableWith, drawableWith);
//        mClearDrawable.setBounds(0, 0, (int) (20 * density),
//                (int) (20 * density));
        // 默认设置隐藏图标
        setClearIconVisible(false);
        // 设置焦点改变的监听
        // setOnFocusChangeListener(this);
        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 -
     * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {

                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable) {
                    this.setText("");
                }
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        this.hasFoucs = focused;
        if (focused) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if (hasFoucs) {
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.setAnimation(shakeAnimation(5));
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

}