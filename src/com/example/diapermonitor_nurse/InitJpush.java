package com.example.diapermonitor_nurse;

import android.app.Application;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
/**
 * For developer startup JPush SDK
 * 
 * 一般建议在自定义 Application 类里初始化。也可以在主 Activity 里。
 */
public class InitJpush extends Application {
    
	private static final String TAG = "InitJpush";
	
    @Override
    public void onCreate() {     
    	 Log.e(TAG, "onCreate");
         super.onCreate();
         JPushInterface.setDebugMode(true);
         JPushInterface.init(this);
         Log.e(TAG, "onCreate finished");
    }

}
