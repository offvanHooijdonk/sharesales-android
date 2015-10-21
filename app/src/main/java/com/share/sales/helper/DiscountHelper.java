package com.share.sales.helper;

public class DiscountHelper {

	public static int calculateDiscount(float currentPrice, float oldPrice) {
		int perCent = Math.round(100 * (1 - currentPrice/oldPrice));
		return perCent;
	}
}
