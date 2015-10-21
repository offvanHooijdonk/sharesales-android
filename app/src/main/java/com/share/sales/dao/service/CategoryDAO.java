package com.share.sales.dao.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.share.sales.dao.DBHelperUtil;
import com.share.sales.dao.model.CategoryBean;
import com.share.sales.helper.GsonHelper;

public class CategoryDAO {
	public static final String TABLE = "category";

	private Context ctx;
	private DBHelperUtil dbHelper;
	
	public CategoryDAO(Context context) {
		this.ctx = context;
		dbHelper = new DBHelperUtil(ctx);
	}
	
	public List<CategoryBean> getParentsHierarchy(CategoryBean bean) {
		List<CategoryBean> parents = new ArrayList<CategoryBean>();
		
		CategoryBean currentBean = bean;
		while (currentBean != null && currentBean.getParent() != null && currentBean.getParent().getId() != CategoryBean.VOID_ID) {
			currentBean = getById(currentBean.getParent().getId());
			
			if (currentBean != null) {
				parents.add(0, currentBean);
			}
		}
		
		return parents;
	}
	
	public void saveAll(List<CategoryBean> list) {
		for (CategoryBean bean : list) {
			save(bean);
		}
	}
	
	public CategoryBean save(CategoryBean bean) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		
		cv.put("_id", bean.getId());
		cv.put("content", GsonHelper.getGson().toJson(bean));
		if (bean.getParent() != null) {
			cv.put("parent", bean.getParent().getId());
		}
		
		long id = db.insert(TABLE, null, cv);
		
		bean.setId(id);
		
		return bean;
	}
	
	public CategoryBean getById(long id) {
		CategoryBean bean;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		Cursor cursor = db.query(TABLE, null, "_id=?", new String[] {String.valueOf(id)}, null, null, null);
		
		if (cursor.moveToFirst()) {
			bean = GsonHelper.getGson().fromJson(cursor.getString(cursor.getColumnIndex("content")), CategoryBean.class);
			bean.setId(cursor.getLong(cursor.getColumnIndex("_id")));
		} else {
			bean = null;
		}
		
		return bean;
	}
	
	public List<CategoryBean> getAll(Integer parent) {
		List<CategoryBean> sales = new ArrayList<CategoryBean>();
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(TABLE, null, parent == null ? "parent is null" : "parent = ?", 
				parent == null ? null : new String[]{String.valueOf(parent)}, null, null, "");
		
		if (cursor.moveToFirst()) {
			do {
				CategoryBean bean = GsonHelper.getGson().fromJson(cursor.getString(cursor.getColumnIndex("content")), CategoryBean.class);
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
