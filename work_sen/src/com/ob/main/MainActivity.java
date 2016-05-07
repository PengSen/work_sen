package com.ob.main;

import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.ob.custom.view.CircleImageView;
import com.ob.custom.view.CircleLayout;
import com.ob.custom.view.CircleLayout.OnItemClickListener;
import com.ob.custom.view.CircleLayout.OnItemSelectedListener;
import com.ob.dao.CopyDbToSdcard;
import com.ob.dao.SqlHelper;
import com.ob.utils.GetAreaDataSaveDB;
import com.ob.utils.GetNumDataSaveDB;
import com.ob.utils.UtilClass;
import com.ob.workmain.R;

/**
 * 导入数据位置选择 即把城市数据以及url录入数据库，考虑把旅游景区天气，城市对应的车牌录入~
 * url:http://www.ip138.com/carlist.htm
 * 
 * @author 森
 */
public class MainActivity extends Activity implements OnWheelChangedListener,
		OnClickListener, OnItemSelectedListener, OnItemClickListener,
		AMapLocationListener, Runnable, OnTouchListener {

	private static String tag = "MainActivity";
	private ConnectivityManager manager;// 检查网络连通性

	private EditText main_edit_number;
	private ImageView main_iv_search;

	private TextView main_tv_show_information;
	private TextView main_tv_zipcode;

	private ProgressBar main_pb_circle;
	private RelativeLayout main_rl_all_view;// 总布局
	/**
	 * 圆环滑动布局
	 */
	private CircleLayout main_circle_layout;
	/**
	 * 定位管理代理
	 */
	private LocationManagerProxy mLocationManagerProxy;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private String cityName;// 定位得到的地区name

	private LinearLayout main_ll_area;
	private WheelView wheel_province;
	private WheelView wheel_district;
	private LinearLayout main_ll_plate;
	private WheelView wheel_plate_province;
	private WheelView wheel_plate;
	private WheelView wheel_area;
	/**
	 * 所有省
	 */
	private String[] mProvinceDatas;
	/**
	 * 省级下的所有地区数据
	 */
	private String[] mDistrictDatas;
	/**
	 * 所有车牌的省级数据
	 */
	private String[] mPlateProDatas;

	/**
	 * 记录手指按下时的纵坐标。
	 */
	private float yDown;
	/**
	 * 记录手机抬起时的纵坐标。
	 */
	private float yUp;
	/**
	 * 用于计算手指滑动的速度。
	 */
	private VelocityTracker mVelocityTracker;
	/**
	 * 滚动显示和隐藏menu时，手指滑动需要达到的速度。
	 */
	public static final int SNAP_VELOCITY = 200;
	/**
	 * 下载数据操作时的dialog
	 */
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		//不同手机测试相关
//		 TextView main_tv_screen_parameter = (TextView) findViewById(R.id.main_tv_screen_parameter);
//		int screenHeight = UtilClass.getScreenHeight(this);
//		int screenWidth = UtilClass.getScreenWidth(this);
//
//		SimpleDateFormat formatter = new SimpleDateFormat(
//				"yyyy年MM月dd日    HH:mm:ss     ");
//		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
//		String str = formatter.format(curDate);
//
//		Boolean dbFile = new CopyDbToSdcard(this).isDbFile();
//		// 手机型号
//		String mobilePhoneModel = android.os.Build.MODEL;
//		// 系统版本
//		String systemVersion = android.os.Build.VERSION.RELEASE;
//
//		main_tv_screen_parameter.setText("运行时间：" + str + "\n屏幕宽*高："
//				+ screenWidth + "*" + screenHeight + "\n有无sdcard:" + dbFile
//				+ "\n手机型号:" + mobilePhoneModel
//				+ "\n系统版本:" + systemVersion); 

		init();
		initMenu();
		// initLocation();//数据得全之后再定位，可以冲掉数据没全下拉弹出其它界面的操作
	}

	/**
	 * 相关view的初始化和is本地数据操作
	 */
	private void init() {
		main_rl_all_view = (RelativeLayout) findViewById(R.id.main_rl_all_view);
		main_edit_number = (EditText) findViewById(R.id.main_edit_number);
		main_iv_search = (ImageView) findViewById(R.id.main_iv_search);
		main_circle_layout = (CircleLayout) findViewById(R.id.main_circle_layout);
		main_tv_show_information = (TextView) findViewById(R.id.main_tv_show_information);
		main_tv_zipcode = (TextView) findViewById(R.id.main_tv_zipcode);
		main_pb_circle = (ProgressBar) findViewById(R.id.main_pb_circle);
		main_ll_area = (LinearLayout) findViewById(R.id.main_ll_area);
		wheel_province = (WheelView) findViewById(R.id.wheel_province);
		wheel_district = (WheelView) findViewById(R.id.wheel_district);
		main_ll_plate = (LinearLayout) findViewById(R.id.main_ll_plate);
		wheel_plate_province = (WheelView) findViewById(R.id.wheel_plate_province);
		wheel_plate = (WheelView) findViewById(R.id.wheel_plate);
		wheel_area = (WheelView) findViewById(R.id.wheel_area);
		main_iv_search.setOnClickListener(this);
		// 滑动改变的监听
		wheel_province.addChangingListener(this);
		wheel_district.addChangingListener(this);
		main_rl_all_view.setOnTouchListener(this);
		wheel_plate_province.addChangingListener(this);
		wheel_plate.addChangingListener(this);
		wheel_area.addChangingListener(this);

		if (!new CopyDbToSdcard(this).isDbFile()) {// 查看有没有数据库文件
			Log.e(tag, "没有数据库");
			showCustomMessage("首次使用提示", "下载初始数据——耗时\n拷贝初始数据——不耗时");

		} else {// 有文件
			String sql = "Select provinceId From district Where provinceId = ?";
			Boolean isDB = new SqlHelper(this).isSelectDbInfomation(sql, "33");
			if (isDB) {// 有文件有数据
				initLocation();
			} else {// 有文件没数据
					// 删除文件重新创建
				CopyDbToSdcard copyDbFile = new CopyDbToSdcard(this);
				Boolean deleteDbFile = copyDbFile.deleteDbFile();
				if (deleteDbFile) {
					copyDbFile.copyDatabase();
				}
			}
		}

	}

	/**
	 * 初始化旋转菜单
	 */
	private void initMenu() {
		main_circle_layout.setOnItemSelectedListener(this);
		main_circle_layout.setOnItemClickListener(this);
		main_tv_show_information.setText(((CircleImageView) main_circle_layout
				.getSelectedItem()).getName());
		LayoutParams lp;
		lp = main_circle_layout.getLayoutParams();
		lp.height = (int) (UtilClass.getScreenHeight(this) * 0.5);
		main_circle_layout.setLayoutParams(lp);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "更新数据");
		menu.add(0, 2, 0, "关于作者");
		/*
		 * 1.菜单分组 2.菜单序号 3.订单？ 4.显示文本
		 */
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		if (id == 1) {
			Boolean deleteDbFile = new CopyDbToSdcard(this).deleteDbFile();
			if(deleteDbFile){
				checkNetworkState();
			}

		} else if (id == 2) {
			// 关于界面
			// intent.putExtra("id", 2);
			Intent intent = new Intent(MainActivity.this, MeActivity.class);
			startActivity(intent);
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onClick(View view) {
		// 搜索操作 分id和phone以及湘A类车牌三种~
		String str = main_edit_number.getText().toString();
		if (new UtilClass().TestcardID(str)) {
			// 强制隐藏键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			// 异步抓取网页数据并做存入数据库操作 ！so，抓取时要看数据库中有没有存！
			String sql = "Select idcard From idcard where idcard = ?";
			if (!new SqlHelper(this).isSelectDbInfomation(sql, str)) {// 没查到数据，访问网络获取数据并写入数据库
				// Log.e("MainActivity！", "我到这里了么！");
				GetNumDataSaveDB sdbId = new GetNumDataSaveDB(this,
						main_tv_show_information, main_pb_circle);
				sdbId.getData(str);
			} else {// 查到数据则直接用数据库的数据
				String sqlData = "Select sex,birthday,address From idcard where idcard = ?";
				List<String> dbData = new SqlHelper(this).getDbData(sqlData,
						str);
				main_tv_show_information.setVisibility(View.VISIBLE);
				main_tv_show_information.setText("" + dbData.get(0) + "\n"
						+ dbData.get(1) + "\n" + dbData.get(2));
			}
			// 根据输入的手机号码从数据库中抽取数据 so，抓取时要看数据库中有没有存！
		} else if (new UtilClass().TestPhoneNum(str)) {
			// 强制隐藏键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			String sql = "Select phone From phone Where phone = ?";
			if (!new SqlHelper(this).isSelectDbInfomation(sql, str)) {
				GetNumDataSaveDB sdbPhone = new GetNumDataSaveDB(this,
						main_tv_show_information, main_pb_circle);
				sdbPhone.getData(str);
			} else {
				String sqlData = "Select type,area,zip From phone Where phone = ?";
				List<String> dbData = new SqlHelper(this).getDbData(sqlData,
						str);
				main_tv_show_information.setVisibility(View.VISIBLE);
				main_tv_show_information.setText("" + dbData.get(0) + "\n"
						+ dbData.get(1) + "\n" + dbData.get(2));
			}
		} else if (new SqlHelper(this).SelectDbInfomation(
				"Select plate From plate_area Where plate = ?", str)) {
			List<String> selectPlate = new SqlHelper(this).selectPlate(str);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			main_tv_show_information.setText(selectPlate.get(1) + "\n"
					+ selectPlate.get(0));

		} else {
			Toast.makeText(this, "请确认输入数据格式是否正确~，Σ( ° △ °|||)︴", 0).show();
		}
	}

	@Override
	public void onItemSelected(View view, int position, long id, String name) {
		main_tv_show_information.setText(name);
		main_ll_area.setVisibility(View.GONE);
		main_ll_plate.setVisibility(View.GONE);
		main_edit_number.setVisibility(View.INVISIBLE);
		main_iv_search.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onItemClick(View view, int position, long id, String name) {
		Animation anim_id = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.show_edit_translate);
		switch (view.getId()) {
		case R.id.circle_id:
			main_edit_number
					.setWidth((int) (UtilClass.getScreenWidth(this) / 1.5));
			main_edit_number
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							18) });
			main_edit_number.setHint("身份证号码查询验证...");

			main_edit_number.startAnimation(anim_id);
			main_iv_search.startAnimation(anim_id);
			// anim_id.setFillAfter(fillAfter)
			main_edit_number.setVisibility(View.VISIBLE);
			main_iv_search.setVisibility(View.VISIBLE);
			// edit的键盘格式
			main_edit_number.setInputType(InputType.TYPE_MASK_CLASS);
			// 清理edit
			main_edit_number.setText("");
			// 强制弹出键盘
			main_edit_number.requestFocus();
			InputMethodManager imm_id = (InputMethodManager) main_edit_number
					.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm_id.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
			break;
		case R.id.circle_phone:
			main_edit_number.setWidth(UtilClass.getScreenWidth(this) / 2);
			main_edit_number
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							11) });
			main_edit_number.setHint("手机号码归属地查询...");

			main_edit_number.startAnimation(anim_id);
			main_iv_search.startAnimation(anim_id);

			main_edit_number.setVisibility(View.VISIBLE);
			main_iv_search.setVisibility(View.VISIBLE);
			// edit的键盘格式
			main_edit_number.setInputType(InputType.TYPE_CLASS_PHONE);
			// 清理edit
			main_edit_number.setText("");
			// 强制弹出键盘
			main_edit_number.requestFocus();
			InputMethodManager imm_phone = (InputMethodManager) main_edit_number
					.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm_phone.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			break;
		case R.id.circle_plate:
			main_edit_number
					.setWidth((int) (UtilClass.getScreenWidth(this) / 2.2));
			main_edit_number.setHint("地域识别：湘A");
			// 清理edit
			main_edit_number.setText("");

			main_edit_number.startAnimation(anim_id);
			main_iv_search.startAnimation(anim_id);
			main_edit_number.setInputType(InputType.TYPE_MASK_CLASS);
			main_edit_number.setVisibility(View.VISIBLE);
			main_iv_search.setVisibility(View.VISIBLE);
			// Log.e("", "车牌查询");
			mPlateProDatas = new SqlHelper(this).getPlateProData();
			initAreaSelect(1);
			break;
		case R.id.circle_weather:
			// 天气查询
			mProvinceDatas = new SqlHelper(this).getProvinceData();
			initAreaSelect(0);

			break;

		default:
			break;
		}
	}

	/**
	 * 初始化城市选择器
	 */
	private void initAreaSelect(int state) {
		Animation animation = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.wheel_move_in);
		if (state == 0) {// 地区选择的wheel
			main_ll_area.startAnimation(animation);
			main_ll_area.setVisibility(View.VISIBLE);
			// 地区选择时的双击监听
			wheel_district.setOnTouchListener(new onDoubleClickListener());
			wheel_province.setViewAdapter(new ArrayWheelAdapter<String>(
					MainActivity.this, mProvinceDatas));
			// 设置显示的最大条数
			wheel_province.setVisibleItems(7);
			wheel_district.setVisibleItems(7);
			updateDistricts();
		} else {
			main_ll_plate.startAnimation(animation);
			main_ll_plate.setVisibility(View.VISIBLE);
			// 展示车牌号的wheel
			wheel_plate_province.setViewAdapter(new ArrayWheelAdapter<String>(
					MainActivity.this, mPlateProDatas));
			// Log.e("mPlateProDatas", "" + mPlateProDatas[0]);
			// 设置显示的最大条数
			wheel_plate_province.setVisibleItems(7);
			wheel_plate.setVisibleItems(7);
			wheel_area.setVisibleItems(7);
			updatePlates();
		}
	}

	/**
	 * 根据当前省更新市一级数据
	 */
	private void updateDistricts() {
		int provinceId = wheel_province.getCurrentItem();
		mDistrictDatas = new SqlHelper(this).getDistrictData(String
				.valueOf(provinceId));
		// 设置适配器，与province数组数据相对应的数据即可~
		wheel_district.setViewAdapter(new ArrayWheelAdapter<String>(this,
				mDistrictDatas));
		wheel_district.setCurrentItem(0);
	}

	private void updatePlates() {
		int currentItem = wheel_plate_province.getCurrentItem();
		// 从数据库得到数据放入wheel控件~
		List<String[]> plateData = new SqlHelper(this).getPlateData(String
				.valueOf(currentItem));
		wheel_plate.setViewAdapter(new ArrayWheelAdapter<String>(this,
				plateData.get(0)));
		wheel_area.setViewAdapter(new ArrayWheelAdapter<String>(this, plateData
				.get(1)));
		wheel_plate.setCurrentItem(0);
		wheel_area.setCurrentItem(0);
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (wheel == wheel_province) {
			// 根据省数据改变市数据
			updateDistricts();
		} else if (wheel == wheel_plate_province) {
			updatePlates();
		} else if (wheel == wheel_plate) {
			int index = wheel_plate.getCurrentItem();
			wheel_area.setCurrentItem(index);
		} else if (wheel == wheel_area) {
			int index = wheel_area.getCurrentItem();
			wheel_plate.setCurrentItem(index);
		}
	}

	/**
	 * 双击选中的监听
	 *
	 * @author sen
	 */
	class onDoubleClickListener implements OnTouchListener {
		int count = 0;
		int firClick = 0;
		int secClick = 0;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				count++;
				if (count == 1) {
					firClick = (int) System.currentTimeMillis();
				} else if (count == 2) {
					secClick = (int) System.currentTimeMillis();
					if (secClick - firClick < 1000) {// 第二次点击的间隔小于1s
						int provinceItem = wheel_province.getCurrentItem();
						if (provinceItem != 34) {// 非旅游地区的天气
							// 双击把区级名称传到天气界面
							int currentItem = wheel_district.getCurrentItem();
							String districtStr = mDistrictDatas[currentItem];
							Intent intent = new Intent(getApplicationContext(),
									WeatherShowActivity.class);
							intent.putExtra("districtName", districtStr);
							startActivity(intent);
						} else {// 旅游景点的天气
							Log.e("main", "旅游景点的天气等功能完善了做啦。");
						}
					}
					count = 0;
					firClick = 0;
					secClick = 0;
				}
			}
			// 返回true则消耗掉其他触摸之类的点击事件 详见郭神or洋神的事件分发机制
			return false;
		}
	}

	/**
	 * 检测网络是否连接
	 * 
	 * @return
	 */
	private boolean checkNetworkState() {
		boolean flag = false;
		// 得到网络连接信息
		manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// 去进行判断网络是否连接
		if (manager.getActiveNetworkInfo() != null) {
			flag = manager.getActiveNetworkInfo().isAvailable();
		}
		if (!flag) {
			Toast.makeText(this, "网络异常，copy初始数据~~", Toast.LENGTH_SHORT).show();
			Boolean copyDatabase = new CopyDbToSdcard(getApplicationContext())
					.copyDatabase();
			copyFileOrLoad(copyDatabase);
		} else {
			isNetworkAvailable();
		}

		return flag;
	}

	/**
	 * 网络已经连接，然后去判断是wifi连接还是GPRS连接 设置逻辑调用
	 */
	private void isNetworkAvailable() {

		State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		// GPRS，流量状态下，拷贝数据库
		if (gprs == State.CONNECTED || gprs == State.CONNECTING) {
			Toast.makeText(this, "gprs（流量）状态下，copy初始数据~~", Toast.LENGTH_SHORT)
					.show();
			Boolean copyDatabase = new CopyDbToSdcard(getApplicationContext())
					.copyDatabase();
			copyFileOrLoad(copyDatabase);
		}
		// 判断为wifi状态下才加载地区数据，否则不加载
		if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
			// 没有数据的时候初始化一个dialog做初始化操作用
			dialog = new ProgressDialog(MainActivity.this);
			// 设置进度条风格，风格为圆形，旋转的
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// 设置ProgressDialog 提示信息
			dialog.setMessage("正在初始化数据，请稍候...");
			// 设置ProgressDialog 标题图标
			// m_pDialog.setIcon(R.drawable.icon);
			// 设置ProgressDialog 的进度条是否不明确
			dialog.setIndeterminate(false);
			// 设置ProgressDialog 是否可以按退回按键取消
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			Toast.makeText(getApplicationContext(), "Wifi状态下，数据录入准备~请稍等...",
					Toast.LENGTH_LONG).show();
			new GetAreaDataSaveDB(getApplicationContext(),
					MainActivity.this.dialog);
			initLocation();
		}
	}

	/**
	 * 数据库拷贝状态的结果
	 * 
	 * @param is
	 *            失败or成功
	 */
	public void copyFileOrLoad(Boolean is) {
		if (is) {
			// Toast.makeText(getApplicationContext(), "非wifi状态下,数据库拷贝成功",
			// Toast.LENGTH_SHORT).show();
			initLocation();
		} else {// 数据库拷贝失败,没有检测到sdcard——最后一层额..
			// Toast.makeText(getApplicationContext(), "数据库拷贝失败",
			// Toast.LENGTH_LONG).show();
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage("检查sd卡或者下载固定数据");
			builder.setTitle("拷贝失败");
			builder.setNegativeButton("检查",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							finish();
						}
					});
			builder.setPositiveButton("下载",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							MainActivity.this.dialog.show();
							new GetAreaDataSaveDB(getApplicationContext(),
									MainActivity.this.dialog);
						}
					});
			builder.create().show();
		}
	}

	/**
	 * 初始化定位相关
	 */
	public void initLocation() {
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
		// 在定位结束后，在合适的生命周期调用destroy()方法
		// 其中如果间隔时间为-1，则定位只定一次
//		 mLocationManagerProxy.requestLocationData(
//		 LocationManagerProxy.NETWORK_PROVIDER, -1, 15, this);
		// 注册定位监听
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, -1, 15, this);
		mLocationManagerProxy.setGpsEnable(false);

	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null && location.getAMapException().getErrorCode() == 0) {
			this.aMapLocation = location;// 判断超时机制
			cityName = location.getCity();
			String cityCode = location.getCityCode();
			main_tv_zipcode.setText(cityName + "\n" + "电话区号" + cityCode);
		} else {
			Log.e("code", ""+location.getAMapException().getErrorCode());
			Log.e("location", ""+(location == null));
			main_tv_zipcode.setText("定位失败...");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopLocation();// 停止定位
		Log.e("onPause", "onPause");
	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (mLocationManagerProxy != null) {
			mLocationManagerProxy.removeUpdates(this);
			mLocationManagerProxy.destroy();
			;
		}
		mLocationManagerProxy = null;
	}

	@Override
	public void run() {
		if (aMapLocation == null) {
			main_tv_zipcode.setText("定位失败....");
			stopLocation();// 销毁掉定位
		}
	}

	// 只能监听down,需要move和up的话 1.return true，view的xml加longClickable=true
	// 看情况来~
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		createVelocityTracker(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 记录手指按下的纵坐标
			yDown = event.getRawY();
			// Log.e("yDown", "" + yDown);
			break;
		// case MotionEvent.ACTION_MOVE:
		// yMove = event.getRawY();
		// // Log.e("yMove", ""+yMove);
		// break;
		case MotionEvent.ACTION_UP:
			// 记录手指抬起的纵坐标
			yUp = event.getRawY();
			// Log.e("yUp", "" + yUp);
			if (moveWeatherActivity()) {
				// Log.e("cityName", ""+cityName);
				if (cityName != null && !cityName.equals("")) {// 定位的城市要有啊亲
					String replace = cityName.replace("市", "");
					Intent intent = new Intent(getApplicationContext(),
							WeatherShowActivity.class);
					intent.putExtra("districtName", replace);
					startActivity(intent);
					// 第一个是参数是离开时动画
					// 只能 startActivity或者finish后调用
					overridePendingTransition(R.anim.weather_activity_move_in,
							R.anim.weather_activity_move_out);
				} else {
					Toast.makeText(getApplicationContext(), "定位失败不支持此功能~",
							Toast.LENGTH_SHORT).show();
				}
			}
			recycleVelocityTracker();
			break;
		}
		return false;
	}

	/**
	 * 判断是否应该从下移动天气界面展示出来。如果手指移动距离大于200，或者手指移动速度大于SNAP_VELOCITY，
	 * 就认为应该从下移动天气界面展示出来。
	 * 
	 * @return 如果应该从下移动天气界面展示出来返回true，否则返回false。
	 */
	private boolean moveWeatherActivity() {
		return yDown - yUp > 200 || getScrollVelocity() > SNAP_VELOCITY;
	}

	/**
	 * 获取手指在主界面滑动的速度。
	 * 
	 * @return 滑动速度，以每秒钟移动了多少像素值为单位。
	 */
	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getXVelocity();
		return Math.abs(velocity);
	}

	/**
	 * 创建VelocityTracker对象，并将触摸事件加入到VelocityTracker当中。
	 * 
	 * @param event
	 *            右侧布局监听控件的滑动事件
	 */
	private void createVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}

	/**
	 * 回收VelocityTracker对象。
	 */
	private void recycleVelocityTracker() {
		mVelocityTracker.recycle();
		mVelocityTracker = null;
	}

	private void showCustomMessage(String pTitle, final String pMsg) {
		final Dialog iDialog = new Dialog(MainActivity.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		iDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		iDialog.setContentView(R.layout.dialog_view);
		((TextView) iDialog.findViewById(R.id.dialog_title)).setText(pTitle);
		((TextView) iDialog.findViewById(R.id.dialog_message)).setText(pMsg);

		((Button) iDialog.findViewById(R.id.dialog_download))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						iDialog.dismiss();

						// 网络状态判断
						checkNetworkState();
					}
				});
		((Button) iDialog.findViewById(R.id.dialog_copy))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						iDialog.dismiss();
						Boolean copyDatabase = new CopyDbToSdcard(
								getApplicationContext()).copyDatabase();
						copyFileOrLoad(copyDatabase);
					}
				});
		iDialog.show();
	}

	private long exitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果按下的是返回键，并且没有重复
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (main_ll_area.getVisibility() == 0
					|| main_ll_plate.getVisibility() == 0) {// 可见状态
				Animation anima = AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.wheel_move_out);
				main_ll_area.startAnimation(anima);
				main_ll_area.setVisibility(View.GONE);
				main_ll_plate.startAnimation(anima);
				main_ll_plate.setVisibility(View.GONE);
			} else {
				if ((System.currentTimeMillis() - exitTime) > 2000) {
					Toast.makeText(MainActivity.this, "再次按返回键退出~",
							Toast.LENGTH_SHORT).show();
					exitTime = System.currentTimeMillis();
				} else {
					finish();
				}
			}
		}
		return false;
	}
}
