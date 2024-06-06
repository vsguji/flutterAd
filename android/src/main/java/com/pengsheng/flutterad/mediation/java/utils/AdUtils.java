package com.pengsheng.flutterad.mediation.java.utils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.mediation.IMediationPreloadRequestInfo;
import com.bytedance.sdk.openadsdk.mediation.MediationConstant;
import com.bytedance.sdk.openadsdk.mediation.MediationPreloadRequestInfo;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationAdEcpmInfo;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationBaseManager;

import java.util.ArrayList;
import java.util.List;

public class AdUtils {

    /**
     * Gromore服务端激励验证处理逻辑示例
     * @param isRewardValid
     * @param rewardType
     * @param extraInfo
     */
    public static void handleGromoreServerVerify(boolean isRewardValid, int rewardType, Bundle extraInfo) {
        boolean isGromoreServerSideVerify = extraInfo.getBoolean(MediationConstant.KEY_IS_GROMORE_SERVER_SIDE_VERIFY);
        if (isGromoreServerSideVerify) {
            // 开启了GroMore的服务端激励验证，这里可以获取GroMore的服务端激励验证信息
            boolean isVerify = isRewardValid;
            // 如果isVerify=false，则可以根据下面的错误码来判断为什么是false，
            //  1、如果errorCode为40001/40002/50001/50002，则是因为请求异常导致，媒体可以根据自己的判断决定是否发放奖励。
            //  2、否则，就是媒体服务端回传的验证结果是false，此时应该不发放奖励。

            int reason = extraInfo.getInt(MediationConstant.KEY_REASON);
            Log.d("Demo", "onRewardArrived，开发者服务器回传的reason，开发者不传时为空, reason: " + reason);

            int errorCode = extraInfo.getInt(MediationConstant.KEY_ERROR_CODE);
            String errorMsg = extraInfo.getString(MediationConstant.KEY_ERROR_MSG);
            Log.d("Demo", "onRewardArrived, gromore服务端验证异常时的错误信息，未发生异常时为0或20000：errorCode:" + errorCode + ", errMsg: " + errorMsg);

            String gromoreExtra = extraInfo.getString(MediationConstant.KEY_GROMORE_EXTRA);
            Log.d("Demo", "rewardItem, 开发者通过AdSlot传入的extra信息，会透传给媒体的服务器。开发者不传时为空，extra:" + gromoreExtra);

            String transId = extraInfo.getString(MediationConstant.KEY_TRANS_ID);
            Log.d("Demo", "rewardItem, gromore服务端验证产生的transId，一次广告播放会产生的唯一的transid: " + transId);
        }
    }

    /**
     * 打印展示后Ecpm信息
     */
    public static void printShowEcpmInfo(MediationBaseManager mediationManager) {
        if (mediationManager != null) {
            MediationAdEcpmInfo showEcpm = mediationManager.getShowEcpm();
            if (showEcpm != null) {
                logEcpmInfo(showEcpm);
            }
        }
    }

    /**
     * MediationAdEcpmInfo 字段参数如下：
     */
    public static void logEcpmInfo(MediationAdEcpmInfo item){
        Log.d(Const.TAG, "EcpmInfo: \n" +
                "adn名称 SdkName: " + item.getSdkName() + ",\n" +
                "自定义adn名称 CustomSdkName: " + item.getCustomSdkName() + ",\n" +
                "代码位Id SlotId: " + item.getSlotId() + ",\n" +
                "广告价格 Ecpm: " + item.getEcpm() + ",\n" +
                "广告竞价类型 ReqBiddingType: " + item.getReqBiddingType() + ",\n" +
                "多阶底价标签 LevelTag: " + item.getLevelTag() + ",\n" +
                "多阶底价标签解析失败原因 ErrorMsg: " + item.getErrorMsg() + ",\n" +
                "adn请求Id RequestId: " + item.getRequestId() + ",\n" +
                "广告类型 RitType: " + item.getRitType() + ",\n" +
                "AB实验Id AbTestId: " + item.getAbTestId() + ",\n" +
                "场景Id ScenarioId: " + item.getScenarioId() + ",\n" +
                "流量分组Id SegmentId: " + item.getSegmentId() + ",\n" +
                "流量分组渠道 Channel: " + item.getChannel() + ",\n" +
                "流量分组子渠道 SubChannel: " + item.getSubChannel() + ",\n" +
                "开发者传入的自定义数据 customData: " + item.getCustomData()
        );
    }

    public static void mediationPreloadAds(Activity activity) {
        //第一步: 准备激励视频预请求信息

        AdSlot rewardAdSlot = new AdSlot.Builder().build();
        List<String> rewardPrimeRitList = new ArrayList<>();
        rewardPrimeRitList.add("激励视频聚合广告位ID1");
        rewardPrimeRitList.add("激励视频聚合广告位ID2");

        IMediationPreloadRequestInfo rewardPreloadInfo = new MediationPreloadRequestInfo(AdSlot.TYPE_REWARD_VIDEO, rewardAdSlot, rewardPrimeRitList);

        //第二步: 准备信息流预请求信息

        AdSlot feedAdSlot = new AdSlot.Builder().build();
        List<String> feedPrimeRitList = new ArrayList<>();
        feedPrimeRitList.add("信息流聚合广告位ID1");
        feedPrimeRitList.add("信息流聚合广告位ID2");

        IMediationPreloadRequestInfo feedPreloadInfo = new MediationPreloadRequestInfo(AdSlot.TYPE_FEED, feedAdSlot, feedPrimeRitList);

        List<IMediationPreloadRequestInfo> requestInfoList = new ArrayList<>();
        requestInfoList.add(rewardPreloadInfo);
        requestInfoList.add(feedPreloadInfo);

        // 第三步: 发起预请求

        TTAdSdk.getMediationManager().preload(activity, requestInfoList, 2, 2);
    }
}
