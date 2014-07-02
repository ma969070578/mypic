package com.my.activity.sq;

/**
 * 列表
 */
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amima.pic.R;
import com.amima.ui.base.BaseActivity;
import com.my.activity.sq.adapter.AskHistoryAdapter;
import com.my.activity.sq.bean.AskHistoryBean;
import com.my.android.http.AsyncHttpResponseHandler;
import com.my.android.http.RequestParams;
import com.my.util.HttpConnection;
import com.my.util.Protocol;
import com.my.util.SystemOut;
import com.my.util.TimeTools;
import com.my.util.Tools;

public class Sq_ZxActivity extends BaseActivity implements OnClickListener {
	private View footerView;
	View Progress_view;
	LayoutInflater mInflater;
	private ListView listView;
	private boolean isfinishing = true;// 加载数据完成标示
	private int currentpage = 1;
	private int pagenum = 10;
	private TextView updataButton;
	private AskHistoryAdapter adapter;

	private ArrayList<AskHistoryBean> AskHistoryBeanlist = new ArrayList<AskHistoryBean>();

	private final int GET_SUCCESS = 1;
	private final int GET_FAIL = 2;
	private TextView tv_nodata;
	private TextView tv_message;
	private ImageView image_back;

	// id 281 有数据 分页有问题

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ask_history);

		image_back = (ImageView) findViewById(R.id.image_back);
		image_back.setImageDrawable(getResources().getDrawable(
				R.drawable.user_image_back));
		image_back.setOnClickListener(this);

		mInflater = LayoutInflater.from(this);
		footerView = mInflater.inflate(R.layout.my_list_footer, null);

		Progress_view = footerView.findViewById(R.id.Progress_view);
		listView = (ListView) findViewById(R.id.list1);
		listView.addFooterView(footerView);
		footerView.setVisibility(View.GONE);
		listView.setOnScrollListener(new MyOnScrollListener());

		updataButton = (TextView) footerView.findViewById(R.id.updata_button);
		// updataButton.setText("更多评价数据");

		adapter = new AskHistoryAdapter(this, AskHistoryBeanlist);
		listView.setAdapter(adapter);

		tv_message = (TextView) findViewById(R.id.asktime);
		tv_nodata = (TextView) findViewById(R.id.tv_nodata);

		if (!Tools.isNull(app.user.getId())) {
			if (Tools.isDebug) {
				app.user.setId("281");
			}
			getAllData(app.user.getId(), currentpage, pagenum);
		}

	}

	/**
	 * 获取所有数据
	 * 
	 * @id id 人员ID
	 * @param currentpage2
	 * @param pagenum2
	 * @type 用户类型 1患者 2医生
	 * @flag 咨询记录类型 -1或者无为所有0未归档1已归档
	 */
	private void getAllData(String id, int currentpage2, int pagenum2) {

		RequestParams params = new RequestParams();
		params.put("id", id);
		// params.put("flag", "0");
		params.put("type", "1");

		params.put("pageNo", currentpage2 + "");
		params.put("pageSize", pagenum2 + "");
		String url = Tools.getUrl(Protocol.URL_RECORD_LIST);
		HttpConnection.HttpClientPost(url, params, new allAskHandler());
	}

	/**
	 * 获取所有评价数据的异步请求
	 */
	private class allAskHandler extends AsyncHttpResponseHandler {
		@Override
		public void onStart() {
			super.onStart();
		}

		@Override
		public void onFinish() {
			isfinishing = true;
			super.onFinish();
		}

		@Override
		public void onSuccess(String content) {
			List<AskHistoryBean> tempAskHistoryBeanlist = new ArrayList<AskHistoryBean>();
			try {
				tempAskHistoryBeanlist = AskHistoryBean.getAskHistory(content);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// tempAskHistoryBeanlist.addAll(AskHistoryBeanFunctions
			// .getAskHistoryBean(content));
			isfinishing = true;
			if (tempAskHistoryBeanlist != null
					&& tempAskHistoryBeanlist.size() > 0) {
				currentpage++;
				Message m = mHandler.obtainMessage();
				m.what = GET_SUCCESS;
				m.obj = tempAskHistoryBeanlist;

				Sq_ZxActivity.this.mHandler.sendMessage(m);
			} else {
				Message m = mHandler.obtainMessage();
				m.what = GET_FAIL;
				Sq_ZxActivity.this.mHandler.sendMessage(m);
			}

			SystemOut.out("成功：" + content);
			super.onSuccess(content);
		}

		@Override
		public void onFailure(Throwable error, String content) {
			SystemOut.out("失败：" + content);
			Message m = mHandler.obtainMessage();
			m.what = GET_FAIL;
			mHandler.sendMessage(m);
			super.onFailure(error, content);
		}
	};

	/**
	 * Handler 消息处理
	 */
	@SuppressWarnings("unchecked")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_SUCCESS:
				List<AskHistoryBean> all_list = (List<AskHistoryBean>) msg.obj;
				if (all_list != null && all_list.size() > 0) {

					AskHistoryBeanlist.addAll(all_list);
				} else {
					Tools.showShortToast("暂无最新数据", Sq_ZxActivity.this);
				}

				footerView.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();

				String mtime = all_list.get(0).getTotalTime();
				if (!Tools.isNull(mtime)) {

					long thistime = Long.parseLong(mtime);
					mtime = TimeTools.formatStringDuring(thistime);
				}

				String message = "总计咨询" + all_list.get(0).getTotalNum()
						+ "次，时长" + mtime;
				tv_message.setText(message);
				break;

			case GET_FAIL:
				// AskHistoryBeanlist.clear();
				footerView.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
				if (currentpage <= 1 && AskHistoryBeanlist.size() <= 0) {
					tv_nodata.setVisibility(View.VISIBLE);
					tv_nodata.setText("暂无咨询记录");
				} else {
					Toast.makeText(getApplicationContext(), "暂无更多数据",
							Toast.LENGTH_SHORT).show();
				}

				break;

			}
		}
	};

	/**
	 * 
	 * ListView 滚动事件
	 * 
	 */
	private class MyOnScrollListener implements OnScrollListener {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (view.getLastVisiblePosition() >= view.getCount() - 1
					&& scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& isfinishing) {

				footerView.setVisibility(View.VISIBLE);
				isfinishing = false;
				updataButton.setVisibility(View.INVISIBLE);
				Progress_view.setVisibility(View.VISIBLE);
				getAllData(app.user.getId(), currentpage, pagenum);
			}
		}

	}

	/**
	 * 评价的点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.image_back:

			finish();

			break;

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Sq_ZxActivity.this.finish();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
