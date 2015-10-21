package com.share.sales.dao.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.share.sales.dao.DBHelperUtil;
import com.share.sales.dao.model.LocationBean;
import com.share.sales.helper.GsonHelper;

public class LocationDAO {
	private Context ctx;
	private DBHelperUtil dbHelper;
	
	public LocationDAO(Context context) {
		this.ctx = context;
		dbHelper = new DBHelperUtil(ctx);
	}
	
	public LocationBean save(LocationBean bean) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		
		cv.put("_id", bean.getId());
		cv.put("content", GsonHelper.getGson().toJson(bean));
		cv.put("code", bean.getNameCode());
		
		long id = db.insert("location", null, cv);
		
		bean.setId(id);
		
		return bean;
	}
	
	public LocationBean getById(long id) {
		LocationBean bean;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		Cursor cursor = db.query("location", null, "_id=?", new String[] {String.valueOf(id)}, null, null, null);
		
		if (cursor.moveToFirst()) {
			bean = GsonHelper.getGson().fromJson(cursor.getString(cursor.getColumnIndex("content")), LocationBean.class);
			bean.setId(cursor.getLong(cursor.getColumnIndex("_id")));
		} else {
			bean = null;
		}
		
		cursor.close();
		return bean;
	}
	
	public List<LocationBean> getAll() {
		List<LocationBean> sales = new ArrayList<LocationBean>();
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("location", null, null, null, null, null, "");
		
		if (cursor.moveToFirst()) {
			do {
				LocationBean bean = GsonHelper.getGson().fromJson(cursor.getString(cursor.getColumnIndex("content")), LocationBean.class);
				bean.setId(cursor.getLong(cursor.getColumnIndex("_id")));
				sales.add(bean);
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		return sales;
	}
	
	public LocationBean getByCode(String locationCode) {
		LocationBean bean;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		Cursor cursor = db.query("location", null, "code = ?", new String[] {locationCode}, null, null, null);
		
		if (cursor.moveToFirst()) {
			bean = GsonHelper.getGson().fromJson(cursor.getString(cursor.getColumnIndex("content")), LocationBean.class);
			bean.setId(cursor.getLong(cursor.getColumnIndex("_id")));
		} else {
			bean = null;
		}
		
		cursor.close();
		return bean;
	}
	
	public LocationBean saveHierarchy(LocationBean country, LocationBean adminArea, LocationBean city) {
		// TODO make better finding including types and parents
		LocationBean savedCountry = getById(country.getId());
		if (savedCountry == null) {
			savedCountry = save(country);
		}
		
		LocationBean savedAdminArea = getById(adminArea.getId());
		if (savedAdminArea == null) {
			adminArea.setParent(savedCountry);
			savedAdminArea = save(adminArea);
		}
		
		LocationBean savedCity = getById(city.getId());
		if (savedCity == null) {
			city.setParent(savedCountry);
			savedCity = save(city);
		}
		
		return savedCity;
	}
}
