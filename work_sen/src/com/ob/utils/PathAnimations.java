package com.ob.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;

public class PathAnimations {
	
	private static int	xOffset		= 15;
	private static int	yOffset		= -13;

	public static void initOffset(Context context){ 
		xOffset		= (int) (1.667 *context.getResources().getDisplayMetrics().density);
		yOffset		= -(int) (8.667 *context.getResources().getDisplayMetrics().density);
	}
	
	public static Animation getRotateAnimation(float fromDegrees ,float toDegrees,int durationMillis){
		RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(durationMillis);
		return rotate;
	}

	public static void startAnimationsIn(ViewGroup viewgroup,int durationMillis) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
				ImageButton inoutimagebutton = (ImageButton) viewgroup
						.getChildAt(i);
				inoutimagebutton.setVisibility(0);
				MarginLayoutParams mlp = (MarginLayoutParams) inoutimagebutton.getLayoutParams();
				Animation animation = new TranslateAnimation(mlp.rightMargin-xOffset,0F,
														  yOffset - mlp.topMargin, 0F);
				//
//				animation.setFillAfter(true);
				animation.setDuration(durationMillis);
				animation.setStartOffset((i * 100)
						/ (-1 + viewgroup.getChildCount()));
				animation.setInterpolator(new OvershootInterpolator(2F));
				inoutimagebutton.startAnimation(animation);
			
		}
	}
	public static void startAnimationsOut(ViewGroup viewgroup,int durationMillis) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
				final ImageButton inoutimagebutton = (ImageButton) viewgroup
						.getChildAt(i);
				MarginLayoutParams mlp = (MarginLayoutParams) inoutimagebutton.getLayoutParams();
				Animation animation = new TranslateAnimation(0F,mlp.rightMargin-xOffset,
															 0F,yOffset - mlp.topMargin);
				animation.setDuration(durationMillis);
//				animation.setFillAfter(true);
				animation.setStartOffset(((viewgroup.getChildCount()-i) * 100)
						/ (-1 + viewgroup.getChildCount())); 
				animation.setInterpolator(new AnticipateInterpolator(2F));
				animation.setAnimationListener(new Animation.AnimationListener() {
					 
					public void onAnimationStart(Animation arg0) {}
					 
					public void onAnimationRepeat(Animation arg0) {}
					 
					public void onAnimationEnd(Animation arg0) {
						inoutimagebutton.setVisibility(View.GONE);
					}
				});
				inoutimagebutton.startAnimation(animation);
			}
		
	}

	
}