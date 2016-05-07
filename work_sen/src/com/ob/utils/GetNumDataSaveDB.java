package com.ob.utils;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ob.dao.DBOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GetNumDataSaveDB {

	private Context context;
	private TextView view;
	private ProgressBar pb_circle;
	private DBOpenHelper helper;

	private String url;
	private boolean isNum;

	public GetNumDataSaveDB(Context context, TextView view,
			ProgressBar pb_circle) {
		this.context = context;
		this.view = view;
		this.pb_circle = pb_circle;
	}

	/**
	 * 开启异步，获取身份证号返回的信息,并把信息相应的存入数据库___ 做个url的判定 18位的话是身份证传身份证的url 反之则传手机号的url
	 * isNum 记录要做的操作 true是身份证操作，false是手机操作
	 * 
	 * @param userid
	 *            传递的字符串， idcard or phonenum
	 */
	public void getData(String userid) {
		if (userid.length() == 18) {
			isNum = true;
		} else {
			isNum = false;
		}

		Task task = new Task();
		if (isNum) {
			url = "http://qq.ip138.com/idsearch/index.asp?action=idcard&userid="
					+ userid + "&B1=%B2%E9+%D1%AF";
		} else {
			url = "http://www.ip138.com:8080/search.asp?action=mobile&mobile="
					+ userid;
		}
		task.execute(url, userid);
		// Log.e("userid","userid"+userid);
	}

	/**
	 * getIdData 获取数据并插入数据库的的异步线程
	 * 
	 * @author sen
	 */
	class Task extends AsyncTask<String, Integer, String> {

		// private Document doc;
		private String strAll;
		private String num;

		@Override
		protected String doInBackground(String... params) {
			// try {

			// Date startdate=new Date();
			// Log.e("Σ( ° △ °|||)︴","不会是线程卡太久吧Σ( ° △ °|||)︴");
			// doc=Jsoup.connect(params[0]).timeout(5000).get();
			strAll = new UtilClass().httpURLconn(params[0], context);
			num = params[1];
			// Date enddate=new Date();
			// Long time=enddate.getTime()-startdate.getTime();
			// Log.e("time",""+time);
			// } catch (Exception e) {
			// Toast.makeText(context, "请联系管理员："+e.toString(), 1).show();
			// }
			return strAll;
		}

		@Override
		protected void onPostExecute(String result) {
			// main_progress_bar.setVisibility(View.GONE);
			Elements select;
			if (result != null) {
				Document doc2 = Jsoup.parse(result);
				// 取得所有的table并拿到第三个table下的tr
				if (isNum) {
					select = doc2.select("table").eq(4).select("tr");
				} else {
					select = doc2.select("table").eq(1).select("tr");
				}
				List<String> list = new ArrayList<String>();
				for (Element element : select) {

					list.add(element.getElementsByTag("td").text());
				}
				if (isNum) {
					view.setVisibility(View.VISIBLE);
					view.setText(list.get(0) + "\n" + list.get(1) + "\n"
							+ list.get(3));
				} else {
					view.setVisibility(View.VISIBLE);
					if (list.get(5).length() > 10) {// 妈蛋154号现在还没公开，先写着，判断一下截取数
						Log.e("list2", "" + list.get(2));
						Log.e("list3", "" + list.get(3));
						Log.e("list4", "" + list.get(4));
						Log.e("list5", "" + list.get(5));
						view.setText(list.get(2) + "\n" + list.get(3) + "\n"
								+ list.get(4) + "\n"
								+ list.get(5).substring(0, 11));
					} else {
						view.setText("154移动号码暂未公开发售 \n" + list.get(2) + "\n"
								+ list.get(3) + "\n" + list.get(4) + "\n"
								+ list.get(5));
					}
				}

				helper = new DBOpenHelper(context, "sen.db", null, 1);
				SQLiteDatabase db = helper.getWritableDatabase();
				// 存入数据库的操作
				if (isNum) {
					db.execSQL(
							"Insert Into idcard(idcard,sex,birthday,address) Values(?,?,?,?)",
							new Object[] { num, list.get(0), list.get(1),
									list.get(3) });
					Log.e("插入成功", "id插入成功");
					db.close();
				} else {
					if (list.get(5).length() > 10) {
						db.execSQL(
								"Insert Into phone(phone,type,area,zip) Values(?,?,?,?)",
								new Object[] { num, list.get(3), list.get(4),
										list.get(5).substring(0, 11) });
					} else {
						db.execSQL(
								"Insert Into phone(phone,type,area,zip) Values(?,?,?,?)",
								new Object[] { num, list.get(3), list.get(4),
										list.get(5) });

					}
					Log.e("插入成功", "phone插入成功");
					db.close();
				}
				pb_circle.setVisibility(View.GONE);

			} else {
				pb_circle.setVisibility(View.GONE);
				Toast.makeText(context, "网络异常...", 0).show();
			}

		}

		@Override
		protected void onPreExecute() {
			pb_circle.setVisibility(View.VISIBLE);
		}
	}

	// public static String getPhoneData(String phoneNum){
	// String
	// url="http://www.ip138.com:8080/search.asp?action=mobile&mobile="+phoneNum;
	//
	// Document doc;
	// try {
	// doc=Jsoup.connect(url).timeout(5000).get();
	// Log.e("get", "doc:"+doc.toString());
	// // String html=doc.select("[bordercolorlight=#008000]").text();
	// // //title~
	// // String title=doc.select("#Title").text();
	// }catch(Exception e){
	//
	// }
	// return null;
	// }

}
