package com.ob.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBOpenHelper extends SQLiteOpenHelper {
	public DBOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 版本未更新执行的方法
		// 创建存放省级数据的表 _id自增长
		// db.execSQL("Create Table place(_id Integer Primary Key Autoincrement,name Varchar(50),url Varchar(50))");
		// //创建存放区级数据的表 _id自定义
		// db.execSQL("Create Table placequ(_id Integer Primary Key ,id Integer,quname Varchar(50),quurl Varchar(50))");

		// 存放查询过的身份证信息——做无流量查询与editText的提醒查询
		db.execSQL("Create Table idcard(idcard Integer Primary Key ,sex Varchar(10),birthday Varchar(50),address Varchar(50))");
		// 存放查询过的手机号信息——做无流量查询与editText的提醒查询
		db.execSQL("Create Table phone(phone Integer Primary Key ,type Varchar(10),area Varchar(50),zip Varchar(50))");
		// 省
		db.execSQL("Create Table province(id Integer Primary Key , name Varchar(50),url Varchar(30))");
		// 市
//		db.execSQL("Create Table city(id Integer Primary Key , name Varchar(50),url Varchar(30))");
		// 区
		db.execSQL("Create Table district(id Integer Primary Key Autoincrement,provinceId Integer , name Varchar(50),url Varchar(30))");
		//车牌省式 比如：湖南省（湘）
		db.execSQL("Create Table plate_province(id Integer Primary Key,name Varchar(50))");
		//二级数据  湘A——长沙市
		db.execSQL("Create Table plate_area(id Integer Primary Key Autoincrement,plate_Id Integer,plate Varchar(50),area_name Varchar(50))");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 版本更新执行的方法，可以用来达到修改数据库的效果
		// db.execSQL("Alter Table persen Add ? Integer");
//		Log.e("oldVersion+newVersion", ""+oldVersion+newVersion);
//		db.execSQL("Create Table plate_province(id Integer Primary Key,name Varchar(50))");
//		db.execSQL("Create Table plate_area(id Integer Primary Key,plate Varchar(50),area_name Varchar(50))");
			
	}

//	/**
//	 * 删除数据库
//	 * 
//	 * @param context
//	 * @return
//	 */
//	public boolean deleteDatabase() {
//		return context.deleteDatabase("sen.db");
//	}
	
	
}
