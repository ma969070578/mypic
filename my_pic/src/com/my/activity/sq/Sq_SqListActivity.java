package com.my.activity.sq;

/**
 * 用户已绑定社区
 */
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amima.app.MyApp;
import com.amima.pic.R;
import com.amima.ui.base.BaseActivity;
import com.google.zxing.CaptureActivity;
import com.my.activity.sq.adapter.SqListAdapter;
import com.my.activity.sq.bean.SqInfoBean;
import com.my.android.http.AsyncHttpResponseHandler;
import com.my.android.http.RequestParams;
import com.my.dialog.ProgressDlg;
import com.my.util.HttpConnection;
import com.my.util.Protocol;
import com.my.util.SystemOut;
import com.my.util.Tools;

public class Sq_SqListActivity extends BaseActivity implements OnClickListener {

	private final int GET_SUCCESS = 1;
	private final int GET_FAIL = 2;
	private final int DATA_CHANGED = 3;

	private View footerView;
	private View listfooterView;
	View Progress_view;
	LayoutInflater mInflater;
	private RelativeLayout bindingLayout;
	private ListView listView;
	private boolean isfinishing = true;// 加载数据完成标示
	private int currentpage = 1;
	private int pagenum = 10;

	private SqListAdapter adapter;

	private ArrayList<SqInfoBean> sqBeanlist = new ArrayList<SqInfoBean>();

	private TextView tv_nodata;
	private int listsize;
	private ImageView image_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sq_list);
		bindViews();

	}

	private void bindViews() {
		
		image_back = (ImageView) findViewById(R.id.image_back);
		image_back.setImageDrawable(getResources().getDrawable(
				R.drawable.user_image_back));
		image_back.setOnClickListener(this);


		mInflater = LayoutInflater.from(this);

		listfooterView = mInflater.inflate(R.layout.my_sqlist_footer, null);
		footerView = mInflater.inflate(R.layout.my_list_footer, null);

		Progress_view = footerView.findViewById(R.id.Progress_view);

		bindingLayout = (RelativeLayout) listfooterView
				.findViewById(R.id.binding);
		listView = (ListView) findViewById(R.id.list1);

		listView.addFooterView(listfooterView);
		// listView.addFooterView(footerView);

		// footerView.setVisibility(View.GONE);
		// listView.setOnScrollListener(new MyOnScrollListener());
		sqBeanlist = app.user.getListSq();

		if (app.user.getListSq() != null) {
			// SystemOut.out(sqBeanlist.get(0).getName());
			listsize = app.user.getListSq().size();

		}
		adapter = new SqListAdapter(this, sqBeanlist);
		listView.setAdapter(adapter);

		bindingLayout.setOnClickListener(this);
		tv_nodata = (TextView) findViewById(R.id.tv_nodata);

		// if (!Tools.isNull(app.user.getId()))
		// getAllData(app.user.getId(), currentpage, pagenum);

	}

	/**
	 * @param id
	 * @param currentpage2
	 * @param pagenum2
	 */
	private void getAllData(String id, int currentpage2, int pagenum2) {

		RequestParams params = new RequestParams();
		params.put("id", id);
		params.put("flag", "0");
		params.put("type", "1");

		params.put("pageNo", currentpage2 + "");
		params.put("pageSize", pagenum2 + "");
		String url = Tools.getUrl(Protocol.URL_RECORD_LIST);
		HttpConnection.HttpClientPost(url, params, allAskHandler);
	}

	AsyncHttpResponseHandler allAskHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			ProgressDlg.showDlg(Sq_SqListActivity.this, "提交中...");
			SystemOut.out("开始");
			super.onStart();
		}

		@Override
		public void onFinish() {
			SystemOut.out("完成");
			ProgressDlg.cancleDlg(Sq_SqListActivity.this);
			super.onFinish();
		}

		@Override
		public void onSuccess(String content) {
			ProgressDlg.cancleDlg(Sq_SqListActivity.this);
			try {

				JSONObject obj = new JSONObject(content);
				String result = obj.optString("code");
				if ("0".equals(result)) {
					Message m = handler.obtainMessage();
					m.what = GET_SUCCESS;

					Sq_SqListActivity.this.handler.sendMessage(m);
				} else {
					Message m = handler.obtainMessage();
					m.what = GET_FAIL;
					Sq_SqListActivity.this.handler.sendMessage(m);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			SystemOut.out("成功：" + content);
			super.onSuccess(content);
		}

		@Override
		public void onFailure(Throwable error, String content) {
			Message msg = handler.obtainMessage();
			msg.what = GET_FAIL;
			handler.sendMessage(msg);
			SystemOut.out("失败：" + content);
			super.onFailure(error, content);
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.image_back:

			finish();

			break;

		case R.id.binding:

			Intent intent1 = new Intent();
			intent1.setClass(Sq_SqListActivity.this, CaptureActivity.class);
			startActivity(intent1);
			// startActivityForResult(new Intent(this, CaptureActivity.class),
			// GET_CODE);
			break;
		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_SUCCESS:
				List<SqInfoBean> all_list = (List<SqInfoBean>) msg.obj;
				if (all_list != null && all_list.size() > 0) {

					sqBeanlist.addAll(all_list);
				} else {
					Tools.showShortToast("暂无最新数据", Sq_SqListActivity.this);
				}

				footerView.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();

				break;

			case GET_FAIL:
				// AskHistoryBeanlist.clear();
				footerView.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
				if (currentpage <= 1 && sqBeanlist.size() <= 0) {
					tv_nodata.setVisibility(View.VISIBLE);
					tv_nodata.setText("暂无咨询记录");
				} else {
					Toast.makeText(getApplicationContext(), "暂无更多数据",
							Toast.LENGTH_SHORT).show();
				}

				break;
			case DATA_CHANGED:
				sqBeanlist = app.user.getListSq();
				adapter.notifyDataSetChanged();

				break;
			}
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (app.user.getListSq() != null
				&& listsize != app.user.getListSq().size()) {
			Message m = handler.obtainMessage();
			m.what = DATA_CHANGED;
			Sq_SqListActivity.this.handler.sendMessage(m);
		}

	}

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

				Progress_view.setVisibility(View.VISIBLE);
				getAllData(MyApp.user.getId(), currentpage, pagenum);
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
