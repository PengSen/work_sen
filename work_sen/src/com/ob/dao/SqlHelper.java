package com.ob.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SqlHelper {
	private DBOpenHelper helper;

	public SqlHelper(Context context) {
		// 获取数据库连接
		helper = new DBOpenHelper(context,"sen.db",null,1);
	}
	
	/**
	 * 查询数据库有木有相应的数据
	 * 
	 * @param sql
	 *            sql语句
	 * @return 有or无 _____ true or false
	 */
	public Boolean isSelectDbInfomation(String sql, String num) {
			// 设定数据库读取方式
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, new String[] { num });
			if (cursor.moveToFirst()) {
				cursor.close();
				db.close();
				return true;
			} else {
				cursor.close();
				db.close();
				return false;
			}
	}
	/**
	 * 手机号码和地区车牌查询数据库有木有相应的数据
	 * 
	 * @param sql
	 *            sql语句
	 * @return 有or无 _____ true or false
	 */
	public Boolean SelectDbInfomation(String sql, String num) {
		if(num.length() == 2){
			String substring = num.substring(1, 2);
			String upperCase = substring.toUpperCase();
//			Log.e("upperCase", ""+upperCase);
			String substring2 = num.substring(0, 1);
			num = substring2+upperCase;
//			Log.e("substring2", ""+num);
			
			// 设定数据库读取方式
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, new String[] { num });
			// cursor.moveToFirst() = true 是没有的， 有的话是cursor.moveToFirst() = false
			if (cursor.moveToFirst()) {
				cursor.close();
				db.close();
				return true;
			} else {
				cursor.close();
				db.close();
				return false;
			}
		}else{
			return false;
		}
	}

	/**
	 * 根据传入的字符串的长度不同来做手机号码查询or身份证号码查询
	 * 
	 * @param sql
	 *            sql字符串
	 * @param num
	 *            身份证号码or手机号码
	 * @return string集合
	 */
	public List<String> getDbData(String sql, String num) {
		SQLiteDatabase db = helper.getReadableDatabase();
		List<String> data = new ArrayList<String>();
		Cursor c = db.rawQuery(sql, new String[] { num });
		if (num.length() == 18) {
			while (c.moveToNext()) {
				data.add(c.getString(c.getColumnIndex("sex")));
				data.add(c.getString(c.getColumnIndex("birthday")));
				data.add(c.getString(c.getColumnIndex("address")));
			}
		} else {
			while (c.moveToNext()) {
				data.add(c.getString(c.getColumnIndex("type")));
				data.add(c.getString(c.getColumnIndex("area")));
				data.add(c.getString(c.getColumnIndex("zip")));
			}
		}
		c.close();
		db.close();
		return data;
	}

	/**
	 * 省级数据
	 * 
	 * @return 省级String数组
	 */
	public String[] getProvinceData() {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("Select name From province", null);
		int count = c.getCount();
		String[] proStr = new String[count];
		int i = 0;
		// -1开始的下标
		while (c.moveToNext()) {
			proStr[i] = c.getString(c.getColumnIndex("name"));
			i++;
		}
		c.close();
		db.close();
		return proStr;
	}

	/**
	 * 得到省级相应的市级数据
	 * 
	 * @param provinceId
	 *            省级Id
	 * @return 市级String数组
	 */
	public String[] getDistrictData(String provinceId) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery(
				"Select name From district where provinceId = ?",
				new String[] { provinceId });
		int count = c.getCount();
		String[] disStr = new String[count];
		int i = 0;
		while (c.moveToNext()) {
			disStr[i] = c.getString(c.getColumnIndex("name"));
			i++;
		}
		c.close();
		db.close();
		return disStr;
	}

	/**
	 * 根据城市名得到相应的URL
	 * 
	 * @param districtName
	 *            城市名
	 * @return URL
	 */
	public String getDistrictUrl(String districtName) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String districtUrl = null;
		// 注入漏洞攻击是输入特殊字符蒙蔽数据库导致系统负载，这里的值是特定的不存在特殊字符~
		String sqlStr = "Select url From district Where name Like '%"
				+ districtName + "%' ";
		Cursor c = db.rawQuery(sqlStr, null);

		while (c.moveToNext()) {
			districtUrl = c.getString(c.getColumnIndex("url"));
		}
		c.close();
		db.close();
		return districtUrl;
	}

	/**
	 * 所有车牌省级数据
	 * 
	 * @return
	 */
	public String[] getPlateProData() {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("Select name From plate_province", null);
		int count = c.getCount();
		String[] proStr = new String[count];
		int i = 0;
		// -1开始的下标
		while (c.moveToNext()) {
			proStr[i] = c.getString(c.getColumnIndex("name"));
			i++;
		}
		c.close();
		db.close();
		return proStr;
	}

	/**
	 * 根据车牌的省级ID获得相应的车牌数据
	 * 
	 * @param plateId
	 * @return list包含了两个数据数组
	 */
	public List<String[]> getPlateData(String plateId) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery(
				"Select plate,area_name From plate_area where  plate_Id= ?",
				new String[] { plateId });
		int count = c.getCount();
		List<String[]> list = new ArrayList<String[]>();
		String[] plateStr = new String[count];
		String[] areaStr = new String[count];
		int i = 0;
		while (c.moveToNext()) {
			plateStr[i] = c.getString(c.getColumnIndex("plate"));
			areaStr[i] = c.getString(c.getColumnIndex("area_name"));
			i++;
		}
		c.close();
		db.close();
		list.add(plateStr);
		list.add(areaStr);
		return list;
	}

	public List<String> selectPlate(String str) {
		if(str.length() == 2){
			String substring = str.substring(1, 2);
			String upperCase = substring.toUpperCase();
			Log.e("upperCase", ""+upperCase);
			String substring2 = str.substring(0, 1);
			str = substring2+upperCase;
		}
		List<String> list = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		String area_name = null;
		String plate_Id = null;
		String sql = "Select plate_Id,area_name From plate_area where plate = ? ";
		Cursor cursor = db.rawQuery(sql, new String[]{str});
		while (cursor.moveToNext()) {
			area_name = cursor.getString(cursor.getColumnIndex("area_name"));
			plate_Id = cursor.getString(cursor.getColumnIndex("plate_Id"));
		}
		String sqlPro = "Select name From plate_province where id = ? ";
		cursor = db.rawQuery(sqlPro, new String[]{plate_Id});
		while (cursor.moveToNext()) {
			plate_Id = cursor.getString(cursor.getColumnIndex("name"));
		}
		cursor.close();
		db.close();
		list.add(area_name);
		list.add(plate_Id);
		return list;
	}
}
