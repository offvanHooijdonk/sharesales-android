package com.share.sales.ui.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ExpandCollapseAnimatorUtils {

	public static final long DEFAULT_DURATION = 300;

	public static void expandView(final View v) {
		expandView(v, DEFAULT_DURATION);
	}

	public static void expandView(final View v, long duration) {
		v.measure(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		final int targetHeight = v.getMeasuredHeight();

		v.setPivotY(0);
		v.setScaleY(0);
		v.getLayoutParams().height = 0;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				v.getLayoutParams().height = /*interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT
						:*/ (int) (targetHeight * interpolatedTime);
				v.setScaleY(interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		a.setDuration(duration);
		v.startAnimation(a);
	}

	public static void collapseView(final View v) {
		collapseView(v, DEFAULT_DURATION);
	}

	public static void collapseView(final View v, long duration) {
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				} else {
					v.getLayoutParams().height = initialHeight
							- (int) (initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		a.setDuration(duration);
		v.startAnimation(a);
	}
}
