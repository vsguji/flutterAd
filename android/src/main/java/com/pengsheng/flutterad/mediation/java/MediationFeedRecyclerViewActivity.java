package com.pengsheng.flutterad.mediation.java;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.mediation.ad.MediationExpressRenderListener;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationNativeManager;
import com.pengsheng.flutterad.R;
import com.pengsheng.flutterad.config.TTAdManagerHolder;
import com.pengsheng.flutterad.mediation.java.utils.Const;
import com.pengsheng.flutterad.utils.UIUtils;
import com.pengsheng.flutterad.view.ILoadMoreListener;
import com.pengsheng.flutterad.view.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 融合demo，Feed流广告 RecyclerView 使用示例（模板和自渲染）。更多功能参考接入文档。
 *
 * 注意：每次加载的广告，只能展示一次
 *
 * 接入步骤：
 * 1、创建AdSlot对象
 * 2、创建TTAdNative对象
 * 3、创建加载、展示监听器
 * 4、加载广告
 * 5、加载成功后，展示广告
 * 6、在onDestroy中销毁广告
 */
public class MediationFeedRecyclerViewActivity extends Activity {

    public String mMediaId; // 融合广告位

    private static final int LIST_ITEM_COUNT = 3; // 每3条item中，展示一条广告
    private LoadMoreRecyclerView mRecyclerView;
    private MediationFeedRecyclerViewAdapter mAdapter;

    private List<TTFeedAd> mData;

    private final Handler mHandler = new Handler(Looper.getMainLooper());


    private TTAdNative.FeedAdListener mFeedAdListener; // 广告加载监听器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediation_activity_feed_recyclerview);

        // 聚合广告位（在GroMore平台的广告位，注意不是adn的代码位）
        mMediaId = getResources().getString(R.string.feed_native_media_id);
        TextView tvMediationId = findViewById(R.id.tv_media_id);
        tvMediationId.setText(String.format(getResources().getString(R.string.ad_mediation_id), mMediaId));

        // 初始化RecyclerView
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.feed_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mData = new ArrayList<>();
        mAdapter = new MediationFeedRecyclerViewAdapter(this, mData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLoadMoreListener(new ILoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadFeedAd();
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadFeedAd();
            }
        }, 500);
    }

    private void loadFeedAd() {
        /** 1、创建AdSlot对象 */

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(this.mMediaId)
                .setImageAcceptedSize(UIUtils.getScreenWidthInPx(this), UIUtils.dp2px(this, 340)) // 单位px
                .setAdCount(3) // 请求广告数量为1到3条 （优先采用平台配置的数量）
                .build();

        /** 2、创建TTAdNative对象 */

        TTAdNative adNativeLoader = TTAdManagerHolder.get().createAdNative(this);

        /** 3、创建加载、展示监听器 */
        initListeners();

        /** 4、加载广告 */

        adNativeLoader.loadFeedAd(adSlot, mFeedAdListener);
    }

    private void initListeners() {
        // 广告加载监听器

        this.mFeedAdListener = new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                if (mRecyclerView != null) {
                    mRecyclerView.setLoadingFinish();
                }
                Toast.makeText(MediationFeedRecyclerViewActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override

            public void onFeedAdLoad(List<TTFeedAd> ads) {
                if (mRecyclerView != null) {
                    mRecyclerView.setLoadingFinish();
                }

                if (ads == null || ads.isEmpty()) {
                    Toast.makeText(MediationFeedRecyclerViewActivity.this,
                            "on FeedAdLoaded: ad is null!", Toast.LENGTH_SHORT).show();
                    return;
                }


                for (final TTFeedAd ad : ads) {
                    /** 5、加载成功后，添加到RecyclerView中展示广告 */
                    if (ad != null) {
                        MediationNativeManager manager = ad.getMediationManager();
                        if (manager != null && manager.isExpress()) {
                            ad.setExpressRenderListener(new MediationExpressRenderListener() {
                                @Override
                                public void onRenderFail(View view, String s, int i) {
                                    Log.d(Const.TAG, "feed express render fail, errCode: " + i + ", errMsg: " + s);
                                }

                                @Override
                                public void onAdClick() {
                                    Log.d(Const.TAG, "feed express click");
                                }

                                @Override
                                public void onAdShow() {
                                    Log.d(Const.TAG, "feed express show");
                                }

                                @Override
                                public void onRenderSuccess(View view, float v, float v1, boolean b) {
                                    // 模板广告在renderSuccess后，添加到ListView中展示
                                    Log.d(Const.TAG, "feed express render success");
                                    updateListView(ad);
                                }
                            });
                            ad.render(); // 调用render方法进行渲染

                        } else { // 自渲染广告在load成功后，即可添加到ListView中展示
                            updateListView(ad);
                        }
                    }
                }
            }
        };
    }


    private void updateListView(TTFeedAd ad) {
        for (int i = 0; i < LIST_ITEM_COUNT; i++) {
            mData.add(null);
        }
        mData.set(mData.size() - 1, ad);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /** 6、在onDestroy中销毁广告 */
        if (mData != null) {

            for (TTFeedAd ad : mData) {
                if (ad != null) {
                    ad.destroy();
                }
            }
        }
        mHandler.removeCallbacksAndMessages(null);
    }

}
