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
		JSONObject newJO;

		SharedPreferences tempSP = context.getSharedPreferences(
				"DiaperMonitor", Activity.MODE_PRIVATE);
		// �Ͼ���ӦΪcontext����MainActivity?????????????
		

		try {
			// JA
			tempJA = new JSONArray(tempSP.getString("DiaperMonitor", ""));
			Log.e(TAG, "DiaperMonitor" + tempSP.getString("DiaperMonitor", ""));
			String jpushData = bundle.getString(JPushInterface.EXTRA_MESSAGE);

			// JO:��ȡJPUSH����
			newJO = new JSONObject(jpushData);
			Log.e(TAG, "���յ�������"+tempJA.toString());
			int length = tempJA.length();
			Log.e("tempJA.length()", "" + length);
			if (length == 0) {
				tempJA.put(newJO);
				Log.e(TAG, "SPΪ0"+tempJA.toString());
			} else {
				// ����JSONArray
				for (int i = length - 1; i >= 0; i--) {
					JSONObject tempJO = tempJA.getJSONObject(i);
					if (newJO.optInt("dataID") == (tempJO.optInt("dataID"))) {
						// ��������б���

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
						tempJA.put(newJO);
					}
				}
				//���� JA

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
