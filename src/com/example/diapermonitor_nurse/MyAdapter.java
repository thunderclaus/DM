//护工端 
package com.example.diapermonitor_nurse;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter{
    
    // 填充数据的list
    private ArrayList<HashMap<String, Object>> mData;
    // 用来导入布局
    private LayoutInflater inflater;
    	//private ArrayList<HashMap<String, Object>> list;

	private Bitmap stateRed;
	private Bitmap stateYellow;
	private Bitmap stateGray;
	private Bitmap stateBlue;
	private Bitmap alertWet;
	private Bitmap alertKey;
//	private Bitmap alertLowPow;
//	private Bitmap alertDrop;
   
    // 构造器
    public MyAdapter(Context context, ArrayList<HashMap<String, Object>> list){
        mData = list;
        inflater = LayoutInflater.from(context);

        alertWet = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow);
        alertKey = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow);
//        alertDrop= BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow);
//        alertLowPow =  BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow);

        stateRed = BitmapFactory.decodeResource(context.getResources(), R.drawable.red);
        stateYellow = BitmapFactory.decodeResource(context.getResources(), R.drawable.orange);
        stateGray = BitmapFactory.decodeResource(context.getResources(), R.drawable.gray);
        stateBlue= BitmapFactory.decodeResource(context.getResources(), R.drawable.blue);
    }
                              
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }
    
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mData.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.list_item, null);
            holder.alert = (ImageView)convertView.findViewById(R.id.item_Alert);//应该是多种图片的映射,需要重绘
            holder.name =(TextView)convertView.findViewById(R.id.item_Name) ;
            holder.state = (ImageView)convertView.findViewById(R.id.item_State);
        	holder.time = (TextView)convertView.findViewById(R.id.item_Time) ;
            
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        // 设置list中TextView的显示
       
        holder.alert.setImageBitmap(imageAlert(mData.get(position).get("alertType")));
        holder.name.setText(mData.get(position).get("patientName").toString());
        holder.state.setImageBitmap(imageState(mData.get(position).get("alertState")));
        
        holder.time.setText(mData.get(position).get("recordTime").toString());
        return convertView;
    }

	//报警图片
//  1   表示尿湿报警
//  2   表示按键报警
//  3   表示脱落报警
//  4   表示低电量报警
    private Bitmap imageAlert(Object object ) {
    	int i = (Integer) object; 
		switch (i) {
		case 1:
			return alertWet;
		case 2:
			return alertKey;
//		case 3:
//			return alertDrop;
//		case 4:
//			return alertLowPow;
		default:
			break;
		}// TODO Auto-generated method stub
		return null;
	}

	 //护理情况 
//  红 未护理
//  黄 我护理
//  灰 他护理
    private Bitmap imageState(Object object) {
		int i = (Integer) object; 
		switch (i) {
		case 0:
			return stateRed;
		case 1:
			return stateYellow;
		case 2:
			return stateGray;
		case 3:
			return stateBlue;
		default:
			break;
		}// TODO Auto-generated method stub
		return null;
	}


	final class ViewHolder{
    	ImageView alert;
    	TextView name;
    	ImageView state;
    	TextView time;

    }
}
