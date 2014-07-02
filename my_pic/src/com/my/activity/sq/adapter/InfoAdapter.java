package com.my.activity.sq.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amima.app.MyApp;
import com.amima.pic.R;
import com.my.activity.sq.Sq_WebActivity;
import com.my.activity.sq.bean.InfoBean;
import com.my.android.http.AsyncHttpResponseHandler;
import com.my.android.http.RequestParams;
import com.my.util.HttpConnection;
import com.my.util.Protocol;
import com.my.util.SystemOut;
import com.my.util.Tools;
import com.my.widget.HeadImageView;

public class InfoAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<InfoBean> list = new ArrayList<InfoBean>();
	protected ListView mListView;

	private static final int GET_SUCCESS = 4;
	private static final int GET_FAIL = 5;

	public ArrayList<InfoBean> getList() {
		return list;
	}

	public void setList(ArrayList<InfoBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public ListView getmListView() {
		return mListView;
	}

	public void setmListView(ListView mListView) {
		this.mListView = mListView;
	}

	public InfoAdapter(Context context, ArrayList<InfoBean> list) {
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
		final InfoBean info = list.get(position);
		H h = null;
		if (view == null) {
			h = new H();
			view = LayoutInflater.from(context).inflate(R.layout.info, parent,
					false);
			h.pic = (HeadImageView) view.findViewById(R.id.infoImg);
			h.title = (TextView) view.findViewById(R.id.infoTitle);
			h.content = (TextView) view.findViewById(R.id.infoDesc);
			h.time = (TextView) view.findViewById(R.id.time);
			view.setTag(h);
		} else {
			h = (H) view.getTag();
		}

		h.pic.setDefaultImage(R.drawable.avt_doctor_male);
		if (info.getPic() != null)
			h.pic.setImageUrl(info.getPic(), position, mListView, null, 0);

		String title = info.getTitle();
		if (title.length() > 10)
			title = title.substring(0, 10);
		h.title.setText(title);

		// String content = info.getContent();
		// content = content.replaceAll("<[^>]*>", "");
		// if (content.length() > 10) {
		// content = content.substring(0, 10);
		// }
		h.content.setText(info.getDescription());

		String time = info.getCreateTime();
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

				String userid = MyApp.user.getId();
				if (!Tools.isNull(userid)) {
					changeInfoState(userid, info.getId());
				}

				Intent intent1 = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("url", info.getUrl());
				
				
				
				intent1.putExtras(bundle);

				intent1.setClass(context, Sq_WebActivity.class);
				context.startActivity(intent1);
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

	private void changeInfoState(String userid, String infoid) {

		RequestParams params = new RequestParams();
		params.put("userID", userid);
		params.put("id", infoid);
		String url = Tools.getUrl(Protocol.URL_INFO_READ);
		HttpConnection
				.HttpClientPost(url, params, listAsyncHttpResponseHandler);
	}

	AsyncHttpResponseHandler listAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			SystemOut.out("开始");
			super.onStart();
		}

		@Override
		public void onFinish() {
			SystemOut.out("完成");
			super.onFinish();
		}

		@Override
		public void onSuccess(String content) {

			try {
				JSONObject json = new JSONObject(content);

				if (json != null) {

					String code = Tools.JSONString(json, "code");

					if ("0".equals(code)) {
						SystemOut.out("标记咨询已读成功");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			SystemOut.out("成功：" + content);
			super.onSuccess(content);
		}

		@Override
		public void onFailure(Throwable error, String content) {

			super.onFailure(error, content);
		}
	};

}
