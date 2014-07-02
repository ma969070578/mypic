package com.my.activity.sq;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amima.app.MyApp;
import com.amima.pic.R;
import com.amima.ui.base.BaseActivity;
import com.my.activity.sq.adapter.InfoAdapter;
import com.my.activity.sq.adapter.UserAdapter;
import com.my.activity.sq.bean.InfoBean;
import com.my.activity.sq.bean.SqInfoBean;
import com.my.activity.sq.bean.UserBean;
import com.my.activity.sq.popup.UserPopupWindow;
import com.my.android.http.AsyncHttpResponseHandler;
import com.my.android.http.RequestParams;
import com.my.comm.MyScrollLayout;
import com.my.comm.OnViewChangeListener;
import com.my.dialog.ProgressDlg;
import com.my.download.AutoUpdater;
import com.my.util.Constants;
import com.my.util.HttpConnection;
import com.my.util.Protocol;
import com.my.util.SharedPreTools;
import com.my.util.SystemOut;
import com.my.util.Tools;
import com.my.widget.HeadImageView;

public class Sq_MainActivity extends BaseActivity implements
		OnViewChangeListener, OnClickListener {
	private MyScrollLayout mScrollLayout;
	private LinearLayout[] mImageViews;
	private int mViewCount;
	private int mCurSel;
	private ImageView set;
	// private ImageView add;

	private TextView shipin;
	private TextView info;
	private TextView me;

	private boolean isOpen = false;
	boolean isfinishing = true;
	boolean infoIsfinishing = true;

	private ListView listMainView1;
	private ListView listInfoView;

	private ArrayList<UserBean> userList = new ArrayList<UserBean>();
	private UserAdapter userAdapter;

	private ArrayList<InfoBean> infoList = new ArrayList<InfoBean>();
	private InfoAdapter infoAdapter;

	private TextView infoCount;

	private int page = 1;
	int pageSize = 10;
	int totalPage = Integer.MAX_VALUE;

	private int infoPage = 1;
	int infoPageSize = 10;
	int infoTotalPage = Integer.MAX_VALUE;

	private HeadImageView mMyAvatar;
	private TextView mMyName;
	private TextView mMyKey;
	private LinearLayout mAddressLayout;
	private TextView mAddressDesc;
	private LinearLayout mSettingsLayout;
	private LinearLayout mZxLayout;

	private static final int LOGINSUCCESS = 1;
	private static final int LOGINFAIL = 2;
	private static final int LOGINTIMEOUT = 3;
	private static final int LISTSUCCESS = 4;
	private static final int LISTFAIL = 5;
	private static final int LISTTIMEOUT = 6;
	private static final int INFOSUCCESS = 7;
	private static final int INFOFAIL = 8;
	private static final int INFOTIMEOUT = 9;
	private static final int COUNTSUCCESS = 10;
	private static final int COUNTFAIL = 11;
	private static final int COUNTTIMEOUT = 12;

	// 自定义的弹出框类
	UserPopupWindow menuWindow; // 弹出框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();

		autoLogin(this);

		TextView mLocationResult = (TextView) findViewById(R.id.addressDesc);
		((MyApp) getApplication()).mLocationResult = mLocationResult;

	}

	private void autoLogin(Context context) {

		RequestParams params = new RequestParams();
		params.put("userKey", MyApp.userKey);
		String url = Tools.getUrl(Protocol.URL_GET_USER);
		HttpConnection.HttpClientPost(url, params,
				loginAsyncHttpResponseHandler);

	}

	private void init() {
		shipin = (TextView) findViewById(R.id.sp);
		info = (TextView) findViewById(R.id.faxian);
		me = (TextView) findViewById(R.id.textme);

		listMainView1 = (ListView) findViewById(R.id.listMainView1);
		listInfoView = (ListView) findViewById(R.id.listInfoView);

		mMyAvatar = (HeadImageView) findViewById(R.id.myAvatar);
		mAddressDesc = (TextView) findViewById(R.id.addressDesc);
		mSettingsLayout = (LinearLayout) findViewById(R.id.settingsLayout);
		mZxLayout = (LinearLayout) findViewById(R.id.zxLayout);

		mSettingsLayout.setOnClickListener(this);
		mZxLayout.setOnClickListener(this);

		userAdapter = new UserAdapter(this, userList);
		userAdapter.setmListView(listMainView1);
		listMainView1.setAdapter(userAdapter);
		listMainView1.setCacheColorHint(0);

		listMainView1.setOnScrollListener(new ScrollListener());

		infoAdapter = new InfoAdapter(this, infoList);
		infoAdapter.setmListView(listInfoView);
		listInfoView.setAdapter(infoAdapter);
		listInfoView.setCacheColorHint(0);

		listInfoView.setOnScrollListener(new InfoScrollListener());

		infoCount = (TextView) findViewById(R.id.infoCount);
		infoCount.setVisibility(View.GONE);

		// UserBean h1 = new UserBean();
		// h1.setAvatar(Tools.getUrl("/resources/images/img200210.jpg"));
		// h1.setUserName("test");
		// h1.setLoginName("test1");
		// userList.add(h1);
		// userAdapter.notifyDataSetChanged();

		mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lllayout);
		mViewCount = mScrollLayout.getChildCount();
		mImageViews = new LinearLayout[mViewCount];
		for (int i = 0; i < mViewCount; i++) {
			mImageViews[i] = (LinearLayout) linearLayout.getChildAt(i);
			mImageViews[i].setEnabled(true);
			mImageViews[i].setOnClickListener(this);
			mImageViews[i].setTag(i);
		}
		mCurSel = 0;
		mImageViews[mCurSel].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);

		set = (ImageView) findViewById(R.id.set);
		set.setVisibility(View.VISIBLE);
		set.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				viewSettings(Sq_MainActivity.this);
			}
		});

	}

	public void viewList(JSONArray arr) {
		if (arr == null || arr.length() < 1)
			return;
		ArrayList<UserBean> hhList = new ArrayList<UserBean>();

		for (int i = 0; i < arr.length(); i++) {
			// UserBean h1 = new UserBean();
			// h1.setAvatar(Tools.getUrl("/resources/images/img200210.jpg"));
			// h1.setUserName("test"+i);
			// h1.setLoginName("test1"+i);
			// hhList.add(h1);
			try {
				JSONObject obj = arr.getJSONObject(i);

				if (obj != null) {
					UserBean h1 = new UserBean();
					h1.setAvatar(Tools.getUrl(getJsonAttr(obj, "avatar")));
					h1.setUserName(getJsonAttr(obj, "userName"));
					h1.setLoginName(getJsonAttr(obj, "loginName"));
					h1.setMobile(getJsonAttr(obj, "mobile"));
					h1.setLastLogin(getJsonAttr(obj, "lastLogin"));
					h1.setWorkStatus(getJsonAttr(obj, "workStatus"));
					h1.setDn(getJsonAttr(obj, "dn"));
					h1.setConfNo(getJsonAttr(obj, "confNo"));
					h1.setExpire(getJsonAttr(obj, "expire"));

					hhList.add(h1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		userList.addAll(hhList);
		userAdapter.setList(userList);
	}

	public void viewInfo(JSONArray arr) {
		if (arr == null || arr.length() < 1)
			return;
		ArrayList<InfoBean> hhList = new ArrayList<InfoBean>();

		for (int i = 0; i < arr.length(); i++) {
			try {
				JSONObject obj = arr.getJSONObject(i);

				if (obj != null) {
					InfoBean info = new InfoBean();
					info.setPic(Tools.getUrl(getJsonAttr(obj, "pic")));
					info.setTitle(getJsonAttr(obj, "title"));
					info.setContent(getJsonAttr(obj, "content"));
					info.setId(getJsonAttr(obj, "id"));
					info.setCreateTime(getJsonAttr(obj, "createTime"));
					info.setType(getJsonAttr(obj, "type"));
					info.setReadFlag(getJsonAttr(obj, "readFlag"));
					info.setUrl(getJsonAttr(obj, "url"));
					info.setDescription(getJsonAttr(obj, "description"));

					hhList.add(info);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		infoList.addAll(hhList);
		infoAdapter.setList(infoList);
	}

	public void viewSettings(final Activity context) {
		menuWindow = new UserPopupWindow(Sq_MainActivity.this, itemsOnClick);

		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		float GESTURE_THRESHOLD_DP = 54.0f;
		final float scale = getResources().getDisplayMetrics().density;
		int sp = (int) (GESTURE_THRESHOLD_DP * scale + 0.5f);
		sp += (statusBarHeight + 2);

		// 显示窗口
		menuWindow.showAtLocation(Sq_MainActivity.this.findViewById(R.id.set),
				Gravity.TOP | Gravity.RIGHT, 5, sp); // 设置layout在PopupWindow中显示的位置
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
		}
	};

	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		mImageViews[mCurSel].setEnabled(true);
		mImageViews[index].setEnabled(false);
		mCurSel = index;

		if (index == 0) {
			shipin.setTextColor(0xff228B22);
			info.setTextColor(Color.BLACK);
			me.setTextColor(Color.BLACK);
		} else if (index == 1) {
			shipin.setTextColor(Color.BLACK);
			info.setTextColor(0xff228B22);
			me.setTextColor(Color.BLACK);
		} else {
			shipin.setTextColor(Color.BLACK);
			info.setTextColor(Color.BLACK);
			me.setTextColor(0xff228B22);
		}
	}

	@Override
	public void OnViewChange(int view) {
		// TODO Auto-generated method stub
		setCurPoint(view);

		if (!isOpen && view == 1) {
			getInfo();
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.settingsLayout:
			Intent setIntent = new Intent();
			setIntent.setClass(Sq_MainActivity.this, Sq_SettingActivity.class);
			startActivity(setIntent);
			break;
		case R.id.zxLayout:
			Intent zxIntent = new Intent();
			zxIntent.setClass(Sq_MainActivity.this, Sq_ZxActivity.class);
			startActivity(zxIntent);
			break;
		}

		// TODO Auto-generated method stub

		if (v.getTag() != null) {
			int pos = (Integer) (v.getTag());

			if (!isOpen && pos == 1) {
				getInfo();
			}

			setCurPoint(pos);
			mScrollLayout.snapToScreen(pos);

			infoCount.setText("");
			infoCount.setVisibility(View.GONE);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_MENU)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public String getJsonAttr(JSONObject obj, String key) throws Exception {
		if (obj == null)
			return null;
		if (!obj.has(key))
			return null;
		if ("null".equals(obj.getString(key)))
			return null;
		return obj.getString(key);
	}

	AsyncHttpResponseHandler listAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			SystemOut.out("开始");
			ProgressDlg.showDlg(Sq_MainActivity.this, "正在连接服务器.......");
			super.onStart();
		}

		@Override
		public void onFinish() {
			SystemOut.out("完成");
			ProgressDlg.cancleDlg(Sq_MainActivity.this);
			super.onFinish();
		}

		@Override
		public void onSuccess(String content) {

			try {
				JSONObject json = new JSONObject(content);

				if (json != null) {

					String code = getJsonAttr(json, "code");

					if ("0".equals(code)) {
						JSONArray arr = json.getJSONArray("list");
						int total = Integer.MAX_VALUE;
						try {
							total = Integer.parseInt(Tools.JSONString(json,
									"total"));
							totalPage = (total - 1) / pageSize + 1;
						} catch (Exception e) {
						}

						if (arr != null) {
							viewList(arr);
						}

						Message msg = handler.obtainMessage();
						msg.what = LISTSUCCESS;
						handler.sendMessage(msg);
					} else {
						Message msg = handler.obtainMessage();
						msg.what = LISTFAIL;
						handler.sendMessage(msg);
						SystemOut.out("失败：" + content);
					}
				} else {
					Message msg = handler.obtainMessage();
					msg.what = LISTFAIL;
					handler.sendMessage(msg);
					SystemOut.out("失败：" + content);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			SystemOut.out("成功：" + content);
			super.onSuccess(content);
		}

		@Override
		public void onFailure(Throwable error, String content) {
			Message msg = handler.obtainMessage();
			msg.what = LISTFAIL;
			handler.sendMessage(msg);
			SystemOut.out("失败：" + content);
			super.onFailure(error, content);
		}
	};

	AsyncHttpResponseHandler loginAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			SystemOut.out("开始");
			// ProgressDlg.showDlg(Sq_MainActivity.this, "正在获取数据......");
			super.onStart();
		}

		@Override
		public void onFinish() {
			SystemOut.out("完成");
			ProgressDlg.cancleDlg(Sq_MainActivity.this);
			super.onFinish();
		}

		@Override
		public void onSuccess(String content) {

			try {

				UserBean muser = UserBean.getUser(content);
				if (muser != null && !Tools.isNull(muser.getId())) {

					muser.setType(1);

					MyApp.user = muser;

					String id = muser.getId();
					SharedPreTools.writeShare(Constants.MY_LOGIN_SHARE_NAME,
							Constants.MY_LOGIN_USER_PRE_ID, id);

					SharedPreTools.writeShare(Constants.MY_LOGIN_SHARE_NAME,
							Constants.MY_LOGIN_USER_TYPE, "1");

					if (MyApp.user.getUserName() == null
							|| MyApp.user.getUserName().length() < 1) {

						ArrayList<SqInfoBean> mInfoList = MyApp.user
								.getListSq();

						if (mInfoList != null && mInfoList.size() > 0) {
							SqInfoBean minfo = mInfoList.get(0);

							if (minfo != null) {
								MyApp.user.setUserName(minfo.getName() + "用户");
							}
						}
					}

					Message msg = handler.obtainMessage();
					msg.what = LOGINSUCCESS;
					handler.sendMessage(msg);
				} else {
					String errorinfo = muser.getErrorInfo();
					Message msg = handler.obtainMessage();
					msg.what = LOGINFAIL;
					msg.obj = errorinfo;
					handler.sendMessage(msg);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			SystemOut.out("成功：" + content);
			super.onSuccess(content);
		}

		@Override
		public void onFailure(Throwable error, String content) {
			Message msg = handler.obtainMessage();
			msg.what = LOGINFAIL;
			handler.sendMessage(msg);
			SystemOut.out("失败：" + content);
			super.onFailure(error, content);
		}
	};

	AsyncHttpResponseHandler infoAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			SystemOut.out("开始");
			ProgressDlg.showDlg(Sq_MainActivity.this, "正在获取资讯信息......");
			super.onStart();
		}

		@Override
		public void onFinish() {
			SystemOut.out("完成");
			ProgressDlg.cancleDlg(Sq_MainActivity.this);
			super.onFinish();
		}

		@Override
		public void onSuccess(String content) {

			try {
				JSONObject json = new JSONObject(content);

				if (json != null) {

					String code = getJsonAttr(json, "code");
					if ("0".equals(code)) {
						JSONArray arr = json.getJSONArray("list");
						int total = Integer.MAX_VALUE;
						try {
							total = Integer.parseInt(Tools.JSONString(json,
									"total"));
							infoTotalPage = (total - 1) / infoPageSize + 1;
						} catch (Exception e) {
						}

						if (arr != null) {
							viewInfo(arr);
						}

						Message msg = handler.obtainMessage();
						msg.what = INFOSUCCESS;
						handler.sendMessage(msg);
					} else {
						Message msg = handler.obtainMessage();
						msg.what = INFOFAIL;
						handler.sendMessage(msg);
						SystemOut.out("失败：" + content);
					}
				} else {
					Message msg = handler.obtainMessage();
					msg.what = INFOFAIL;
					handler.sendMessage(msg);
					SystemOut.out("失败：" + content);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			SystemOut.out("成功：" + content);
			super.onSuccess(content);
		}

		@Override
		public void onFailure(Throwable error, String content) {
			Message msg = handler.obtainMessage();
			msg.what = LISTFAIL;
			handler.sendMessage(msg);
			SystemOut.out("失败：" + content);
			super.onFailure(error, content);
		}
	};

	AsyncHttpResponseHandler infoCountAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {
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

			try {
				JSONObject json = new JSONObject(content);

				if (json != null) {

					String code = getJsonAttr(json, "code");
					if ("0".equals(code)) {
						int count = 0;
						String c = getJsonAttr(json, "count");
						try {
							count = Integer.parseInt(c);
						} catch (Exception e) {
						}

						if (count > 0) {
							infoCount.setVisibility(View.VISIBLE);
							infoCount.setText("" + count);
						} else {
							infoCount.setVisibility(View.GONE);
						}

						Message msg = handler.obtainMessage();
						msg.what = COUNTSUCCESS;
						handler.sendMessage(msg);
					} else {
						Message msg = handler.obtainMessage();
						msg.what = COUNTFAIL;
						handler.sendMessage(msg);
						SystemOut.out("失败：" + content);
					}
				} else {
					Message msg = handler.obtainMessage();
					msg.what = COUNTFAIL;
					handler.sendMessage(msg);
					SystemOut.out("失败：" + content);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			SystemOut.out("成功：" + content);
			super.onSuccess(content);
		}

		@Override
		public void onFailure(Throwable error, String content) {
			Message msg = handler.obtainMessage();
			msg.what = COUNTFAIL;
			handler.sendMessage(msg);
			SystemOut.out("失败：" + content);
			super.onFailure(error, content);
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOGINSUCCESS:
				initUserInfo();
				getData();
				break;
			case LOGINFAIL:
				Toast.makeText(Sq_MainActivity.this, "网络连接错误！",
						Toast.LENGTH_SHORT).show();
				break;
			case LOGINTIMEOUT:
				AlertDialog.Builder tpromptDialog = new AlertDialog.Builder(
						Sq_MainActivity.this);
				tpromptDialog.setTitle("提示");
				tpromptDialog.setMessage("连接超时，请重试");
				tpromptDialog.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						});

				tpromptDialog.show();
				break;
			case LISTSUCCESS:
				isfinishing = true;
				RequestParams params1 = new RequestParams();
				params1.put("id", MyApp.user.getId());
				params1.put("pageNo", "1");
				params1.put("pageSize", "10");
				String url = Tools.getUrl(Protocol.URL_INFO_NEW);
				HttpConnection.HttpClientPost(url, params1,
						infoCountAsyncHttpResponseHandler);
				break;
			case LISTFAIL:
				isfinishing = true;
			case LISTTIMEOUT:
				isfinishing = true;
				Toast.makeText(Sq_MainActivity.this, "获取数据失败！",
						Toast.LENGTH_SHORT).show();
				break;
			case INFOSUCCESS:
				isOpen = true;
				infoIsfinishing = true;
				break;
			case INFOFAIL:
				infoIsfinishing = true;
				break;
			case INFOTIMEOUT:
				infoIsfinishing = true;
				Toast.makeText(Sq_MainActivity.this, "获取资讯失败！",
						Toast.LENGTH_SHORT).show();
				break;

			case COUNTSUCCESS:
				break;
			case COUNTFAIL:
				break;
			}

			super.handleMessage(msg);
		}
	};

	public void getInfo() {
		isOpen = true;
		if (infoPage > infoTotalPage) {
			Toast.makeText(Sq_MainActivity.this, "没有更多数据", Toast.LENGTH_SHORT)
					.show();
			infoIsfinishing = true;
		} else {
			RequestParams params = new RequestParams();
			String id = SharedPreTools.readShare(Constants.MY_LOGIN_SHARE_NAME,
					Constants.MY_LOGIN_USER_PRE_ID);
			params.put("id", id);
			params.put("pageNo", "" + (infoPage++));
			params.put("pageSize", "" + infoPageSize);
			String url = Tools.getUrl(Protocol.URL_INFO_LIST);
			HttpConnection.HttpClientPost(url, params,
					infoAsyncHttpResponseHandler);
		}
	}

	public void initUserInfo() {
		mMyAvatar = (HeadImageView) findViewById(R.id.myAvatar);
		mAddressDesc = (TextView) findViewById(R.id.addressDesc);

		if (MyApp.user.getAvatar() != null
				&& MyApp.user.getAvatar().length() > 0) {
			mMyAvatar.setImageUrl(MyApp.user.getAvatar(), mMyAvatar);
		} else {
			mMyAvatar.setImageResource(R.drawable.avt_user_male);
		}

		TextView myName = (TextView) findViewById(R.id.myName);
		if (MyApp.user.getUserName() != null
				&& MyApp.user.getUserName().length() > 0) {
			myName.setText(MyApp.user.getUserName());
		} else {
			myName.setText("未绑定社区用户");
		}

		if (MyApp.user.getCommunityName() != null
				&& MyApp.user.getCommunityName().length() > 0) {
			mAddressDesc.setText(MyApp.user.getCommunityName());
		}

		TextView myKey = (TextView) findViewById(R.id.myKey);
		myKey.setText(MyApp.user.showKey());

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		ProgressDlg.cancleDlg(Sq_MainActivity.this);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (MyApp.isAutoUpate) {
			AutoUpdater.CheckForUpdate(Sq_MainActivity.this, true);
		}
		// TextView addressTxt = (TextView) findViewById(R.id.addressDesc);
		// if (!Tools.isNull(MyApp.naddr)) {
		// addressTxt.setText(MyApp.naddr);
		// }
		SystemOut.out("lon" + MyApp.lon);
		SystemOut.out("lat" + MyApp.lat);
		SystemOut.out("naddr" + MyApp.naddr);
		super.onResume();
	}

	// 获取医生列表数据

	private void getData() {

		if (page > totalPage) {
			Toast.makeText(Sq_MainActivity.this, "没有更多数据", Toast.LENGTH_SHORT)
					.show();
			isfinishing = true;
		} else {
			RequestParams params = new RequestParams();
			params.put("id", MyApp.user.getId());
			params.put("pageNo", "" + (page++));
			params.put("pageSize", "" + pageSize);
			String url = Tools.getUrl(Protocol.URL_LIST_DOCTOR);
			HttpConnection.HttpClientPost(url, params,
					listAsyncHttpResponseHandler);
		}

	}

	// 滑动 加载
	private final class ScrollListener implements OnScrollListener {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			if (view.getLastVisiblePosition() >= view.getCount() - 2
					&& scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& isfinishing) {
				isfinishing = false;

				getData();

			}
		}

	}

	// 滑动 加载
	private final class InfoScrollListener implements OnScrollListener {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			if (view.getLastVisiblePosition() >= view.getCount() - 2
					&& scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& infoIsfinishing) {
				infoIsfinishing = false;

				getInfo();

			}
		}

	}

}
