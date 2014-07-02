package com.my.activity.sq;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amima.pic.R;
import com.amima.ui.base.BaseActivity;
import com.my.activity.sq.adapter.UserAdapter;
import com.my.activity.sq.bean.UserBean;
import com.my.android.http.AsyncHttpResponseHandler;
import com.my.android.http.RequestParams;
import com.my.comm.MyScrollLayout;
import com.my.comm.OnViewChangeListener;
import com.my.comm.SelectAddPopupWindow;
import com.my.comm.SelectPicPopupWindow;
import com.my.dialog.ProgressDlg;
import com.my.util.Constants;
import com.my.util.HttpConnection;
import com.my.util.Protocol;
import com.my.util.SharedPreTools;
import com.my.util.SystemOut;
import com.my.util.Tools;

public class TestActivity extends BaseActivity implements OnViewChangeListener,
		OnClickListener {
	private MyScrollLayout mScrollLayout;
	private LinearLayout[] mImageViews;
	private int mViewCount;
	private int mCurSel;
	private ImageView set;
	private ImageView add;

	private TextView shipin;
	private TextView faxian;
	private TextView tongxunlu;

	private boolean isOpen = false;

	private ListView listview1;
	private ListView listview2;
	
	private ArrayList<UserBean> userList = new ArrayList<UserBean>();
	private UserAdapter userAdapter;

	private static final int LOGINSUCCESS = 1;
	private static final int LOGINFAIL = 2;
	private static final int LOGINTIMEOUT = 3;
	private static final int LISTSUCCESS = 4;
	private static final int LISTFAIL = 5;
	private static final int LISTTIMEOUT = 6;

	// 自定义的弹出框类
	SelectPicPopupWindow menuWindow; // 弹出框
	SelectAddPopupWindow menuWindow2; // 弹出框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		init();
//		autoLogin(this);
	
	}

	/*
	 * private ArrayList<UserBean> getContact() { ArrayList<UserBean> hcList =
	 * new ArrayList<UserBean>(); UserBean c0 = new UserBean();
	 * c0.setTxPath(R.drawable.icon + ""); c0.setName("服务号");
	 * 
	 * hcList.add(c0);
	 * 
	 * return hcList; }
	 * 
	 * private ArrayList<HuiHua> getHuahui() { ArrayList<HuiHua> hhList = new
	 * ArrayList<HuiHua>(); HuiHua h1 = new HuiHua();
	 * h1.setDoctorAvatar(Tools.getUrl("/resources/images/img200210.jpg"));
	 * h1.setDoctorName("测试"); h1.setRemark("remark"); h1.setConsultTime("");
	 * 
	 * hhList.add(h1); return hhList; }
	 */

	private void autoLogin(Context context) {
		String preKey = SharedPreTools.readShare(Constants.MY_LOGIN_SHARE_NAME,
				Constants.MY_LOGIN_USER_PRE_KEY);
		if (preKey == null || preKey.length() < 1) {
			preKey = Tools.getKey(context);
		}
		RequestParams params = new RequestParams();
		params.put("userKey", preKey);
		String url = Tools.getUrl(Protocol.URL_GET_USER);
		HttpConnection.HttpClientPost(url, params,
				loginAsyncHttpResponseHandler);
		

	}
	
	public void addList(final ArrayList<UserBean> list){
		SystemOut.out("list size : "+list.size());
	
	
				userList.addAll(list);


				userAdapter.notifyDataSetChanged();
//		userAdapter.getList().clear();
//		//userList.clear();
//		userAdapter.getList().addAll(list);
//		userAdapter.notifyDataSetChanged();
		
		SystemOut.out("list1 size : "+userAdapter.getList().size());
		
//		((UserAdapter)listview1.getAdapter()).notifyDataSetChanged();
	}

	private void init() {
		set = (ImageView) findViewById(R.id.set111);
		listview1 = (ListView) findViewById(R.id.listView111);

		// HuihuaAdapter ha = new HuihuaAdapter(this, getHuahui());
		// ha.setmListView(listview1);
		// listview1.setAdapter(ha);
		// listview1.setCacheColorHint(0);

		// ContactAdapter hc = new ContactAdapter(this, getContact());
		// listview2.setAdapter(hc);
		// listview2.setCacheColorHint(0);
		

		userAdapter = new UserAdapter(this, userList);
		userAdapter.setmListView(listview1);
		listview1.setAdapter(userAdapter);
//		listview1.setCacheColorHint(0);
		
		UserBean h1 = new UserBean();
		h1.setAvatar(Tools.getUrl("/resources/images/img200210.jpg"));
		h1.setUserName("test");
		h1.setLoginName("test1");
		userList.add(h1);
		userAdapter.notifyDataSetChanged();
		
		
		


		
		 set.setOnClickListener(new View.OnClickListener() {
		 @Override
		 public void onClick(View arg0) {
				UserBean h1 = new UserBean();
				h1.setAvatar(Tools.getUrl("/resources/images/img200210.jpg"));
				h1.setUserName("testa");
				h1.setLoginName("testaa1");
				
//				userList.clear();
				userList.add(h1);
				userAdapter.setList(userList);
				SystemOut.out("zzzzzzzzzz");
		 }
		 });
		
	
	}

	public void viewList(JSONArray arr) {
		//if (true) return;
//		if (arr == null || arr.length() < 1)
//			return;

		ArrayList<UserBean> hhList = new ArrayList<UserBean>();

		for (int i = 0; i <5; i++) {
			UserBean h1 = new UserBean();
			h1.setAvatar(Tools.getUrl("/resources/images/img200210.jpg"));
			h1.setUserName("test"+i);
			h1.setLoginName("test1"+i);
			hhList.add(h1);
//			try {
//				JSONObject obj = arr.getJSONObject(i);
//
//				if (obj != null) {
//					UserBean h1 = new UserBean();
//					h1.setAvatar(Tools.getUrl(getJsonAttr(obj, "avatar")));
//					h1.setUserName(getJsonAttr(obj, "userName"));
//					h1.setLoginName(getJsonAttr(obj, "loginName"));
//					h1.setLoginName(getJsonAttr(obj, "mobile"));
//					h1.setLoginName(getJsonAttr(obj, "lastLogin"));
//					h1.setLoginName(getJsonAttr(obj, "workStatus"));
//					h1.setLoginName(getJsonAttr(obj, "dn"));
//					h1.setLoginName(getJsonAttr(obj, "confNo"));
//					h1.setLoginName(getJsonAttr(obj, "expire"));

//					UserBean h1 = new UserBean();
//					h1.setAvatar(Tools.getUrl("/resources/images/img200210.jpg"));
//					h1.setUserName("test");
//					h1.setLoginName("test1");
//					hhList.add(h1);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
		
//		userAdapter = new UserAdapter(this, userList);
//		userAdapter.setmListView(listview1);
//		listview1.setAdapter(userAdapter);

//		SystemOut.out(userList+"!!!!!!!!!!!!!!!!!!!!!!!!11");
		addList(hhList);
		
		//userAdapter.getList().addAll(hhList);
//		userList.addAll(hhList);
		//userAdapter.setList(userList);
//		userAdapter.notifyDataSetChanged();

//		UserAdapter ha = new UserAdapter(this, hhList);
//		ha.setmListView(listview1);
//		listview1.setAdapter(ha);
//		listview1.setCacheColorHint(0);
//		
//		SystemOut.out("view list end ......................................");
	}

	public void uploadImage(final Activity context) {
		menuWindow = new SelectPicPopupWindow(TestActivity.this,
				itemsOnClick);
		// 显示窗口
		menuWindow.showAtLocation(TestActivity.this.findViewById(R.id.set),
				Gravity.TOP | Gravity.RIGHT, 10, 230); // 设置layout在PopupWindow中显示的位置
	}

	public void uploadImage2(final Activity context) {
		menuWindow2 = new SelectAddPopupWindow(TestActivity.this,
				itemsOnClick2);
		// 显示窗口
		menuWindow2.showAtLocation(TestActivity.this.findViewById(R.id.set),
				Gravity.TOP | Gravity.RIGHT, 10, 230); // 设置layout在PopupWindow中显示的位置
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
		}
	};

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick2 = new OnClickListener() {

		public void onClick(View v) {
			menuWindow2.dismiss();
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
			faxian.setTextColor(Color.BLACK);
			tongxunlu.setTextColor(Color.BLACK);
		} else if (index == 1) {
			shipin.setTextColor(Color.BLACK);
			faxian.setTextColor(0xff228B22);
			tongxunlu.setTextColor(Color.BLACK);
		} else {
			shipin.setTextColor(Color.BLACK);
			faxian.setTextColor(Color.BLACK);
			tongxunlu.setTextColor(0xff228B22);
		}
	}

	@Override
	public void OnViewChange(int view) {
		// TODO Auto-generated method stub
		setCurPoint(view);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int pos = (Integer) (v.getTag());
		setCurPoint(pos);
		mScrollLayout.snapToScreen(pos);
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
			ProgressDlg.showDlg(TestActivity.this, "正在连接服务器.......");
			super.onStart();
		}

		@Override
		public void onFinish() {
			SystemOut.out("完成");
			ProgressDlg.cancleDlg(TestActivity.this);
			super.onFinish();
		}

		@Override
		public void onSuccess(String content) {

			try {
				JSONObject json = new JSONObject(content);

				if (json != null) {

					String code =  getJsonAttr(json, "code");
					
					if ("0".equals(code)) {
						JSONArray arr = json.getJSONArray("list");

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
			ProgressDlg.showDlg(TestActivity.this, "正在获取数据......");
			super.onStart();
		}

		@Override
		public void onFinish() {
			SystemOut.out("完成");
			ProgressDlg.cancleDlg(TestActivity.this);
			super.onFinish();
		}

		@Override
		public void onSuccess(String content) {
			try {
				JSONObject json = new JSONObject(content);

				if (json != null) {

					String code = (String) getJsonAttr(json, "code");
					if ("0".equals(code)) {
						JSONObject obj = json.getJSONObject("data");
						String id = (String) getJsonAttr(obj, "id");
						String loginName = (String) getJsonAttr(obj,
								"loginName");
						SharedPreTools.writeShare(
								Constants.MY_LOGIN_SHARE_NAME,
								Constants.MY_LOGIN_USER_PRE_KEY, loginName);
						SharedPreTools.writeShare(
								Constants.MY_LOGIN_SHARE_NAME,
								Constants.MY_LOGIN_USER_PRE_ID, id);

						Message msg = handler.obtainMessage();
						msg.what = LOGINSUCCESS;
						Bundle data = new Bundle();
						data.putString("id", id);
						data.putString("userKey", loginName);
						msg.setData(data);
						handler.sendMessage(msg);
					} else {
						Message msg = handler.obtainMessage();
						msg.what = LOGINFAIL;
						handler.sendMessage(msg);
						SystemOut.out("失败：" + content);
					}
				} else {
					Message msg = handler.obtainMessage();
					msg.what = LOGINFAIL;
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
			msg.what = LOGINFAIL;
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
				// Toast.makeText(Sq_MainActivity.this, "登录成功",
				// Toast.LENGTH_SHORT)
				// .show();
				
				Bundle data = msg.getData();
				String id = data.getString("id");
				RequestParams params = new RequestParams();
				params.put("id", "11");
//				params.put("id", id);
				params.put("pageNo", "1");
				params.put("pageSize", "10");
				String url = Tools.getUrl(Protocol.URL_LIST_DOCTOR);
				HttpConnection.HttpClientPost(url, params,
						listAsyncHttpResponseHandler);
				break;
			case LOGINFAIL:
				Toast.makeText(TestActivity.this, "网络连接错误！",
						Toast.LENGTH_SHORT).show();
				break;
			case LOGINTIMEOUT:
				AlertDialog.Builder tpromptDialog = new AlertDialog.Builder(
						TestActivity.this);
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
				
				break;
			case LISTFAIL:
			case LISTTIMEOUT:
				Toast.makeText(TestActivity.this, "获取数据失败！",
						Toast.LENGTH_SHORT).show();
				break;
			}
			
			 super.handleMessage(msg);
		}
	};

}
