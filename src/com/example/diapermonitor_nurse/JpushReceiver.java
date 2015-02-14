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

public class JpushReceiver extends BroadcastReceiver {
	private static final String TAG = "JpushReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();
		// Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " +
		// printBundle(bundle));

		if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			Log.d(TAG,
					"���յ�����������message:"
							+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
			compareData(context, bundle);
		}

	}

	private void compareData(Context context, Bundle bundle) {
		// TODO Auto-generated method stub
		// SP->JA+JO;JA->SP->LV
		// SharedPreferences,JSONArray,JSONObject,ListView
		JSONArray tempJA;
		Log.d(TAG,"1:");
		JSONObject newJO;
		Log.d(TAG,"2:");
		SharedPreferences tempSP = MainActivity.projectSP;
		// �Ͼ���ӦΪcontext����MainActivity?????????????
		Log.d(TAG,"3:");
		Log.e(TAG, "����" );
//		try {
//			tempJA = new JSONArray(tempSP.getString("DiaperMonitor", ""));
//			Log.e(TAG, "DiaperMonitor" + tempSP.getString("DiaperMonitor", ""));
//		} catch (JSONException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		Log.e(TAG, "����" );
		Log.d(TAG,"4:");
		try {
			// JA
			tempJA = new JSONArray(tempSP.getString("DiaperMonitor", ""));
			Log.e(TAG, "DiaperMonitor" + tempSP.getString("DiaperMonitor", ""));
			String jpushData = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			Log.d(TAG,"5:");
			// JO:��ȡJPUSH����
			newJO = new JSONObject(jpushData);
			
			Log.e(TAG, "���յ�������"+newJO.toString());
			//Ӧ���з���ͨ��======================================================��֤OK
			int length = tempJA.length();
			Log.e("tempJA.length()", "" + length);
			if (length == 0) {
				tempJA.put(newJO);
				Log.e(TAG, "SPΪ0"+tempJA.toString());
			} else {
				boolean isExist = false;
				// ����JSONArray
				for (int i = length - 1; i >= 0; i--) {
					JSONObject tempJO = tempJA.getJSONObject(i);
					Log.e(TAG, "�������е�JSONArray,��"+i+"="+tempJO.toString());
					if(tempJO.optInt("dataID")!=0) {
						if (newJO.optInt("dataID") == (tempJO.optInt("dataID"))) {
							isExist = true;
							// ��������б���
							Log.e("JpushReceiver","���յ�dataID��ͬ");
							// ��JA�滻
							switch (newJO.optInt("alertState")) {
							case 0:
								// �¾���,ˢ���滻[����+1,���������ֵ��]
								
								tempJO.put("alertState", 0);
								tempJO.put("recordTime",newJO.optString("recordTime"));
								tempJO.put("patientPhone", newJO.opt("patientPhone"));
								tempJO.remove("nurseName");
								// if(tempJO.optString("nurseName")!=null){tempJO.remove("nurseName");}
								break;
							case 1:
								// ������,ˢ��,�ж��Ƿ����Լ������ľ���,�����,[�����޷�Ӧ]
								// ˢ������
								if (tempJO.optString("nurseName").equals(MainActivity.name)) {
									Log.e(TAG,"�ж��Ƿ����Լ������ľ���-�յ�:"+tempJO.optString("nurseName")+",�Ա��ҵ�����"+MainActivity.name);
									// ˵���Լ�������
									tempJO.put("alertState", 1);
									tempJO.put("patientName", newJO.opt("patientName"));
									tempJO.put("recordTime",newJO.optString("recordTime"));
									tempJO.put("nurseName",newJO.optString("nurseName"));
								} else {
									// ���������ɹ�,�Լ���Ŀ�����ɵ��
									tempJO.put("alertState", 3);
									tempJO.put("recordTime",
											newJO.optString("recordTime"));
								}

								break;

							case 2:
								// �������,ˢ���滻[�����޷�Ӧ]
								tempJO.put("alertState", 2);
								tempJO.put("recordTime",newJO.optString("recordTime"));
								break;

							default:
								break;
							}
						} else {
							// ��newJO��ӵ�JA֮��
							Log.e("JpushReceiver","���յ�dataID��ͬ");
							Log.e("JpushReceiver","���յ�dataID��ͬ"+tempJA.toString());
						}
					}
					
				}
				//���� JA
				if(!isExist)
					tempJA.put(newJO);
			}
			// ���������ݱ���������SP"DiaperMonitor"
			SharedPreferences.Editor editor = tempSP.edit();
			editor.putString("DiaperMonitor", tempJA.toString());
			Log.e(TAG, "����������"+tempJA.toString());

			editor.commit();
			Log.e(TAG, MainActivity.projectSP
					.getString("DiaperMonitor", "").toString());
			playNotify(context);
			Intent msgIntent = new Intent(
					MainActivity.MESSAGE_RECEIVED_ACTION);
			context.sendBroadcast(msgIntent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// ���߳�ͨ��handler
			Log.e(TAG, "Ī������ĳ�����");
			MainActivity.SendMessage(MainActivity.handler, 1);
			
			e.printStackTrace();
		}

	}

	private void playNotify(Context context) {
		// TODO Auto-generated method stub

	}

}
