package com.amima.pic.ui.adapter;

import static com.nostra13.example.universalimageloader.Constants.IMAGES;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.amima.app.widget.HeadImageView;
import com.amima.pic.R;
import com.amima.pic.ui.Pic_GroupImageGridActivity;
import com.my.activity.sq.bean.Pic_ServiceBean;
import com.nostra13.example.universalimageloader.Constants.Extra;

public class Pic_ServiceListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Pic_ServiceBean> list = new ArrayList<Pic_ServiceBean>();
	protected ListView mListView;

	public ArrayList<Pic_ServiceBean> getList() {
		return list;
	}

	public void setList(ArrayList<Pic_ServiceBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public ListView getmListView() {
		return mListView;
	}

	public void setmListView(ListView mListView) {
		this.mListView = mListView;
	}

	public Pic_ServiceListAdapter(Context context,
			ArrayList<Pic_ServiceBean> list) {
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
		Pic_ServiceBean hh = list.get(position);
		H h = null;
		if (view == null) {
			h = new H();
			view = LayoutInflater.from(context).inflate(
					R.layout.pic_service_list_item, parent, false);
			h.mAsk_photo = (HeadImageView) view.findViewById(R.id.ask_photo);
			h.mAsk_name = (TextView) view.findViewById(R.id.ask_name);
			h.mMy_call = (Button) view.findViewById(R.id.ask_call);

			h.mAsk_time = (TextView) view.findViewById(R.id.ask_time);
			view.setTag(h);
		} else {
			h = (H) view.getTag();
		}

		h.mAsk_name.setText(hh.getTitle());
		h.mAsk_time.setText(hh.getIntroduce());

		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra(Extra.IMAGES, IMAGES);
				intent.setClass(context, Pic_GroupImageGridActivity.class);
				context.startActivity(intent);
			}
		});
		return view;
	}

	class H {

		private HeadImageView mAsk_photo;

		private TextView mAsk_name;
		private Button mMy_call;
		private TextView mAsk_time;
	}
}
