package com.ob.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ob.workmain.R;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class UtilClass {
	/**
	 * 打开连接获取网页信息
	 * 
	 * @param strUrl
	 *            连接地址
	 * @param context
	 * @return 返回html代码
	 * @throws Exception
	 */
	public String httpURLconn(String strUrl, Context context) {
		StringBuffer strBuf = null;
		ConnectivityManager manager;
		boolean flag = false;
		// 得到网络连接信息
		manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		Log.e("httpURLconn", "我进来了吧~");
		// 去进行判断网络是否连接
		if (manager.getActiveNetworkInfo() != null) {
//			Log.e("httpURLconn", "判断了么");
			flag = manager.getActiveNetworkInfo().isAvailable();
			Log.e("httpURLconn", ""+flag);
		}
		if (!flag) {
//			Log.e("httpURLconn", "没网络啊，返回null就完了，转个屁");
			return null;
		} else {
//			Log.e("httpURLconn", "这也有网？");
			URL url;
			try {
				url = new URL(strUrl);

				HttpURLConnection httpUrlCon = (HttpURLConnection) url
						.openConnection();
				InputStreamReader inRead = new InputStreamReader(
						httpUrlCon.getInputStream(), "GBK");
				BufferedReader bufRead = new BufferedReader(inRead);
				strBuf = new StringBuffer();
				String line = "";
				while ((line = bufRead.readLine()) != null) {
					strBuf.append(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("httpURLconn", ""+e.toString());
			}
			return strBuf.toString();
		}

	}

	/**
	 * 用权计算出身份证是否合法
	 * 
	 * @param ID
	 *            身份证号码
	 * @return true or false
	 */
	public boolean TestcardID(String ID) {
		// 判断输入的身份证是否有18位
		if (ID.length() == 18) {
			// 计算身份证最后一位是否正确！
			String Checkcard = "10X98765432";
			int[] Quan = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
			String Num17 = ID.substring(0, 17);
			String Num18 = ID.substring(17);
			int sum = 0;
			for (int i = 0; i < 17; i++)// 前17位身份证号与权依次相乘的总和
			{
				sum = sum + Integer.parseInt(Num17.substring(i, i + 1))
						* Quan[i];
			}
			int mod = sum % 11;// 取11的余数
			String result = Checkcard.substring(mod, mod + 1);// 对照校验码取得身份证最后一位！
			if (Num18.equalsIgnoreCase(result))// 需要忽略大小写的比较! x X
				return true;
			else
				return false;
		} else
			return false;

	}

	/**
	 * 检查输入是否为电话号码的格式
	 * 
	 * @param mobiles
	 *            手机号码
	 * @return true or false
	 */
	public boolean TestPhoneNum(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 获得屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	/**
	 * 格式化取得的日期数据
	 * 
	 * @param str
	 *            str[0]=2015-4-18 str[1]=星期六
	 * @return 数组 split2[0]==2015 split2[1]==4 split2[2]==18
	 */
	public String[] dateFormat(String[] str) {
		String string2 = str[0];
		String[] split = string2.split("-");// 继续分裂，取出单独的数字
		return split;
	}

	public String getWeatherStateStr(String state) {
		int length = state.length();
		String stateStr = null;
		if (length > 3 && length < 9) {// 带转字的两种天气 or 中到大雨转阴
			String[] split = state.split("转");
			stateStr = split[1];
		} else if (length > 9) {
			String[] split1 = state.split("转");
			int count = split1.length - 1;
			stateStr = split1[count];
		} else {
			stateStr = state;
		}
		return stateStr;
	}

	/**
	 * 天气状态文字转图片
	 * 
	 * @param state
	 * @return 图片资源ID
	 */
	public int getWeatherImgRes(String state) {
		int length = state.length();
		if (length > 3) {// 一些没把握的字符串，比如晴间多云
			state = state.substring(length - 1, length);
		}
		if (state.equals("晴")) {
			return R.drawable.weathericon_condition_01;
		} else if (state.equals("阴")) {
			return R.drawable.weathericon_condition_04;
		} else if (state.equals("多云") || state.equals("云")) {
			return R.drawable.weathericon_condition_03;
		} else if (state.equals("小雨") || state.equals("中雨")) {
			return R.drawable.weathericon_condition_07;
		} else if (state.equals("阵雨")) {
			return R.drawable.weathericon_condition_08;
		} else if (state.equals("大雨") || state.equals("暴雨")) {
			return R.drawable.weathericon_condition_09;
		} else if (state.equals("雷阵雨")) {
			return R.drawable.weathericon_condition_10;
		} else if (state.equals("浮尘")) {
			return R.drawable.weathericon_condition_06;
		} else if (state.equals("雨夹雪")) {
			return R.drawable.weathericon_condition_13;
		} else if (state.equals("小雪") || state.equals("中雪")) {
			return R.drawable.weathericon_condition_11;
		} else if (state.equals("大雪") || state.equals("暴雪")) {
			return R.drawable.weathericon_condition_12;
		} else if (state.equals("冰雹")) {
			return R.drawable.weathericon_condition_14;
		} else {// 意外的天气状态，没找到...
			Log.e("UtilClass", "" + state);
			return R.drawable.weathericon_condition_15;
		}

	}

	public int getWeatherBackgroundImgRes(String state) {
		if (state.equals("晴")) {
			return R.drawable.app_bg02;
		} else if (state.equals("多云")) {
			return R.drawable.app_bg01;
		} else if (state.equals("小雨") || state.equals("中雨")
				|| state.equals("大雨") || state.equals("阵雨")
				|| state.equals("雷阵雨") || state.equals("暴雨")
				|| state.equals("浮尘") || state.equals("轻度霾")
				|| state.equals("重度霾") || state.equals("雾霾")
				|| state.equals("阴")) {
			return R.drawable.app_bg03;
		} else if (state.equals("雨夾雪") || state.equals("小雪")
				|| state.equals("中雪") || state.equals("大雪")) {
			return R.drawable.app_bg04;
		} else {
			return R.drawable.app_bg03;
		}

	}

}
