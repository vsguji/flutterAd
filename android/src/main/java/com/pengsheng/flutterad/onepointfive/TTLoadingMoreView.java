package com.pengsheng.flutterad.onepointfive;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.pengsheng.flutterad.utils.UIUtils;


/**
 * 左滑时，直线根据滑动距离改变为箭头
 */
public class TTLoadingMoreView extends View {
    private static final String TAG = TTLoadingMoreView.class.getSimpleName();
    private final int INVAILID = -1;
    private Paint mPaint;
    private Path mPath;
    private int mWidth = INVAILID;
    private int mHeight = INVAILID;
    private int mMaxWidth = INVAILID;
    private int mMaxWidthPercent = 1;
    private float rate = 0f;
    private int screenWidth;
    private float maxRate = 0.8f;
    private float margin = 0;

    public TTLoadingMoreView(Context context) {
        this(context, null);
    }

    public TTLoadingMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TTLoadingMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(0xFFCACACA);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPath = new Path();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        margin = UIUtils.dp2px(context, 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mMaxWidth = mWidth >> mMaxWidthPercent;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        //刚开始的时候是100
        if (rate != 0) {
            // 移动到view中心点
            mPath.moveTo(mWidth >> 1, margin);
            float width = (mWidth >> 1) - mMaxWidth * rate;
            if (width < 0) {
                width = 0;
            }
            mPath.lineTo(width, mHeight >> 1);
            // 下半部分回归到中心点
            mPath.lineTo(mWidth >> 1, mHeight - margin);
            // 划线
            canvas.drawPath(mPath, mPaint);
        } else {
            // 如果滑动距离为0，这里则为直线。。
            mPath.moveTo(mWidth * 0.5f, margin);
            mPath.lineTo(mWidth * 0.5f, mHeight - margin);
            canvas.drawPath(mPath, mPaint);
        }
        super.onDraw(canvas);
    }

    /**
     * @param space px 根据这个值去计算箭头的绘制角度。触发view刷新 ,滑动距离 / screenwidth
     */
    public void setMoveSpace(float space) {
        rate = Math.abs(space) * 2f / screenWidth;
        if (rate >= maxRate) {
            rate = maxRate;
        }
        invalidate();
    }

    /**
     * 重置view状态，状态为直线了
     */
    public void reset() {
        rate = 0;
        invalidate();
    }
}

