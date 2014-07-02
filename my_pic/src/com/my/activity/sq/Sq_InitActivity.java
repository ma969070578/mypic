package com.my.activity.sq;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Toast;

import com.amima.app.MyApp;
import com.amima.pic.R;
import com.amima.pic.ui.Pic_MainActivity;
import com.amima.ui.base.BaseActivity;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.my.util.Constants;
import com.my.util.SharedPreTools;
import com.my.util.SystemOut;
import com.my.util.Tools;

public class Sq_InitActivity extends BaseActivity {

	private LocationClient mLocClient;
	private boolean mLocationInit;
	private LocationMode mLocationMode;
	private boolean mLocationSequency;
	private int mScanSpan;
	private boolean mIsNeedAddress;
	private String mCoordType;
	private boolean mIsNeedDirection;

	private int mGeofenceType;
	private boolean mGeofenceInit;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.init_main, null);
		setContentView(view);

		String preKey = SharedPreTools.readShare(Constants.MY_LOGIN_SHARE_NAME,
				Constants.MY_LOGIN_USER_PRE_KEY);
		if (preKey == null || preKey.length() < 1) {
			preKey = Tools.getKey(Sq_InitActivity.this);
			SharedPreTools.writeShare(Constants.MY_LOGIN_SHARE_NAME,
					Constants.MY_LOGIN_USER_PRE_KEY, preKey);
		}
		MyApp.userKey = preKey;

		PackageInfo packageInfo = null;
		try {
			packageInfo = getApplicationContext().getPackageManager()
					.getPackageInfo(getPackageName(), 0);
			MyApp.localVersion = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mLocClient = ((MyApp) getApplication()).mLocationClient;

		getLocationParams(1);
		setLocationOption();

		// 开始定位
		if (mLocationInit) {
			mLocClient.start();
		} else {
			if (Tools.isDebug)
				Toast.makeText(this, "请设置定位相关的参数", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!mLocationSequency && mLocClient.isStarted()) {
			// 单次请求定位
			mLocClient.requestLocation();
		}

		redirectTo();
		if (true)
			return;
		// 渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		});

	}

	/**
	 * 跳转到...
	 */
	private void redirectTo() {
		String userType = SharedPreTools.readShare(
				Constants.MY_LOGIN_SHARE_NAME, Constants.MY_LOGIN_USER_TYPE);
		String id = SharedPreTools.readShare(Constants.MY_LOGIN_SHARE_NAME,
				Constants.MY_LOGIN_USER_PRE_ID);
		MyApp.user.setId(id);
		int type = 1;
		try {
			type = Integer.parseInt(userType);
		} catch (Exception e) {
		}
		MyApp.user.setType(type);
		SystemOut.out("type : " + userType);

		Intent intent = new Intent(this, Pic_MainActivity.class);
		startActivity(intent);
		finish();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// EdjTools.appExit(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// 加载客户端网络配置

		super.onResume();
	}

	// 获取定位参数数据
	private void getLocationParams(int locationMode) {
		// 定位精度 2高精度 1低功耗 0 设备定位
		if (locationMode == 2) {
			mLocationMode = LocationMode.Hight_Accuracy;
		} else if (locationMode == 1) {
			mLocationMode = LocationMode.Battery_Saving;
		} else if (locationMode == 0) {
			mLocationMode = LocationMode.Device_Sensors;
		}

		// 定位模式及间隔时间
		mLocationSequency = true;
		mScanSpan = 5000;
		// 地址信息
		mIsNeedAddress = true;

		// 是否需要方向
		mIsNeedDirection = true;

		// 定位坐标类型
		mCoordType = "bd09ll";
		mGeofenceInit = false;
	}

	// 设置Option
	private void setLocationOption() {
		try {
			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(mLocationMode);
			option.setCoorType(mCoordType);
			option.setScanSpan(mScanSpan);
			option.setNeedDeviceDirect(mIsNeedDirection);
			option.setIsNeedAddress(mIsNeedAddress);
			mLocClient.setLocOption(option);
			mLocationInit = true;
		} catch (Exception e) {
			e.printStackTrace();
			mLocationInit = false;
		}
	}

}
