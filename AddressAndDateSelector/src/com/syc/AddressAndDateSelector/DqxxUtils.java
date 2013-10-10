package com.syc.AddressAndDateSelector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

public class DqxxUtils {
	public static final String DATABASE_NAME = "dqxx.db";
	public static final String TABLE_NAME = "dqxx";

	public static void copyDqdbIfNeeded(Context context) {
		if (new File(context.getFilesDir(), DATABASE_NAME).exists()) {
			return;
		}		
		Log.i("DqxxUtils", "开始复制地区数据库");
		try {
			FileOutputStream fos = context.openFileOutput(DATABASE_NAME,
					Context.MODE_PRIVATE);
			
			InputStream is = context.getResources().getAssets().open(DATABASE_NAME);
			byte[] buffer = new byte[1024 * 4];
			int len = 0;
			while ((len = is.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			is.close();
			Log.i("DqxxUtils", "地区数据库复制成功");			
			
			
		} catch (Exception e) {
			Log.e("DqxxUtils", "复制地区数据库时发生意外："+e.getMessage());
		}
	}
	
	public static boolean isCityExist(Context context,String city){
		File file=new File(context.getFilesDir(),DATABASE_NAME);		
		SQLiteDatabase db=context.openOrCreateDatabase(file.getAbsolutePath(), Context.MODE_PRIVATE, null);
		Cursor cursor=db.rawQuery("select count(1) from "+TABLE_NAME+" where DQXX02=?", new String[]{city});
		cursor.moveToNext();
		return cursor.getInt(0)>0;
	}
		
	
	public static ArrayList<ContentValues> getCities(Context context){
		ArrayList<ContentValues> cities=new ArrayList<ContentValues>();
		File file=new File(context.getFilesDir(),DATABASE_NAME);		
		SQLiteDatabase db=context.openOrCreateDatabase(file.getAbsolutePath(), Context.MODE_PRIVATE, null);
		Cursor cursor=db.query(TABLE_NAME,
				new String[]{"DQXX01","DQXX02","TOKEN"},
				"DQXX03=?", 
				new String[]{"2"},
				null,
				null, 
				null);
		ContentValues values=null;
		while(cursor.moveToNext()){
			values=new ContentValues();
			values.put("id", cursor.getInt(0));
			values.put("name", cursor.getString(1));
			values.put("token", cursor.getString(2));
			cities.add(values);
		}
		cursor.close();		
		db.close();	
		Collections.sort(cities, new PinyinComparator());
		return cities;
		
	}
	
	static class PinyinComparator implements Comparator<ContentValues>{	
		@Override
		public int compare(ContentValues lhs, ContentValues rhs) {
			String lToken=lhs.getAsString("token");
			String rToken=rhs.getAsString("token");
			return lToken.compareTo(rToken);
		}		
	}
	

	public static ArrayList<ContentValues> getProvince(SQLiteDatabase db) {
		ArrayList<ContentValues> list = new ArrayList<ContentValues>();
		ContentValues values = null;
		Cursor cursor = db.query(TABLE_NAME, 
				new String[] { "DQXX01","DQXX02" }, 
				"DQXX03=?", 
				new String[] { "1" }, 
				null, 
				null,
				"DQX_DQXX01 ASC");
		if (cursor != null) {
			while (cursor.moveToNext()) {
				values =new ContentValues();
				values.put("province_id", cursor.getInt(0));
				values.put("province", cursor.getString(1));
				list.add(values);
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public static ArrayList<ContentValues> getCity(SQLiteDatabase db,
			int dqx_dqxx01, boolean municipalities) {
		ArrayList<ContentValues> list = new ArrayList<ContentValues>();
		ContentValues values = null;
		Cursor cursor = db.query(TABLE_NAME,
				new String[] { "DQXX01", "DQXX02" }, 
				"DQX_DQXX01=?",
				new String[] { "" + dqx_dqxx01 },
				null, 
				null, 
				"DQXX01 ASC");
		if (cursor != null) {
			if (municipalities) {
				cursor.moveToNext();
			}
			while (cursor.moveToNext()) {
				values = new ContentValues();
				values.put("city_id", cursor.getInt(0));
				values.put("city", cursor.getString(1));
				list.add(values);
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public static ArrayList<ContentValues> getArea(SQLiteDatabase db, int dqx_dqxx01) {
		ArrayList<ContentValues> list = new ArrayList<ContentValues>();
		ContentValues values = null;
		Cursor cursor = db.query(TABLE_NAME,
				new String[] { "DQXX01", "DQXX02","DQXX05" }, 
				"DQX_DQXX01=?",
				new String[] { "" + dqx_dqxx01 }, 
				null, 
				null, 
				"DQXX01 ASC");
		if (cursor != null) {
			while (cursor.moveToNext()) {
				values = new ContentValues();
				values.put("area_id", cursor.getInt(0));
				values.put("area", cursor.getString(1));
				values.put("full_area", cursor.getString(2));
				list.add(values);
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public static int findPrimaryKey(SQLiteDatabase db, String address) {
		int key = -1;
		Cursor cursor = db.query(TABLE_NAME, 
				new String[] { "DQXX01" },
				"DQXX05=?", 
				new String[] { address }, 
				null, 
				null, 
				null);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				key = cursor.getInt(0);
			}
		}
		return key;
	}
}
