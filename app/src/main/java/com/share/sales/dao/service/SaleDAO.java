package com.share.sales.dao.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.share.sales.dao.DBHelperUtil;
import com.share.sales.dao.model.CategoryBean;
import com.share.sales.dao.model.CurrencyBean;
import com.share.sales.dao.model.LocationBean;
import com.share.sales.dao.model.SalesBean;
import com.share.sales.dao.model.StoreBean;
import com.share.sales.dao.model.TagBean;
import com.share.sales.helper.GsonHelper;
import com.share.sales.helper.TagsHelper;

public class SaleDAO {
	private static final String TABLE_NAME = "sale";
	
	private Context ctx;
	private DBHelperUtil dbHelper;
	
	public SaleDAO(Context context) {
		this.ctx = context;
		dbHelper = new DBHelperUtil(ctx);
	}

	public SalesBean save(SalesBean bean) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		
		cv.put("_id", bean.getId());
		cv.put("content", GsonHelper.getGson().toJson(bean));
		
		long id = db.insert(TABLE_NAME, null, cv);
		
		bean.setId(id);
	
		return bean;
	}
	
	public void deleteAll() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		db.delete(TABLE_NAME, null, null);
	}
	
	public void update(SalesBean bean) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("content", GsonHelper.getGson().toJson(bean));
		
		db.update(TABLE_NAME, cv, "_id=?", new String[]{String.valueOf(bean.getId())});
	}
	
	public void delete(SalesBean bean) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		db.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(bean.getId())});
	}
	
	public SalesBean getById(long id) {
		SalesBean bean;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		Cursor cursor = db.query(TABLE_NAME, null, "_id=?", new String[] {String.valueOf(id)}, null, null, null);
		
		if (cursor.moveToFirst()) {
			bean = GsonHelper.getGson().fromJson(cursor.getString(cursor.getColumnIndex("content")), SalesBean.class);
			bean.setId(cursor.getLong(cursor.getColumnIndex("_id")));
			bean = populateSale(bean);
		} else {
			bean = null;
		}
		
		cursor.close();
		return bean;
	}
	
	public List<SalesBean> getAll() {
		List<SalesBean> sales = new ArrayList<SalesBean>();
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "");
		
		if (cursor.moveToFirst()) {
			do {
				SalesBean bean = GsonHelper.getGson().fromJson(cursor.getString(cursor.getColumnIndex("content")), SalesBean.class);
				bean.setId(cursor.getLong(cursor.getColumnIndex("_id")));
				//bean = populateSale(bean);
				sales.add(bean);
			} while (cursor.moveToNext());
		}
		cursor.close();
		
		int i = 0;
		for (SalesBean bean : sales) {
			bean = populateSale(bean);
			sales.set(i, bean);
			i++;
		}
		
		return sales;
	}
	
	public SalesBean populateSale(SalesBean bean) {
		if (bean.getCurrency() != null) {
			CurrencyDAO currencyDAO = new CurrencyDAO(ctx);
			CurrencyBean currency = currencyDAO.getById(bean.getCurrency().getId());
			bean.setCurrency(currency);
		}
		if (bean.getCity() != null) {
			LocationDAO locationDAO = new LocationDAO(ctx);
			LocationBean city = locationDAO.getById(bean.getCity().getId());
			bean.setCity(city);
		}
		if (bean.getStore() != null) {
			StoreDAO storeDAO = new StoreDAO(ctx);
			StoreBean store = storeDAO.getById(bean.getStore().getId());
			bean.setStore(store);
		}
		if (bean.getCategory() != null) {
			CategoryDAO categoryDAO = new CategoryDAO(ctx);
			CategoryBean category = categoryDAO.getById(bean.getCategory().getId());
			bean.setCategory(category);
		}
		if (bean.getTagsString() != null) {
			List<TagBean> tags = TagsHelper.createTagsList(bean.getTagsString(), ctx);
			bean.setTags(tags);
		} else {
			bean.setTags(new ArrayList<TagBean>());
		}
		return bean;
	}

}
