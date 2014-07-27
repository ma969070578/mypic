package com.amima.pic.ui;

/**
 * 列表
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amima.app.widget.CustomButton;
import com.amima.app.widget.XListView;
import com.amima.app.widget.XListView.IXListViewListener;
import com.amima.pic.R;
import com.amima.pic.ui.adapter.Pic_ServiceListAdapter;
import com.amima.ui.base.BaseActivity;
import com.my.activity.sq.bean.AskHistoryBean;
import com.my.activity.sq.bean.Pic_ServiceBean;
import com.my.android.http.AsyncHttpResponseHandler;
import com.my.android.http.RequestParams;
import com.my.util.HttpConnection;
import com.my.util.IntentUtil;
import com.my.util.Protocol;
import com.my.util.SystemOut;
import com.my.util.Tools;
import com.slidingmenu.lib.SlidingMenu;
import com.umeng.fb.FeedbackAgent;

public class Pic_MainActivity extends BaseActivity implements OnClickListener, IXListViewListener {
//	private View footerView;
//	View Progress_view;
//	LayoutInflater mInflater;
	private XListView listview;
//	private boolean isfinishing = true;// 加载数据完成标示
	private int currentpage = 1;
	private int pagenum = 10;
//	private TextView updataButton;
	private Pic_ServiceListAdapter adapter;

	private ArrayList<Pic_ServiceBean> AskHistoryBeanlist = new ArrayList<Pic_ServiceBean>();

	private final int GET_SUCCESS = 1;
	private final int GET_FAIL = 2;
	private TextView tv_nodata;
	private TextView tv_message;
	private ImageView image_back;

	private SlidingMenu slidingMenu = null;

	private ListView lvTitle;
	private SimpleAdapter lvAdapter;

	private LinearLayout llGoHome;
	private ImageButton imgLogin;
	private Button bn_refresh;

	private TextView mAboveTitle;
	private SlidingMenu sm;
	private boolean mIsTitleHide = false;
	private boolean mIsAnim = false;
	private final String LIST_TEXT = "text";
	private final String LIST_IMAGEVIEW = "img";
	private int mTag = 0;

	private CustomButton cbFeedback;
	private CustomButton cbAbove;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pic_main_list);

		image_back = (ImageView) findViewById(R.id.image_back);
		image_back.setImageDrawable(getResources().getDrawable(
				R.drawable.user_image_back));
		image_back.setOnClickListener(this);

//		mInflater = LayoutInflater.from(this);
//		footerView = mInflater.inflate(R.layout.my_list_footer, null);
//
//		Progress_view = footerView.findViewById(R.id.Progress_view);
//		listView = (ListView) findViewById(R.id.list1);
		
		listview = (XListView) findViewById(R.id.list_view);
		 
		listview.setPullLoadEnable(true);
		listview.setPullRefreshEnable(false);
//		
//		listView.addFooterView(footerView);
//		footerView.setVisibility(View.GONE);
//		listView.setOnScrollListener(new MyOnScrollListener());
		AskHistoryBeanlist = Tools.getServiceItem();
		adapter = new Pic_ServiceListAdapter(this, AskHistoryBeanlist);
		listview.setAdapter(adapter);

		tv_message = (TextView) findViewById(R.id.asktime);
		tv_nodata = (TextView) findViewById(R.id.tv_nodata);

		if (!Tools.isNull(app.user.getId())) {

			getAllData(app.user.getId(), currentpage, pagenum);
		}

		leftSlidingMenu();
		lvTitle = (ListView) findViewById(R.id.behind_list_show);
		cbFeedback = (CustomButton) findViewById(R.id.cbFeedback);
		cbFeedback.setOnClickListener(this);
		cbAbove = (CustomButton) findViewById(R.id.cbAbove);
		cbAbove.setOnClickListener(this);
		initListView();
	}

	// // [start]初始化函数
	// private void initSlidingMenu() {
	// setBehindContentView(R.layout.behind_slidingmenu);
	// // customize the SlidingMenu
	// sm = getSlidingMenu();
	// sm.setShadowWidthRes(R.dimen.shadow_width);
	// sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
	// // sm.setFadeDegree(0.35f);
	// sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	// sm.setShadowDrawable(R.drawable.slidingmenu_shadow);
	// //sm.setShadowWidth(20);
	// sm.setBehindScrollScale(0);
	// }

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(LIST_TEXT, getResources().getString(R.string.menuGood));
		map.put(LIST_IMAGEVIEW, R.drawable.dis_menu_handpick);
		list.add(map);
		map = new HashMap<String, Object>();
		map.put(LIST_TEXT, getResources().getString(R.string.menuNews));
		map.put(LIST_IMAGEVIEW, R.drawable.dis_menu_news);
		list.add(map);
		map = new HashMap<String, Object>();
		map.put(LIST_TEXT, getResources().getString(R.string.menuStudio));
		map.put(LIST_IMAGEVIEW, R.drawable.dis_menu_studio);
		list.add(map);
		map = new HashMap<String, Object>();
		map.put(LIST_TEXT, getResources().getString(R.string.menuBlog));
		map.put(LIST_IMAGEVIEW, R.drawable.dis_menu_blog);
		list.add(map);
		return list;
	}

	private void leftSlidingMenu() {
		// TODO Auto-generated method stub
		// 设置抽屉菜单
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN); // 触摸边界拖出菜单
		slidingMenu.setMenu(R.layout.behind_slidingmenu);// 设置抽屉菜单布局文件
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// 设置抽屉菜单的宽度
		// 将抽屉菜单与主页面关联起来
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
	}

	private void initListView() {
		lvAdapter = new SimpleAdapter(this, getData(),
				R.layout.behind_list_show, new String[] { LIST_TEXT,
						LIST_IMAGEVIEW },
				new int[] { R.id.textview_behind_title,
						R.id.imageview_behind_icon }) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub.
				View view = super.getView(position, convertView, parent);
				
//                toggle();
//               getSlidingMenu().showMenu();// show menu
               slidingMenu.showContent();//show content

				if (position == mTag) {
					view.setBackgroundResource(R.drawable.back_behind_list);
					lvTitle.setTag(view);
				} else {
					view.setBackgroundColor(Color.TRANSPARENT);
				}
				return view;
			}
		};
		lvTitle.setAdapter(lvAdapter);
		lvTitle.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// 重新加载数据
				mTag = arg2;
				lvAdapter.notifyDataSetChanged();
			}
		});
		// lvTitle.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// NavigationModel navModel = navs.get(position);
		// mAboveTitle.setText(navModel.getName());
		// current_page = navModel.getTags();
		// if (lvTitle.getTag() != null) {
		// if (lvTitle.getTag() == view) {
		// MainActivity.this.showContent();
		// return;
		// }
		// ((View) lvTitle.getTag())
		// .setBackgroundColor(Color.TRANSPARENT);
		// }
		// lvTitle.setTag(view);
		// view.setBackgroundResource(R.drawable.back_behind_list);
		// imgQuery.setVisibility(View.VISIBLE);
		// switch (position) {
		//
		// }
		// }
		// });

	}

	/**
	 * 获取所有数据
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
			if (tempAskHistoryBeanlist != null
					&& tempAskHistoryBeanlist.size() > 0) {
				currentpage++;
				Message m = mHandler.obtainMessage();
				m.what = GET_SUCCESS;
				m.obj = tempAskHistoryBeanlist;

				Pic_MainActivity.this.mHandler.sendMessage(m);
			} else {
				Message m = mHandler.obtainMessage();
				m.what = GET_FAIL;
				Pic_MainActivity.this.mHandler.sendMessage(m);
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
				
				onLoad();
				List<Pic_ServiceBean> all_list = (List<Pic_ServiceBean>) msg.obj;
				if (all_list != null && all_list.size() > 0) {

					AskHistoryBeanlist.addAll(all_list);
				} else {
					Tools.showShortToast("暂无最新数据", Pic_MainActivity.this);
				}

//				footerView.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();

				break;

			case GET_FAIL:
				// AskHistoryBeanlist.clear();
//				footerView.setVisibility(View.GONE);
				onLoad();
				adapter.notifyDataSetChanged();
				if (currentpage <= 1 && AskHistoryBeanlist.size() <= 0) {
					tv_nodata.setVisibility(View.VISIBLE);
					tv_nodata.setText("暂无咨询记录");
				} else {
					Toast.makeText(getApplicationContext(), "暂无数据",
							Toast.LENGTH_SHORT).show();
				}

				break;

			}
		}
	};

//	/**
//	 * 
//	 * ListView 滚动事件
//	 * 
//	 */
//	private class MyOnScrollListener implements OnScrollListener {
//
//		@Override
//		public void onScroll(AbsListView view, int firstVisibleItem,
//				int visibleItemCount, int totalItemCount) {
//		}
//
//		@Override
//		public void onScrollStateChanged(AbsListView view, int scrollState) {
//			if (view.getLastVisiblePosition() >= view.getCount() - 1
//					&& scrollState == OnScrollListener.SCROLL_STATE_IDLE
//					&& isfinishing) {
//
//				footerView.setVisibility(View.VISIBLE);
//				isfinishing = false;
//				Progress_view.setVisibility(View.VISIBLE);
//				// getAllData(app.user.getId(), currentpage, pagenum);
//			}
//		}
//
//	}

	/**
	 * 评价的点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.image_back:

//			finish();

			break;

		case R.id.cbFeedback:
			FeedbackAgent agent = new FeedbackAgent(this);
			agent.startFeedbackActivity();
			break;
		case R.id.cbAbove:
			IntentUtil.start_activity(this, Pic_AboutActivity.class);
			break;

		}
	}

	

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		getAllData(app.user.getId(), currentpage, pagenum);
	}
	
	
	public void onLoad() {
		listview.stopRefresh();
		listview.stopLoadMore();
		listview.setRefreshTime("刚刚");
	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Tools.appExit(Pic_MainActivity.this);

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
