package com.my.activity.sq.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amima.app.widget.HeadImageView;
import com.amima.pic.R;
import com.my.activity.sq.bean.RecordBean;
import com.my.util.Tools;

public class RecordAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<RecordBean> list = new ArrayList<RecordBean>();
	protected ListView mListView;

	public ArrayList<RecordBean> getList() {
		return list;
	}

	public void setList(ArrayList<RecordBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public ListView getmListView() {
		return mListView;
	}

	public void setmListView(ListView mListView) {
		this.mListView = mListView;
	}

	public RecordAdapter(Context context, ArrayList<RecordBean> list) {
		this.context = context;
		this.list = list;
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
		final RecordBean record = list.get(position);
		H h = null;
		if (view == null) {
			h = new H();
			view = LayoutInflater.from(context).inflate(R.layout.user, parent,
					false);
			h.pic = (HeadImageView) view.findViewById(R.id.userImg);
			h.title = (TextView) view.findViewById(R.id.userName);
			h.content = (TextView) view.findViewById(R.id.userRemark);
			h.time = (TextView) view.findViewById(R.id.time);
			view.setTag(h);
		} else {
			h = (H) view.getTag();
		}

		h.pic.setDefaultImage(R.drawable.avt_doctor_male);
		if (record.getUserAvatar() != null)
			h.pic.setImageUrl(record.getUserAvatar(), position, mListView,
					null, 0);

		String title = record.getUserName();
		if (title!=null&&title.length() > 10)
			title = title.substring(0, 10);
		
		h.title.setText(title);

		String content = record.getRemark();
		if (content.length() > 15) {
			content = content.substring(0, 15);
		}
		h.content.setText(record.getRemark());

		String time = record.getConsultTime();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			time = Tools.timeDesc(sdf.parse(time));
		} catch (Exception e) {
			e.printStackTrace();
		}
		h.time.setText(time);

		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

		return view;
	}

	class H {
		HeadImageView pic;
		TextView title;
		TextView content;
		TextView time;
	}

}
