package com.ob.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ob.dao.SqlHelper;
import com.ob.fragment.WeatherFragment0;
import com.ob.fragment.WeatherFragment1;
import com.ob.fragment.WeatherFragment2;
import com.ob.fragment.WeatherFragment3;
import com.ob.fragment.WeatherFragment4;
import com.ob.fragment.WeatherFragment5;
import com.ob.fragment.WeatherFragment6;
import com.ob.utils.UtilClass;
import com.ob.workmain.R;

/**
 * 天气预报的主界面 考虑左滑右滑...
 * 
 * @author sen
 */
public class WeatherShowActivity extends FragmentActivity implements OnTouchListener{

	/**
	 * 记录所有正在下载或者等待下载的任务
	 */
	private Set<GetDistrictData> taskCollection;
	/**
	 * 从主界面接收到的城市名
	 */
	private String districtName;
	/**
	 * 根据城市名模糊检索数据库得到的url
	 */
	private String districtUrl;
	/**
	 * 记录手指按下时的纵坐标。
	 */
	private float yDown;
	/**
	 * 记录手机抬起时的纵坐标。
	 */
	/**
	 * 用于计算手指滑动的速度。
	 */
	private VelocityTracker mVelocityTracker;
	/**
	 * 滚动显示和隐藏menu时，手指滑动需要达到的速度。
	 */
	public static final int SNAP_VELOCITY = 200;
	private float yUp;
//	private TextView weather_tv_address;
	private TextView weather_tv_areacode;
	private TextView weather_tv_zipcode;
	private ProgressBar weather_pb_circle;
	private RelativeLayout weather_rl_all_view;
	
	private List<SparseArray<String>> list = new ArrayList<SparseArray<String>>();;
//	private DisplayMetrics dm;
//	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	
	private WeatherFragment0 weatherFragment0;
	private WeatherFragment1 weatherFragment1;
	private WeatherFragment2 weatherFragment2;
	private WeatherFragment3 weatherFragment3;
	private WeatherFragment4 weatherFragment4;
	private WeatherFragment5 weatherFragment5;
	private WeatherFragment6 weatherFragment6;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_activity);
		
//		weather_tv_address = (TextView) findViewById(R.id.weather_tv_address);
		weather_tv_areacode = (TextView) findViewById(R.id.weather_tv_areacode);
		weather_tv_zipcode = (TextView) findViewById(R.id.weather_tv_zipcode);
		weather_pb_circle = (ProgressBar) findViewById(R.id.weather_pb_circle);
		weather_rl_all_view = (RelativeLayout) findViewById(R.id.weather_rl_all_view);
		weather_rl_all_view.setOnTouchListener(this);
		taskCollection = new HashSet<GetDistrictData>();
		districtName = getIntent().getStringExtra("districtName");
//		Log.e("WeatherShowActivity", "" + districtName);

		districtUrl = new SqlHelper(this).getDistrictUrl(districtName);
		// 启动线程获取数据
		GetDistrictData task = new GetDistrictData();
		taskCollection.add(task);
		task.execute(districtUrl);
		
	}
	

	class GetDistrictData extends AsyncTask<String, Void, String> {

//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			weather_pb_circle.setVisibility(View.VISIBLE);
//		}
		@Override
		protected String doInBackground(String... params) {
			String httpURLconn = new UtilClass().httpURLconn(params[0],getApplicationContext());
			return httpURLconn;
		}

		@Override
		protected void onPostExecute(String result) {
			//下载结束移除线程
			taskCollection.remove(this);
			weather_pb_circle.setVisibility(View.GONE);
			if (result != null) {
				Document parse = Jsoup.parse(result);
				Elements select2 = parse.select("#phonezip").select("ul").select("li");
				String phone = select2.eq(0).text();
				String zipcode = select2.eq(1).text();
				weather_tv_areacode.setText(phone);
				weather_tv_zipcode.setText(zipcode);
//				String title = parse.select("#Title").text();
//				weather_tv_address.setText(title);
				 Elements select = parse.select("Table").eq(3).select("tr");
				 select.remove(0);
//				 Log.e("select", ""+select.toString());
				 int size = select.size();
//				 Log.e("size",""+size);
				 int size2 = select.eq(0).select("td").size();
//				 Log.e("size2", ""+size2);
				 String str = "td";
				for(int i=0;i<size;i++){
					//Integer String 类型的HashMap 用 SparseArray代替~~~用法差不多，效率高
					SparseArray<String> map2 = new SparseArray<String>();
//					if(i != 0){
//						str ="td";
//					}
					Elements eq2 = select.select("tr").eq(i);
//					Log.e("eq2", ""+eq2.toString());
					for(int j=0;j<size2;j++){
						String text2 = eq2.select(str).eq(j).text();
						map2.put(j, text2);
					}
//					Log.e("map2", ""+map2.toString());
					list.add(map2);
				}
				
//				String date = list.get(0).get(1);
//				String[] split = date.split(" ");
//				String[] dateStr = new UtilClass().dateFormat(split);
				
				initViewPager();
			} else {
				Toast.makeText(getApplicationContext(), "地区天气预报获取失败~请联系管理员",
						Toast.LENGTH_LONG).show();
			}
		}
	}
	/**
	 * 在拿到数据之后初始化viewpager
	 */
	private void initViewPager() {
//		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
//		dm = getResources().getDisplayMetrics();
		pager.setAdapter(new MyPagerAdapter(this.getSupportFragmentManager())); 
//		tabs.setViewPager(pager);
//		setTabsValue();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
		Date mDate = new Date(System.currentTimeMillis()); 
		String format = dateFormat.format(mDate);
		for(int i=0;i<list.size();i++){//默认选中的为今天的时间
			String string = list.get(0).get(i).split(" ")[0];
			int length = string.length();
			String substring = string.substring(length-2, length);
			if(Integer.valueOf(substring) == Integer.valueOf(format)){
				pager.setCurrentItem(i);
				return;
			} 
		}
	}
  
	class MyPagerAdapter extends FragmentPagerAdapter{

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		@Override
		public CharSequence getPageTitle(int position) {
			return list.get(0).get(position+1).split(" ")[1];
		}
		@Override
		public int getCount() {
//			Log.e("getCount", ""+(list.get(0).size()));
			return list.get(0).size();
		}
		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if(weatherFragment0==null){
					weatherFragment0 = new WeatherFragment0();
					Bundle bundle = new Bundle();
					bundle.putString("date", list.get(0).get(0));
					bundle.putString("state", list.get(1).get(0));
					bundle.putString("temperature", list.get(2).get(0));
					bundle.putString("wind", list.get(3).get(0));
					weatherFragment0.setArguments(bundle);
				}
				return weatherFragment0;
			case 1:
				if(weatherFragment1==null){
					weatherFragment1 = new WeatherFragment1();
					Bundle bundle = new Bundle();
					bundle.putString("date", list.get(0).get(1));
					bundle.putString("state", list.get(1).get(1));
					bundle.putString("temperature", list.get(2).get(1));
					bundle.putString("wind", list.get(3).get(1));
					weatherFragment1.setArguments(bundle);
				}
				return weatherFragment1;
			case 2:
				if(weatherFragment2==null){
					weatherFragment2 = new WeatherFragment2();
					Bundle bundle = new Bundle();
					bundle.putString("date", list.get(0).get(2));
					bundle.putString("state", list.get(1).get(2));
					bundle.putString("temperature", list.get(2).get(2));
					bundle.putString("wind", list.get(3).get(2));
					weatherFragment2.setArguments(bundle);
				}
				return weatherFragment2;
			case 3:
				if(weatherFragment3==null){
					weatherFragment3 = new WeatherFragment3();
					Bundle bundle = new Bundle();
					bundle.putString("date", list.get(0).get(3));
					bundle.putString("state", list.get(1).get(3));
					bundle.putString("temperature", list.get(2).get(3));
					bundle.putString("wind", list.get(3).get(3));
					weatherFragment3.setArguments(bundle);
				}
				return weatherFragment3;
			case 4:
				if(weatherFragment4==null){
					weatherFragment4 = new WeatherFragment4();
					Bundle bundle = new Bundle();
					bundle.putString("date", list.get(0).get(4));
					bundle.putString("state", list.get(1).get(4));
					bundle.putString("temperature", list.get(2).get(4));
					bundle.putString("wind", list.get(3).get(4));
					weatherFragment4.setArguments(bundle);
				}
				return weatherFragment4;
			case 5:
				if(weatherFragment5==null){
					weatherFragment5 = new WeatherFragment5();
					Bundle bundle = new Bundle();
					bundle.putString("date", list.get(0).get(5));
					bundle.putString("state", list.get(1).get(5));
					bundle.putString("temperature", list.get(2).get(5));
					bundle.putString("wind", list.get(3).get(5));
					weatherFragment5.setArguments(bundle);
				}
				return weatherFragment5;
			case 6:
				if(weatherFragment6==null){
					weatherFragment6 = new WeatherFragment6();
					Bundle bundle = new Bundle();
					bundle.putString("date", list.get(0).get(6));
					bundle.putString("state", list.get(1).get(6));
					bundle.putString("temperature", list.get(2).get(6));
					bundle.putString("wind", list.get(3).get(6));
					weatherFragment6.setArguments(bundle);
				}
				return weatherFragment6;
				
			default:
				return null;
			}
		}
		
	}
	
	/**
	 * 取消所有正在下载或等待下载的任务。
	 */
	public void cancelAllTasks() {
		if (taskCollection != null) {
			for (GetDistrictData task : taskCollection) {
				task.cancel(false);
			}
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.weather_activity_move_in, R.anim.weather_activity_move_out);
	}
//	@Override
//	protected void onStop() {
//		super.onStop();
//	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出程序时结束所有的下载任务 
		cancelAllTasks();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		createVelocityTracker(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 记录手指按下的纵坐标
			yDown = event.getRawY();
//			Log.e("yDown", "" + yDown);
			break;
		case MotionEvent.ACTION_UP:
			// 记录手指抬起的纵坐标
			yUp = event.getRawY();
//			Log.e("yUp", "" + yUp);
			if (moveWeatherActivity()) {
					 //只能 startActivity或者finish后调用
				this.finish();
				overridePendingTransition(R.anim.weather_activity_move_in, R.anim.weather_activity_move_out);
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
		return yUp - yDown > 200 || getScrollVelocity() > SNAP_VELOCITY;
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

}
