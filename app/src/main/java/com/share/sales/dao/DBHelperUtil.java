package com.share.sales.dao;

import com.share.sales.Constants;
import com.share.sales.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelperUtil extends SQLiteOpenHelper {

	private static final String DB_NAME = "sales_db";
	
	private Context ctx;
	
	public DBHelperUtil(Context context) {
		super(context, DB_NAME, null, 22);
		ctx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			String[] tablesQueries = ctx.getResources().getString(R.string.sql_create_tables).split(";");
			for (String q : tablesQueries) {
				//db.beginTransaction();
				db.execSQL(q);
				//db.endTransaction();
			}
			
			//initTempData(db);
			
		} catch(Exception e) {
			Log.e("SH_SALES", "Error creating db: " + e.toString());
		} finally {
			
		}
	}

	/*private void initTempData(SQLiteDatabase db) {
		String[] insertQueries = ctx.getResources().getString(R.string.sql_insert_tmp_data).split(";");
		for (String q : insertQueries) {
			try {
				//db.beginTransaction();
				db.execSQL(q);
			} catch (Exception e) {
				Log.e("SH_SALES", "Error creating db: " + e.toString());
			} finally {
				//db.endTransaction();
			}
		}
	}*/
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		String dropTables = "DROP TABLE sale; DROP TABLE location; DROP TABLE store; DROP TABLE currency;DROP TABLE currency_location;DROP TABLE category;DROP TABLE tag";
		try {
			String[] dropQueries = dropTables.split(";");
			for (String q : dropQueries) {
				db.execSQL(q);
			}
			
		} catch(Exception e) {
			Log.w(Constants.LOG_TAG, e);
		}
		onCreate(db);
	}

}
