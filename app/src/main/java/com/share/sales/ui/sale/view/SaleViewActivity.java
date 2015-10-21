package com.share.sales.ui.sale.view;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import org.apmem.tools.layouts.FlowLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.share.sales.Constants;
import com.share.sales.R;
import com.share.sales.dao.model.CategoryBean;
import com.share.sales.dao.model.SalesBean;
import com.share.sales.dao.model.TagBean;
import com.share.sales.dao.service.CategoryDAO;
import com.share.sales.dao.service.SaleDAO;
import com.share.sales.helper.ColorsHelper;
import com.share.sales.helper.DateHelper;
import com.share.sales.helper.GsonHelper;
import com.share.sales.helper.ImagesHelper;
import com.share.sales.helper.TagsHelper;
import com.share.sales.ui.sale.form.SaleFormActivity;
import com.share.sales.ui.utils.ProgressBarUtil;
import com.share.sales.web.client.ResponseBean;
import com.share.sales.web.client.ServiceCallListener;
import com.share.sales.web.client.impl.SaleRestClient;

public class SaleViewActivity extends FragmentActivity implements ServiceCallListener {
	public static final String SEND_SALE_ID = "send_sale_id";
	
	private static final String REQUEST_TAG_REFRESH = "request_tag_refresh";
	private static final String REQUEST_TAG_DELETE = "request_tag_delete";
	
	private SaleViewActivity that;
	private LayoutInflater layoutInflater;
	
	private TextView textName;
	private TextView textCategory;
	private TextView textDiscount;
	private TextView textSymbolPerCent;
	private TextView textPriceNew;
	private TextView textPriceOld;
	private TextView textCurrency;
	private FlowLayout blockImages;
	private TextView textAddress;
	private TextView textStore;
	private ImageButton btnLocation;
	private TextView textFrom;
	private TextView textDateFrom;
	private TextView textUpcomming;
	private TextView textTill;
	private TextView textDateTo;
	private TextView textExpiring;
	private FlowLayout blockTags;
	
	private SalesBean saleBean;
	private LatLng latLng = null;
	//private ProgressBarUtil progressBarUtil;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		that = this;
		setContentView(R.layout.layout_sale_view);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		textName = (TextView) findViewById(R.id.textName);
		textCategory = (TextView) findViewById(R.id.textCategory);
		textAddress = (TextView) findViewById(R.id.textAddress);
		textStore = (TextView) findViewById(R.id.textStore);
		btnLocation = (ImageButton) findViewById(R.id.btnLocation);
		textDiscount = (TextView) findViewById(R.id.textDiscount);
		textSymbolPerCent = (TextView) findViewById(R.id.textSymbolPerCent);
		textPriceNew = (TextView) findViewById(R.id.textPriceNew);
		textPriceOld = (TextView) findViewById(R.id.textPriceOld);
		textCurrency = (TextView) findViewById(R.id.textCurrency);
		blockImages = (FlowLayout) findViewById(R.id.blockImages);
		blockTags = (FlowLayout) findViewById(R.id.blockTags);
		textFrom = (TextView) findViewById(R.id.textFrom);
		textDateFrom = (TextView) findViewById(R.id.textDateFrom);
		textUpcomming = (TextView) findViewById(R.id.textUpcomming);
		textTill = (TextView) findViewById(R.id.textTill);
		textDateTo = (TextView) findViewById(R.id.textDateTo);
		textExpiring = (TextView) findViewById(R.id.textExpiring);
		
		// Get Sale Bean and display
		Long saleId = getIntent().getExtras().getLong(SEND_SALE_ID);
		if (saleId == null) {
			Toast.makeText(that, "Incorrect data", Toast.LENGTH_LONG).show();
			that.finish();
			return;
		}
		SaleDAO dao = new SaleDAO(that);
		saleBean = dao.getById(saleId);
		
		if (saleBean == null) {
			// TODO : some fancy text about sale not found.
			Toast.makeText(that, "Sale not found!", Toast.LENGTH_LONG).show();
		} else {
			displaySale(saleBean);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_sale_view, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_edit) {
			if (saleBean.getId() != SalesBean.VOID_ID) {
				Intent intent = new Intent(that, SaleFormActivity.class);
				intent.putExtra(SaleFormActivity.EXTRA_SALE_ID, saleBean.getId());
				that.startActivity(intent);
			}
		} else if (item.getItemId() == R.id.action_remove) {
			AlertDialog.Builder alert = new AlertDialog.Builder(that);
			alert.setTitle("Delete sale");
			alert.setMessage("Are you sure you want to delete this sale?");
			alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					/*progressBarUtil = new ProgressBarUtil(that, false);
					progressBarUtil.startIndeterminateBar();*/
					new SaleRestClient(that, that).delete(saleBean.getId(), REQUEST_TAG_DELETE);
					dialog.dismiss();
				}
			});
			alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alert.show();
		} else if (item.getItemId() == R.id.action_refresh) {
			SaleRestClient client = new SaleRestClient(that, that);
			client.getById(saleBean.getId(), REQUEST_TAG_REFRESH);
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void displaySale(SalesBean bean) {
		textName.setText(bean.getName());
		// Category
		CategoryDAO categoryDAO = new CategoryDAO(that);
		List<CategoryBean> catHierarchy = categoryDAO.getParentsHierarchy(saleBean.getCategory());
		StringBuilder strCategories = new StringBuilder();
		for (CategoryBean cat : catHierarchy) {
			strCategories.append(cat.getNameLocal()).append(" > ");
		}
		strCategories.append(saleBean.getCategory().getNameLocal());
		textCategory.setText(strCategories);
		// Prices and Currency
		int discountColor = ColorsHelper.getPerCentColor(that, bean.getPerCent());
		textDiscount.setText(String.valueOf(bean.getPerCent()));
		textDiscount.setTextColor(discountColor);
		textSymbolPerCent.setTextColor(discountColor);
		textPriceNew.setText(String.valueOf(bean.getPriceCurrent()));
		textPriceNew.setTextColor(discountColor);
		if (bean.getPriceOrigin() != 0) {
			textPriceOld.setText(String.valueOf(bean.getPriceOrigin()));
			textPriceOld.setVisibility(View.VISIBLE);			
		} else {
			textPriceOld.setVisibility(View.GONE);
			textPriceOld.setText("");
		}
		textCurrency.setText(saleBean.getCurrency().getSignCode());
		textCurrency.setTextColor(discountColor);
		// Images
		blockImages.removeAllViews();
		boolean allFound = true;
		if (bean.getPictures() != null) {
			for (String filePath : bean.getPictures()) {
				try {
					ImageView iv = (ImageView) layoutInflater.inflate(R.layout.li_image_on_view, blockImages, false);
					int height = that.getResources().getDimensionPixelSize(R.dimen.view_image_height);
					Bitmap bitmap = ImagesHelper.createScaledBitmapByHight(filePath, height);
					iv.setImageBitmap(bitmap);
					blockImages.addView(iv);
				} catch (FileNotFoundException e) {
					allFound = false;
				}
			}
		}
		if (!allFound) { // TODO : fine handling if some files are missing
			Toast.makeText(that, "Some pictures are not found!", Toast.LENGTH_LONG).show();
		}
		// Location
		String address = bean.getAddress() != null ? bean.getAddress() : "";
		String cityName = bean.getCity() != null && 
				bean.getCity().getNameLocal() != null &&
				!"".equals(bean.getCity().getNameLocal())  
				? bean.getCity().getNameLocal() : "<unknown>";
		textAddress.setText(String.format("%s, %s", cityName, address));
		
		if (bean.getStore() != null) {
			textStore.setText(bean.getStore().getNameLocal());
			textStore.setVisibility(View.VISIBLE);
		} else {
			textStore.setVisibility(View.GONE);
			textStore.setText("");
		}
		if (bean.getLatitude() != null && bean.getLongitude() != null) {
			btnLocation.setVisibility(View.VISIBLE);
			latLng = new LatLng(bean.getLatitude(), bean.getLongitude());
			
			btnLocation.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (latLng != null) {
						Intent intent = new Intent(that, MapViewActivity.class);
						intent.putExtra(MapViewActivity.EXTRA_LATLNG, latLng);
						that.startActivity(intent);
					}
				}
			});
			btnLocation.setVisibility(View.VISIBLE);
		} else {
			latLng = null;
			btnLocation.setVisibility(View.INVISIBLE);
		}
		
		// DATES
		if (bean.getStartDate() != null) {
			long startDateMlls = bean.getStartDate();
			textDateFrom.setText(DateUtils.formatDateTime(that, startDateMlls, Constants.DATE_FLAGS_DATE_DIGIT_YEAR));
			
			float textSize;
			if (DateHelper.isUpcommingDate(new Date(startDateMlls))) {
				textSize = that.getResources().getDimension(R.dimen.view_date_highlighted);
				textUpcomming.setVisibility(View.VISIBLE);
			} else {
				textSize = that.getResources().getDimension(R.dimen.view_date_normal);
				textUpcomming.setVisibility(View.GONE);
			}
			textFrom.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			textDateFrom.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			textFrom.setVisibility(View.VISIBLE);
			textDateFrom.setVisibility(View.VISIBLE);
		} else {
			textFrom.setVisibility(View.GONE);
			textDateFrom.setVisibility(View.GONE);
			textUpcomming.setVisibility(View.GONE);
		}
		
		
		if (bean.getEndDate() != null) {
			long endtDateMlls = bean.getEndDate();
			
			textDateTo.setText(DateUtils.formatDateTime(that, endtDateMlls, Constants.DATE_FLAGS_DATE_DIGIT_YEAR));

			float textSize;
			if (DateHelper.isExpiringDate(new Date(endtDateMlls))) {
				textSize = that.getResources().getDimension(R.dimen.view_date_highlighted);
				textExpiring.setVisibility(View.VISIBLE);
			} else {
				textSize = that.getResources().getDimension(R.dimen.view_date_normal);
				textExpiring.setVisibility(View.GONE);
			}
			textTill.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			textDateTo.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			textTill.setVisibility(View.VISIBLE);
			textDateTo.setVisibility(View.VISIBLE);
		} else {
			textTill.setVisibility(View.GONE);
			textDateTo.setVisibility(View.GONE);
			textExpiring.setVisibility(View.GONE);
		}
		// TAGS
		String tagsString = bean.getTagsString();
		if (tagsString != null && !"".equals(tagsString)) {
			List<TagBean> tagsList = TagsHelper.createTagsList(tagsString, that);
			for (TagBean tag : tagsList) {
				TextView tv = (TextView) layoutInflater.inflate(R.layout.li_tag, blockTags, false);
				tv = TagsHelper.settleTagTextView(tag, tv);
				blockTags.addView(tv);
			}
		}
	}

	@Override
	public void handleResponse(ResponseBean responseBean) {
		if (responseBean.isFailed()) {
			Toast.makeText(that, "Error performing request", Toast.LENGTH_LONG).show();
		} else if (!responseBean.isValidResponseCode()) {
			Toast.makeText(that, "Invalid response code " + responseBean.getCode(), Toast.LENGTH_LONG).show();
		} else {
			if (REQUEST_TAG_REFRESH.equals(responseBean.getTag())) {
				SalesBean bean = GsonHelper.getGson().fromJson(responseBean.getBody(), SalesBean.class);
				saleBean = bean;
				displaySale(saleBean);
			} else if (REQUEST_TAG_DELETE.equals(responseBean.getTag())) {
				SaleDAO dao = new SaleDAO(that);
				dao.delete(saleBean);

				/*progressBarUtil.startIndeterminateBar();*/
				that.finish();
			}
		}
	}
}
