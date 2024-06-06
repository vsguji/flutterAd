package com.pengsheng.flutterad.mediation.java;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.pengsheng.flutterad.R;
import com.pengsheng.flutterad.config.TTAdManagerHolder;
import com.pengsheng.flutterad.mediation.java.utils.Const;

/**
 * 融合demo，激励视频广告使用示例。更多功能参考接入文档。
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
public class MediationRewardActivity extends Activity {

    public String mMediaId; // 融合广告位

    private TTRewardVideoAd mTTRewardVideoAd; // 插全屏广告对象

    private TTAdNative.RewardVideoAdListener mRewardVideoListener; // 广告加载监听器

    private TTRewardVideoAd.RewardAdInteractionListener mRewardVideoAdInteractionListener; // 广告展示监听器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediation_activity_reward);

        // 聚合广告位（在GroMore平台的广告位，注意不是adn的代码位）
        this.mMediaId = getResources().getString(R.string.reward_media_id);
        TextView tvMediationId = findViewById(R.id.tv_media_id);
        tvMediationId.setText(String.format(getResources().getString(R.string.ad_mediation_id), mMediaId));

        // 广告加载
        findViewById(R.id.bt_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRewardVideoAd();
            }
        });

        // 广告展示
        findViewById(R.id.bt_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardVideoAd();
            }
        });
    }

    private void loadRewardVideoAd() {
        /** 1、创建AdSlot对象 */

        AdSlot adslot = new AdSlot.Builder()
                .setCodeId(mMediaId)

                .setOrientation(TTAdConstant.ORIENTATION_VERTICAL)
                .build();

        /** 2、创建TTAdNative对象 */

        TTAdNative adNativeLoader = TTAdManagerHolder.get().createAdNative(this);

        /** 3、创建加载、展示监听器 */
        initListeners();

        /** 4、加载广告 */

        adNativeLoader.loadRewardVideoAd(adslot, mRewardVideoListener);
    }

    // 广告加载成功后，开始展示广告
    private void showRewardVideoAd() {
        if (mTTRewardVideoAd == null) {
            Log.i(Const.TAG, "请先加载广告或等待广告加载完毕后再调用show方法");
            return;
        }
        /** 5、设置展示监听器，展示广告 */

        mTTRewardVideoAd.setRewardAdInteractionListener(mRewardVideoAdInteractionListener);

        mTTRewardVideoAd.showRewardVideoAd(this);
    }

    private void initListeners() {
        // 广告加载监听器

        this.mRewardVideoListener = new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int i, String s) {
                Log.i(Const.TAG, "reward load fail: errCode: " + i + ", errMsg: " + s);
            }

            @Override

            public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
                Log.i(Const.TAG, "reward load success");
                mTTRewardVideoAd = ttRewardVideoAd;
            }

            @Override

            public void onRewardVideoCached() {
                Log.i(Const.TAG, "reward cached success");
            }

            @Override

            public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {
                Log.i(Const.TAG, "reward cached success 2");
                mTTRewardVideoAd = ttRewardVideoAd;
            }
        };
        // 广告展示监听器

        this.mRewardVideoAdInteractionListener = new TTRewardVideoAd.RewardAdInteractionListener() {
            @Override

            public void onAdShow() {
                Log.i(Const.TAG, "reward show");
            }

            @Override

            public void onAdVideoBarClick() {
                Log.i(Const.TAG, "reward click");
            }

            @Override

            public void onAdClose() {
                Log.i(Const.TAG, "reward close");
            }

            @Override
            public void onVideoComplete() {
                Log.i(Const.TAG, "reward onVideoComplete");
            }

            @Override
            public void onVideoError() {
                Log.i(Const.TAG, "reward onVideoError");
            }

            @Override

            public void onRewardVerify(boolean b, int i, String s, int i1, String s1) {
                Log.i(Const.TAG, "reward onRewardVerify");
            }

            @Override
            public void onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo) {
                Log.i(Const.TAG, "reward onRewardArrived");
            }

            @Override
            public void onSkippedVideo() {
                Log.i(Const.TAG, "reward onSkippedVideo");
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /** 6、在onDestroy中销毁广告 */
        if (mTTRewardVideoAd != null && mTTRewardVideoAd.getMediationManager() != null) {
            mTTRewardVideoAd.getMediationManager().destroy();
        }
    }
}
