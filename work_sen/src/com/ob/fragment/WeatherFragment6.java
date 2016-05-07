package com.ob.fragment;

import com.ob.utils.Lunar;
import com.ob.utils.UtilClass;
import com.ob.workmain.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherFragment6 extends Fragment{

	private TextView weather_tv_date;
	private TextView weather_tv_state;
	private TextView weather_tv_temperature;
	private TextView weather_tv_wind;
	private TextView weather_tv_lunar;
	private ImageView weather_iv_state;
	private LinearLayout weather_ll_all_view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.weather_fragment, container,false);
		
		weather_tv_date = (TextView) view.findViewById(R.id.weather_tv_date); 
		weather_tv_state = (TextView) view.findViewById(R.id.weather_tv_state); 
		weather_tv_temperature = (TextView) view.findViewById(R.id.weather_tv_temperature); 
		weather_tv_wind = (TextView) view.findViewById(R.id.weather_tv_wind); 
		weather_tv_lunar = (TextView) view.findViewById(R.id.weather_tv_lunar); 
		weather_iv_state = (ImageView) view.findViewById(R.id.weather_iv_state); 
		weather_ll_all_view = (LinearLayout) view.findViewById(R.id.weather_ll_all_view); 
		
		
		UtilClass util = new UtilClass();
		
		Bundle bundle = this.getArguments();
		String date = bundle.getString("date");
		String[] split = date.split(" ");
		String[] dateStr = util.dateFormat(split);
		weather_tv_date.setText(dateStr[0]+"年"+dateStr[1]+"月"+dateStr[2]+"日"+split[1]);
		
		String state = bundle.getString("state");
		weather_tv_state.setText(state);
		
		weather_tv_temperature.setText(bundle.getString("temperature"));
		
		weather_tv_wind.setText(bundle.getString("wind"));
		
		String stateStr = util.getWeatherStateStr(state);
		weather_iv_state.setImageResource(util.getWeatherImgRes(stateStr));
		
		weather_ll_all_view.setBackgroundResource(util.getWeatherBackgroundImgRes(stateStr));
		
		Lunar lunar = new Lunar(dateStr[0],dateStr[1],dateStr[2]);
		String string = lunar.toString();
		String cyclical = lunar.cyclical();
		String animalsYear = lunar.animalsYear();
		weather_tv_lunar.setText(cyclical+"("+animalsYear+")年"+string);
		
		
		return view;
	}
}
