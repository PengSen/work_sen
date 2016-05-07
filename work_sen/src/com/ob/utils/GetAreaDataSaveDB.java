package com.ob.utils;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.ob.dao.DBOpenHelper;

/**
 * 全国各地省市区的车牌数据  and url
 * 插入本地数据库操作
 * 
 * @author sen
 */
public class GetAreaDataSaveDB {

	private Context context;
	// private ProgressBar pb_circle;
	private DBOpenHelper helper;
	private SQLiteDatabase db;
	private final String weatherUrl = "http://qq.ip138.com/weather/";
	private final String Url = "http://qq.ip138.com";
	private final String plateUrl = "http://www.ip138.com/carlist.htm";
	private SparseArray<String> urlMap;// 省级url本地保存，用来抽出需要的市级url部分

	private int num = 0;// 用来做省级数据和地区数据的匹配，比如即可查district表下的0 安徽数据
	private int progress = -2;// 做%比的数据记录
	private int j = 1;
	private int num_plate = 20;
	private int plate_id = -1;
	
	private ProgressDialog dialog;

	public GetAreaDataSaveDB(Context context, ProgressDialog dialog) {
		this.context = context;
		this.dialog = dialog;
		helper = new DBOpenHelper(context,"sen.db",null,1);
		db = helper.getWritableDatabase();
		new GetPlateData().execute(plateUrl);//开启车牌录入，
	}
	
//	public void savaPlateData(){
//		
//	}
//	public void savaAreaData(){
//		
//	}

	/**
	 * 获取province的数据和url并保存入本地数据库
	 * 
	 * @author sen
	 */
	class GetProvinceTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String httpURLconn = new UtilClass().httpURLconn(params[0],context);

			return httpURLconn;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				Document parse = Jsoup.parse(result);
				Elements select = parse.select("#ProvList").select("table")
						.select("tr");
				Elements select2 = select.select("a[href]");
				urlMap = new SparseArray<String>();
				int size = select2.size();
				for (int i = 0; i < size; i++) {
					String province = select2.get(i).text();
					String url = select2.get(i).attr("href");
					// Log.e("",""+attr);
					// provinceMap.put(i,text);
					urlMap.put(i, url);
					db.execSQL(
							"Insert Into province(id,name,url) Values(?,?,?)",
							new String[] { String.valueOf(i), province,
									Url + url });
				}
				Toast.makeText(context, "数据开始录入~请稍等", 0).show();

				getCityUrl();

			} else {
				Toast.makeText(context, "省级数据获取失败，检查网络是否正常连接或联系管理员~", 1).show();
			}
		}
	}

	/**
	 * 开启地区级数据获取
	 */
	private void getCityUrl() {
		String string = urlMap.get(0);
		String cityUrl = Url + string;
		new DistrictTask().execute(cityUrl);

	}

	class DistrictTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
				String httpURLconn = new UtilClass().httpURLconn(params[0],context);
			return httpURLconn;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				Document parse = Jsoup.parse(result);
				new SavaAreaDataToDb().execute(parse);
			} else {
				Toast.makeText(context, "地区数据获取失败，检查网络是否正常连接或联系管理员~", 1).show();
			}
		}
	}

	class SavaAreaDataToDb extends AsyncTask<Document, Void, Void>{

		@Override
		protected Void doInBackground(Document... params) {
			Document parse = params[0];
			Elements select = parse.select("#CityList").select("table")
					.select("tr");
			Elements select2 = select.select("a[href]");
			int size = select2.size();
			for (int i = 0; i < size; i++) {
				String district = select2.get(i).text();
				String url = select2.get(i).attr("href");
				db.execSQL(
						"Insert Into district(provinceId,name,url) Values (?,?,?)",
						new String[] { String.valueOf(num), district,
								Url + url });
			}
			num++;
			progress += 3;
			if (progress == 100) {
				dialog.dismiss();
			}
			String string = urlMap.get(j);
			if (string != null) {
				if (!string.equals("trip.htm")) {// 判断下旅游景点的url表示就不用开异步了
					String cityUrl = Url + string;
					new DistrictTask().execute(cityUrl);
					j++;
				} else {
					Log.e("getArea", "旅游景点之后做啦~");
				}
			} else {
				//天气省级数据没     旅游景点     一栏
				helper.close();
				db.close();
				Log.e("getArea", "先关闭数据库相关咯,旅游景点那一栏再说又没了我艹~");
			}
			
			return null;
		}
//		@Override
//		protected void onPostExecute(Void result) {
//			if(result == null){
//				Toast.makeText(context, "Thx~", 0).show();
//			}
//		}
	}
	
	class GetPlateData extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			String httpURLconn = new UtilClass().httpURLconn(params[0],context);

			return httpURLconn;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				Document parse = Jsoup.parse(result);
				
				new SavaPlateDataToDb().execute(parse);
				
				new GetProvinceTask().execute(weatherUrl);//录入地区url数据
			} else {
				Toast.makeText(context, "车牌数据获取失败，请联系管理员！", Toast.LENGTH_LONG)
						.show();
			}
		}
	}
	class SavaPlateDataToDb extends AsyncTask<Document, Void, Void>{

		@Override
		protected Void doInBackground(Document... params) {
			getData(params[0]);
			return null;
		}
//		@Override
//		protected void onPostExecute(Void result) {
//			if(result == null){
//				Toast.makeText(context, "Thx~", 0).show();
//			}
//		}
	}
	
	public void getData(Document parse) {
		Elements eq = parse.select("Table").eq(2);
		int o = 0;// o b 用来控制取td数据对
		int b = 1;
		for (int i = 0; i < 3; i++) {
			plate_id++;
			String text = eq.select("th").eq(i).text();
			db.execSQL("Insert Into plate_province(id,name) Values (?,?)",
					new String[] { String.valueOf(plate_id), text });
			Log.e("text", i + "   " + text);
			int l = 2; 
			while (true) {
				Elements eq2 = eq.select("tr").eq(l);
				String string = eq2.html();
				String substring = string.substring(1, 3);
				if (!substring.equals("th")) {
					Elements select = eq2.select("td");
					String text1 = select.eq(o).html();
					if (!text1.equals("&nbsp;")) {
						Log.e("text1", i + "   " + text1);
						String text2 = select.eq(b).text();
						Log.e("text2", i + "   " + text2);
						db.execSQL(
								"Insert Into plate_area(plate_Id,plate,area_name) Values (?,?,?)",
								new String[] { String.valueOf(plate_id), text1, text2 });
					} else
						break;
				} else {
					num_plate = l;
					break;
				}
				l++;
			}
			o += 2;
			b += 2;
		}

		for (int k = 0; k < 10; k++) {
			int count = num_plate;// 到了多少个tr了
			int z = 0;
			int x = 1;
			Log.e("k", "" + k);
			for (int i = 0; i < 3; i++) {
				plate_id++;
				Log.e("province_id", "" + plate_id);
				Elements eq3 = eq.select("tr").eq(count).select("th").eq(i);
				String text = eq3.text();
				db.execSQL("Insert Into plate_province(id,name) Values (?,?)",
						new String[] { String.valueOf(plate_id), text });
				Log.e("text", "" + text);
				String text10 = eq3.html();
				if (!text10.equals("&nbsp;")) {
					int l = count;
					while (true) {
						Elements eq2 = eq.select("tr").eq(l + 1);
						String string = eq2.html();
						// Log.e("string", ""+string);
						String substring = string.substring(1, 3);
						if (!substring.equals("th")) {
							Elements select = eq2.select("td");
							String text11 = select.eq(z).html();
							if (!text11.equals("&nbsp;")) {
								Log.e("text1", "" + text11);
								String text22 = select.eq(x).text();
								Log.e("text2", "" + text22);
								db.execSQL(
										"Insert Into plate_area(plate_Id,plate,area_name) Values (?,?,?)",
										new String[] { String.valueOf(plate_id), text11, text22 });
							} else
								break;
						} else {
							Log.e("l", "" + l);
							// 第五次循环的时候会多加11~即k==5的时候会进来两次
							if (!(k == 5)) {
								num_plate += l - count + 1;
							} else {
								num_plate = 113;// +毛啊，直接赋值113不就好了！
							}
							Log.e("num_plate", "" + num_plate);
							break;
						}
						l++;
						if (l == 164) {// 等于164代表上海录完了，即无数据了，那么可以跳出去了
							break;
						}
					}
				} else {
					Log.e("", "退出大循环");
					break;
				}
				z += 2;
				x += 2;
			}
		}

//		helper.close();
//		db.close();
	}
}
