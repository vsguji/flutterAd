package com.pengsheng.flutterad.mediation.java.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bytedance.sdk.openadsdk.ComplianceInfo;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.bytedance.sdk.openadsdk.mediation.ad.MediationViewBinder;
import com.pengsheng.flutterad.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * 自渲染feed流广告工具类，将信息流素材渲染成view
 */
public class FeedAdUtils {


    public static View getFeedAdFromFeedInfo(TTFeedAd feedAd, Activity activity, View convertView, TTNativeAd.AdInteractionListener feedInteractionListener) {
        View view = null;

        if (feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_SMALL_IMG) {             // 原生小图
            view = getSmallAdView(null, convertView, feedAd, activity, feedInteractionListener);

        } else if (feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_LARGE_IMG) {      // 原生大图
            view = getLargeAdView(null, convertView, feedAd, activity, feedInteractionListener);

        } else if (feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_GROUP_IMG) {      // 原生组图
            view = getGroupAdView(null, convertView, feedAd, activity, feedInteractionListener);

        } else if (feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO) {          // 原生视频
            view = getVideoView(null, convertView, feedAd, activity, feedInteractionListener);

        } else if (feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_VERTICAL_IMG) {   // 原生竖版图片
            view = getVerticalAdView(null, convertView, feedAd, activity, feedInteractionListener);

        } else if (feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO_VERTICAL) { // 原生视频
            view = getVideoView(null, convertView, feedAd, activity, feedInteractionListener);
        } else {
            Toast.makeText(activity, "图片展示样式错误", Toast.LENGTH_SHORT).show();
        }
        return view;
    }


    private static View getVerticalAdView(ViewGroup parent, View convertView, TTFeedAd ad, Activity activity, TTNativeAd.AdInteractionListener feedInteractionListener) {
        VerticalAdViewHolder adViewHolder = null;
        MediationViewBinder viewBinder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.mediation_listitem_ad_vertical_pic, parent, false);
            adViewHolder = new VerticalAdViewHolder();
            adViewHolder.mTitle = convertView.findViewById(R.id.tv_listitem_ad_title);
            adViewHolder.mSource = convertView.findViewById(R.id.tv_listitem_ad_source);
            adViewHolder.mDescription = convertView.findViewById(R.id.tv_listitem_ad_desc);
            adViewHolder.mVerticalImage = convertView.findViewById(R.id.iv_listitem_image);
            adViewHolder.mIcon = convertView.findViewById(R.id.iv_listitem_icon);
            adViewHolder.mDislike = convertView.findViewById(R.id.iv_listitem_dislike);
            adViewHolder.mCreativeButton = convertView.findViewById(R.id.btn_listitem_creative);
            adViewHolder.mLogo = convertView.findViewById(R.id.tt_ad_logo); //logoView 建议传入GroupView类型
            adViewHolder.app_info = convertView.findViewById(R.id.app_info);
            adViewHolder.app_name = convertView.findViewById(R.id.app_name);
            adViewHolder.author_name = convertView.findViewById(R.id.author_name);
            adViewHolder.package_size = convertView.findViewById(R.id.package_size);
            adViewHolder.permissions_url = convertView.findViewById(R.id.permissions_url);
            adViewHolder.permissions_content = convertView.findViewById(R.id.permissions_content);
            adViewHolder.privacy_agreement = convertView.findViewById(R.id.privacy_agreement);
            adViewHolder.version_name = convertView.findViewById(R.id.version_name);
            viewBinder = new MediationViewBinder.Builder(R.layout.listitem_ad_vertical_pic).titleId(R.id.tv_listitem_ad_title).descriptionTextId(R.id.tv_listitem_ad_desc).mainImageId(R.id.iv_listitem_image).iconImageId(R.id.iv_listitem_icon).callToActionId(R.id.btn_listitem_creative).sourceId(R.id.tv_listitem_ad_source).logoLayoutId(R.id.tt_ad_logo) //logoView 建议传入GroupView类型
                    .build();
            adViewHolder.viewBinder = viewBinder;
            convertView.setTag(adViewHolder);
        } else {
            adViewHolder = (VerticalAdViewHolder) convertView.getTag();
            viewBinder = adViewHolder.viewBinder;
        }
        bindData(convertView, adViewHolder, ad, viewBinder, activity, feedInteractionListener);
        if (ad.getImageList() != null && ad.getImageList().size() > 0) {
            Glide.with(activity).load(ad.getImageList().get(0).getImageUrl()).into(adViewHolder.mVerticalImage);
        }
        return convertView;
    }

    //渲染视频广告，以视频广告为例，以下说明

    private static View getVideoView(ViewGroup parent, View convertView, TTFeedAd ad, Activity activity, TTNativeAd.AdInteractionListener feedInteractionListener) {
        VideoAdViewHolder adViewHolder = null;
        MediationViewBinder viewBinder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.mediation_listitem_ad_large_video, parent, false);
            adViewHolder = new VideoAdViewHolder();
            adViewHolder.mTitle = convertView.findViewById(R.id.tv_listitem_ad_title);
            adViewHolder.mDescription = convertView.findViewById(R.id.tv_listitem_ad_desc);
            adViewHolder.mSource = convertView.findViewById(R.id.tv_listitem_ad_source);
            adViewHolder.videoView = convertView.findViewById(R.id.iv_listitem_video);
            adViewHolder.mIcon = convertView.findViewById(R.id.iv_listitem_icon);
            adViewHolder.mDislike = convertView.findViewById(R.id.iv_listitem_dislike);
            adViewHolder.mCreativeButton = convertView.findViewById(R.id.btn_listitem_creative);
            adViewHolder.mLogo = convertView.findViewById(R.id.tt_ad_logo); //logoView 建议传入GroupView类型
            adViewHolder.app_info = convertView.findViewById(R.id.app_info);
            adViewHolder.app_name = convertView.findViewById(R.id.app_name);
            adViewHolder.author_name = convertView.findViewById(R.id.author_name);
            adViewHolder.package_size = convertView.findViewById(R.id.package_size);
            adViewHolder.permissions_url = convertView.findViewById(R.id.permissions_url);
            adViewHolder.permissions_content = convertView.findViewById(R.id.permissions_content);
            adViewHolder.privacy_agreement = convertView.findViewById(R.id.privacy_agreement);
            adViewHolder.version_name = convertView.findViewById(R.id.version_name);

            //MediationViewBinder 是必须类,需要开发者在确定好View之后把Id设置给TTViewBinder类，并在注册事件时传递给SDK
            viewBinder = new MediationViewBinder.Builder(R.layout.listitem_ad_large_video).titleId(R.id.tv_listitem_ad_title).sourceId(R.id.tv_listitem_ad_source).descriptionTextId(R.id.tv_listitem_ad_desc).mediaViewIdId(R.id.iv_listitem_video).callToActionId(R.id.btn_listitem_creative).logoLayoutId(R.id.tt_ad_logo).iconImageId(R.id.iv_listitem_icon).build();
            adViewHolder.viewBinder = viewBinder;
            convertView.setTag(adViewHolder);
        } else {
            adViewHolder = (VideoAdViewHolder) convertView.getTag();
            viewBinder = adViewHolder.viewBinder;
        }
        //绑定广告数据、设置交互回调
        bindData(convertView, adViewHolder, ad, viewBinder, activity, feedInteractionListener);
        return convertView;
    }


    private static View getLargeAdView(ViewGroup parent, View convertView, TTFeedAd ad, Activity activity, TTNativeAd.AdInteractionListener feedInteractionListener) {
        LargeAdViewHolder adViewHolder = null;
        MediationViewBinder viewBinder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.mediation_listitem_ad_large_pic, parent, false);
            adViewHolder = new LargeAdViewHolder();
            adViewHolder.mTitle = convertView.findViewById(R.id.tv_listitem_ad_title);
            adViewHolder.mDescription = convertView.findViewById(R.id.tv_listitem_ad_desc);
            adViewHolder.mSource = convertView.findViewById(R.id.tv_listitem_ad_source);
            adViewHolder.mLargeImage = convertView.findViewById(R.id.iv_listitem_image);
            adViewHolder.mIcon = convertView.findViewById(R.id.iv_listitem_icon);
            adViewHolder.mDislike = convertView.findViewById(R.id.iv_listitem_dislike);
            adViewHolder.mCreativeButton = convertView.findViewById(R.id.btn_listitem_creative);
            adViewHolder.mLogo = convertView.findViewById(R.id.tt_ad_logo); //logoView 建议传入GroupView类型
            adViewHolder.app_info = convertView.findViewById(R.id.app_info);
            adViewHolder.app_name = convertView.findViewById(R.id.app_name);
            adViewHolder.author_name = convertView.findViewById(R.id.author_name);
            adViewHolder.package_size = convertView.findViewById(R.id.package_size);
            adViewHolder.permissions_url = convertView.findViewById(R.id.permissions_url);
            adViewHolder.permissions_content = convertView.findViewById(R.id.permissions_content);
            adViewHolder.privacy_agreement = convertView.findViewById(R.id.privacy_agreement);
            adViewHolder.version_name = convertView.findViewById(R.id.version_name);
            viewBinder = new MediationViewBinder.Builder(R.layout.listitem_ad_large_pic).titleId(R.id.tv_listitem_ad_title).descriptionTextId(R.id.tv_listitem_ad_desc).sourceId(R.id.tv_listitem_ad_source).mainImageId(R.id.iv_listitem_image).callToActionId(R.id.btn_listitem_creative).logoLayoutId(R.id.tt_ad_logo).iconImageId(R.id.iv_listitem_icon).build();
            adViewHolder.viewBinder = viewBinder;
            convertView.setTag(adViewHolder);
        } else {
            adViewHolder = (LargeAdViewHolder) convertView.getTag();
            viewBinder = adViewHolder.viewBinder;
        }
        bindData(convertView, adViewHolder, ad, viewBinder, activity, feedInteractionListener);
        if (ad.getImageList() != null && ad.getImageList().size() > 0) {
            Glide.with(activity).load(ad.getImageList().get(0).getImageUrl()).into(adViewHolder.mLargeImage);
        }
        return convertView;
    }


    private static View getGroupAdView(ViewGroup parent, View convertView, TTFeedAd ad, Activity activity, TTNativeAd.AdInteractionListener feedInteractionListener) {
        GroupAdViewHolder adViewHolder = null;
        MediationViewBinder viewBinder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.mediation_listitem_ad_group_pic, parent, false);
            adViewHolder = new GroupAdViewHolder();
            adViewHolder.mTitle = convertView.findViewById(R.id.tv_listitem_ad_title);
            adViewHolder.mSource = convertView.findViewById(R.id.tv_listitem_ad_source);
            adViewHolder.mDescription = convertView.findViewById(R.id.tv_listitem_ad_desc);
            adViewHolder.mGroupImage1 = convertView.findViewById(R.id.iv_listitem_image1);
            adViewHolder.mGroupImage2 = convertView.findViewById(R.id.iv_listitem_image2);
            adViewHolder.mGroupImage3 = convertView.findViewById(R.id.iv_listitem_image3);
            adViewHolder.mIcon = convertView.findViewById(R.id.iv_listitem_icon);
            adViewHolder.mDislike = convertView.findViewById(R.id.iv_listitem_dislike);
            adViewHolder.mCreativeButton = convertView.findViewById(R.id.btn_listitem_creative);
            adViewHolder.mLogo = convertView.findViewById(R.id.tt_ad_logo); //logoView 建议传入GroupView类型
            adViewHolder.app_info = convertView.findViewById(R.id.app_info);
            adViewHolder.app_name = convertView.findViewById(R.id.app_name);
            adViewHolder.author_name = convertView.findViewById(R.id.author_name);
            adViewHolder.package_size = convertView.findViewById(R.id.package_size);
            adViewHolder.permissions_url = convertView.findViewById(R.id.permissions_url);
            adViewHolder.permissions_content = convertView.findViewById(R.id.permissions_content);
            adViewHolder.privacy_agreement = convertView.findViewById(R.id.privacy_agreement);
            adViewHolder.version_name = convertView.findViewById(R.id.version_name);
            viewBinder = new MediationViewBinder.Builder(R.layout.listitem_ad_group_pic).titleId(R.id.tv_listitem_ad_title).descriptionTextId(R.id.tv_listitem_ad_desc).sourceId(R.id.tv_listitem_ad_source).mainImageId(R.id.iv_listitem_image1).logoLayoutId(R.id.tt_ad_logo).callToActionId(R.id.btn_listitem_creative).iconImageId(R.id.iv_listitem_icon).groupImage1Id(R.id.iv_listitem_image1).groupImage2Id(R.id.iv_listitem_image2).groupImage3Id(R.id.iv_listitem_image3).build();
            adViewHolder.viewBinder = viewBinder;
            convertView.setTag(adViewHolder);
        } else {
            adViewHolder = (GroupAdViewHolder) convertView.getTag();
            viewBinder = adViewHolder.viewBinder;
        }
        bindData(convertView, adViewHolder, ad, viewBinder, activity, feedInteractionListener);
        if (ad.getImageList() != null && ad.getImageList().size() >= 3) {
            String image1 = ad.getImageList().get(0).getImageUrl();
            String image2 = ad.getImageList().get(1).getImageUrl();
            String image3 = ad.getImageList().get(2).getImageUrl();
            if (image1 != null) {
                Glide.with(activity).load(image1).into(adViewHolder.mGroupImage1);
            }
            if (image2 != null) {
                Glide.with(activity).load(image2).into(adViewHolder.mGroupImage2);
            }
            if (image3 != null) {
                Glide.with(activity).load(image3).into(adViewHolder.mGroupImage3);
            }
        }
        return convertView;
    }


    private static View getSmallAdView(ViewGroup parent, View convertView, TTFeedAd ad, Activity activity, TTNativeAd.AdInteractionListener feedInteractionListener) {
        SmallAdViewHolder adViewHolder = null;
        MediationViewBinder viewBinder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.mediation_listitem_ad_small_pic, null, false);
            adViewHolder = new SmallAdViewHolder();
            adViewHolder.mTitle = convertView.findViewById(R.id.tv_listitem_ad_title);
            adViewHolder.mSource = convertView.findViewById(R.id.tv_listitem_ad_source);
            adViewHolder.mDescription = convertView.findViewById(R.id.tv_listitem_ad_desc);
            adViewHolder.mSmallImage = convertView.findViewById(R.id.iv_listitem_image);
            adViewHolder.mIcon = convertView.findViewById(R.id.iv_listitem_icon);
            adViewHolder.mDislike = convertView.findViewById(R.id.iv_listitem_dislike);
            adViewHolder.mCreativeButton = convertView.findViewById(R.id.btn_listitem_creative);
            adViewHolder.app_info = convertView.findViewById(R.id.app_info);
            adViewHolder.app_name = convertView.findViewById(R.id.app_name);
            adViewHolder.author_name = convertView.findViewById(R.id.author_name);
            adViewHolder.package_size = convertView.findViewById(R.id.package_size);
            adViewHolder.permissions_url = convertView.findViewById(R.id.permissions_url);
            adViewHolder.permissions_content = convertView.findViewById(R.id.permissions_content);
            adViewHolder.privacy_agreement = convertView.findViewById(R.id.privacy_agreement);
            adViewHolder.version_name = convertView.findViewById(R.id.version_name);
            viewBinder = new MediationViewBinder.Builder(R.layout.listitem_ad_small_pic).titleId(R.id.tv_listitem_ad_title).sourceId(R.id.tv_listitem_ad_source).descriptionTextId(R.id.tv_listitem_ad_desc).mainImageId(R.id.iv_listitem_image).logoLayoutId(R.id.tt_ad_logo).callToActionId(R.id.btn_listitem_creative).iconImageId(R.id.iv_listitem_icon).build();
            adViewHolder.viewBinder = viewBinder;
            convertView.setTag(adViewHolder);
        } else {
            adViewHolder = (SmallAdViewHolder) convertView.getTag();
            viewBinder = adViewHolder.viewBinder;
        }
        bindData(convertView, adViewHolder, ad, viewBinder, activity, feedInteractionListener);
        if (ad.getImageList() != null && ad.getImageList().size() > 0) {
            Glide.with(activity).load(ad.getImageList().get(0).getImageUrl()).into(adViewHolder.mSmallImage);
        }
        return convertView;
    }


    private static void bindData(View convertView, AdViewHolder adViewHolder, TTFeedAd ad, MediationViewBinder viewBinder, Activity activity, TTNativeAd.AdInteractionListener feedInteractionListener) {
        // 设置dislike弹窗，如果有
        if (ad.getMediationManager().hasDislike()) {

            final TTAdDislike ttAdDislike = ad.getDislikeDialog(activity);
            adViewHolder.mDislike.setVisibility(View.VISIBLE);
            adViewHolder.mDislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ttAdDislike.showDislikeDialog();
                }
            });
        } else {
            if (adViewHolder.mDislike != null) adViewHolder.mDislike.setVisibility(View.GONE);
        }
        // 下载类广告合规五要素信息
        setDownLoadAppInfo(ad, adViewHolder);

        // 可以被点击的view, 也可以把convertView放进来意味item可被点击
        ArrayList<View> clickViewList = new ArrayList<>();
        clickViewList.add(convertView);
        clickViewList.add(adViewHolder.mSource);
        clickViewList.add(adViewHolder.mTitle);
        clickViewList.add(adViewHolder.mDescription);
        clickViewList.add(adViewHolder.mIcon);
        // 添加点击区域
        if (adViewHolder instanceof LargeAdViewHolder) {
            clickViewList.add(((LargeAdViewHolder) adViewHolder).mLargeImage);
        } else if (adViewHolder instanceof SmallAdViewHolder) {
            clickViewList.add(((SmallAdViewHolder) adViewHolder).mSmallImage);
        } else if (adViewHolder instanceof VerticalAdViewHolder) {
            clickViewList.add(((VerticalAdViewHolder) adViewHolder).mVerticalImage);
        } else if (adViewHolder instanceof VideoAdViewHolder) {
            clickViewList.add(((VideoAdViewHolder) adViewHolder).videoView);
        } else if (adViewHolder instanceof GroupAdViewHolder) {
            clickViewList.add(((GroupAdViewHolder) adViewHolder).mGroupImage1);
            clickViewList.add(((GroupAdViewHolder) adViewHolder).mGroupImage2);
            clickViewList.add(((GroupAdViewHolder) adViewHolder).mGroupImage3);
        }
        // 触发创意广告的view（点击下载或拨打电话）
        ArrayList<View> creativeViewList = new ArrayList<>();
        creativeViewList.add(adViewHolder.mCreativeButton);

        // 重要! 这个涉及到广告计费，必须正确调用。
        ad.registerViewForInteraction(activity, (ViewGroup) convertView, clickViewList, creativeViewList, null, feedInteractionListener, viewBinder);

        adViewHolder.mTitle.setText(ad.getTitle()); // title为广告的简单信息提示
        adViewHolder.mDescription.setText(ad.getDescription()); // description为广告的较长的说明
        adViewHolder.mSource.setText(TextUtils.isEmpty(ad.getSource()) ? "" : ad.getSource());
        String icon = ad.getIcon() != null ? ad.getIcon().getImageUrl() : null;
        if (!TextUtils.isEmpty(icon)) {
            Glide.with(activity).load(icon).into(adViewHolder.mIcon);
        }
        Button adCreativeButton = adViewHolder.mCreativeButton;
        switch (ad.getInteractionType()) {

            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                adCreativeButton.setVisibility(View.VISIBLE);
                adCreativeButton.setText(TextUtils.isEmpty(ad.getButtonText()) ? "立即下载" : ad.getButtonText());
                break;

            case TTAdConstant.INTERACTION_TYPE_DIAL:
                adCreativeButton.setVisibility(View.VISIBLE);
                adCreativeButton.setText("立即拨打");
                break;

            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:

            case TTAdConstant.INTERACTION_TYPE_BROWSER:
                adCreativeButton.setVisibility(View.VISIBLE);
                adCreativeButton.setText(TextUtils.isEmpty(ad.getButtonText()) ? "查看详情" : ad.getButtonText());
                break;
            default:
                adCreativeButton.setVisibility(View.GONE);
                Toast.makeText(activity, "交互类型异常", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private static void setDownLoadAppInfo(TTFeedAd ttNativeAd, AdViewHolder adViewHolder) {
        if (adViewHolder == null) {
            return;
        }
        if (ttNativeAd == null || ttNativeAd.getComplianceInfo() == null) {
            adViewHolder.app_info.setVisibility(View.GONE);
        } else {
            adViewHolder.app_info.setVisibility(View.VISIBLE);
            ComplianceInfo appInfo = ttNativeAd.getComplianceInfo();
            adViewHolder.app_name.setText("应用名称：" + appInfo.getAppName());
            adViewHolder.author_name.setText("开发者：" + appInfo.getDeveloperName());
            adViewHolder.privacy_agreement.setText("隐私url：" + appInfo.getPrivacyUrl());
            adViewHolder.version_name.setText("版本号：" + appInfo.getAppVersion());
            adViewHolder.permissions_content.setText("权限内容:" + getPermissionsContent(appInfo.getPermissionsMap()));
        }
    }

    private static String getPermissionsContent(Map<String, String> permissionsMap) {
        if (permissionsMap == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : permissionsMap.entrySet()) {
            String str = entry.getKey() + ", " + entry.getValue();
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

    private static class VideoAdViewHolder extends AdViewHolder {
        FrameLayout videoView = null;
    }

    private static class LargeAdViewHolder extends AdViewHolder {
        ImageView mLargeImage = null;
    }

    private static class SmallAdViewHolder extends AdViewHolder {
        ImageView mSmallImage = null;
    }

    private static class VerticalAdViewHolder extends AdViewHolder {
        ImageView mVerticalImage = null;
    }

    private static class GroupAdViewHolder extends AdViewHolder {
        ImageView mGroupImage1 = null;
        ImageView mGroupImage2 = null;
        ImageView mGroupImage3 = null;
    }

    private static class AdViewHolder {
        MediationViewBinder viewBinder = null;
        ImageView mIcon = null;
        ImageView mDislike = null;
        Button mCreativeButton = null;
        TextView mTitle = null;
        TextView mDescription = null;
        TextView mSource = null;
        RelativeLayout mLogo = null;
        LinearLayout app_info = null;
        TextView app_name = null;
        TextView author_name = null;
        TextView package_size = null;
        TextView permissions_url = null;
        TextView privacy_agreement = null;
        TextView version_name = null;
        TextView permissions_content = null;
    }
}
