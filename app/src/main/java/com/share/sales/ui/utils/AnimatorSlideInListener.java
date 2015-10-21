package com.share.sales.ui.utils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.View;

public class AnimatorSlideInListener implements AnimatorListener, AnimatorUpdateListener{
	private View view;
	
	private int heightInit;

	public AnimatorSlideInListener(View view, int height) {
		this.view = view;
		this.heightInit = height;
	}
	
	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		float value = (Float) animation.getAnimatedValue();
		view.getLayoutParams().height = (int) value * heightInit;
		view.requestLayout();
	}

	@Override
	public void onAnimationCancel(Animator animation) {
		
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
		
	}

	@Override
	public void onAnimationStart(Animator animation) {
		//heightInit = view.getLayoutParams().height;
		view.getLayoutParams().height = 0;
		view.setVisibility(View.VISIBLE);
	}

}
