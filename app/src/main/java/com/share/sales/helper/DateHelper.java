package com.share.sales.helper;

import java.util.Calendar;
import java.util.Date;

public class DateHelper {

	private static final int DAYS_UPCOMMING = 2;
	private static final int DAYS_EXPIRING = 2;
	
	public static boolean isUpcommingDate(Date date) {
		boolean result = false;
		Calendar cNow = Calendar.getInstance();
		Calendar cDate = Calendar.getInstance();
		cDate.setTime(date);
		if (cNow.before(cDate)) {
			cNow.roll(Calendar.DAY_OF_MONTH, DAYS_UPCOMMING);
			if (cNow.after(cDate)) {
				result = true;
			}
		}
		
		return result;
	}
	
	public static boolean isExpiringDate(Date date) {
		boolean result = false;
		Calendar cNow = Calendar.getInstance();
		Calendar cDate = Calendar.getInstance();
		cDate.setTime(date);
		if (cDate.after(cNow)) {
			cNow.roll(Calendar.DAY_OF_MONTH, DAYS_EXPIRING);
			if (cDate.before(cNow)) {
				result = true;
			}
		}
		
		return result;
	}
}
