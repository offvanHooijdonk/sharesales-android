package com.share.sales.ui.utils;

import android.app.Activity;
import android.view.Window;

public class ProgressBarUtil {
	
	public static final int PROGRESS_ON_START = 100;
	public static final int PROGRESS_MAX_VALIDATION = 1000;
	public static final int PROGRESS_MAX = 10000;
	
	private Activity activity;
	private int progressDisplay;
	
	public ProgressBarUtil(Activity activity, boolean initProgressBar) {
		this.activity = activity;
		if (initProgressBar) {
			activity.requestWindowFeature(Window.FEATURE_PROGRESS);
		}
		progressDisplay = 0;
	}
	
	public void startShowProgress() {
		progressDisplay = 0;
		addProgress(PROGRESS_ON_START);
	}
	
	public void progressValidation(float step) {
		if (step > 1.0f) {
			step = 1.0f;
		}
		if (step < 0.0f) {
			step = 0.0f;
		}
		int number = (int) ((PROGRESS_MAX_VALIDATION - PROGRESS_ON_START) * step) + PROGRESS_ON_START;
		setProgress(number);
	}
	
	public void startLoad() {
		addProgress(PROGRESS_ON_START);
	}
	
	public void progressLoad(float step) {
		if (step > 1.0f) {
			step = 1.0f;
		}
		if (step < 0.0f) {
			step = 0.0f;
		}
		int number = (int) ((PROGRESS_MAX - PROGRESS_MAX_VALIDATION - PROGRESS_ON_START ) * step) + PROGRESS_MAX_VALIDATION + PROGRESS_ON_START;
		setProgress(number);
	}
	
	public void finishProgress() {
		setProgress(PROGRESS_MAX);
		progressDisplay = 0;
	}
	
	public void cancelProgress() {
		setProgress(0);
	}
	
	public void startIndeterminateBar() {
		activity.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		activity.setProgressBarIndeterminateVisibility(true);
	}
	
	public void stopIndeterminateBar() {
		activity.setProgressBarIndeterminateVisibility(false);
	}
	
	private void addProgress(int number) {
		progressDisplay += number;
		activity.setProgress(progressDisplay);
	}
	
	private void setProgress(int number) {
		progressDisplay = number;
		activity.setProgress(progressDisplay);
	}
}
