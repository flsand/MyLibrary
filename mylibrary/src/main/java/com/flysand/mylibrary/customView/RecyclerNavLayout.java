package com.flysand.mylibrary.customView;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import com.flysand.mylibrary.util.Utils;

/**
 * 镶嵌多个view，一个RecycleView解决滑动冲突
 */

public class RecyclerNavLayout extends LinearLayout {

    private RecyclerView mRecyclerView;
    private OnScrollListener listener;

    private int mTopViewHeight;
    private boolean isTopHidden = false;

    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMaximumVelocity, mMinimumVelocity;

    private float mLastY;
    private boolean mDragging;

    private boolean isInControl = false;

    public RecyclerNavLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);

        mScroller = new OverScroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i <  getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof RecyclerView){
                mRecyclerView = (RecyclerView) v;
            }
        }
        if (mRecyclerView == null){
            throw new RuntimeException(" show used by RecyclerView !");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
//        params.height = getMeasuredHeight() - mNav.getMeasuredHeight();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        for (int i = 0; i <  getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof RecyclerView){
                break;
            }else {
                mTopViewHeight += v.getMeasuredHeight();
            }
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Utils.print("dispatchTouchEvent");
        int action = ev.getAction();
        float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;

                RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                //获取第一个可见view的位置
                int firstItemPosition = 0;
                if (layoutManager instanceof GridLayoutManager){
                    firstItemPosition =  ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                }else if (layoutManager instanceof LinearLayoutManager) {
                    firstItemPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                }
                RecyclerView rv = mRecyclerView;
                View v = rv.getChildAt(firstItemPosition);

                if (!isInControl && v != null && v.getTop() == 0 && isTopHidden && dy > 0) {
                    isInControl = true;
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    MotionEvent ev2 = MotionEvent.obtain(ev);
                    dispatchTouchEvent(ev);
                    ev2.setAction(MotionEvent.ACTION_DOWN);
                    return dispatchTouchEvent(ev2);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     *
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Utils.print("onInterceptTouchEvent");
        final int action = ev.getAction();
        float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                if (Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
                    RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                    //获取第一个可见view的位置
                    int firstItemPosition = 0;
                    if (layoutManager instanceof GridLayoutManager){
                        firstItemPosition =  ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    }else if (layoutManager instanceof LinearLayoutManager) {
                        firstItemPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    }
                    View c = mRecyclerView.getChildAt(firstItemPosition);
                    // 如果topView没有隐藏
                    // 或sc的GridView在顶部 && topView隐藏 && 下拉，则拦截
                    if (!isTopHidden || //
                            (c != null //
                                    && c.getTop() == 0//
                                    && isTopHidden && dy > 0)) {
                        initVelocityTrackerIfNotExists();
                        mVelocityTracker.addMovement(ev);
                        mLastY = y;
                        return true;
                    }
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mDragging = false;
                recycleVelocityTracker();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(event);
        int action = event.getAction();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished())
                    mScroller.abortAnimation();
                mLastY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;

                if (!mDragging && Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
                }
                if (mDragging) {
                    scrollBy(0, (int) -dy);

                    // 如果topView隐藏，且上滑动时，则改变当前事件为ACTION_DOWN
                    if (getScrollY() == mTopViewHeight && dy < 0) {
                        event.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(event);
                        isInControl = false;
                    }
                }

                mLastY = y;
                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                recycleVelocityTracker();
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                mDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    fling(-velocityY);
                }
                recycleVelocityTracker();
                break;
        }
        return super.onTouchEvent(event);
    }

    public void fling(int velocityY) {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
        invalidate();
    }


    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > mTopViewHeight) {
            y = mTopViewHeight;
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }
        isTopHidden = getScrollY() == mTopViewHeight;
        if (listener != null){
            listener.scroll(x,y);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public void setOnOnScrollListener(OnScrollListener listener) {
        this.listener = listener;
    }

    public interface OnScrollListener {
        void scroll(int x, int y);
    }
}
