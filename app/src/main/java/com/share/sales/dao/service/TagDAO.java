package com.share.sales.dao.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.share.sales.dao.DBHelperUtil;
import com.share.sales.dao.model.TagBean;
import com.share.sales.helper.GsonHelper;

public class TagDAO {
	public static final String TABLE = "tag";

	private Context ctx;
	private DBHelperUtil dbHelper;

	public TagDAO(Context context) {
		this.ctx = context;
		dbHelper = new DBHelperUtil(ctx);
	}

	public TagBean save(TagBean bean) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put("_id", bean.getId());
		cv.put("content", GsonHelper.getGson().toJson(bean));

		long id = db.insert(TABLE, null, cv);

		bean.setId(id);

		return bean;
	}

	public TagBean getById(long id) {
		TagBean bean;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Cursor cursor = db.query(TABLE, null, "_id=?", new String[] { String.valueOf(id) }, null, null, null);

		if (cursor.moveToFirst()) {
			bean = GsonHelper.getGson().fromJson(cursor.getString(cursor.getColumnIndex("content")), TagBean.class);
			bean.setId(cursor.getLong(cursor.getColumnIndex("_id")));
		} else {
			bean = null;
		}

		return bean;
	}

	public List<TagBean> getAll() {
		List<TagBean> sales = new ArrayList<TagBean>();

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(TABLE, null, null, null, null, null, "");

		if (cursor.moveToFirst()) {
			do {
				TagBean bean = GsonHelper.getGson().fromJson(cursor.getString(cursor.getColumnIndex("content")), TagBean.class);
				bean.setId(cursor.getLong(cursor.getColumnIndex("_id")));
				sales.add(bean);
			} while (cursor.moveToNext());
		}

		return sales;
	}

	public void removeAll() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.delete(TABLE, null, null);
	}
}
