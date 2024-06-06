package com.pengsheng.flutterad.onepointfive;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pengsheng.flutterad.onepointfive.TTLoadingMoreView;

import com.pengsheng.flutterad.R;


/**
 * Created by linqiming on 6/19/22
 * Usage: recyclerview foot view
 * Doc:
 */
public class TTEmptyFootViewHolder extends RecyclerView.ViewHolder {
    private TTLoadingMoreView loadMoreView;
    private TextView moreTips;
    private View aniViewLeft;
    private View aniViewRight;
    private View aniViewMid;
    private String mNormalTip = "";
    private String mLeftSlipTip = "";

    public TTEmptyFootViewHolder(View itemView) {
        super(itemView);
        moreTips = itemView.findViewById(R.id.tips);
        loadMoreView = itemView.findViewById(R.id.more);
        aniViewLeft = itemView.findViewById(R.id.anim_view_left);
        aniViewRight = itemView.findViewById(R.id.anim_view_right);
        aniViewMid = itemView.findViewById(R.id.anim_view_middle);
        itemView.setVisibility(View.VISIBLE);
    }

    public void setTip(String leftSlipText, String normalText) {
        mNormalTip = normalText;
        mLeftSlipTip = leftSlipText;
        setNormalTip();
    }

    public void scrollViewToOrigin(final View listRoot) {
        if (aniViewRight != null) {
            //松手后的还原动画
            setNormalTip();
            final LinearLayout.LayoutParams lpRight = (LinearLayout.LayoutParams) aniViewRight.getLayoutParams();
            final LinearLayout.LayoutParams lpLeft = (LinearLayout.LayoutParams) aniViewLeft.getLayoutParams();
            final LinearLayout.LayoutParams lpMid = (LinearLayout.LayoutParams) aniViewMid.getLayoutParams();
            int width = lpRight.width;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(width, 0);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    float animatedFraction = animation.getAnimatedFraction();
                    float value = (float) animation.getAnimatedValue();
                    loadMoreView.setMoveSpace(value);
                    int space = 0;
                    lpRight.width *= (1.0f - animatedFraction);
                    space += lpRight.width;
                    lpLeft.width *= (1.0f - animatedFraction);
                    space += lpLeft.width;
                    lpMid.width *= (1.0f - animatedFraction);
                    space += lpMid.width;
                    aniViewRight.setLayoutParams(lpRight);
                    aniViewLeft.setLayoutParams(lpLeft);
                    aniViewMid.setLayoutParams(lpMid);
                    if (listRoot != null) {
                        listRoot.scrollTo(space, listRoot.getScrollY());
                    }
                }
            });
            valueAnimator.setInterpolator(new AccelerateInterpolator());
            valueAnimator.setDuration(300);
            valueAnimator.start();
        }
    }

    public void resetViewImme(View listRoot) {
        setNormalTip();
        loadMoreView.reset();
        if (aniViewLeft != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) aniViewLeft.getLayoutParams();
            layoutParams.width = 0;
            aniViewLeft.setLayoutParams(layoutParams);
        }
        if (aniViewRight != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) aniViewRight.getLayoutParams();
            layoutParams.width = 0;
            aniViewRight.setLayoutParams(layoutParams);
        }
        if (listRoot != null) {
            listRoot.scrollTo(0, listRoot.getScrollY());
        }
    }

    private void animateView(int offerX, View listRoot) {
        offerX = Math.abs(offerX);
        int space = 0;
        if (aniViewLeft != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) aniViewLeft.getLayoutParams();
            layoutParams.width = (int) (offerX / 8f);
            space += layoutParams.width;
            aniViewLeft.setLayoutParams(layoutParams);
        }
        if (aniViewRight != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) aniViewRight.getLayoutParams();
            layoutParams.width = (int) (offerX / 8f);
            space += layoutParams.width;
            aniViewRight.setLayoutParams(layoutParams);
        }
        if (listRoot != null) {
            listRoot.scrollTo(space, listRoot.getScrollY());
        }
    }

    public void actionMove(int offerX, View listRoot) {
        //滑动过程中距离变化
        loadMoreView.setMoveSpace(offerX);
        animateView(offerX, listRoot);
    }

    public void setLeftSlipTip() {
        if (moreTips != null && itemView != null) {
            moreTips.setText(mLeftSlipTip);
            itemView.setContentDescription(mLeftSlipTip);
        }
    }

    public void setNormalTip() {
        if (moreTips != null && itemView != null) {
            moreTips.setText(mNormalTip);
            itemView.setContentDescription(mNormalTip);
        }
    }

    public void animateTips() {
        startScale(moreTips, 200, 1.0f, 1.05f);
    }

    private void startScale(View view, long duration,float fromScale,float toScale) {
        //左滑后，文字动画
        if (moreTips != null) {
            AnimatorSet animationSet = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", fromScale, toScale);
            scaleX.setDuration(duration);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", fromScale, toScale);
            scaleY.setDuration(duration);
            ObjectAnimator scaleXshrink = ObjectAnimator.ofFloat(view, "scaleX", toScale, fromScale);
            scaleXshrink.setDuration(duration);
            ObjectAnimator scaleYshrink = ObjectAnimator.ofFloat(view, "scaleY", toScale, fromScale);
            scaleYshrink.setDuration(duration);
            animationSet.play(scaleX).with(scaleY);
            animationSet.play(scaleXshrink).with(scaleYshrink);
            animationSet.play(scaleX).before(scaleXshrink);
            animationSet.start();
        }
    }
}
