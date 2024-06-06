package com.pengsheng.flutterad.onepointfive;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.pengsheng.flutterad.R;
import com.pengsheng.flutterad.utils.TToast;
import com.pengsheng.flutterad.utils.UIUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linqiming on 6/26/22
 * Usage:
 * Doc:
 */
public class TTOnePointFiveAdapter extends RecyclerView.Adapter {
    private static final int FOOTER_VIEW_COUNT = 1;

    private static final int ITEM_VIEW_TYPE_LOAD_MORE = -1;
    private static final int ITEM_VIEW_TYPE_NORMAL = 0;
    private static final int ITEM_VIEW_TYPE_GROUP_PIC_AD = 1;
    private static final int ITEM_VIEW_TYPE_SMALL_PIC_AD = 2;
    private static final int ITEM_VIEW_TYPE_LARGE_PIC_AD = 3;
    private static final int ITEM_VIEW_TYPE_VIDEO = 4;
    private static final int ITEM_VIEW_TYPE_VERTICAL_PIC_AD = 5;//竖版图片
    private TTOnPreloadListener mPreloadListener = null;


    private List<TTFeedAd> mData;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RequestManager mRequestManager;

    private TTFeedAd.AdInteractionListener mAdInteractionListener;

    private TTFeedAd.VideoAdListener mVideoAdListener;
    private int mWidth;
    private int mHeight;
    private boolean isSaasScene = false;


    public TTOnePointFiveAdapter(Context context, List<TTFeedAd> data, int width, int height) {
        this.mContext = context;
        this.mData = data;
        mRequestManager = Glide.with(mContext);
        mWidth = width;
        mHeight = height;

    }

    public void setSaasScene(boolean isSaasScene) {
        this.isSaasScene = isSaasScene;
    }


    public void setAdInteractionListener(TTFeedAd.AdInteractionListener listener) {
        mAdInteractionListener = listener;
    }


    public void setVideoAdListener(TTFeedAd.VideoAdListener videoAdListener) {
        this.mVideoAdListener = videoAdListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case ITEM_VIEW_TYPE_LOAD_MORE:
                TTEmptyFootViewHolder emptyFootViewHolder;
                View convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_pulltorefresh_empty_foot_view, parent, false);
                emptyFootViewHolder = new TTEmptyFootViewHolder(convertView);
                return emptyFootViewHolder;
            case ITEM_VIEW_TYPE_SMALL_PIC_AD:
                View smallAdView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_small_pic, parent, false);
                RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(mWidth, mHeight);
                smallAdView.setLayoutParams(layoutParams);
                return new SmallAdViewHolder(smallAdView);
            case ITEM_VIEW_TYPE_LARGE_PIC_AD:
                View largeAdView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_large_pic, parent, false);
                RecyclerView.LayoutParams smallAdViewParams = new RecyclerView.LayoutParams(mWidth, mHeight);
                largeAdView.setLayoutParams(smallAdViewParams);
                return new LargeAdViewHolder(largeAdView);
            case ITEM_VIEW_TYPE_VERTICAL_PIC_AD:
                View verticalAdView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_vertical_pic, parent, false);
                RecyclerView.LayoutParams verticalAdViewParams = new RecyclerView.LayoutParams(mWidth, mHeight);
                verticalAdView.setLayoutParams(verticalAdViewParams);
                return new VerticalAdViewHolder(verticalAdView);
            case ITEM_VIEW_TYPE_GROUP_PIC_AD:
                View groupAdView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_group_pic, parent, false);
                RecyclerView.LayoutParams groupAdViewParams = new RecyclerView.LayoutParams(mWidth, mHeight);
                groupAdView.setLayoutParams(groupAdViewParams);
                return new GroupAdViewHolder(groupAdView);
            case ITEM_VIEW_TYPE_VIDEO:
                View videoView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_large_video, parent, false);
                RecyclerView.LayoutParams videoViewParams = new RecyclerView.LayoutParams(mWidth, mHeight);
                videoView.setLayoutParams(videoViewParams);
                return new VideoAdViewHolder(videoView);
            default:
                return null;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int count = mData.size();

        final TTFeedAd ttFeedAd;
        if (holder instanceof SmallAdViewHolder) {
            ttFeedAd = mData.get(position);
            SmallAdViewHolder smallAdViewHolder = (SmallAdViewHolder) holder;
            ArrayList<View> images = new ArrayList<>();
            images.add(((SmallAdViewHolder) holder).mSmallImage);
            bindData(smallAdViewHolder, images, ttFeedAd);
            if (ttFeedAd.getImageList() != null && !ttFeedAd.getImageList().isEmpty()) {

                TTImage image = ttFeedAd.getImageList().get(0);
                if (image != null && image.isValid()) {
                    mRequestManager.load(image.getImageUrl()).into(smallAdViewHolder.mSmallImage);
                }
            }


        } else if (holder instanceof LargeAdViewHolder) {
            ttFeedAd = mData.get(position);
            LargeAdViewHolder largeAdViewHolder = (LargeAdViewHolder) holder;
            ArrayList<View> images = new ArrayList<>();
            images.add(((LargeAdViewHolder) holder).mLargeImage);
            bindData(largeAdViewHolder, images, ttFeedAd);
            if (ttFeedAd.getImageList() != null && !ttFeedAd.getImageList().isEmpty()) {
                TTImage image = ttFeedAd.getImageList().get(0);
                if (image != null && image.isValid()) {
                    mRequestManager.load(image.getImageUrl()).into(largeAdViewHolder.mLargeImage);
                }
            }

        } else if (holder instanceof VerticalAdViewHolder) {
            ttFeedAd = mData.get(position);
            VerticalAdViewHolder verticalAdViewHolder = (VerticalAdViewHolder) holder;
            ArrayList<View> images = new ArrayList<>();
            images.add((((VerticalAdViewHolder) holder).mVerticalImage));
            bindData(verticalAdViewHolder, images, ttFeedAd);
            if (ttFeedAd.getImageList() != null && !ttFeedAd.getImageList().isEmpty()) {
                TTImage image = ttFeedAd.getImageList().get(0);
                if (image != null && image.isValid()) {
                    mRequestManager.load(image.getImageUrl()).into(verticalAdViewHolder.mVerticalImage);
                }
            }
        } else if (holder instanceof GroupAdViewHolder) {
            ttFeedAd = mData.get(position);
            GroupAdViewHolder groupAdViewHolder = (GroupAdViewHolder) holder;
            ArrayList<View> images = new ArrayList<>();
            images.add(((GroupAdViewHolder) holder).mGroupImage1);
            images.add(((GroupAdViewHolder) holder).mGroupImage2);
            images.add(((GroupAdViewHolder) holder).mGroupImage3);
            bindData(groupAdViewHolder, images, ttFeedAd);
            if (ttFeedAd.getImageList() != null && ttFeedAd.getImageList().size() >= 3) {
                TTImage image1 = ttFeedAd.getImageList().get(0);
                TTImage image2 = ttFeedAd.getImageList().get(1);
                TTImage image3 = ttFeedAd.getImageList().get(2);
                if (image1 != null && image1.isValid()) {
                    mRequestManager.load(image1.getImageUrl()).into(groupAdViewHolder.mGroupImage1);
                }
                if (image2 != null && image2.isValid()) {
                    mRequestManager.load(image2.getImageUrl()).into(groupAdViewHolder.mGroupImage2);
                }
                if (image3 != null && image3.isValid()) {
                    mRequestManager.load(image3.getImageUrl()).into(groupAdViewHolder.mGroupImage3);
                }
            }

        } else if (holder instanceof VideoAdViewHolder) {
            ttFeedAd = mData.get(position);
            final VideoAdViewHolder videoAdViewHolder = (VideoAdViewHolder) holder;
            ArrayList<View> images = new ArrayList<>();
            images.add(((VideoAdViewHolder) holder).videoView);
            bindData(videoAdViewHolder, images, ttFeedAd);

            ttFeedAd.setVideoAdListener(mVideoAdListener);
            if (videoAdViewHolder.videoView != null) {

                View video = ttFeedAd.getAdView();
                videoAdViewHolder.videoView.post(new Runnable() {
                    @Override
                    public void run() {
                        int width = videoAdViewHolder.videoView.getWidth();

                        int videoWidth = ttFeedAd.getAdViewWidth();
                        int videoHeight = ttFeedAd.getAdViewHeight();

                        // 根据广告View的宽高比，将adViewHolder.videoView的高度动态改变
                        UIUtils.setViewSize(videoAdViewHolder.videoView, width, (int) (width / (videoWidth / (double) videoHeight)));
                    }
                });
                if (video != null) {
                    if (video.getParent() == null) {
                        videoAdViewHolder.videoView.removeAllViews();
                        videoAdViewHolder.videoView.addView(video);
                    }
                }
            }

        } else if (holder instanceof NormalViewHolder) {
            NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
            normalViewHolder.idle.setText("Recycler item " + position);
        } else if (holder instanceof TTEmptyFootViewHolder) {
            TTEmptyFootViewHolder emptyFootViewHolder = (TTEmptyFootViewHolder) holder;
            if (isSaasScene) {
                emptyFootViewHolder.setTip("松手查看更多", "左滑查看更多");
            } else {
                emptyFootViewHolder.setTip("看完啦，刷新再看看", "看完啦，刷新再看看");
            }
        }

        holder.itemView.setBackgroundColor(Color.WHITE);
        if (!isSaasScene && position == mData.size() - 1 && mPreloadListener != null) {
            mPreloadListener.onPreload();
        }
    }


    private void bindData(final AdViewHolder adViewHolder, List<View> images, final TTFeedAd ad) {
        //设置dislike弹窗
        //可以被点击的view, 也可以把convertView放进来意味item可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(adViewHolder.itemView);
        //触发创意广告的view（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
        creativeViewList.add(adViewHolder.mCreativeButton);
        //如果需要点击图文区域也能进行下载或者拨打电话动作，请将图文区域的view传入
//            creativeViewList.add(convertView);
        //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。

        ad.registerViewForInteraction((ViewGroup) adViewHolder.itemView, images, clickViewList, creativeViewList, adViewHolder.mDislike, new TTNativeAd.AdInteractionListener() {
            @Override

            public void onAdClicked(View view, TTNativeAd ad) {
                if (ad != null) {
                    TToast.show(mContext, "广告" + ad.getTitle() + "被点击");
                }
                if (mAdInteractionListener != null) {

                    mAdInteractionListener.onAdClicked(view, ad);
                }
            }

            @Override

            public void onAdCreativeClick(View view, TTNativeAd ad) {
                if (ad != null) {
                    TToast.show(mContext, "广告" + ad.getTitle() + "被创意按钮被点击");
                }
                if (mAdInteractionListener != null) {

                    mAdInteractionListener.onAdCreativeClick(view, ad);
                }
            }

            @Override

            public void onAdShow(TTNativeAd ad) {
                if (ad != null) {
                    TToast.show(mContext, "广告" + ad.getTitle() + "展示");
                }
                if (mAdInteractionListener != null) {

                    mAdInteractionListener.onAdShow(ad);
                }
            }
        });
        adViewHolder.mTitle.setText(ad.getTitle());
        adViewHolder.mDescription.setText(ad.getDescription());
        adViewHolder.mSource.setText(ad.getSource() == null ? "广告来源" : ad.getSource());
        TTImage icon = ad.getIcon();
        if (icon != null && icon.isValid()) {
            mRequestManager.load(icon.getImageUrl()).into(adViewHolder.mIcon);
        }
        Button adCreativeButton = adViewHolder.mCreativeButton;
        switch (ad.getInteractionType()) {

            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                //如果初始化ttAdManager.createAdNative(getApplicationContext())没有传入activity 则需要在此传activity，否则影响使用Dislike逻辑
                if (mContext instanceof Activity) {
                    ad.setActivityForDownloadApp((Activity) mContext);
                }
                adCreativeButton.setVisibility(View.VISIBLE);
                adViewHolder.mStopButton.setVisibility(View.VISIBLE);
                adViewHolder.mRemoveButton.setVisibility(View.VISIBLE);
                //绑定下载状态控制器
                break;

            case TTAdConstant.INTERACTION_TYPE_DIAL:
                adCreativeButton.setVisibility(View.VISIBLE);
                adCreativeButton.setText("立即拨打");
                adViewHolder.mStopButton.setVisibility(View.GONE);
                adViewHolder.mRemoveButton.setVisibility(View.GONE);
                break;

            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:

            case TTAdConstant.INTERACTION_TYPE_BROWSER:
//                    adCreativeButton.setVisibility(View.GONE);
                adCreativeButton.setVisibility(View.VISIBLE);
                adCreativeButton.setText("查看详情");
                adViewHolder.mStopButton.setVisibility(View.GONE);
                adViewHolder.mRemoveButton.setVisibility(View.GONE);
                break;
            default:
                adCreativeButton.setVisibility(View.GONE);
                adViewHolder.mStopButton.setVisibility(View.GONE);
                adViewHolder.mRemoveButton.setVisibility(View.GONE);
                TToast.show(mContext, "交互类型异常");
        }
        //1.5卡标签
        String cardTag = getCardTag(ad);
        if (!TextUtils.isEmpty(cardTag)) {
            adViewHolder.mTvCardTag.setText(cardTag);
            adViewHolder.mTvCardTag.setVisibility(View.VISIBLE);
        } else {
            adViewHolder.mTvCardTag.setVisibility(View.GONE);
        }
    }

    private String getCardTag(TTFeedAd feedAd) {
        try {
            if (feedAd != null) {
                //获取标签
                JSONObject jsonObject = new JSONObject(feedAd.getMediaExtraInfo());
                JSONObject groupInfoJson  = jsonObject.optJSONObject("group_info");
                String cardTag = groupInfoJson != null ? groupInfoJson.optString("card_tag") : null;
                return cardTag;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        int count = mData == null ? 0 : mData.size();
        return count + FOOTER_VIEW_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData != null) {
            int count = mData.size();
            if (position >= count) {
                return ITEM_VIEW_TYPE_LOAD_MORE;
            } else {

                TTFeedAd ad = mData.get(position);
                if (ad == null) {
                    return ITEM_VIEW_TYPE_NORMAL;

                } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_SMALL_IMG) {
                    return ITEM_VIEW_TYPE_SMALL_PIC_AD;

                } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_LARGE_IMG) {
                    return ITEM_VIEW_TYPE_LARGE_PIC_AD;

                } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_GROUP_IMG) {
                    return ITEM_VIEW_TYPE_GROUP_PIC_AD;

                } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO || ad.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO_VERTICAL || ad.getImageMode() == TTAdConstant.IMAGE_MODE_LIVE) {
                    return ITEM_VIEW_TYPE_VIDEO;

                } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_VERTICAL_IMG) {//竖版图片
                    return ITEM_VIEW_TYPE_VERTICAL_PIC_AD;
                }
            }

        }
        return super.getItemViewType(position);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager layout = recyclerView.getLayoutManager();
        if (layout != null && layout instanceof GridLayoutManager) {
            final GridLayoutManager manager = (GridLayoutManager) layout;
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if (type == ITEM_VIEW_TYPE_LOAD_MORE || type == ITEM_VIEW_TYPE_VIDEO) {
                        return manager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        //noinspection unchecked
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            int position = holder.getLayoutPosition();
            int type = getItemViewType(position);
            if (type == ITEM_VIEW_TYPE_LOAD_MORE || type == ITEM_VIEW_TYPE_VIDEO) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    private static class VideoAdViewHolder extends AdViewHolder {
        @SuppressWarnings("CanBeFinal")
        FrameLayout videoView;

        @SuppressWarnings("RedundantCast")
        public VideoAdViewHolder(View itemView) {
            super(itemView);

            mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
            mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
            mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
            mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
            videoView = (FrameLayout) itemView.findViewById(R.id.iv_listitem_video);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
            mCreativeButton = (Button) itemView.findViewById(R.id.btn_listitem_creative);
            mStopButton = (Button) itemView.findViewById(R.id.btn_listitem_stop);
            mRemoveButton = (Button) itemView.findViewById(R.id.btn_listitem_remove);
            mTvCardTag = (TextView) itemView.findViewById(R.id.tv_card_tag);

        }
    }

    private static class LargeAdViewHolder extends AdViewHolder {
        ImageView mLargeImage;

        @SuppressWarnings("RedundantCast")
        public LargeAdViewHolder(View itemView) {
            super(itemView);

            mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
            mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
            mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
            mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
            mLargeImage = (ImageView) itemView.findViewById(R.id.iv_listitem_image);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
            mCreativeButton = (Button) itemView.findViewById(R.id.btn_listitem_creative);
            mStopButton = (Button) itemView.findViewById(R.id.btn_listitem_stop);
            mRemoveButton = (Button) itemView.findViewById(R.id.btn_listitem_remove);
            mTvCardTag = (TextView) itemView.findViewById(R.id.tv_card_tag);
        }
    }

    private static class SmallAdViewHolder extends AdViewHolder {
        ImageView mSmallImage;

        @SuppressWarnings("RedundantCast")
        public SmallAdViewHolder(View itemView) {
            super(itemView);

            mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
            mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
            mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
            mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
            mSmallImage = (ImageView) itemView.findViewById(R.id.iv_listitem_image);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
            mCreativeButton = (Button) itemView.findViewById(R.id.btn_listitem_creative);
            mStopButton = (Button) itemView.findViewById(R.id.btn_listitem_stop);
            mRemoveButton = (Button) itemView.findViewById(R.id.btn_listitem_remove);
            mTvCardTag = (TextView) itemView.findViewById(R.id.tv_card_tag);
        }
    }

    private static class VerticalAdViewHolder extends AdViewHolder {
        ImageView mVerticalImage;

        @SuppressWarnings("RedundantCast")
        public VerticalAdViewHolder(View itemView) {
            super(itemView);

            mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
            mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
            mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
            mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
            mVerticalImage = (ImageView) itemView.findViewById(R.id.iv_listitem_image);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
            mCreativeButton = (Button) itemView.findViewById(R.id.btn_listitem_creative);
            mStopButton = (Button) itemView.findViewById(R.id.btn_listitem_stop);
            mRemoveButton = (Button) itemView.findViewById(R.id.btn_listitem_remove);
            mTvCardTag = (TextView) itemView.findViewById(R.id.tv_card_tag);
        }
    }


    @SuppressWarnings("CanBeFinal")
    private static class GroupAdViewHolder extends AdViewHolder {
        ImageView mGroupImage1;
        ImageView mGroupImage2;
        ImageView mGroupImage3;

        @SuppressWarnings("RedundantCast")
        public GroupAdViewHolder(View itemView) {
            super(itemView);

            mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
            mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
            mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
            mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
            mGroupImage1 = (ImageView) itemView.findViewById(R.id.iv_listitem_image1);
            mGroupImage2 = (ImageView) itemView.findViewById(R.id.iv_listitem_image2);
            mGroupImage3 = (ImageView) itemView.findViewById(R.id.iv_listitem_image3);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
            mCreativeButton = (Button) itemView.findViewById(R.id.btn_listitem_creative);
            mStopButton = (Button) itemView.findViewById(R.id.btn_listitem_stop);
            mRemoveButton = (Button) itemView.findViewById(R.id.btn_listitem_remove);
            mTvCardTag = (TextView) itemView.findViewById(R.id.tv_card_tag);
        }
    }

    private static class AdViewHolder extends RecyclerView.ViewHolder {
        ImageView mIcon;
        ImageView mDislike;
        Button mCreativeButton;
        TextView mTitle;
        TextView mDescription;
        TextView mSource;
        Button mStopButton;
        Button mRemoveButton;
        TextView mTvCardTag;

        public AdViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class NormalViewHolder extends RecyclerView.ViewHolder {
        TextView idle;

        @SuppressWarnings("RedundantCast")
        public NormalViewHolder(View itemView) {
            super(itemView);

            idle = (TextView) itemView.findViewById(R.id.text_idle);

        }
    }

    public void setOnPreloadListener(TTOnPreloadListener listener) {
        this.mPreloadListener = listener;
    }

}
