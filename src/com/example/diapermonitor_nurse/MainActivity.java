//护工端
package com.example.diapermonitor_nurse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.diapermonitor_nurse.MyAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
	private static int redNumber = 0;
	private static int yellowNumber = 0;
	private static int grayNumber = 0;

	private com.example.diapermonitor_nurse.ListViewForScrollView mListviewCareds;
	private MyAdapter adapter;
	// private ArrayList<HashMap<String,Object>> mData;
	private org.json.JSONArray projectJA;
	private MessageReceiver mMessageReceiver;
	// 护工姓名
	protected String sysTime;
	private static SharedPreferences nameSP;
	protected String name;
	private EditText editName;
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
		nameSP = getSharedPreferences("name", Activity.MODE_PRIVATE);
		// 判断name是否为空,为空则弹出窗口,请求填写护工编号
		// 添加修改护工编号的响应
		name = nameSP.getString("nurseName", "");
//		Log.d("onCreate","清空前"+nameSP.getString("nurseName", "")+"end");
//		//空数据集合测试
//		SharedPreferences.Editor editor01 = nameSP.edit();
//		editor01.clear();
//		editor01.commit();
//		Log.d("onCreate","清空后"+nameSP.getString("nurseName", "")+"end");

		// //JA获取JPUSH数据,如果同事来了多组数据怎么办

		mListviewCareds = (ListViewForScrollView) findViewById(R.id.mainactivy_list_Careds); /* 定义一个动态数组 */

		try {
			setView();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		registerMessageReceiver();

		// 病人数据集合-----------------------感觉应该设置成JSONArray
		// Set patientSet = MainActivity.projectSP.getStringSet("patientSet",
		// null);//不仅是string
		// if(patientSet!=null)
		// mListItemCareds = (ArrayList<HashMap<String, Object>>)
		// patientSet.iterator().next();
		// else mListItemCareds = new ArrayList<HashMap<String, Object>>();

		//
		// 点击按钮操作-----------------------------
		mListviewCareds.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				setTitle("你选择了" + arg2 + "进行护理");// 设置标题栏显示点击的行

				int i = 3;
				HashMap<String, Object> item;
				item = mList.get(arg2);

				if (name.equals("")) {
					nameDialog();
					Toast.makeText(getApplicationContext(), "联系患者时,需要填写您的信息", Toast.LENGTH_SHORT).show();
				} else {

					if (mList.get(arg2).get("alertState").equals("")) {
						// 如果列表为空,无响应
					} else {
						i = (Integer) item.get("alertState");
						switch (i) {
						case 0:
							// 0->1
							// 刷新响应状态和响应时间,添加护工信息
							item.remove("alertState");
							item.put("alertState", 1);
							sysTime = format.format(System.currentTimeMillis());
							item.remove("recordTime");
							item.put("recordTime", sysTime);
							item.put("nurseName", name);
							Log.d("点击", "响应:状态为从0变1");
							// 记录SP
							// 遍历mList,将其以JSONArray形式保存
							mList2JA2SP();

							// 发送(线程)
							send(item);

							Toast.makeText(getApplicationContext(),
									"感谢您响应了护理请求", Toast.LENGTH_SHORT).show();
							adapter.notifyDataSetChanged();

							break;
						case 1:
							// 自己:1->2,刷新,同时记录SP
							if (item.get("nurseName").equals(name)) {
								item.remove("alertState");
								item.put("alertState", 1);
								sysTime = format.format(System
										.currentTimeMillis());
								item.remove("recordTime");
								item.put("recordTime", sysTime);
								Log.d("点击", "响应:状态为从1变2");
								// 记录SP?????????????????那一步应该放在前面
								// 遍历mList,将其以JSONArray形式保存
								mList2JA2SP();
								// 发送(线程)===============================================================
								send(item);

								Toast.makeText(getApplicationContext(),
										"感谢您完成了护理", Toast.LENGTH_SHORT).show();
								adapter.notifyDataSetChanged();
							} else {
								// 别人:无动作
								Log.d("点击", "1不是自己的响应,无动作");
							}
							break;
						case 2:
							// 无动作
							Log.d("点击", "2不是自己的响应,无动作");
							break;
						default:
							break;
						}
					}
				}

			}

		});

	}

	private void nameDialog() {
		// TODO Auto-generated method stub
		editName = new EditText(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("您好,请输入您的姓名或工作号").setView(editName)
				.setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						SharedPreferences.Editor editor = nameSP.edit();
						editor.putString("nurseName", editName.getText().toString());
						editor.commit();
						Log.d("nameDialog","已记录"+nameSP.getString("nurseName", ""));
					}
				})
				.show();
	}

	private void send(HashMap<String, Object> mItem) {
		// TODO Auto-generated method stub
		JSONObject newJpushJson = new JSONObject(mItem);
		String jpushStr = newJpushJson.toString();
		JpushUpload uploadJpush = new JpushUpload(jpushStr);
		uploadJpush.upload();
	}

	// 将mList保存为SP
	protected void mList2JA2SP() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		JSONArray mJA = new JSONArray();
		int count = 0;
		int red = 0;
		int yellow = 0;
		int gray = 0;
		int l = mList.size();
		for (int a = 0; a < l; a++) {
			JSONObject mJO = new JSONObject(mList.get(a));
			while (mList.get(a).get("alertState") != null) {
				count = Integer.parseInt(mList.get(a).get("alertState")
						.toString());
				switch (count) {
				case 0:
					red++;
					break;
				case 1:
					if (mList.get(a).get("nurseName").equals(name)) {
						yellow++;
					} else {
						gray++;
					}
					break;
				default:
					break;
				}

			}
			mJA.put(mJO);
		}
		SharedPreferences.Editor editor = projectSP.edit();
		editor.putString("DiaperMonitor", mJA.toString());
		editor.commit();
		redNumber = red;
		yellowNumber = yellow;
		grayNumber = gray;
		Log.d("mList2JA2SP()", projectSP.toString());
		Log.d("统计", "red:" + red + ",yellow:" + yellow + ",gray:" + gray);
	}

	// 从SP拿数据-------------------------------------------------------------
	// 警报类型,姓名,时间,状态按钮
	private ArrayList<HashMap<String, Object>> getData() throws JSONException {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map;
		projectJA = new JSONArray();
		// projectJA = new JSONArray(projectSP.getString("DiaperMonitor", ""));
		if (projectSP.getString("DiaperMonitor", "").equals("")) {
			map = new HashMap<String, Object>();
			JSONObject mJO = new JSONObject();
			map.put("patientName", mJO.optString("patientName"));
			map.put("alertType", mJO.optInt("alertType"));
			map.put("recordTime", mJO.optString("recordTime"));
			map.put("nursePhone", mJO.optString("nursePhone"));
			map.put("alertState", mJO.optInt("alertState"));
			map.put("dataID", mJO.optInt("dataID"));
			list.add(map);
		} else {
			int l = projectJA.length();
			for (int i = 0; i < l; i++) {
				map = new HashMap<String, Object>();
				JSONObject mJO = projectJA.getJSONObject(i);
				map.put("patientName", mJO.optString("patientName"));
				map.put("alertType", mJO.optInt("alertType"));
				map.put("recordTime", mJO.optString("recordTime"));
				map.put("nursePhone", mJO.optString("nursePhone"));
				map.put("alertState", mJO.optInt("alertState"));
				map.put("dataID", mJO.optInt("dataID"));
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

	// 测试用本地生成数据
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
				Toast.makeText(context, "读取JPUSH数据出错", Toast.LENGTH_SHORT)
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
		numRed.setText("" + redNumber);
		numYellow.setText("" + yellowNumber);
		numGray.setText("" + grayNumber);
		try {
			mList = getData();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.d("nameDialog","已记录"+nameSP.getString("nurseName", ""));
		if (name.equals("")) {
			nameDialog();
		}
		Log.d("onstart", "执行了onstart");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d("onstop", "执行了onstop");
		mList2JA2SP();

	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

}
