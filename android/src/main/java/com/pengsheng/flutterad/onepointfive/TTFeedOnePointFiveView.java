package com.pengsheng.flutterad.onepointfive;


import android.content.Context;
import android.os.SystemClock;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.pengsheng.flutterad.R;
import com.pengsheng.flutterad.config.TTAdManagerHolder;
import com.pengsheng.flutterad.utils.UIUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linqiming on 6/23/22
 * Usage: 1.5卡 完整视图
 * Doc:
 */
public class TTFeedOnePointFiveView extends FrameLayout {

    private static final String TAG = "ExpressOnePointFiveView";

    private List<TTFeedAd> mFeedAdList; //实际广告内容

    private List<TTFeedAd> mPreloadAdList; //预加载广告内容
//    private TTAdSlot mNetRequestAdSlot; //用于模版渲染广告请求参数
//    private TTAdSlot mExpressAdSlot; //用于本地构建动态布局
    private TTPullToRefreshHorizontalRecyclerView mPullToRefresh;
    private TextView mTvAdTitle;
    private LinearLayoutManager mLayoutManager;
    private TTOnePointFiveAdapter mAdapter;
    private boolean isSaasScene; //电商场景： true-Saas, false-调抖/落地页

    private static long sLastTouchUpTime = 0;
    private final AtomicBoolean mNeedUpdate = new AtomicBoolean(false); //1.5广告数据更新控制
    private int mSingleAdAcceptedWidth = 0; //单广告的宽度，必须大于0
    private int mSingleAdAcceptedHeight = 0; //单广告的高度，0表示自适应

    private TTFeedAd.AdInteractionListener mAdInteractionListener;

    private TTFeedAd.VideoAdListener mVideoAdListener;
    private TTOnePointFiveRefreshListener mRefreshListener;
    private final AtomicBoolean mAdLoading = new AtomicBoolean(false); //广告请求控制

    private AdSlot mAdSlot;

    private TTAdNative mTTAdNative;


    public TTFeedOnePointFiveView(Context context, AdSlot adSlot) {
        super(context);
        mAdSlot = adSlot;
        initFullView();
    }


    public void setData(List<TTFeedAd> ads) {
        if (ads == null || ads.size() == 0) {
            return;
        }
        mFeedAdList.clear();
        mFeedAdList.addAll(ads);
        render();
    }

    private void initFullView() {
        LayoutInflater.from(getContext()).inflate(R.layout.listitem_feed_onepointfive_ad, this);
        mPullToRefresh = findViewById(R.id.ptr_horizontal_recyclerview);
        mTvAdTitle = findViewById(R.id.tv_ad_title);
        initLayoutParams();
        initRecyclerView();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAdInteractionListener != null) {

            mAdInteractionListener.onAdShow(mFeedAdList.get(0));
        }
    }
    
    private void initSaasScene() {
        try {

            TTFeedAd lastFeedAd = mFeedAdList.get(mFeedAdList.size() - 1);
            Map<String, Object> map = lastFeedAd.getMediaExtraInfo();
            isSaasScene = (Boolean) map.get("live_support_saas_live");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void render() {
        if (mAdapter == null || mTvAdTitle == null) {
            return;
        }
        initSaasScene();
        mAdapter.setSaasScene(isSaasScene);
        if (mFeedAdList != null && mFeedAdList.size() > 0) {
            mAdapter.notifyDataSetChanged();
            try {
                mPullToRefresh.getRecyclerView().scrollToPosition(0);

                TTFeedAd feedAd = mFeedAdList.get(0);
                if (feedAd != null && feedAd.getMediaExtraInfo() != null) {
                    Map<String, Object> map = feedAd.getMediaExtraInfo();
                    JSONObject groupInfoJson = (JSONObject) map.get("group_info");
                    mTvAdTitle.setText(groupInfoJson == null || !groupInfoJson.has("group_tag")
                            ? "" : groupInfoJson.optString("group_tag"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            mTvAdTitle.setText("");

        }
    }
    public void setRefreshListener(TTOnePointFiveRefreshListener listener) {
        mRefreshListener = listener;
    }

    private void initLayoutParams() {
        int screenWidth = UIUtils.getScreenWidthInPx(getContext());
        mSingleAdAcceptedWidth = screenWidth * 2 /3;
        mSingleAdAcceptedHeight = mSingleAdAcceptedWidth * 16 / 9;
        int pullToRefreshLayoutHeight;
        //上面的边距 + 下面Textview内容高度 = 52dp
        pullToRefreshLayoutHeight = mSingleAdAcceptedHeight + UIUtils.dp2px(getContext(), 52);
        LayoutParams pullToRefreshLayoutParams = new LayoutParams(screenWidth, pullToRefreshLayoutHeight);
        mPullToRefresh.setLayoutParams(pullToRefreshLayoutParams);
    }

    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPullToRefresh.setLayoutManager(mLayoutManager);
        //mCommerceType = getLastMetaCommerceType();
        mFeedAdList = new ArrayList<>();
        mAdapter = new TTOnePointFiveAdapter(getContext(), mFeedAdList, mSingleAdAcceptedWidth, mSingleAdAcceptedHeight);
        mPullToRefresh.getRecyclerView().setAdapter(mAdapter);
        //Item对齐到左边界位置
        TTGallerySnapHelper snapHelper = new TTGallerySnapHelper();
        snapHelper.attachToRecyclerView(mPullToRefresh.getRecyclerView());
        mPullToRefresh.setOnPullToBottomListener(new TTPullToRefreshHorizontalRecyclerView.OnPullToBottomListener() {
            @Override
            public void onPullToBottom() {
                if (isDoubleTap(500)) {
                    return;
                }
                if (isSaasScene) {
                    //闭环Saas场景 内跳最后一个广告的直播间
                    jumpToLastLiveRoom();
                } else {
                    //调抖场景，刷新1.5卡广告
                    mNeedUpdate.set(true);
                    loadNextOnePointFiveAd(false);
                }
            }
        });
        mAdapter.setOnPreloadListener(new TTOnPreloadListener() {
            @Override
            public void onPreload() {
                loadNextOnePointFiveAd(true);
            }
        });
    }

    private void jumpToLastLiveRoom() {
        try {
            //闭环Saas场景 左滑到底继续左滑，会自动跳转 最后一个闭环电商广告直播间
            View view = mLayoutManager.findViewByPosition(mFeedAdList.size() -1);
            view.performClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadNextOnePointFiveAd(boolean isPreload) {
        if (mPreloadAdList != null) {
            if (!isPreload) { //实际刷新操作下，更新内容
                updateNextOnePointFiveAd();
            }
            return;
        }
        if (mAdLoading.get()) {
            return;
        }
        mAdLoading.set(true);
        try {
            mAdSlot.setGroupLoadMore(1); //标识二请刷新操作
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (mTTAdNative == null) {

            TTAdManager ttAdManager = TTAdManagerHolder.get();

            mTTAdNative = ttAdManager.createAdNative(getContext().getApplicationContext());
        }

        mTTAdNative.loadFeedAd(mAdSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                if (mAdLoading.getAndSet(false)) {
                    //..
                }
            }

            @Override

            public void onFeedAdLoad(List<TTFeedAd> ads) {
                if (mAdLoading.getAndSet(false)) {
                    mPreloadAdList = ads;
                    updateNextOnePointFiveAd();
                }
            }
        });
    }

    private void updateNextOnePointFiveAd() {
        if (mNeedUpdate.getAndSet(false)) {
            mFeedAdList.clear();
            mFeedAdList.addAll(mPreloadAdList);
            mPreloadAdList = null;
            //mCommerceType = getLastMetaCommerceType();
            render();
            if (mRefreshListener != null) {
                mRefreshListener.onAdDataRefreshed();
            }
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


    public void setAdInteractionListener(TTFeedAd.AdInteractionListener listener) {
        mAdInteractionListener = listener;
        if (mAdapter != null) {
            mAdapter.setAdInteractionListener(listener);
        }
    }


    public void setVideoAdListener(TTFeedAd.VideoAdListener videoAdListener) {
        this.mVideoAdListener = videoAdListener;
        if (mAdapter != null) {
            mAdapter.setVideoAdListener(videoAdListener);
        }
    }

    public interface TTOnePointFiveRefreshListener {
        void onRequestDataRefresh();

        /**
         * 左滑刷新，更新数据后回调
         */
        void onAdDataRefreshed();
    }

}
