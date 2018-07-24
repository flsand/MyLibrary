package com.flysand.mylibrary.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.flysand.mylibrary.R;

import java.util.ArrayList;


/**
 * 多节点进度条自定义视图
 *    <com.flysand.mylibrary.customView.MutiProgress
 android:id="@+id/release_top_MutiProgress"
 android:layout_width="match_parent"
 android:layout_height="35dp"
 android:layout_marginLeft="16dp"
 android:layout_marginRight="16dp"
 app:progressTextSize="12sp"
 android:layout_centerInParent="true"
 app:nodeRadius="5dp"
 app:textMarginTop="5dp"
 app:completenodeColor="@color/viewColorWhite"
 app:unfinishedeColor="#FFC0A6"
 app:completeLineColor="@color/viewColorWhite"
 app:unfinishedLineColor="#FFB990"
 />
 */
public class MutiProgress extends View {

    private int nodeRadius = 10;  //节点的半径
    private int nodesNum;//节点数量
    private int currProcessing = 0;//当前进度

    private String[] promptTexts = {"等待接单", "预付租金","验收车辆"};

    private int mWidth;
    private Paint paint;
    private int completenodeColor;//已完成节点颜色
    private int unfinishedeColor;//未完成节点颜色
    private int completeLineColor;//已完成节点线颜色
    private int unfinishedLineColor;//未完成节点线颜色
    private int progressTextSize;//字体大小
    private int textMarginTop;

    private int paddingLR = 0;//左右间距
    private int paddingTop;

    //节点集合
    private ArrayList<MyPoint> nodes;
    private RectF mRectF = new RectF();

    public MutiProgress(Context context) {
        this(context, null);
    }

    public MutiProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MutiProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.MutiProgress);
        nodeRadius = mTypedArray.getDimensionPixelSize(R.styleable.MutiProgress_nodeRadius, 20); //节点半径
        progressTextSize = mTypedArray.getDimensionPixelSize(R.styleable.MutiProgress_progressTextSize, 30); //字体大小
        textMarginTop = mTypedArray.getDimensionPixelSize(R.styleable.MutiProgress_textMarginTop, 5); //字体上间隔
        completenodeColor = mTypedArray.getColor(R.styleable.MutiProgress_completenodeColor, Color.BLUE); //完成节点颜色
        unfinishedeColor = mTypedArray.getColor(R.styleable.MutiProgress_unfinishedeColor, Color.GRAY); //未完成节点颜色
        completeLineColor = mTypedArray.getColor(R.styleable.MutiProgress_completeLineColor, Color.BLACK); //完成节点线颜色
        unfinishedLineColor = mTypedArray.getColor(R.styleable.MutiProgress_unfinishedLineColor, Color.DKGRAY); //未完成节点线颜色
        paddingTop = (int) (mTypedArray.getDimension(R.styleable.MutiProgress_completepaddingTop, 0) + 10);
    }

    public void setPromptTexts(String... strings) {
        promptTexts = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            promptTexts[i] = strings[i];
        }
        initData();
    }

    private int getLength() {
        return Math.max(promptTexts[0].length(), promptTexts[promptTexts.length - 1].length());
    }

    public void setProcessing(int processing) {
        currProcessing = Math.max(Math.min(processing, promptTexts.length - 1), 0);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        initData();
    }

    private void initData() {
        nodesNum = promptTexts.length; //节点数量

        if (paint == null) {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setTextSize(progressTextSize);
        }

        paddingLR = (int) (getLength() * Math.abs(paint.getFontMetrics().ascent));

        nodes = new ArrayList<>();
        float currentUseWidth = mWidth - paddingLR - 2 * nodeRadius;
        float nodeWidth = (currentUseWidth) / (nodesNum - 1);
        for (int i = 0; i < nodesNum; i++) {
            MyPoint node = null;
            if (i == 0) {
                node = new MyPoint(paddingLR / 2, paddingTop);
            } else if (i == (nodesNum - 1)) {
                node = new MyPoint((currentUseWidth + paddingLR / 2), paddingTop);
            } else {
                node = new MyPoint(((nodeWidth * i) + paddingLR / 2), paddingTop);
            }
            nodes.add(node);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#00000000"));
        drawProgerss(canvas);
        for (int i = 0; i < nodes.size(); i++) {
            MyPoint node = nodes.get(i);
            mRectF.set(node.x, node.y, node.x + nodeRadius * 2, node.y + nodeRadius * 2);
            if (i < currProcessing) {  //已完成的进度节点
                paint.setColor(completenodeColor);
                canvas.drawCircle(node.x + nodeRadius, node.y + nodeRadius, nodeRadius, paint);
            } else if (i == currProcessing) {  //当前所到达的进度节点
                paint.setColor(completenodeColor);
                canvas.drawCircle(node.x + nodeRadius, node.y + nodeRadius, nodeRadius, paint);
                paint.setColor(Color.parseColor("#ffffff"));
                canvas.drawCircle(node.x + nodeRadius, node.y + nodeRadius, nodeRadius * 3 / 5, paint);
                paint.setColor(completenodeColor);
            } else {   //未完成的进度节点  #666666
                paint.setColor(unfinishedeColor);
                canvas.drawCircle(node.x + nodeRadius, node.y + nodeRadius, nodeRadius, paint);
            }
            paint.setStrokeWidth(0);

            canvas.drawText(promptTexts[i], node.x - paint.measureText(promptTexts[i]) / 2 + nodeRadius,
                    node.y + nodeRadius * 2 + Math.abs(paint.getFontMetrics().ascent) + textMarginTop, paint);
        }
    }

    private void drawProgerss(Canvas mCanvas) {
        //先画线段，线段的高度为nodeRadius/2
        paint.setStrokeWidth(6);
        paint.setColor(completeLineColor);
        //前半截线段   drawLine(float startX, float startY, float stopX, float stopY,
        if (currProcessing != 0) {
            mCanvas.drawLine(paddingLR / 2 + nodeRadius * 2, nodes.get(currProcessing).y + nodeRadius,
                    nodes.get(currProcessing).x, nodes.get(currProcessing).y + nodeRadius, paint);  //线段2端去掉nodeRadius
        }
        //后半截线段
        paint.setColor(unfinishedLineColor);
        if (currProcessing < nodesNum - 1) {
            mCanvas.drawLine(nodes.get(currProcessing).x + nodeRadius * 2, nodes.get(currProcessing).y + nodeRadius,
                    nodes.get(nodesNum - 1).x, nodes.get(currProcessing).y + nodeRadius, paint);  //线段2端去掉nodeRadius
        }
    }

    private class MyPoint {
        public float x;
        public float y;

        public MyPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}