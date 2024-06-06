package com.pengsheng.flutterad.onepointfive;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.pengsheng.flutterad.R;
import com.pengsheng.flutterad.utils.UIUtils;

/**
 * Created by linqiming on 6/19/22
 * Usage: 1.5卡横向加载更多布局
 * Doc:
 */
public class TTPullToRefreshHorizontalRecyclerView extends FrameLayout implements Handler.Callback{
    private static final String TAG = TTPullToRefreshHorizontalRecyclerView.class.getSimpleName();
    private static final int MSG_RESET_STATUS = 1;
    private RecyclerView recyclerView;
    private float lastDownX;
    private float lastDownY;
    private RecyclerView.LayoutManager layoutManager;
    private boolean canScroll;
    private boolean canForward;
    private OnPullToBottomListener onPullToBottomListener;
    private static final float LIMIT = 64;
    private float mLimitWidth = 0;
    private boolean isAnimated;
    private Handler mUiHandler;
    private boolean mIsLoadMore = false;
    private MotionEvent lastEvent;
    private boolean mInterceptFlag;
    private View listRoot;
    private static long sLastTouchUpTime = 0;

    public TTPullToRefreshHorizontalRecyclerView(Context context) {
        this(context, null);
    }

    public TTPullToRefreshHorizontalRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TTPullToRefreshHorizontalRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.listitem_pulltorefresh_horizontal_recyclerview, this);
        listRoot = findViewById(R.id.list_root);
        recyclerView = listRoot.findViewById(R.id.content_list);
        mUiHandler = new Handler(Looper.getMainLooper(), this);
        mLimitWidth = UIUtils.dp2px(getContext(), LIMIT);
    }


    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        recyclerView.setLayoutManager(layout);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastDownX = (int) ev.getRawX();
                lastDownY = (int) ev.getRawY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                lastEvent = ev;
                // dx > 0, 内容向右滚动，从左边出；dx < 0，内容向左滚动，从右边出
                int dX = (int) (ev.getRawX() - lastDownX);
                int dY = (int) (ev.getRawY() - lastDownY);
                if (Math.abs(dX) < Math.abs(dY) && !canScroll) {
                    mInterceptFlag = false;
                    //上下滑动
                    getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mInterceptFlag = false;
                break;
            default:
        }
        boolean result = false;
        try {
            result = super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "dispatchTouchEvent error ", e);
        }
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (recyclerView == null || recyclerView.getChildCount() == 0) {
            return super.onInterceptTouchEvent(ev);
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastDownX = ev.getRawX();
                lastDownY = ev.getRawY();
                if (mInterceptFlag) {
                    canScroll = true;
                    mInterceptFlag = false;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (ev.getRawX() - lastDownX);
                int dy = (int) (ev.getRawY() - lastDownY);
                if (Math.abs(dx) < Math.abs(dy)) {
                    resetView();
                    break;
                }
                int lastPos = -1;
                layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    lastPos = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
                }
                try {
                    if (dx < 0) { // 最后一个item可见时，再往左滑
                        if (lastPos == recyclerView.getAdapter().getItemCount() - 1 && !mIsLoadMore) {
                            canScroll = true;
                            return true;
                        } else {
                            canScroll = false;
                        }
                    } else {
                        canScroll = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
        }
        boolean result = false;
        try {
            result = super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            Log.e(TAG, "onInterceptTouchEvent error ", e);
        }
        return result;
    }

    private void resetView() {
        if (canForward) {
            forward();
            canForward = false;
        } else {
            scrollViewToOrigin();
        }
    }

    private void scrollViewToOrigin() {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(recyclerView.getAdapter().getItemCount() - 1);
        if (viewHolder instanceof TTEmptyFootViewHolder) {
            ((TTEmptyFootViewHolder)viewHolder).scrollViewToOrigin(listRoot);
        }
    }

    private void resetViewImme() {
        try {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(recyclerView.getAdapter().getItemCount() - 1);
            if (viewHolder instanceof TTEmptyFootViewHolder) {
                ((TTEmptyFootViewHolder)viewHolder).resetViewImme(listRoot);
            }
        } catch (Throwable e) {
            Log.e(TAG, "resetView error ", e);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (recyclerView == null || recyclerView.getChildCount() == 0) {
            return super.onTouchEvent(ev);
        }
        int offerX;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastDownX = ev.getRawX();
                lastDownY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (canScroll) {
                    offerX = (int) (ev.getRawX() - lastDownX);
                    if (offerX >= 0) {
                        resetViewImme();
                        break;
                    }
                    try {
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(recyclerView.getAdapter().getItemCount() - 1);
                        if (viewHolder instanceof TTEmptyFootViewHolder) {
                            ((TTEmptyFootViewHolder)viewHolder).actionMove(offerX, listRoot);
                            if(Math.abs(offerX) > mLimitWidth) {
                                ((TTEmptyFootViewHolder)viewHolder).setLeftSlipTip();
                                canForward = true;
                                if (!isAnimated) {
                                    isAnimated = true;
                                    ((TTEmptyFootViewHolder)viewHolder).animateTips();
                                }
                            } else {
                                isAnimated = false;
                                ((TTEmptyFootViewHolder)viewHolder).setNormalTip();
                                canForward = false;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (canScroll) {
                    resetView();
                    canScroll = false;
                }
                break;
            default:
        }
        return true;
    }

    private void forward() { // 拉动到一定距离
        if (isDoubleTap(1000)) {
            mUiHandler.removeMessages(MSG_RESET_STATUS);
            mUiHandler.sendEmptyMessageDelayed(MSG_RESET_STATUS, 280);
            return;
        }
        //还原footview动画
        scrollViewToOrigin();
        //触发加载更多回调
        if (onPullToBottomListener != null) {
            this.onPullToBottomListener.onPullToBottom();
        }
    }

    private static boolean isDoubleTap(long intervalTime) {
        // Detec double tap
        boolean doubleTap = false;
        if ((SystemClock.uptimeMillis() - sLastTouchUpTime) <= intervalTime) {
            doubleTap = true;
        }
        sLastTouchUpTime = SystemClock.uptimeMillis();
        return doubleTap;
    }

    public void setOnPullToBottomListener(OnPullToBottomListener onPullToBottomListener) {
        this.onPullToBottomListener = onPullToBottomListener;
    }

    public OnPullToBottomListener getOnPullToBottomListener() {
        return onPullToBottomListener;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_RESET_STATUS:
                resetViewImme();
                break;
            default:
        }
        return true;
    }

    public void setIsLoadingMore(boolean isLoadingMore) {
        this.mIsLoadMore = isLoadingMore;
    }

    public interface OnPullToBottomListener {
        void onPullToBottom();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus){
            mUiHandler.removeMessages(MSG_RESET_STATUS);
            mUiHandler.sendEmptyMessageDelayed(MSG_RESET_STATUS, 280);
        }
    }

}
