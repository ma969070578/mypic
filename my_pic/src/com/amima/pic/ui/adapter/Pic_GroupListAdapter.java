package com.amima.pic.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.amima.pic.R;
import com.my.activity.sq.bean.AskHistoryBean;
import com.my.util.Tools;
import com.my.widget.HeadImageView;

public class Pic_GroupListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<AskHistoryBean> list = new ArrayList<AskHistoryBean>();
	protected ListView mListView;

	public ArrayList<AskHistoryBean> getList() {
		return list;
	}

	public void setList(ArrayList<AskHistoryBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public ListView getmListView() {
		return mListView;
	}

	public void setmListView(ListView mListView) {
		this.mListView = mListView;
	}

	public Pic_GroupListAdapter(Context context, ArrayList<AskHistoryBean> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		if (list != null)
			return list.size();
		return 10;
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
		AskHistoryBean hh = list.get(position);
		H h = null;
		if (view == null) {
			h = new H();
			view = LayoutInflater.from(context).inflate(
					R.layout.ask_history_list_item, parent, false);
			h.mAsk_photo = (HeadImageView) view.findViewById(R.id.ask_photo);
			h.mAsk_name = (TextView) view.findViewById(R.id.ask_name);
			h.mMy_call = (Button) view.findViewById(R.id.ask_call);

			h.mAsk_time = (TextView) view.findViewById(R.id.ask_time);
			view.setTag(h);
		} else {
			h = (H) view.getTag();
		}

		String url = hh.getDoctorAvatar();
		h.mAsk_photo.setDefaultImage(R.drawable.avt_doctor_male);
		if (!Tools.isNull(url))
			h.mAsk_photo.setImageUrl(url, position, mListView, null, 0);

		h.mAsk_name.setText(hh.getDoctorName());
		h.mAsk_time.setText(hh.getConsultTime());
		return view;
	}

	class H {

		private HeadImageView mAsk_photo;

		private TextView mAsk_name;
		private Button mMy_call;
		private TextView mAsk_time;
	}
}
