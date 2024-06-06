package com.pengsheng.flutterad.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Luojun on 2021/6/24
 * Usage:
 * Doc
 */
public class CountdownView extends View {
    public static final String DEFUALT_TEXT_CONTEXT = "跳过";
    /**
     * 圆弧颜色
     */
    private int arcColor = Color.parseColor("#fce8b6");

    /**
     * 空心内圆颜色
     */
    private int innerStrokeCirclColor = Color.parseColor("#f0f0f0");


    /**
     * 实体内圆颜色
     */
    private int innerFillCircleColor = Color.parseColor("#ffffff");

    /**
     * 字体颜色
     */
    private int textColor = Color.parseColor("#7c7c7c");
    /**
     * 圆弧厚度 px
     */
    private float arcStrokeWidth = 2;
    /**
     * 字体大小 px
     */
    private float textSize = 12;
    /**
     * 圆弧半径
     */
    private float radius = 18;
    /**
     * 倒计时开始角度
     */
    private int initDegree = 270;
    /**
     * 是否顺时针倒计时
     */
    private boolean isCW = false;
    /**
     * 倒计时最大数值（时长）
     */
    private float countDownTime = 5;
    /**
     * 用于文字
     */
    private float maxNumForText = 5;


    /**
     * 圈内文字内容
     */
    private String textContext = DEFUALT_TEXT_CONTEXT;

    /**
     * 圈内内容是使用数字还是固定文字内容
     */
    private boolean isUseNumContext = false;


    //绘图相关

    private Paint arcPaint;
    private Paint innerArcPaint;
    private Paint innerStrokeArcPaint;
    private Paint numPaint;
    private float arcFraction = 1.0f;
    private float numFraction = 1.0f;
    private RectF arcRectF;
    private CountdownListener countdownListener;

    private AnimatorSet countdownAnim;
    private ValueAnimator numCountdownAnim;
    private ValueAnimator arcCountdownAnim;
    private ValueAnimator plusArcAnim;
    private boolean canceledByOut = false;
    private AtomicBoolean mIsHasWindowFocus = new AtomicBoolean(true);

    public CountdownView(Context context) {
        this(context, null);
    }

    public CountdownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        arcStrokeWidth = dp2px(2);
        radius = dp2px(18);
        textSize = sp2px(12);
        initDegree = initDegree % 360;
        initPaint();
        initData();
        initListener(context);
    }

    /**
     * 如果是异形屏，下移按钮的位置，防止被挖孔遮挡
     *
     * @param context
     */
    private void initListener(final Context context) {
        addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                int safeInsetTop = 0;
                int statusBarHeight = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    DisplayCutout displayCutout = null;
                    final WindowInsets windowInsets = v.getRootWindowInsets();
                    if (windowInsets != null) {
                        displayCutout = windowInsets.getDisplayCutout();
                    }
                    if (displayCutout != null) {
                        safeInsetTop = displayCutout.getSafeInsetTop();
                    }
                }
                final int resourceId = context.getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    statusBarHeight = context.getApplicationContext().getResources().getDimensionPixelSize(resourceId);
                }
                final float marginTop = Math.max(statusBarHeight, safeInsetTop);
                final ViewGroup.LayoutParams lp = v.getLayoutParams();
                if (lp instanceof FrameLayout.LayoutParams) {
                    ((FrameLayout.LayoutParams) lp).topMargin += marginTop;
                }
                if (lp instanceof RelativeLayout.LayoutParams) {
                    ((RelativeLayout.LayoutParams) lp).topMargin += marginTop;
                }
                if (lp instanceof LinearLayout.LayoutParams) {
                    ((LinearLayout.LayoutParams) lp).topMargin += marginTop;
                }
                v.setLayoutParams(lp);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });
    }

    private void initPaint() {
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setColor(arcColor);
        arcPaint.setStrokeWidth(arcStrokeWidth);
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.STROKE);
        innerArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerArcPaint.setColor(innerFillCircleColor);
        innerArcPaint.setAntiAlias(true);
        innerArcPaint.setStrokeWidth(arcStrokeWidth);
        innerArcPaint.setStyle(Paint.Style.FILL);
        innerStrokeArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerStrokeArcPaint.setColor(innerStrokeCirclColor);
        innerStrokeArcPaint.setAntiAlias(true);
        innerStrokeArcPaint.setStrokeWidth(arcStrokeWidth / 2);
        innerStrokeArcPaint.setStyle(Paint.Style.STROKE);
        numPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numPaint.setColor(textColor);
        innerStrokeArcPaint.setAntiAlias(true);
        numPaint.setTextSize(textSize);
        numPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void initData() {
        arcRectF = new RectF(-radius, -radius, radius, radius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wSize = View.MeasureSpec.getSize(widthMeasureSpec);
        final int wMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int hSize = View.MeasureSpec.getSize(heightMeasureSpec);
        final int hMode = View.MeasureSpec.getMode(heightMeasureSpec);
        //WRAP_CONTENT
        if (wMode != View.MeasureSpec.EXACTLY) {
            wSize = calculateMinWidth();
        }
        if (hMode != View.MeasureSpec.EXACTLY) {
            hSize = calculateMinWidth();
        }
        setMeasuredDimension(wSize, hSize);
    }

    /**
     * 计算控件最小边长
     *
     * @return
     */
    private int calculateMinWidth() {
        final float minWidth = (arcStrokeWidth / 2.0f + radius) * 2;
        return (int) (minWidth + dp2px(4));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f);
        drawArc(canvas);
        drawNum(canvas);
    }

    private void drawNum(Canvas canvas) {
        canvas.save();
        final  Paint.FontMetrics metrics = numPaint.getFontMetrics();
        String currentNum = DEFUALT_TEXT_CONTEXT;
        if (isUseNumContext) {
            currentNum = "" + (int) Math.ceil(getCurrentNumByFraction(numFraction,
                    maxNumForText));
        } else {
            currentNum = textContext;
        }
        if (TextUtils.isEmpty(currentNum)) {
            currentNum = DEFUALT_TEXT_CONTEXT;
        }
        canvas.drawText(currentNum
                , 0
                , 0 - (metrics.ascent + metrics.descent) / 2 //真正居中);
                , numPaint);
        canvas.restore();
    }

    private void drawArc(Canvas canvas) {
        canvas.save();
        final float currentSweepDegree = getCurrentSweepDegree(arcFraction, 360);
        final float startAngle, sweepAngle;
        if (isCW) {
            startAngle = initDegree - currentSweepDegree;
            sweepAngle = currentSweepDegree;
        } else {
            startAngle = initDegree;
            sweepAngle = currentSweepDegree;
        }
        canvas.drawCircle(0, 0, radius, innerArcPaint);
        canvas.drawCircle(0, 0, radius, innerStrokeArcPaint);
        canvas.drawArc(arcRectF
                , startAngle
                , sweepAngle
                , false
                , arcPaint);
        canvas.restore();
    }


    public void startCountDown() {
        if (countdownAnim != null && countdownAnim.isRunning()) {
            countdownAnim.cancel();
            countdownAnim = null;
        }
        countdownAnim = new AnimatorSet();
        countdownAnim.playTogether(getNumAnim(), getArcAnim());
        countdownAnim.setInterpolator(new LinearInterpolator());
        countdownAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationCancel(Animator animation) {
                canceledByOut = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (canceledByOut) {
                    canceledByOut = false;
                    return;
                }
                if (countdownListener != null) {
                    countdownListener.onEnd();
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {

            }
        });
        countdownAnim.start();
        //为了解决开屏立马点击开屏失去焦点但此时倒计时还没初始化完成导致无法暂停的问题
        if (!mIsHasWindowFocus.get()) {
            pauseCountdown();
        }
    }

    private ValueAnimator getNumAnim() {
        if (numCountdownAnim != null) {
            numCountdownAnim.cancel();
            numCountdownAnim = null;
        }
        numCountdownAnim = ValueAnimator.ofFloat(numFraction, 0.0f);
        numCountdownAnim.setInterpolator(new LinearInterpolator());
        numCountdownAnim.setDuration((long) (getCurrentNumByFraction(numFraction, maxNumForText) * 1000));
        numCountdownAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                numFraction = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        return numCountdownAnim;
    }

    private ValueAnimator getArcAnim() {
        if (arcCountdownAnim != null) {
            arcCountdownAnim.cancel();
            arcCountdownAnim = null;
        }
        arcCountdownAnim = ValueAnimator.ofFloat(arcFraction, 0.0f);
        arcCountdownAnim.setInterpolator(new LinearInterpolator());
        arcCountdownAnim.setDuration((long) (getCurrentNumByFraction(arcFraction, countDownTime) * 1000));
        arcCountdownAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                arcFraction = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        return arcCountdownAnim;
    }


    /**
     * 重置
     */
    public void reset() {
        try {
            if (countdownAnim != null) {
                countdownAnim.cancel();
                countdownAnim = null;
            }
            if (plusArcAnim != null) {
                plusArcAnim.cancel();
                plusArcAnim = null;
            }
            if (numCountdownAnim != null) {
                numCountdownAnim.cancel();
                numCountdownAnim = null;
            }
            if (arcCountdownAnim != null) {
                arcCountdownAnim.cancel();
                arcCountdownAnim = null;
            }
            arcFraction = 1.0f;
            numFraction = 1.0f;
            invalidate();
        } catch (Exception e) {
            //e
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        reset();
        super.onDetachedFromWindow();

    }


    /**
     * 根据当前倒计时进度比例和倒计时时长换算出当前倒计时值
     *
     * @param numFraction   当前倒计时进度比例
     * @param countDownTime 倒计时最大值（倒计时时长）
     * @return 当前倒计时值(s ）
     */
    public float getCurrentNumByFraction(float numFraction, float countDownTime) {
        return numFraction * countDownTime;
    }

    /**
     * 根据当前倒计时值和倒计时时长换算出进度比例
     *
     * @param currentNum    当前倒计时值(s)
     * @param countDownTime 倒计时最大值（倒计时时长）
     * @return 进度比例
     */
    public float getCurrentFractionByNum(float currentNum, float countDownTime) {
        return currentNum / countDownTime;
    }

    /**
     * 圆弧当前弧度计算
     *
     * @param arcFraction
     * @param maxDegree
     * @return
     */
    public float getCurrentSweepDegree(float arcFraction, int maxDegree) {
        return maxDegree * arcFraction;
    }


    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }


    public void setCountdownListener(CountdownListener countdownListener) {
        this.countdownListener = countdownListener;
        if (!mIsHasWindowFocus.get()) {
            if (countdownListener != null) {
                countdownListener.onPause();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        mIsHasWindowFocus.set(hasWindowFocus);
        if (!mIsHasWindowFocus.get()) {
            pauseCountdown();
            if (countdownListener != null) {
                countdownListener.onPause();
            }
        } else {
            resumeCountdown();
            if (countdownListener != null) {
                countdownListener.onStart();
            }
        }
    }

    public void pauseCountdown() {
        try {
            if (countdownAnim != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    countdownAnim.pause();
                }
            }
        } catch (Throwable throwable) {
            //throwable
        }
    }

    public void resumeCountdown() {
        try {
            if (countdownAnim != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    countdownAnim.resume();
                }
            }
        } catch (Throwable throwable) {
            //throwable
        }
    }

    /**
     * 监听接口
     */
    public interface CountdownListener {

        void onStart();

        void onEnd();

        void onPause();

    }
}
