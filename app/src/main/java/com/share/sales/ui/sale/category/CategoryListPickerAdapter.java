package com.share.sales.ui.sale.category;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.share.sales.R;
import com.share.sales.dao.model.CategoryBean;

public class CategoryListPickerAdapter extends BaseAdapter {
	
	private List<CategoryBean> categories;
	
	public CategoryListPickerAdapter(List<CategoryBean> categories) {

		this.categories = categories;
	}

	@Override
	public int getCount() {
		return categories.size();
	}

	@Override
	public Object getItem(int position) {
		return categories.get(position);
	}

	@Override
	public long getItemId(int position) {
		return categories.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_category_picker, parent, false);
        }
        
        view = populateView(view, position);
        
        return view;
	}
	
	private View populateView(View view, int position) {
		TextView textCatName = (TextView) view.findViewById(R.id.textCategoryName);
		textCatName.setText(categories.get(position).getNameLocal());
		return view;
	}

}
