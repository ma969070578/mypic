package com.my.activity.sq.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amima.app.widget.HeadImageView;
import com.amima.pic.R;
import com.my.activity.sq.bean.UserBean;

public class UserAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<UserBean> list = new ArrayList<UserBean>();
	protected ListView mListView;
	
	public ArrayList<UserBean> getList() {
		return list;
	}

	public void setList(ArrayList<UserBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}


	
	public ListView getmListView() {
		return mListView;
	}

	public void setmListView(ListView mListView) {
		this.mListView = mListView;
	}

	public UserAdapter(Context context,ArrayList<UserBean> list){
		this.context = context;
		this.list=list;
	}

	@Override
	public int getCount() {
		if (list != null)
			return list.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		UserBean hh = list.get(position);
		H h = null;
		if(view==null){
			h = new H();
			view = LayoutInflater.from(context).inflate(R.layout.listdoc, parent, false);
			h.pic = (HeadImageView)view.findViewById(R.id.avatar);
			h.name = (TextView)view.findViewById(R.id.name);
			h.call = (ImageView)view.findViewById(R.id.shipin);
			h.status=(ImageView)view.findViewById(R.id.doctorStatus);
			view.setTag(h);
		}else{
			h = (H)view.getTag();
		}
		
		
		h.pic.setDefaultImage(R.drawable.avt_doctor_male);
		h.pic.setImageUrl(hh.getAvatar(), position, mListView, null, 0);

		h.name.setText(hh.getUserName());
		
		if ("2".equals( hh.getWorkStatus())){
			h.call.setImageResource(R.drawable.user_anniu_zixun);
			h.status.setImageResource(R.drawable.icon_zaixian);
		}else{
			h.status.setImageResource(R.drawable.icon_manglu);
			h.call.setImageResource(R.drawable.user_anniu_zixun2);
		}
		
		return view;
	}

	class H{
		HeadImageView pic;
		TextView name;
		ImageView call;
		ImageView status;
	}
}
