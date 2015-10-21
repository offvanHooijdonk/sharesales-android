package com.share.sales.dao.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.share.sales.dao.DBHelperUtil;
import com.share.sales.dao.model.CurrencyBean;
import com.share.sales.helper.GsonHelper;

public class CurrencyDAO {
	public static final String TABLE = "currency";

	private Context ctx;
	private DBHelperUtil dbHelper;

	public CurrencyDAO(Context context) {
		this.ctx = context;
		dbHelper = new DBHelperUtil(ctx);
	}

	public CurrencyBean save(CurrencyBean bean) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put("_id", bean.getId());
		cv.put("content", GsonHelper.getGson().toJson(bean));

		long id = db.insert(TABLE, null, cv);

		bean.setId(id);

		return bean;
	}

	public CurrencyBean getById(long id) {
		CurrencyBean bean;
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cursor = db.query(TABLE, null, "_id=?", new String[] { String.valueOf(id) }, null, null, null);

		if (cursor.moveToFirst()) {
			bean = GsonHelper.getGson().fromJson(cursor.getString(cursor.getColumnIndex("content")), CurrencyBean.class);
			bean.setId(cursor.getLong(cursor.getColumnIndex("_id")));
		} else {
			bean = null;
		}

		cursor.close();
		return bean;
	}

	public List<CurrencyBean> getAll() {
		List<CurrencyBean> sales = new ArrayList<CurrencyBean>();

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(TABLE, null, null, null, null, null, "");

		if (cursor.moveToFirst()) {
			do {
				CurrencyBean bean = GsonHelper.getGson().fromJson(cursor.getString(cursor.getColumnIndex("content")), CurrencyBean.class);
				bean.setId(cursor.getLong(cursor.getColumnIndex("_id")));
				sales.add(bean);
			} while (cursor.moveToNext());
		}

		cursor.close();
		return sales;
	}

	public void removeAll() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.delete(TABLE, null, null);
	}
}
