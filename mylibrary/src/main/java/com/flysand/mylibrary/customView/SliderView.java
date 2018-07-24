package com.flysand.mylibrary.customView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.flysand.mylibrary.R;


/**
 * Created by Administrator on 2017/3/24.
 */

public class SliderView extends View {

    private static final int TOUCH_MODE_IDLE = 0;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;

    private static final int duration = 600; //左右切换动画持续时间

    private Paint mPaint;
    private Paint bgPaint;
    private Paint bluePaint;
    private Paint textPaint;
    private SwitchScroller switchScroller;  //切换滚动器，用于实现平滑滚动效果
    private ValueAnimator rotateAnim;//旋转动画

    private Bitmap maskBitmap;//中间圆的按钮
    private RectF maskButtonRectF;   //按钮的位置
    private RectF bgRectF;//背景

    private int drawableRs;//.slide_mask_icon
    private int btnWidth;
    private int btnHeight;
    private int paddingLeft;
    private int paddingTop;
    private float mTextSize;
    private String defaultTipText;//提示信息
    private String toggleConductText;//操作中
    private String toggleCompleteText;//操作结束

    private int mTextColor;

    //如果滑动距离大于等于最小切换距离就切换状态，否则回滚
    private int threshold;
    //是否显示成功的文字
    private boolean isShowSuccessText = false;

    private Matrix matrix = null;//按钮旋转
    private float mRotate = 0.0f;    //  图片旋转角度
    private int touchMode; //触摸模式，用来在处理滑动事件的时候区分操作
    private int tempSlideX = 0; //X轴当前坐标，用于动态绘制图片显示坐标，实现滑动效果
    private int tempTotalSlideDistance;   //滑动距离，用于记录每次滑动的距离，在滑动结束后根据距离判断是否切换状态或者回滚
    private float touchX;   //记录上次触摸坐标，用于计算滑动距离
    private int touchSlop;//是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动控件
    private boolean btnState = true;//判断按钮是否在左边，屏蔽左侧点击滑动导致按钮状态失效

    private OnSliderListener listener;//滑动监听

    public SliderView(Context context) {
        this(context, null);
    }

    public SliderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SliderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SliderView);

        btnWidth = (int) mTypedArray.getDimension(R.styleable.SliderView_mBtnWidth, 0);
        btnHeight = (int) mTypedArray.getDimension(R.styleable.SliderView_mBtnHeight, 100);

        mTextSize = mTypedArray.getDimensionPixelSize(R.styleable.SliderView_mTextSize, 30); //字体大小
        mTextColor = mTypedArray.getColor(R.styleable.SliderView_mTextColor, Color.WHITE);

        drawableRs = mTypedArray.getResourceId(R.styleable.SliderView_maskDrawable,R.mipmap.delete_text_icon);

        paddingLeft = (int) mTypedArray.getDimension(R.styleable.SliderView_mPaddingLeft, 0);
        paddingTop = (int) mTypedArray.getDimension(R.styleable.SliderView_mPaddingTop, 0);
        defaultTipText = mTypedArray.getText(R.styleable.SliderView_defaultTipText).toString();
        toggleConductText = mTypedArray.getText(R.styleable.SliderView_toggleConductText).toString();
        toggleCompleteText = mTypedArray.getText(R.styleable.SliderView_toggleCompleteText).toString();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (btnWidth < 1) {
            btnWidth = getResources().getDisplayMetrics().widthPixels - 2 * paddingLeft ;
        }
        init();
    }

    private void init() {
        mPaint = new Paint();
        bgPaint = new Paint();
        bluePaint = new Paint();
        textPaint = new Paint();
        mPaint.setAntiAlias(true);
        bgPaint.setAntiAlias(true);
        bluePaint.setAntiAlias(true);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(mTextSize);
        textPaint.setColor(mTextColor);
        matrix = new Matrix();
        isShowSuccessText = false;
        threshold = btnWidth / 6 * 4;
        //按钮
        maskBitmap = drawableToBitmap(getResources().getDrawable(drawableRs), btnHeight, btnHeight);
        //触发移动事件的最小距离，自定义View处理touch事件的时候，有的时候需要判断用户是否真的存在movie，系统提供了这样的方法。表示滑动的时候，手的移动要大于这个返回的距离值才开始移动控件。
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        //RectF(float left, float top, float right, float bottom)  按扭的宽度=按钮的高度，按键是正方形的
        maskButtonRectF = new RectF(paddingLeft, paddingTop, paddingLeft + btnHeight, paddingTop + btnHeight);
        bgRectF = new RectF(paddingLeft, paddingTop, paddingLeft + btnWidth, paddingTop + btnHeight);
         /*
             * LinearGradient shader = new LinearGradient(0, 0, endX, endY, new
             * int[]{startColor, midleColor, endColor},new float[]{0 , 0.5f,
             * 1.0f}, TileMode.MIRROR);
             * 参数一为渐变起初点坐标x位置，参数二为y轴位置，参数三和四分辨对应渐变终点
             * 其中参数new int[]{startColor, midleColor,endColor}是参与渐变效果的颜色集合，
             * 其中参数new float[]{0 , 0.5f, 1.0f}是定义每个颜色处于的渐变相对位置， 这个参数可以为null，如果为null表示所有的颜色按顺序均匀的分布
             *
            // REPEAT:沿着渐变方向循环重复
            // CLAMP:如果在预先定义的范围外画的话，就重复边界的颜色
            // MIRROR:与REPEAT一样都是循环重复，但这个会对称重复
             */
        //灰色渐变背景
        Shader gtaybgShader = new LinearGradient(bgRectF.left, bgRectF.top, bgRectF.right, bgRectF.bottom,
                new int[]{Color.parseColor("#D3D3D3"), Color.parseColor("#7B7B7B")},
                new float[]{0.1f, 0.9f}, Shader.TileMode.REPEAT);
        //蓝色渐变背景
        bgPaint.setShader(gtaybgShader);// 用Shader中定义定义的颜色来话
        Shader mblShader = new LinearGradient(bgRectF.left, bgRectF.top, bgRectF.right, bgRectF.bottom,
                new int[]{Color.parseColor("#67A4FF"), Color.parseColor("#0061F2")},
                new float[]{0.1f, 0.9f}, Shader.TileMode.REPEAT);
        //背景
        bluePaint.setShader(mblShader);// 用Shader中定义定义的颜色来话
        switchScroller = new SwitchScroller(getContext(), new AccelerateDecelerateInterpolator());


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float moveLeft = maskButtonRectF.left + tempSlideX;
        canvas.drawRoundRect(bgRectF, btnHeight / 2, btnHeight / 2, bgPaint);

        RectF buleBgRectF = new RectF(bgRectF.left, bgRectF.top, moveLeft + btnHeight, bgRectF.top + btnHeight);
        canvas.drawRoundRect(buleBgRectF, btnHeight / 2, btnHeight / 2, bluePaint);

        if (isShowSuccessText) {
            canvas.drawText(toggleCompleteText, bgRectF.left + btnWidth / 2 - textPaint.measureText(toggleCompleteText) / 2, bgRectF.top + btnHeight / 2 + textPaint.measureText("A") / 2, textPaint);
        } else {
            if (tempSlideX < btnHeight / 2) {
                canvas.drawText(defaultTipText, bgRectF.left + btnWidth / 2 - textPaint.measureText(defaultTipText) / 2, bgRectF.top + btnHeight / 2 + textPaint.measureText("A") / 2, textPaint);
            } else if (tempSlideX > threshold) {
                canvas.drawText(toggleConductText, maskButtonRectF.left + tempSlideX / 2 - textPaint.measureText(toggleConductText) / 2 + btnHeight / 2, bgRectF.top + btnHeight / 2 + textPaint.measureText("A") / 2, textPaint);
            }
            matrix.setRotate(mRotate, btnHeight / 2, btnHeight / 2);
            matrix.postTranslate(moveLeft, maskButtonRectF.top);
            canvas.drawBitmap(maskBitmap, matrix, mPaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            eventType = event.getActionMasked();
        } else {
            eventType = event.getAction() & MotionEvent.ACTION_MASK;
        }
        switch (eventType) {
            case MotionEvent.ACTION_DOWN: {
                // 如果按钮当前可用并且按下位置正好在按钮之内
                if (isEnabled() && maskButtonRectF.contains(event.getX(), event.getY()) && btnState) {
                    touchMode = TOUCH_MODE_DOWN;
                    tempTotalSlideDistance = 0; // 清空总滑动距离
                    touchX = event.getX();  // 记录X轴坐标
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                switch (touchMode) {
                    case TOUCH_MODE_IDLE: {
                        break;
                    }
                    case TOUCH_MODE_DOWN: {
                        final float x = event.getX();
                        if (Math.abs(x - touchX) > touchSlop) {
                            touchMode = TOUCH_MODE_DRAGGING;
                            // 禁值父View拦截触摸事件
                            // 如果不加这段代码的话，当被ScrollView包括的时候，你会发现，当你在此按钮上按下，
                            // 紧接着滑动的时候ScrollView会跟着滑动，然后按钮的事件就丢失了，这会造成很难完成滑动操作
                            // 这样一来用户会抓狂的，加上这句话呢ScrollView就不会滚动了
                            if (getParent() != null) {
                                getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            touchX = x;
                            return true;
                        }
                        break;
                    }
                    case TOUCH_MODE_DRAGGING: {
                        float newTouchX = event.getX();
                        tempTotalSlideDistance += setSlideX(tempSlideX + ((int) (newTouchX - touchX)));    // 更新X轴坐标并记录总滑动距离
                        touchX = newTouchX;
                        invalidate();
                        return true;
                    }
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                //结尾滑动操作
                if (touchMode == TOUCH_MODE_DRAGGING) {// 这是滑动操作
                    touchMode = TOUCH_MODE_IDLE;
                    // 如果滑动距离大于等于最小切换距离就切换状态，否则回滚
                    if (Math.abs(tempTotalSlideDistance) >= threshold) {
                        toggleComplete();   //切换状态
                    } else {
                        startScroll();
                    }
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE: {
                if (touchMode == TOUCH_MODE_DRAGGING) {
                    touchMode = TOUCH_MODE_IDLE;
                    startScroll(); //回滚
                } else {
                    touchMode = TOUCH_MODE_IDLE;
                }
                break;
            }
        }

        super.onTouchEvent(event);
        return isEnabled();
    }

    /**
     * 切换状态
     */
    private void toggleComplete() {
        switchScroller.startScroll(true);
        btnState = false;
        if (listener != null) {
            listener.onSliderSuccess();
        }
        rotateAnim = ValueAnimator.ofFloat(0, 360);
        rotateAnim.setDuration(1000);
        //无限循环
        rotateAnim.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnim.setInterpolator(new LinearInterpolator());
        //从头开始动画
        rotateAnim.setRepeatMode(ValueAnimator.RESTART);
        rotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRotate = (int) ((float) animation.getAnimatedValue() % 360);
                postInvalidate();
            }
        });
        rotateAnim.start();
    }

    /**
     * 回滚
     */
    private void startScroll() {
        switchScroller.startScroll(false);
        btnState = true;
    }

    /**
     * 切换成功
     */
    public void setSliderSuccess() {
        isShowSuccessText = true;
        switchScroller.startScroll(true);
        btnState = false;
        postInvalidate();
    }

    /**
     * 切换失败
     * 恢复默认
     */
    public void setSliderFailer() {
        mRotate = 0;
        startScroll();
        if (rotateAnim != null) {
            rotateAnim.cancel();
        }
        btnState = true;
        isShowSuccessText = false;
        postInvalidate();
    }

    /**
     * 设置滑动成功的监听
     *
     * @param listener
     */
    public void setOnSliderListener(OnSliderListener listener) {
        this.listener = listener;
    }

    /**
     * 设置X轴坐标
     *
     * @param newSlideX 新的X轴坐标
     * @return Xz轴坐标增加的值，例如newSlideX等于100，旧的X轴坐标为49，那么返回值就是51
     */
    private int setSlideX(int newSlideX) {
        //防止滑动超出范围
        if (newSlideX <= 0) {
            newSlideX = 0;
        }
        if (newSlideX >= btnWidth - btnHeight) {
            newSlideX = btnWidth - btnHeight;
        }
        //计算本次距离增量
        int addDistance = newSlideX - tempSlideX;
        this.tempSlideX = newSlideX;
        return addDistance;
    }

    /**
     * 切换滚动器，用于实现滚动动画
     */
    private class SwitchScroller implements Runnable {
        private Scroller scroller;
        private boolean off;

        public SwitchScroller(Context context, android.view.animation.Interpolator interpolator) {
            this.scroller = new Scroller(context, interpolator);
        }

        /**
         * 开始滚动
         *
         * @param off 开关状态
         */
        public void startScroll(boolean off) {
            this.off = off;
            if (off) {//右边激活状态
                scroller.startScroll(tempSlideX, 0, btnWidth - paddingLeft - btnHeight / 2, 0, duration);
            } else {//左边默认状态
                scroller.startScroll(0, 0, tempSlideX, 0, duration);
            }
            post(this);
        }

        @Override
        public void run() {
            if (scroller.computeScrollOffset()) {
                if (off) {
                    setSlideX(tempSlideX + scroller.getCurrX());
                } else {
                    setSlideX(tempSlideX - scroller.getCurrX());
                }
                invalidate();
                post(this);
            }
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 滑动成功监听
     */
    public interface OnSliderListener {
        void onSliderSuccess();
    }
}
