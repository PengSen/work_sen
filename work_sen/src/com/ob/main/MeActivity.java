package com.ob.main;

import com.ob.workmain.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MeActivity extends Activity {

	private TextView me_tv_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me);

		Window window = getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		// 设置窗口的大小及透明度
//		layoutParams.width = (int) ((int)UtilClass.getScreenWidth(getApplicationContext())*0.7);
//		 layoutParams.height = (int)((int)UtilClass.getScreenHeight(getApplicationContext())*0.9);
		 layoutParams.alpha = 0.5f;
		window.setAttributes(layoutParams);

//		int id = getIntent().getIntExtra("id", 0);

		me_tv_content = (TextView) findViewById(R.id.me_tv_content);
		me_tv_content.setText("自定制的小玩具，把偶尔上去查信息的网页数据抓取到手机上实现相应需要功能\n"
				+ "\n四个功能：1、全国近一周天气查询\n2、全国车牌首地域查询\n3、手机号码归属地查询\n4、身份证号码验证查询\n"
				+ "\n友情提示：显示数据不完整or突然闪退请在menu键中更新数据——谢谢~\n"
				+ "\n如有问题可联系开发者：574834424@qq.com  欢迎交流"
				 );
//		if (id == 1) {
//			tv_defects.setVisibility(View.GONE);
//			tv_learning.setVisibility(View.GONE);
//		} else {
//			tv_function.setVisibility(View.GONE);
//		}
	}
}
