//������ 
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
    
    // ������ݵ�list
    private ArrayList<HashMap<String, Object>> mData;
    // �������벼��
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
   
    // ������
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
            // ���ViewHolder����
            holder = new ViewHolder();
            // ���벼�ֲ���ֵ��convertview
            convertView = inflater.inflate(R.layout.list_item, null);
            holder.alert = (ImageView)convertView.findViewById(R.id.item_Alert);//Ӧ���Ƕ���ͼƬ��ӳ��,��Ҫ�ػ�
            holder.name =(TextView)convertView.findViewById(R.id.item_Name) ;
            holder.state = (ImageView)convertView.findViewById(R.id.item_State);
        	holder.time = (TextView)convertView.findViewById(R.id.item_Time) ;
            
            // Ϊview���ñ�ǩ
            convertView.setTag(holder);
        } else {
            // ȡ��holder
            holder = (ViewHolder) convertView.getTag();
        }
        // ����list��TextView����ʾ
       
        holder.alert.setImageBitmap(imageAlert(mData.get(position).get("alertType")));
        holder.name.setText(mData.get(position).get("patientName").toString());
        holder.state.setImageBitmap(imageState(mData.get(position).get("alertState")));
        
        holder.time.setText(mData.get(position).get("recordTime").toString());
        return convertView;
    }

	//����ͼƬ
//  1   ��ʾ��ʪ����
//  2   ��ʾ��������
//  3   ��ʾ���䱨��
//  4   ��ʾ�͵�������
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

	 //������� 
//  �� δ����
//  �� �һ���
//  �� ������
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
