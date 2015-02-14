//护工端
package com.example.diapermonitor_nurse;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Set;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
	private TextView mName;
	private SimpleDateFormat format;
	static SharedPreferences projectSP;
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
	protected static String name;
	private EditText editName;
	static boolean tagFlag;
	private static String mMobile ;
	static ArrayList<HashMap<String, Object>> mList;

	static ArrayList<HashMap<String, Object>> mListItemCareds;
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.diapermonitor_nurse.MESSAGE_RECEIVED_ACTION";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		
//		mMobile ="18515976971";//hongminote Bb
//		mMobile ="13261785958";//Mega aa
		
		
		imRed = (ImageView) findViewById(R.id.light_Red);
		imYelow = (ImageView) findViewById(R.id.light_Yellow);
		imGray = (ImageView) findViewById(R.id.light_Gray);
		numRed = (TextView) findViewById(R.id.number_Red);
		numYellow = (TextView) findViewById(R.id.number_Yellow);
		numGray = (TextView) findViewById(R.id.number_Gray);
		mName = (TextView) findViewById(R.id.changeName);
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// //SP->JA+JO;JA->SP->LV
		// //SharedPreferences,JSONArray,JSONObject,ListView
		projectSP = getSharedPreferences("DiaperMonitor", Activity.MODE_PRIVATE);
		Log.d("onCreate projectSP ", projectSP.getString("DiaperMonitor", "")
				+ "//ForInit");
		nameSP = getSharedPreferences("name", Activity.MODE_PRIVATE);
		name = nameSP.getString("nurseName", "");
		mMobile =nameSP.getString("nurseName", "");
		mName.setText(mMobile);
		// 判断name是否为空,为空则弹出窗口,请求填写护工编号
		// 添加修改护工编号的响应
			
		if(mMobile.equals("")) {
			tagFlag = false;
			nameDialog();
		}else {
		//在JPUSH上注册TAG
		JPushInterface.setAliasAndTags(getApplicationContext(), mMobile , null, mAliasCallback);
		
		}
		
		Log.d("onCreate", "清空前" + nameSP.getString("nurseName", "") + "end");
		// 空数据集合测试
		// SharedPreferences.Editor editor01 = nameSP.edit();
		// editor01.clear();
		// editor01.commit();
		// Log.d("onCreate","清空后"+nameSP.getString("nurseName", "")+"end");
		Log.d("onCreate", "判断nameSP为空与否1 内容=" + nameSP.getString("nurseName", ""));
		
		Log.d("onCreate", "判断nameSP为空与否2 内容=" + nameSP.getString("nurseName", ""));
		Log.d("onCreate", "判断变量 name=" + name);
		
		
		// //JA获取JPUSH数据,如果同事来了多组数据怎么办

		
		//没有必要吧
//		Log.d("onCreate", "TRY===creat初始化了mList  ");
//		mList = getData();
//		Log.d("onCreate", "TRY===creat初始化了mList = " + mList.toString());
		
		
		// Log.d("onCreate","creat初始化mList = "+mList.toString());
		mListviewCareds = (ListViewForScrollView) findViewById(R.id.mainactivy_list_Careds); /* 定义一个动态数组 */

		// try {
		// setMainView();
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		
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
				//setTitle("你选择了" + arg2 + "进行护理");// 设置标题栏显示点击的行

				int i = 3;
				HashMap<String, Object> item;
				item = mList.get(arg2);
				Log.d("onItemClick", "点击前 mList.get(arg2)"+item.toString());
				if (nameSP.getString("nurseName", "").equals("")) {
					nameDialog();
					Toast.makeText(getApplicationContext(), "联系患者时,需要填写您的信息",
							Toast.LENGTH_SHORT).show();
				} else {

					if (item.get("alertState").equals("")) {
						// 如果列表为空,无响应
					} else {
						i = (Integer) item.get("alertState");
						switch (i) {
						case 0:
							// 0->1
							//红色
							// 刷新响应状态和响应时间,添加护工信息
							item.remove("alertState");
							item.put("alertState", 1);
							sysTime = format.format(System.currentTimeMillis());
							item.remove("recordTime");
							item.put("recordTime", sysTime);
							item.put("nurseName", name);
							item.put("nursePhone", mMobile);
							Log.d("点击", "响应:状态为从0变1");
							// 记录SP
							// 遍历mList,将其以JSONArray形式保存
							mList2JA2SP(mList);

							// 发送(线程)
							Log.d("onItemClick", "点击后 mList.get(arg2)"+item.toString());
							send(item);//====================================================================================

							Toast.makeText(getApplicationContext(),
									"感谢您响应护理请求 !", Toast.LENGTH_SHORT).show();
							adapter.notifyDataSetChanged();

							break;
						case 1:
							// 1->2,刷新,同时记录SP
							//黄色,和灰色	
							name = nameSP.getString("nurseName", "");
							Log.d("setOnItemClickListener", name);
							
								//如果是自己抢到的
								item.remove("alertState");
								item.put("alertState", 2);
								sysTime = format.format(System
										.currentTimeMillis());
								item.remove("recordTime");
								item.put("recordTime", sysTime);
								item.put("nurseName",name);//添加自己的姓名
								Log.d("点击", "响应:状态为从1变2");
								// 记录SP?????????????????那一步应该放在前面
								// 遍历mList,将其以JSONArray形式保存
								mList2JA2SP(mList);
								// 发送(线程)===============================================================
								send(item);

								Toast.makeText(getApplicationContext(),
										"感谢您为用户进行了护理 ! : )", Toast.LENGTH_SHORT).show();
								adapter.notifyDataSetChanged();
							
							break;
						case 2:
							// 无动作,蓝色
							
							Toast.makeText(getApplicationContext(), "该用户的护理工作已完成~",
									Toast.LENGTH_SHORT).show();
							break;
						case 3:
							// 无动作,灰色
							Toast.makeText(getApplicationContext(), "已有护工进行响应~",
									Toast.LENGTH_SHORT).show();
							Log.d("点击", "3不是自己的响应,无动作");
							break;
						default:
							Log.e("点击", "不是自己的响应,无动作");
							break;
						}
					}
				}
				try {
					Log.d("OnClickItem", "执行setMainView()");
					setMainView();
					//Log.d("OnClickItem" ,"执行setMainView() over");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d("OnClickItem", "执行setMainView() 出错!!");
					e.printStackTrace();
				}
			}

		});

	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d("onstart", "onstart");

		infoScrollView = (ScrollView) findViewById(R.id.my_scrollview);
		infoScrollView.smoothScrollTo(0, 0);
//		numRed.setText("" + redNumber);
//		numYellow.setText("" + yellowNumber);
//		numGray.setText("" + grayNumber);

		try {
			Log.d("onstart", "执行setMainView() ready");
			setMainView();
			Log.d("onstart", "执行setMainView() over");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("onstart", "执行setMainView() 出错!!");
			e.printStackTrace();
		}
		Log.d("onstart", "执行了onstart");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("onResume", "onResume");
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("onstop", "执行了onstop");
		mList2JA2SP(mList);
		Log.d("onstop", "执行mList2JA2SP");

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu)
	    {
	        MenuInflater inflater = getMenuInflater();
	       inflater.inflate(R.menu.main, menu);
	        return true;
	    }
	 
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	        // TODO Auto-generated method stub
	        switch (item.getItemId()) {
	        case R.id.cleanBlue:
	            clearDialog();	        	        	
	            break;
	        case R.id.changeName:
	        	nameDialog();	        	
	            break;
	        default:
	            break;
	        }
	        return super.onOptionsItemSelected(item);
	    }

	private void nameDialog() {
		// TODO Auto-generated method stub
		editName = new EditText(this);
		Log.d("nameDialog", "被调用了");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("您好,请输入本手机的号码").setView(editName)
				.setCancelable(true)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						SharedPreferences.Editor editor = nameSP.edit();
						editor.putString("nurseName", editName.getText()
								.toString());
						editor.commit();	
						mName.setText(nameSP.getString("nurseName", ""));
						mMobile =nameSP.getString("nurseName", "");
						Log.e("MainActivity", mMobile);
						JPushInterface.setAliasAndTags(getApplicationContext(), mMobile , null, mAliasCallback);
							
						Log.d("nameDialog",
								"已记录nameSP.getString="
										+ nameSP.getString("nurseName", ""));
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel(); 
					}
					})
				.show();
	}

	private void send(HashMap<String, Object> mItem) {
		// TODO Auto-generated method stub
		JSONObject newJpushJson = new JSONObject(mItem);
		String jpushStr = newJpushJson.toString();
		JpushUpload uploadJpush = new JpushUpload(jpushStr);
		uploadJpush.start();
	}

	// 将mList保存为SP
	protected void mList2JA2SP(ArrayList<HashMap<String, Object>> mlist) {
		// TODO Auto-generated method stub

		JSONArray mJA = new JSONArray();
//		int count = 0;
//		int red = 0;
//		int yellow = 0;
//		int gray = 0;
		//没有必要 
		name = nameSP.getString("nurseName", "");
		Log.d("mList2JA2SP", name);
		ArrayList<HashMap<String, Object>> list = mlist;
		if(list==null) {
//			redNumber = 0;
//			yellowNumber = 0;
//			grayNumber = 0;
			SharedPreferences.Editor editor = projectSP.edit();
			editor.putString("DiaperMonitor", "");
			editor.commit();
		}
		else {
		int l = list.size();
		Log.d("mList2JA2SP", "list.size() = " + l);
		Log.d("mList2JA2SP", "list内容 = " + list.toString());
		for (int a = 0; a < l; a++) {
			JSONObject mJO = new JSONObject(list.get(a));
			Log.d("mList2JA2SP", "mJO = " + mJO.toString());
//			while (list.get(a).get("alertState") != null) {
//				count = Integer.parseInt(list.get(a).get("alertState")
//						.toString());
//				Log.d("mList2JA2SP", "count = " + count);
//				switch (count) {
//				case 0:
//					red++;
//					// Log.d("mList2JA2SP", "red = "+red);
//					break;
//				case 1:
//					yellow++;
//					// Log.d("mList2JA2SP", "red = "+red);
//					break;
//				case 3:
//					gray++;
//					// Log.d("mList2JA2SP", "red = "+red);
//					break;
//				default:
//					break;
//				}
//				break;
//			}
			mJA.put(mJO);
		}
		SharedPreferences.Editor editor = projectSP.edit();
		editor.putString("DiaperMonitor", mJA.toString());
		editor.commit();
		}
//		redNumber = red;
//		yellowNumber = yellow;
//		grayNumber = gray;
//		Log.d("mList2JA2SP",
//				"projectSP保存为 ="
//						+ projectSP.getString("DiaperMonitor", "").toString());
//		Log.d("统计", "red:" + red + ",yellow:" + yellow + ",gray:" + gray);
//		
	}

	// 从SP拿数据-------------------------------------------------------------
	// 警报类型,姓名,时间,状态按钮
	private ArrayList<HashMap<String, Object>> getData() {
		// TODO Auto-generated catch block
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map;
		int red = 0;
		int yellow = 0;
		int gray = 0;
		if (projectSP.getString("DiaperMonitor", "").equals("")) {
			Log.d("getData", "projectSP.getString-DiaperMonitor为空");
			map = new HashMap<String, Object>();
			JSONObject mJO = new JSONObject();
			// 应该全部保存成字符串
			map.put("patientName", "用户示例");
			map.put("patientPhone", "");
			// map.put("alertType", mJO.optString("alertType"));
			map.put("alertType", 1);
			sysTime = format.format(System.currentTimeMillis());
			Log.e("getData", "sysTime=" +sysTime);
			map.put("recordTime", sysTime);
			map.put("nursePhone", mJO.optString("nursePhone"));
			// map.put("alertState", mJO.optString("alertState"));
			// map.put("dataID", mJO.optString("dataID"));
			map.put("alertState", 2);
			map.put("dataID", 0);
			map.put("nurseName", "");
			Log.d("getData", "mJO=" + mJO.toString());
			list.add(map);
		} else {
			try {
				projectJA = new JSONArray(projectSP.getString("DiaperMonitor",
						""));
			} catch (JSONException e) {
				Log.d("getData", "TRY走空了!!!");
				e.printStackTrace();
			}
			int l = projectJA.length();
			Log.d("getData", "projectSP.getString-DiaperMonitor不为空,长度为" + l);
			Log.d("getData", "projectJA =" + projectJA.toString());
			int count = 0;
			
			for (int i = 0; i < l; i++) {
				map = new HashMap<String, Object>();
				JSONObject mJO;
				try {
					mJO = projectJA.getJSONObject(i);
					map.put("patientName", mJO.optString("patientName"));
					map.put("patientPhone", mJO.optString("patientPhone"));
					map.put("alertType", mJO.optInt("alertType"));
					map.put("recordTime", mJO.optString("recordTime"));
					map.put("nursePhone", mJO.optString("nursePhone"));					
					map.put("alertState", mJO.optInt("alertState"));
					map.put("dataID", mJO.optInt("dataID"));
					map.put("nurseName", mJO.optString("nurseName"));
					Log.d("getData", "mJO =" + mJO.toString());
					
					count = ( mJO.optInt("alertState"));
					
							switch (count) {
							case 0:
								red++;
								// Log.d("mList2JA2SP", "red = "+red);
								break;
							case 1:
								yellow++;
								// Log.d("mList2JA2SP", "red = "+red);
								break;
							case 3:
								gray++;
								// Log.d("mList2JA2SP", "red = "+red);
								break;
							default:
								break;
							}
							
							
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("getData", "list.add(map) will" );
				list.add(map);
				Log.d("getData", "list.add(map) over" );	
			}

		}
		Log.d("getData", "list.toString()=" +list.toString());
		redNumber = red;
		yellowNumber = yellow;
		grayNumber = gray;
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
					setMainView();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	private void setMainView() throws JSONException {
		// TODO Auto-generated method stub
		mList = getData();
		adapter = new MyAdapter(this, mList);
		mListviewCareds.setAdapter(adapter);
		numRed.setText("" + redNumber);
		numYellow.setText("" + yellowNumber);
		numGray.setText("" + grayNumber);
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



	private void clearDialog() {
		// TODO Auto-generated method stub

		Log.e(" clearDialog", "被调用了");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("清屏")
				.setMessage("清除已经完成的护理通知")
				.setCancelable(true)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						ArrayList<HashMap<String, Object>> tempList = new ArrayList<HashMap<String,Object>>();
						if (mList == null) {
							//无动作
						} else {
							int l = mList.size();
							for (int i = 0; i < l; i++) {
								if (mList.get(i).get("alertState").equals(2)) {
									// 无操作,不添加map数据
									Log.e("clearDialog", "alertState="
											+ mList.get(i).get("alertState")
													.toString() + ",无动作 ");
								} else {
									Log.e("clearDialog", "alertState="
											+ mList.get(i).get("alertState")
													.toString() + ",添加map ");
									HashMap<String, Object> tempMap = new HashMap<String, Object>(
											mList.get(i));
									tempList.add(tempMap);
								}
							}
							mList = tempList;
							// Log.e("clearDialog",
							// "tempList = "+tempList.toString());
							mList2JA2SP(mList);
							Log.e("clearDialog", "projectSP = "
									+ projectSP.getString("DiaperMonitor", "")
											.toString());
							//adapter.notifyDataSetChanged();
							try {
								setMainView();
								Log.e("clearDialog", "setMainView() ");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								Log.e("clearDialog", "setMainView()出现异常 ");
								e.printStackTrace();
							}
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).show();
	}
	
	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
	    public void gotResult(int code, String alias, Set<String> tags) {
	        String logs ;
	        String TAG = "mAliasCallback";
	        switch (code) {
	        case 0:
	            logs = "Set tag and alias success";
	            Log.e(TAG, logs);
	            Toast.makeText(getApplicationContext(), "欢迎使用,您辛苦了~", Toast.LENGTH_SHORT).show();
	            tagFlag = true;
//	            SharedPreferences sp = context.getSharedPreferences("alarmclient", MODE_PRIVATE);
//	            SharedPreferences.Editor editor = sp.edit();
//	            editor.putBoolean("firststart", false);
//	            editor.putString("alias", alias);
//	            editor.commit();
//	            Intent intent = new Intent(context, MainAlarm.class);
//	            startActivity(intent);
//	            Login.this.finish();
	            break;
	        case 6002:
	            logs = "Failed to set alias and tags due to timeout";
	            Log.e(TAG, logs);
	            Toast.makeText(getApplicationContext(), "请确认此手机的号码是否填写正确", Toast.LENGTH_LONG).show();
//	            Intent intent1 = new Intent(Login.this, LoginError.class);
//	    		String login_error = "请检查手机网络是否正常";
//	    		intent1.putExtra("login_error", login_error);
//	    		startActivity(intent1);
	            break;
	        default:
	            logs = "Failed with errorCode = " + code;
	            Log.e(TAG, logs);
//	            Intent intent2 = new Intent(Login.this, LoginError.class);
//	    		String login_error2 = "请检查手机网络是否正常";
//	    		intent2.putExtra("login_error", login_error2);
//	    		startActivity(intent2);
	            break;
	        }
	    }
	};  

}
