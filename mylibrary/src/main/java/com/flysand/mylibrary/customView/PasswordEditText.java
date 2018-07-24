package com.flysand.mylibrary.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.NumberKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import com.flysand.mylibrary.R;


/**
 * 文件名：PasswordEditText
 * 描    述：
 * 作    者：flysand - yjf
 * 时    间：2016/7/25.
 * 版    权：山东贝格新能源科技有限公司
 */
@SuppressLint("ClickableViewAccessibility")
public class PasswordEditText extends AppCompatEditText implements TextWatcher {
    /**
     * 删除按钮的引用
     */
    private Drawable mSeeDrawable;
    /**
     * 删除按钮的引用
     */
    private Drawable hSeeDrawable;
    /**
     * 控件是否有焦点
     */
    private boolean hasFoucs;
    /**
     * 显示及隐藏状态
     */
    private boolean isHidden;

    public PasswordEditText(Context context) {
        this(context, null);
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        // 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public PasswordEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        initPsswordInput();
    }

    private void init() {
        // 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mSeeDrawable = getCompoundDrawables()[2];
        if (mSeeDrawable == null) {
            // throw new
            // NullPointerException("You can add drawableRight attribute in XML");
            mSeeDrawable = getResources().getDrawable(R.mipmap.password_show);
        }
        if (hSeeDrawable == null) {
            // throw new
            // NullPointerException("You can add drawableRight attribute in XML");
            hSeeDrawable = getResources().getDrawable(R.mipmap.password_hide);
        }
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        mSeeDrawable.setBounds(0, 0, (int) (35 * density),
                (int) (35 * density));
        hSeeDrawable.setBounds(0, 0, (int) (35 * density),
                (int) (35 * density));
        // 默认设置隐藏图标
        setClearIconVisible(false);
        // 设置焦点改变的监听
        // setOnFocusChangeListener(this);
        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }


    /**
     * 只能输入指定字符
     */
    private void initPsswordInput() {
        setKeyListener(new NumberKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                char[] numberChars = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
                        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                        '.', '+', '-', '*', '/', '@', ','};
                return numberChars;
            }

            @Override
            public int getInputType() {
                return android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD;
            }
        });

        /**
         * 屏蔽复制黏贴
         */
        this.setCustomSelectionActionModeCallback(new ActionModeCallbackInterceptor());
        this.setLongClickable(false);
        this.setTextIsSelectable(false);
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
//                    this.setText("");
                    if (isHidden) {
                        //设置EditText文本为可见的
                        this.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        //设置EditText文本为隐藏的
                        this.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                    isHidden = !isHidden;
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
        Drawable right = null;
        if (isHidden){
             right = visible ? mSeeDrawable : null;
        }else {
            right = visible ? hSeeDrawable : null;
        }

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
    /**
     * Prevents the action bar (top horizontal bar with cut, copy, paste, etc.)
     * from appearing by intercepting the callback that would cause it to be
     * created, and returning false.
     */
    private class ActionModeCallbackInterceptor implements ActionMode.Callback {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }


        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }


        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }


        public void onDestroyActionMode(ActionMode mode) {
        }
    }


    boolean canPaste() {
        return false;
    }
    boolean canCut() {
        return false;
    }
    boolean canCopy() {
        return false;
    }
    boolean canSelectAllText() {
        return false;
    }
    boolean canSelectText() {
        return false;
    }
    boolean textCanBeSelected() {
        return false;
    }
    @Override
    public boolean onTextContextMenuItem(int id) {
        return true;
    }
}