package com.share.sales.helper;

import android.widget.BaseAdapter;
import android.widget.ListView;

public class LayoutHelper {

	public static void refreshListView(ListView lv) {
		BaseAdapter adapter = (BaseAdapter) lv.getAdapter();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		
		lv.invalidateViews();
		lv.refreshDrawableState();
	}
}
