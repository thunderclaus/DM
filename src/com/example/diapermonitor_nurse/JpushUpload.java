package com.example.diapermonitor_nurse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JpushUpload extends Thread{
private String jpushStr;
	
	public  JpushUpload(String jpushStr) {
		this.jpushStr = jpushStr;
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		upload() ;
	}

	public void upload() {
		try {
			Socket socket = new Socket("162.105.76.252", 2016);
			OutputStream output = socket.getOutputStream();
//			JSONObject sendJson = new JSONObject();
//			try {
//				sendJson.put("alert", jpushStr);
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			String sendStr = sendJson.toString();
			byte[] bytes =  jpushStr.getBytes("UTF-8");
			output.write(bytes);
			output.close();
			socket.close();
			Log.e("uploadJupush", "·¢ËÍ³É¹¦"+jpushStr);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
