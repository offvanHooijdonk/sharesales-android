package com.share.sales.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

import com.share.sales.R;

public class ColorsHelper {
	private static final int DEFAULT_COLOR = Color.BLACK;
	private static int[] typeArray = new int[]{android.R.attr.textColor};

	public static int getPerCentColor(Context ctx, int perCent) {
		int color = DEFAULT_COLOR;
		if (perCent >=0) {
			if (perCent < 20) {
				color = getTextColorFromStyle(ctx, R.style.perCent0);
			} else if (perCent < 30) {
				color = getTextColorFromStyle(ctx, R.style.perCent20);
			} else if (perCent < 40) {
				color = getTextColorFromStyle(ctx, R.style.perCent30);
			} else if (perCent < 50) {
				color = getTextColorFromStyle(ctx, R.style.perCent40);
			} else if (perCent < 70) {
				color = getTextColorFromStyle(ctx, R.style.perCent50);
			} else {
				color = getTextColorFromStyle(ctx, R.style.perCent70);
			}
		}
		return color;
	}
	
	private static int getTextColorFromStyle(Context ctx, int styleId) {
		TypedArray ta = ctx.obtainStyledAttributes(styleId, typeArray);
		int color = ta.getColor(0, DEFAULT_COLOR);
		ta.recycle();
		return color;
	}
}
