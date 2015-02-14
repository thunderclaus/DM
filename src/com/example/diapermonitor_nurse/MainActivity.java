//������
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
	// ��������
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
		// �ж�name�Ƿ�Ϊ��,Ϊ���򵯳�����,������д�������
		// ����޸Ļ�����ŵ���Ӧ
			
		if(mMobile.equals("")) {
			tagFlag = false;
			nameDialog();
		}else {
		//��JPUSH��ע��TAG
		JPushInterface.setAliasAndTags(getApplicationContext(), mMobile , null, mAliasCallback);
		
		}
		
		Log.d("onCreate", "���ǰ" + nameSP.getString("nurseName", "") + "end");
		// �����ݼ��ϲ���
		// SharedPreferences.Editor editor01 = nameSP.edit();
		// editor01.clear();
		// editor01.commit();
		// Log.d("onCreate","��պ�"+nameSP.getString("nurseName", "")+"end");
		Log.d("onCreate", "�ж�nameSPΪ�����1 ����=" + nameSP.getString("nurseName", ""));
		
		Log.d("onCreate", "�ж�nameSPΪ�����2 ����=" + nameSP.getString("nurseName", ""));
		Log.d("onCreate", "�жϱ��� name=" + name);
		
		
		// //JA��ȡJPUSH����,���ͬ�����˶���������ô��

		
		//û�б�Ҫ��
//		Log.d("onCreate", "TRY===creat��ʼ����mList  ");
//		mList = getData();
//		Log.d("onCreate", "TRY===creat��ʼ����mList = " + mList.toString());
		
		
		// Log.d("onCreate","creat��ʼ��mList = "+mList.toString());
		mListviewCareds = (ListViewForScrollView) findViewById(R.id.mainactivy_list_Careds); /* ����һ����̬���� */

		// try {
		// setMainView();
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		
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
				//setTitle("��ѡ����" + arg2 + "���л���");// ���ñ�������ʾ�������

				int i = 3;
				HashMap<String, Object> item;
				item = mList.get(arg2);
				Log.d("onItemClick", "���ǰ mList.get(arg2)"+item.toString());
				if (nameSP.getString("nurseName", "").equals("")) {
					nameDialog();
					Toast.makeText(getApplicationContext(), "��ϵ����ʱ,��Ҫ��д������Ϣ",
							Toast.LENGTH_SHORT).show();
				} else {

					if (item.get("alertState").equals("")) {
						// ����б�Ϊ��,����Ӧ
					} else {
						i = (Integer) item.get("alertState");
						switch (i) {
						case 0:
							// 0->1
							//��ɫ
							// ˢ����Ӧ״̬����Ӧʱ��,��ӻ�����Ϣ
							item.remove("alertState");
							item.put("alertState", 1);
							sysTime = format.format(System.currentTimeMillis());
							item.remove("recordTime");
							item.put("recordTime", sysTime);
							item.put("nurseName", name);
							item.put("nursePhone", mMobile);
							Log.d("���", "��Ӧ:״̬Ϊ��0��1");
							// ��¼SP
							// ����mList,������JSONArray��ʽ����
							mList2JA2SP(mList);

							// ����(�߳�)
							Log.d("onItemClick", "����� mList.get(arg2)"+item.toString());
							send(item);//====================================================================================

							Toast.makeText(getApplicationContext(),
									"��л����Ӧ�������� !", Toast.LENGTH_SHORT).show();
							adapter.notifyDataSetChanged();

							break;
						case 1:
							// 1->2,ˢ��,ͬʱ��¼SP
							//��ɫ,�ͻ�ɫ	
							name = nameSP.getString("nurseName", "");
							Log.d("setOnItemClickListener", name);
							
								//������Լ�������
								item.remove("alertState");
								item.put("alertState", 2);
								sysTime = format.format(System
										.currentTimeMillis());
								item.remove("recordTime");
								item.put("recordTime", sysTime);
								item.put("nurseName",name);//����Լ�������
								Log.d("���", "��Ӧ:״̬Ϊ��1��2");
								// ��¼SP?????????????????��һ��Ӧ�÷���ǰ��
								// ����mList,������JSONArray��ʽ����
								mList2JA2SP(mList);
								// ����(�߳�)===============================================================
								send(item);

								Toast.makeText(getApplicationContext(),
										"��л��Ϊ�û������˻��� ! : )", Toast.LENGTH_SHORT).show();
								adapter.notifyDataSetChanged();
							
							break;
						case 2:
							// �޶���,��ɫ
							
							Toast.makeText(getApplicationContext(), "���û��Ļ����������~",
									Toast.LENGTH_SHORT).show();
							break;
						case 3:
							// �޶���,��ɫ
							Toast.makeText(getApplicationContext(), "���л���������Ӧ~",
									Toast.LENGTH_SHORT).show();
							Log.d("���", "3�����Լ�����Ӧ,�޶���");
							break;
						default:
							Log.e("���", "�����Լ�����Ӧ,�޶���");
							break;
						}
					}
				}
				try {
					Log.d("OnClickItem", "ִ��setMainView()");
					setMainView();
					//Log.d("OnClickItem" ,"ִ��setMainView() over");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d("OnClickItem", "ִ��setMainView() ����!!");
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
			Log.d("onstart", "ִ��setMainView() ready");
			setMainView();
			Log.d("onstart", "ִ��setMainView() over");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("onstart", "ִ��setMainView() ����!!");
			e.printStackTrace();
		}
		Log.d("onstart", "ִ����onstart");
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
		Log.d("onstop", "ִ����onstop");
		mList2JA2SP(mList);
		Log.d("onstop", "ִ��mList2JA2SP");

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
		Log.d("nameDialog", "��������");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("����,�����뱾�ֻ��ĺ���").setView(editName)
				.setCancelable(true)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

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
								"�Ѽ�¼nameSP.getString="
										+ nameSP.getString("nurseName", ""));
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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

	// ��mList����ΪSP
	protected void mList2JA2SP(ArrayList<HashMap<String, Object>> mlist) {
		// TODO Auto-generated method stub

		JSONArray mJA = new JSONArray();
//		int count = 0;
//		int red = 0;
//		int yellow = 0;
//		int gray = 0;
		//û�б�Ҫ 
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
		Log.d("mList2JA2SP", "list���� = " + list.toString());
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
//				"projectSP����Ϊ ="
//						+ projectSP.getString("DiaperMonitor", "").toString());
//		Log.d("ͳ��", "red:" + red + ",yellow:" + yellow + ",gray:" + gray);
//		
	}

	// ��SP������-------------------------------------------------------------
	// ��������,����,ʱ��,״̬��ť
	private ArrayList<HashMap<String, Object>> getData() {
		// TODO Auto-generated catch block
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map;
		int red = 0;
		int yellow = 0;
		int gray = 0;
		if (projectSP.getString("DiaperMonitor", "").equals("")) {
			Log.d("getData", "projectSP.getString-DiaperMonitorΪ��");
			map = new HashMap<String, Object>();
			JSONObject mJO = new JSONObject();
			// Ӧ��ȫ��������ַ���
			map.put("patientName", "�û�ʾ��");
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
				Log.d("getData", "TRY�߿���!!!");
				e.printStackTrace();
			}
			int l = projectJA.length();
			Log.d("getData", "projectSP.getString-DiaperMonitor��Ϊ��,����Ϊ" + l);
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



	private void clearDialog() {
		// TODO Auto-generated method stub

		Log.e(" clearDialog", "��������");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("����")
				.setMessage("����Ѿ���ɵĻ���֪ͨ")
				.setCancelable(true)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						ArrayList<HashMap<String, Object>> tempList = new ArrayList<HashMap<String,Object>>();
						if (mList == null) {
							//�޶���
						} else {
							int l = mList.size();
							for (int i = 0; i < l; i++) {
								if (mList.get(i).get("alertState").equals(2)) {
									// �޲���,�����map����
									Log.e("clearDialog", "alertState="
											+ mList.get(i).get("alertState")
													.toString() + ",�޶��� ");
								} else {
									Log.e("clearDialog", "alertState="
											+ mList.get(i).get("alertState")
													.toString() + ",���map ");
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
								Log.e("clearDialog", "setMainView()�����쳣 ");
								e.printStackTrace();
							}
						}
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
	            Toast.makeText(getApplicationContext(), "��ӭʹ��,��������~", Toast.LENGTH_SHORT).show();
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
	            Toast.makeText(getApplicationContext(), "��ȷ�ϴ��ֻ��ĺ����Ƿ���д��ȷ", Toast.LENGTH_LONG).show();
//	            Intent intent1 = new Intent(Login.this, LoginError.class);
//	    		String login_error = "�����ֻ������Ƿ�����";
//	    		intent1.putExtra("login_error", login_error);
//	    		startActivity(intent1);
	            break;
	        default:
	            logs = "Failed with errorCode = " + code;
	            Log.e(TAG, logs);
//	            Intent intent2 = new Intent(Login.this, LoginError.class);
//	    		String login_error2 = "�����ֻ������Ƿ�����";
//	    		intent2.putExtra("login_error", login_error2);
//	    		startActivity(intent2);
	            break;
	        }
	    }
	};  

}
