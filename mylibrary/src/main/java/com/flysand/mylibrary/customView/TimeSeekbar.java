package com.flysand.mylibrary.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.flysand.mylibrary.R;

import java.text.DecimalFormat;

/**
 *     <com.aioute.carmanager.util.customView.TimeSeekbar
 android:id="@+id/settings_enclosure_timeSeekbar"
 android:layout_width="match_parent"
 app:timeSeekbar_markTextArray="@array/distanceArray"
 android:layout_marginLeft="20dp"
 android:layout_marginRight="20dp"
 app:timeSeekbar_maxValue="2000"
 app:timeSeekbar_padding="10dp"
 app:timeSeekbar_textPosition="textBottom"
 app:timeSeekbar_textSize="10dp"
 android:layout_marginBottom="21dp"
 android:layout_height="wrap_content" />


 timeSeekbar.setListener(new TimeSeekbar.SeekBarChangeListener() {
@Override
public void onSeekBarStartChange() {
}

@Override
public void onSeekBarChanging() {
}

@Override
public void onSeekBarChanged(int index, String content) {
seekBarindex = index;
Utils.print("index = " + index + "  content = " + content);
if (circle != null){
circle.remove();

circle =  mAMap.addCircle(new CircleOptions().
center(Utils.coordinateConversion(SettingsEnclosureActivity.this,new LatLng(carPositionBean.getLatitude(), carPositionBean.getLongitude()))).
radius(distances[index]).
fillColor(Color.parseColor("#7FAAAAAA")).
strokeColor(Color.WHITE).
strokeWidth(5));
}
}
});
 */
@SuppressLint({"DrawAllocation", "ClickableViewAccessibility"})
@SuppressWarnings("unused")
public class TimeSeekbar extends View {

    /**
     * 动画默认的时间
     */
    private static final int DEFAULT_DURATION = 100;
    /**
     * 线与字的间隔
     */
    private static final int OFFSET = 20;
    /**
     * 左边的scroller
     */
    private Scroller mLeftScroller;
    /**
     * 动画的时间
     */
    private int mDuration;
    /**
     * 时间轴的高度
     */
    private int mSeekbarHeight;
    /**
     * 线的高度
     */
    private int mLineHeight;
    /**
     * 时间标签字体的大小
     */
    private int mTextSize;
    /**
     * 标签的内容
     */
    private CharSequence[] mTextArray;
    /**
     * 没有选中时字体的颜色
     */
    private int mTextColorNoSelected;
    /**
     * 选中后字体的颜色
     */
    private int mTextColorSelected;
    /**
     * 没有选中是字体的颜色
     */
    private int mBarColorNoSelected;
    /**
     * 选中后字体的颜色
     */
    private int mBarColorSelected;
    /**
     * 标签的个数
     */
    private int mTextArraySize;
    /**
     * 字的画笔
     */
    private Paint mTextPaint;
    /**
     * 标签长度的数组
     */
    private float[] mTextWidthArray;
    /**
     * 两边的padding
     */
    private int mSeekBarPadding;
    /**
     * 所有标签点的最大宽度
     */
    private float mTextMaxWidth;
    /**
     * 选中是圈的大小
     */
    private float mSelectedRidus;
    /**
     * 没有选中是圈的大小
     */
    private float mNoSelectedRidus;
    /**
     * 当前所在的标签下标
     */
    private int mTextIndex;
    /**
     * 每个标签的起始位置
     */
    private float[] mTextStartPos;
    /**
     * 线所在的位置
     */
    private RectF mLineRect;
    /**
     * 是否是触摸导致重绘
     */
    private boolean isTouch;
    /**
     * 当前的进度
     */
    private float progress;
    /**
     * 最大值
     */
    private float maxValue;
    /**
     * 监听器
     */
    private SeekBarChangeListener seekBarChangeListener;

    public TimeSeekbar(Context context) {
        this(context, null, 0);
    }

    public TimeSeekbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeSeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyConfig(context, attrs);

        mLeftScroller = new Scroller(context, new DecelerateInterpolator());

        initPaint();

        setWillNotDraw(false);
        setFocusable(true);
        setClickable(true);
    }

    protected void applyConfig(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TimeSeekbar);

        mDuration = a.getInteger(
                R.styleable.TimeSeekbar_timeSeekbar_autoMoveDuration,
                DEFAULT_DURATION);

        mTextColorNoSelected = a.getColor(
                R.styleable.TimeSeekbar_timeseekbar_textColorNoSelected,
                Color.parseColor("#aaaaaa"));
        mTextColorSelected = a.getColor(
                R.styleable.TimeSeekbar_timeSeekbar_textColorSelected,
                Color.parseColor("#00a5f5"));

        mBarColorNoSelected = a.getColor(
                R.styleable.TimeSeekbar_timeseekbar_barColorNoSelected,
                Color.parseColor("#aaaaaa"));
        mBarColorSelected = a.getColor(
                R.styleable.TimeSeekbar_timeSeekbar_barColorSelected,
                Color.parseColor("#00a5f5"));

        mSeekbarHeight = (int) a.getDimension(
                R.styleable.TimeSeekbar_timeSeekbar_height, 15);
        mLineHeight = (int) a.getDimension(
                R.styleable.TimeSeekbar_timeSeekbar_lineHeight, 8);

        mTextSize = (int) a.getDimension(
                R.styleable.TimeSeekbar_timeSeekbar_textSize, 15);


        mSeekBarPadding = (int) a.getDimension(
                R.styleable.TimeSeekbar_timeSeekbar_padding, 0);

        mNoSelectedRidus = (int) a.getDimension(
                R.styleable.TimeSeekbar_timeSeekbar_noSelectedRidus, 15);

        maxValue = a.getFloat(R.styleable.TimeSeekbar_timeSeekbar_maxValue,
                100f);

        mTextArray = a
                .getTextArray(R.styleable.TimeSeekbar_timeSeekbar_markTextArray);
        if (mTextArray != null && mTextArray.length > 0) {
            mTextWidthArray = new float[mTextArray.length];
            mTextArraySize = mTextArray.length;
        }

        a.recycle();
    }

    /**
     * 初始化字体的画笔
     */
    protected void initPaint() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Style.FILL);
        mTextPaint.setTextSize(mTextSize);
    }

    /**
     * 获取每个标签的长度
     */
    protected void initTextWidthArray() {
        if (mTextArray != null && mTextArray.length > 0) {
            mTextWidthArray = new float[mTextArray.length];
            mTextStartPos = new float[mTextArray.length];
            final int length = mTextArray.length;
            for (int i = 0; i < length; i++) {
                mTextWidthArray[i] = mTextPaint.measureText(mTextArray[i]
                        .toString());
                mTextStartPos[i] = mSeekBarPadding + i * mTextMaxWidth;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mLineHeight > mSeekbarHeight / 2) {
            mLineHeight = mSeekbarHeight / 2;
        }
        if (mNoSelectedRidus > mSeekbarHeight) {
            mNoSelectedRidus = mSeekbarHeight;
        }
        if (mNoSelectedRidus <= mLineHeight) {
            mNoSelectedRidus += mSeekbarHeight / 8;
        }
        mSelectedRidus = mSeekbarHeight;
        // 实际需要的高度
        int heightNeeded = mSeekbarHeight + mTextSize;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightNeeded + OFFSET,
                MeasureSpec.EXACTLY);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (mTextArray != null) {
            float oriTextMaxWidth = 0;
            if (mTextArray.length == 1) {
                oriTextMaxWidth = widthSize;
                mTextMaxWidth = oriTextMaxWidth;
                mSeekBarPadding = 0;
            } else {
                oriTextMaxWidth = widthSize / (mTextArray.length - 1);
                mTextMaxWidth = (widthSize - 2 * mSeekBarPadding)
                        / (mTextArray.length - 1);
            }
        }
        initTextWidthArray();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        mTextPaint.setTextSize(mTextSize);
        final float curPos = (mTextArraySize - 1) * mTextMaxWidth
                * (progress / maxValue) + mSeekBarPadding;
        // 当前包含的标签的最大下标
        int includeNumIndex = 0;
        for (int i = 1; i < mTextArraySize; i++) {
            includeNumIndex = mTextStartPos[i] > curPos ? includeNumIndex : i;
        }
        for (int i = 0; i < mTextArraySize; i++) {
            // 写标签的文字
            if (i <= (isTouch ? mTextIndex : includeNumIndex)) {
                mTextPaint.setColor(mTextColorSelected);
            } else {
                mTextPaint.setColor(mTextColorNoSelected);
            }
            drawText(canvas, i);
            // 画轴
            if (isTouch) {
                if (i <= mTextIndex) {
                    mTextPaint.setColor(mBarColorSelected);
                } else {
                    mTextPaint.setColor(mBarColorNoSelected);
                }
                mLineRect = new RectF(mSeekBarPadding
                        + (Math.max(0, i - 1)) * mTextMaxWidth,
                        (mSeekbarHeight - mLineHeight) / 2 + OFFSET / 2,
                        mTextStartPos[i], (mSeekbarHeight + mLineHeight)
                        / 2 + OFFSET / 2);
                canvas.drawRoundRect(mLineRect, 0, 0, mTextPaint);
            } else {
                if (i == mTextArraySize - 1) {
                    mTextPaint.setColor(mBarColorSelected);
                    mLineRect = new RectF(
                            mSeekBarPadding,
                            (mSeekbarHeight - mLineHeight) / 2 + OFFSET / 2,
                            curPos, (mSeekbarHeight + mLineHeight) / 2
                            + OFFSET / 2);
                    canvas.drawRoundRect(mLineRect, 0, 0, mTextPaint);
                    mTextPaint.setColor(mBarColorNoSelected);
                    mLineRect = new RectF(
                            curPos,
                            (mSeekbarHeight - mLineHeight) / 2 + OFFSET / 2,
                            mTextStartPos[i],
                            (mSeekbarHeight + mLineHeight) / 2 + OFFSET / 2);
                    canvas.drawRoundRect(mLineRect, 0, 0, mTextPaint);
                }
            }
        }

        for (int i = 0; i < mTextArraySize; i++) {
            if (i <= (isTouch ? mTextIndex : includeNumIndex)) {
                mTextPaint.setColor(mBarColorSelected);
            } else {
                mTextPaint.setColor(mBarColorNoSelected);
            }
            // 画圈
            if (i == (isTouch ? mTextIndex : pos2Index(curPos))) {
                canvas.drawCircle(mTextStartPos[i],
                        mSeekbarHeight / 2+ OFFSET / 2, mSelectedRidus, mTextPaint);
                mTextPaint.setColor(Color.parseColor("#ffffff"));
                canvas.drawCircle(mTextStartPos[i] ,
                        mSeekbarHeight / 2+ OFFSET / 2,
                        mSelectedRidus * 3 / 5, mTextPaint);


            } else {
                canvas.drawCircle(mTextStartPos[i], mSeekbarHeight / 2
                        + OFFSET / 2, mNoSelectedRidus, mTextPaint);
            }
        }

        // 画剩余时间
        drawLastTime(canvas);
        super.onDraw(canvas);
    }

    private void drawLastTime(Canvas canvas) {

    }

    private void drawText(Canvas canvas, int i) {
        final float textWidth = mTextWidthArray[i];
        float textDrawLeft = 0;
        if (i == 0) {
            if (textWidth / 2 < mSeekBarPadding) {
                textDrawLeft = mSeekBarPadding - textWidth / 2;
            }
        } else if (i == mTextArraySize - 1) {
            if (textWidth / 2 < mSeekBarPadding) {
                textDrawLeft = mTextStartPos[i] - textWidth / 2;
            } else {
                textDrawLeft = mSeekBarPadding + mTextStartPos[i] - textWidth;
            }
        } else {
            textDrawLeft = mTextStartPos[i] - textWidth / 2;
        }
        canvas.drawText(mTextArray[i].toString(), textDrawLeft,
                mSeekbarHeight + mTextSize + OFFSET, mTextPaint);
    }

    private int pos2Index(float pos) {
        DecimalFormat df = new DecimalFormat(".##");
        String value = df.format((pos - mSeekBarPadding) / mTextMaxWidth);
        if (Float.valueOf(value) % 1.0f == 0) {
            return (int) (Float.parseFloat(value));
        }
        return -1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        setTouch(true);
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handleTouchDown(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                handleTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                handleTouchMove(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                handleTouchUp(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                handleTouchUp(event);
                break;
        }

        return super.onTouchEvent(event);
    }

    private void handleTouchDown(MotionEvent event) {
        final int actionIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int downX = (int) event.getX(actionIndex);
        mTextIndex = 0;
        for (int i = 0; i < mTextArraySize - 1; i++) {
            mTextIndex = isInRectF(downX, i) ? i + 1 : mTextIndex;
        }
        invalidate();
    }

    private void handleTouchMove(MotionEvent event) {
        handleTouchDown(event);
    }

    private void handleTouchUp(MotionEvent event) {
        handleTouchDown(event);
        if (seekBarChangeListener != null && isTouch) {
            seekBarChangeListener.onSeekBarChanged(mTextIndex,
                    mTextArray[mTextIndex].toString());
        }
    }

    private boolean isInRectF(int position, int i) {
        if (position - mSeekBarPadding >= mTextMaxWidth / 2 + i * mTextMaxWidth) {
            return true;
        }
        return false;
    }

    public void setListener(SeekBarChangeListener seekBarChangeListener) {
        this.seekBarChangeListener = seekBarChangeListener;
    }

    public void setTouch(boolean isTouch) {
        this.isTouch = isTouch;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        if (progress < 0) {
            return;
        }
        setTouch(false);
        this.progress = progress > maxValue ? maxValue : progress;
        invalidate();
    }

    public interface SeekBarChangeListener {

        // 开始滑动话点击是执行
        public void onSeekBarStartChange();

        // 正在滑动是执行
        public void onSeekBarChanging();

        // 　滑动或点击完成后改变了位置时执行
        public void onSeekBarChanged(int index, String content);
    }
}
