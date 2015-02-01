package com.example.diapermonitor_nurse;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


public class JpushReceiver extends BroadcastReceiver{
	private static final String TAG = "PushReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();
		//Log.e(TAG, "onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
		if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.e(TAG, "接收到推送下来的message:" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	compareData(context, bundle);
        	}
		
		
	}
	private void compareData(Context context, Bundle bundle) {
		// TODO Auto-generated method stub
		//SP->JA+JO;JA->SP->LV
		//SharedPreferences,JSONArray,JSONObject,ListView
		JSONArray tempJA;
		JSONObject newJO;

		SharedPreferences tempSP = context.getSharedPreferences("DiaperMonitor",Activity.MODE_PRIVATE);
		//上句中应为context还是MainActivity?????????????
				
		try {
			//JA
			tempJA =  new JSONArray(tempSP.getString("DiaperMonitor",""));
			String jpushData = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			//JO:获取JPUSH数据
			newJO = new JSONObject(jpushData);
			int length = tempJA.length();
			
			//遍历JSONArray 
			for(int i = length-1; i >= 0; i--){
				JSONObject tempJO = tempJA.getJSONObject(i);
				if(newJO.optInt("dataID")==(tempJO.optInt("dataID"))) {
					//如果是已有报警
					
					//将JA替换
					switch (newJO.optInt("alertState")) {
					case 0:
						//新警报,刷新替换[按键+1,添加姓名键值对]
						tempJO.put("alertState",0);
						tempJO.put("recordTime", newJO.optString("recordTime"));
						tempJO.remove("nurseName");
//						if(tempJO.optString("nurseName")!=null){tempJO.remove("nurseName");}
						break;
					case 1:
						//处理中,刷新,判断是否是自己发出的警报,如果是,[按键无反应]
						//刷新三项
						tempJO.put("alertState",1);
						tempJO.put("recordTime", newJO.optString("recordTime"));
						tempJO.put("nurseName",newJO.optString("nurseName"));
						
						//以下7行是不考虑服务器的代码,因此屏蔽
//						if(tempJO.optString("nurse")!=null){
//							//说明自己抢过单
//						}else {
//							//别人抢单成功,自己条目将不可点击
//							tempJO.put("alertState",3);
//							tempJO.put("recordTime", newJO.optString("recordTime"));
//						}
		
						break;

					case 2:
						//处理完毕,刷新替换[按键无反应]
						tempJO.put("alertState",2);
						tempJO.put("recordTime", newJO.optString("recordTime"));
						break;

					
					default:
						break;
					}
				}else {
					//将newJO添加到JA之后
					tempJA.put(newJO);
				};
				
				//将更新数据保存至本地SP"DiaperMonitor"
				SharedPreferences.Editor editor = tempSP.edit();
				editor.putString("DiaperMonitor", tempJA.toString());
				editor.commit();
				playNotify(context);
				Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
				context.sendBroadcast(msgIntent);
				
			}
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//主线程通信handler
			MainActivity.SendMessage(MainActivity.handler, 1);
			e.printStackTrace();
		} 
		
	
	}
	private void playNotify(Context context) {
		// TODO Auto-generated method stub
		
	}

}
