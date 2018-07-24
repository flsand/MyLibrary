package com.flysand.mylibrary.customView;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 镶嵌多个view，一个RecycleView解决滑动冲突
 */

public class MoreChildScrollView extends RecyclerNavLayout {

    public MoreChildScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
//        params.height = getMeasuredHeight() - mNav.getMeasuredHeight();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    }
}
