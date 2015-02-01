//������
package com.example.diapermonitor_nurse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.diapermonitor_nurse.MyAdapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MainActivity extends Activity {
	private ScrollView infoScrollView;
	private static MainActivity context;
	private ImageView imRed;
	private ImageView imYelow;
	private ImageView imGray;
	private TextView numRed;
	private TextView numYellow;
	private TextView numGray;
	private SimpleDateFormat format;
	private static SharedPreferences projectSP;

	private com.example.diapermonitor_nurse.ListViewForScrollView mListviewCareds;
	private MyAdapter adapter;
	// private ArrayList<HashMap<String,Object>> mData;
	private org.json.JSONArray projectJA;
	private MessageReceiver mMessageReceiver;
	// ��������
	protected String name;
	protected String sysTime;
	static ArrayList<HashMap<String, Object>> mList;

	static ArrayList<HashMap<String, Object>> mListItemCareds;
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.diapermonitor_nurse.MESSAGE_RECEIVED_ACTION";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		imRed = (ImageView) findViewById(R.id.light_Red);
		imYelow = (ImageView) findViewById(R.id.light_Yellow);
		imGray = (ImageView) findViewById(R.id.light_Gray);
		numRed = (TextView) findViewById(R.id.number_Red);
		numYellow = (TextView) findViewById(R.id.number_Yellow);
		numGray = (TextView) findViewById(R.id.number_Gray);
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// //SP->JA+JO;JA->SP->LV
		// //SharedPreferences,JSONArray,JSONObject,ListView
		projectSP = getSharedPreferences("DiaperMonitor", Activity.MODE_PRIVATE);
		try {
			mList = getData();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// �ж�name�Ƿ�Ϊ��,Ϊ���򵯳�����,������д�������
		// ����޸Ļ�����ŵ���Ӧ

		
		// //JA��ȡJPUSH����,���ͬ�����˶���������ô��
		
		mListviewCareds = (ListViewForScrollView) findViewById(R.id.mainactivy_list_Careds); /* ����һ����̬���� */

		try {
			setView();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		registerMessageReceiver();

		// �������ݼ���-----------------------�о�Ӧ�����ó�JSONArray
		// Set patientSet = MainActivity.projectSP.getStringSet("patientSet",
		// null);//������string
		// if(patientSet!=null)
		// mListItemCareds = (ArrayList<HashMap<String, Object>>)
		// patientSet.iterator().next();
		// else mListItemCareds = new ArrayList<HashMap<String, Object>>();

		//
		// �����ť����-----------------------------
		mListviewCareds.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				setTitle("��ѡ����" + arg2 + "���л���");// ���ñ�������ʾ�������

				int i = 3;
				HashMap<String, Object> item;
				item = mList.get(arg2);

				if (mList.get(arg2).get("alertState").equals("")) {
					//����б�Ϊ��,����Ӧ
				}else {
					i = (Integer) item.get("alertState");
					switch (i) {
					case 0:
						// 0->1
						// ˢ����Ӧ״̬����Ӧʱ��,��ӻ�����Ϣ
						item.remove("alertState");
						item.put("alertState", 1);
						sysTime = format.format(System.currentTimeMillis());
						item.remove("recordTime");
						item.put("recordTime", sysTime);
						item.put("nurseName", name);
						Log.d("���", "��Ӧ:״̬Ϊ��0��1");
						// ��¼SP?????????????????��һ��Ӧ�÷���ǰ��
						// ����mList,������JSONArray��ʽ����
						mList2JA2SP();
						
						// ����(�߳�)===============================================================

						Toast.makeText(getApplicationContext(), "��л����Ӧ�˻�������",
								Toast.LENGTH_SHORT).show();
						adapter.notifyDataSetChanged();

						break;
					case 1:
						// �Լ�:1->2,ˢ��,ͬʱ��¼SP
						if (item.get("nurseName").equals(name)) {
							item.remove("alertState");
							item.put("alertState", 1);
							sysTime = format.format(System.currentTimeMillis());
							item.remove("recordTime");
							item.put("recordTime", sysTime);
							Log.d("���", "��Ӧ:״̬Ϊ��1��2");
							// ��¼SP?????????????????��һ��Ӧ�÷���ǰ��
							

							// ����mList,������JSONArray��ʽ����
							mList2JA2SP();
							// ����(�߳�)===============================================================

							Toast.makeText(getApplicationContext(), "��л������˻���",
									Toast.LENGTH_SHORT).show();
							adapter.notifyDataSetChanged();
						} else {
							// ����:�޶���
							Log.d("���", "1�����Լ�����Ӧ,�޶���");
						}
						break;
					case 2:
						// �޶���
						Log.d("���", "2�����Լ�����Ӧ,�޶���");
						break;
					default:
						break;
					}

				} 
			}



		});

	}
	
	//��mList����ΪSP
	protected void mList2JA2SP() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		JSONArray mJA= new JSONArray();
		int l = mList.size();			
		for (int a = 0; a < l; a++) {
			JSONObject mJO = new JSONObject(mList.get(a));
			mJA.put(mJO);
		}
		SharedPreferences.Editor editor = projectSP.edit();
		editor.putString("DiaperMonitor", mJA.toString());
		editor.commit();
	}
	
	// ��SP������-------------------------------------------------------------
	// ��������,����,ʱ��,״̬��ť
	private ArrayList<HashMap<String, Object>> getData() throws JSONException {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map;
		projectJA = new JSONArray();
		// projectJA = new JSONArray(projectSP.getString("DiaperMonitor", ""));
		if (projectSP.getString("DiaperMonitor", "").equals("")) {
			map = new HashMap<String, Object>();
			JSONObject mJO = new JSONObject();
			map.put("alertType", mJO.optInt("alertType"));
			map.put("patientName", mJO.optString("patientName"));
			map.put("recordTime", mJO.optString("recordTime"));
			map.put("alertState", mJO.optInt("alertState"));
			list.add(map);
		} else {
			int l = projectJA.length();
			for (int i = 0; i < l; i++) {
				map = new HashMap<String, Object>();
				JSONObject mJO = projectJA.getJSONObject(i);
				map.put("alertType", mJO.optInt("alertType"));
				map.put("patientName", mJO.optString("patientName"));
				map.put("recordTime", mJO.optString("recordTime"));
				map.put("alertState", mJO.optInt("alertState"));
				list.add(map);
			}

		}
		return list;
	}

	private void registerMessageReceiver() {
		// TODO Auto-generated method stub
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	private class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				try {
					setView();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	private void setView() throws JSONException {
		// TODO Auto-generated method stub
		// mData = getData();
		adapter = new MyAdapter(this, getData());
		mListviewCareds.setAdapter(adapter);
	}

	// private List<Map<String, Object>> getData() {
	// // TODO Auto-generated method stub
	// return null;
	// }

	

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

	// �����ñ�����������
	// private static JSONObject testJSONObject(int num) {
	// JSONObject jsonObject = new JSONObject();
	// int i = num;
	// List<String> listNurse = new ArrayList<String>() ;
	// listNurse.add("17801090898");
	// listNurse.add("13800000000");
	//
	// try {
	// jsonObject.put("patientName", "Mike"+i);
	// jsonObject.put("alertType",1 );
	// jsonObject.put("recordTime", "2015-01-22 16:11:02");
	// jsonObject.put("nursePhone",listNurse);
	// jsonObject.put("alertState", 0);
	// jsonObject.put("dataID", 1101);
	//
	//
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// //Log.d("testJson", jsonObject.toString() );
	// return jsonObject;
	// }

	public static void SendMessage(Handler handler, int i) {
		Message msg = handler.obtainMessage();
		msg.what = i;
		handler.sendMessage(msg);
	}

	public static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(context, "��ȡJPUSH���ݳ���", Toast.LENGTH_SHORT)
						.show();
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		infoScrollView = (ScrollView) findViewById(R.id.my_scrollview);
		infoScrollView.smoothScrollTo(0, 0);

	}
	
	@Override
	public void onStop(){
		super.onStop();
		mList2JA2SP();
	}

}
