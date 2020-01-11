package com.example.smsben;

import java.util.HashMap;

import android.app.Application;

import com.example.smsben.MainActivity.MyUpdateThroughTimerHandler;

public class MyApplication extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	//public HashMap<String, Integer> map_number_down_countes_global;
	public HashMap<String, ContactBanTimer> map_number_timerobj;
	public MyUpdateThroughTimerHandler global_handler;
	
}
