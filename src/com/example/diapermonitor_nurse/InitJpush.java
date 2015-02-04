package com.example.diapermonitor_nurse;

import android.app.Application;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
/**
 * For developer startup JPush SDK
 * 
 * һ�㽨�����Զ��� Application �����ʼ����Ҳ�������� Activity �
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
