package com.share.sales.dao.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.share.sales.dao.DBHelperUtil;
import com.share.sales.dao.model.StoreBean;
import com.share.sales.helper.GsonHelper;

public class StoreDAO {
	private Context ctx;
	private DBHelperUtil dbHelper;
	
	public StoreDAO(Context context) {
		this.ctx = context;
		dbHelper = new DBHelperUtil(ctx);
	}
	
	public StoreBean save(StoreBean bean) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		
		cv.put("content", GsonHelper.getGson().toJson(bean));
		
		long id = db.insert("store", null, cv);
		
		bean.setId(id);
		
		return bean;
	}
	
	public StoreBean getById(long id) {
		StoreBean bean;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		Cursor cursor = db.query("store", null, "_id=?", new String[] {String.valueOf(id)}, null, null, null);
		
		if (cursor.moveToFirst()) {
			bean = GsonHelper.getGson().fromJson(cursor.getString(cursor.getColumnIndex("content")), StoreBean.class);
			bean.setId(cursor.getLong(cursor.getColumnIndex("_id")));
		} else {
			bean = null;
		}
		
		return bean;
	}
	
	public List<StoreBean> getAll() {
		List<StoreBean> sales = new ArrayList<StoreBean>();
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("store", null, null, null, null, null, "");
		
		if (cursor.moveToFirst()) {
			do {
				StoreBean bean = GsonHelper.getGson().fromJson(cursor.getString(cursor.getColumnIndex("content")), StoreBean.class);
				bean.setId(cursor.getLong(cursor.getColumnIndex("_id")));
				sales.add(bean);
			} while (cursor.moveToNext());
		}
		
		return sales;
	}
}
