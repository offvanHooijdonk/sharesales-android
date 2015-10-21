package com.share.sales.ui.sale;

import java.text.DecimalFormat;
import java.util.List;

import org.apmem.tools.layouts.FlowLayout;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.share.sales.Constants;
import com.share.sales.R;
import com.share.sales.dao.model.SalesBean;
import com.share.sales.dao.model.TagBean;
import com.share.sales.helper.ColorsHelper;
import com.share.sales.helper.DiscountHelper;
import com.share.sales.helper.TagsHelper;

public class SalesListAdapter extends BaseAdapter {

	private List<SalesBean> salesList;
	
	public SalesListAdapter(List<SalesBean> sales) {
		this.salesList = sales;
	}
	
	@Override
	public int getCount() {
		return salesList.size();
	}

	@Override
	public Object getItem(int position) {
		return salesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_sale_item2, parent, false);
        }
        
        view = populateView(view, salesList.get(position));
        
		return view;
	}

	private View populateView(View view, SalesBean sale) {
		Context ctx = view.getContext();
		TextView tvTitle = (TextView) view.findViewById(R.id.tvSaleTitle);
		TextView tvDiscount = (TextView) view.findViewById(R.id.tvDiscountAmount);
		TextView tvPerCentSign = (TextView) view.findViewById(R.id.tvPerCent);
		TextView tvPriceOrigin = (TextView) view.findViewById(R.id.tvOldPrice);
		TextView tvPriceCurrent = (TextView) view.findViewById(R.id.tvNewPrice);
		TextView tvStartDate = (TextView) view.findViewById(R.id.tvStartDate);
		TextView tvEndDate = (TextView) view.findViewById(R.id.tvEndDate);
		TextView tvLocation = (TextView) view.findViewById(R.id.tvLocation);
		FlowLayout flTags = (FlowLayout) view.findViewById(R.id.flTags);
		
		ImageButton btnPlace = (ImageButton) view.findViewById(R.id.btnPlace);
		ImageButton btnFav = (ImageButton) view.findViewById(R.id.btnFav);
		ImageButton btnImages = (ImageButton) view.findViewById(R.id.btnImages);
		ImageButton btnLike = (ImageButton) view.findViewById(R.id.btnLike);
		ImageButton btnShare = (ImageButton) view.findViewById(R.id.btnShare);
		
		DecimalFormat df = new DecimalFormat("#,###.##");
		
		tvTitle.setText(sale.getName());
		
		int perCent;
		if (sale.getPerCent() == 0) {
			perCent = DiscountHelper.calculateDiscount(sale.getPriceCurrent(), sale.getPriceOrigin());
		} else {
			perCent = sale.getPerCent();
		}
		int color = ColorsHelper.getPerCentColor(ctx, perCent);
		tvDiscount.setText(String.valueOf(perCent));
		tvDiscount.setTextColor(color);
		tvPerCentSign.setTextColor(color);
		tvPriceOrigin.setText(df.format(sale.getPriceOrigin()));
		tvPriceCurrent.setText(df.format(sale.getPriceCurrent()));
		tvPriceCurrent.setTextColor(color);
		
		if (sale.getStartDate() != null) {
			tvStartDate.setText(DateUtils.formatDateTime(ctx, sale.getStartDate(), Constants.DATE_FLAGS_DATE_DIGIT_YEAR));
		}
		if (sale.getEndDate() != null) {
			tvEndDate.setText(DateUtils.formatDateTime(ctx, sale.getEndDate(), Constants.DATE_FLAGS_DATE_DIGIT_YEAR));
		}
		tvLocation.setText(sale.getAddress());
		
		flTags.removeAllViews();
		for (TagBean tag : sale.getTags()) {
			TextView tv = (TextView) LayoutInflater.from(view.getContext()).inflate(R.layout.li_tag, flTags, false);
			
			tv = TagsHelper.settleTagTextView(tag, tv);
			
			// TODO implement style in another way
			flTags.addView(tv);
		}
		
		btnFav.setFocusable(false);
		btnImages.setFocusable(false);
		btnLike.setFocusable(false);
		btnPlace.setFocusable(false);
		btnShare.setFocusable(false);
		
		return view;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return true;
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}
}
