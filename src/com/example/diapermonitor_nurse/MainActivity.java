//������
package com.example.diapermonitor_nurse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.diapermonitor_nurse.MyAdapter;
import com.example.diapermonitor_nurse.MyAdapter.ViewHolder;

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
	private static SharedPreferences projectSP;
	
	private com.example.diapermonitor_nurse.ListViewForScrollView mListviewCareds;
	private MyAdapter adapter;
//	private ArrayList<HashMap<String,Object>> mData;
	private JSONArray projectJA;
	private MessageReceiver mMessageReceiver;
	static ArrayList<HashMap<String, Object>> mListItemCareds;
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.diapermonitor_nurse.MESSAGE_RECEIVED_ACTION";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		imRed = (ImageView)findViewById(R.id.light_Red);
		imYelow =  (ImageView)findViewById(R.id.light_Yellow);
		imGray = (ImageView)findViewById(R.id.light_Gray);
		numRed = (TextView)findViewById(R.id.number_Red);
		numYellow= (TextView)findViewById(R.id.number_Yellow);
		numGray = (TextView)findViewById(R.id.number_Gray);
		
//		//SP->JA+JO;JA->SP->LV
//		//SharedPreferences,JSONArray,JSONObject,ListView
		projectSP = getSharedPreferences("DiaperMonitor",
				Activity.MODE_PRIVATE);
		
//		try {
//			projectJA =  new JSONArray(projectSP.getString("DiaperMonitor",null));
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			Toast.makeText(getApplication(), "JA��ȡSP����",Toast.LENGTH_SHORT).show();
//			e.printStackTrace();
//		} 
//		
//		//JA��ȡJPUSH����,���ͬ�����˶���������ô��
//		try {
//			String jpushData = new String();
//			JSONObject newJO = new JSONObject(jpushData);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		//JOͬJA���ݱȶ�,��ʱ�߳�
//		check(newJO,projectJA);
//		
		
		//��pSP��ֵ
		
		mListviewCareds = (ListViewForScrollView) findViewById(R.id.mainactivy_list_Careds); /* ����һ����̬���� */

		try {
			setView();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		registerMessageReceiver(); 
		
		
		
		
		//�������ݼ���-----------------------�о�Ӧ�����ó�JSONArray
//		Set patientSet = MainActivity.projectSP.getStringSet("patientSet", null);//������string
//		if(patientSet!=null)
//			mListItemCareds = (ArrayList<HashMap<String, Object>>) patientSet.iterator().next();
//		else mListItemCareds = new ArrayList<HashMap<String, Object>>();
		
//		
		//�����ť����-----------------------------
		 mListviewCareds.setOnItemClickListener(new OnItemClickListener() {
			 
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,	long arg3) {
				// TODO Auto-generated method stub
				setTitle("��ѡ����" + arg2 +"���л���");// ���ñ�������ʾ�������
				ViewHolder holder = (ViewHolder) arg1.getTag();
                // �ı�CheckBox��״̬
               // holder.cb.toggle();
//				HashMap<String, Object> map = mListItemCareds;
//                //map.put("ItemCheckbox", ""+holder.cb.isChecked());
//                mListItemCareds.remove(arg2);
//                mListItemCareds.add(arg2, map);
                adapter.notifyDataSetChanged();
			}
			
		 });
		 

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
//		mData = getData();
		adapter = new MyAdapter(this, getData());
		mListviewCareds.setAdapter(adapter);
	}

	//��ʱ����-------------------------------------------------------------
	//Ӧ�ô�sp������+json,����sp+json��������?
	//��������,����,ʱ��,״̬��ť`
	//�õ�����ʱ��Ҫ��ͬ�������ݽ��бȶ�------------------------------
	private ArrayList<HashMap<String, Object>> getData() throws JSONException {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map;
		projectJA = new JSONArray(projectSP.getString("DiaperMonitor", ""));
		if (projectSP.getString("DiaperMonitor", "").equals("")) {
			map = new HashMap<String, Object>();
			JSONObject mJO = new JSONObject();
			map.put("alertType", mJO.optString("alertType"));
			map.put("patientName", mJO.optString("patientName"));
			map.put("recordTime", mJO.optString("recordTime"));
			map.put("alertState",mJO.optString("alertState"));
			list.add(map);
		} else {
			int l = projectJA.length();
			for (int i = 0; i < l; i++) {
				map = new HashMap<String, Object>();
				JSONObject mJO = projectJA.getJSONObject(i);
				map.put("alertType", mJO.optString("alertType"));
				map.put("patientName", mJO.optString("patientName"));
				map.put("recordTime", mJO.optString("recordTime"));
				map.put("alertState",mJO.optString("alertState"));
				list.add(map);
			}

		}
		return list;
	}

	
//	private List<Map<String, Object>> getData() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		infoScrollView = (ScrollView) findViewById(R.id.my_scrollview);
		infoScrollView.smoothScrollTo(0, 0);
		
	}
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }
	
	//�����ñ�����������	
//	private static JSONObject testJSONObject(int num) {
//		JSONObject jsonObject = new JSONObject();
//		int i = num;
//		List<String> listNurse = new ArrayList<String>() ;
//		listNurse.add("17801090898");
//		listNurse.add("13800000000");
//		
//		try {
//			jsonObject.put("patientName", "Mike"+i);
//			jsonObject.put("alertType",1 );
//			jsonObject.put("recordTime", "2015-01-22 16:11:02");
//			jsonObject.put("nursePhone",listNurse);
//			jsonObject.put("alertState", 0);
//			jsonObject.put("dataID", 1101);
//			
//  
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		//Log.d("testJson", jsonObject.toString() );
//		return jsonObject;
//    } 
	
	public static void SendMessage(Handler handler, int i){
		Message msg = handler.obtainMessage();
		msg.what = i;
		handler.sendMessage(msg);
		}
	
	public static Handler handler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 1:		
				Toast.makeText(context, "��ȡJPUSH���ݳ���",Toast.LENGTH_SHORT).show();
				break;
			
			default:
				break;
			}
		}
	};

}
