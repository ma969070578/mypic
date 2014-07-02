package com.amima.ui.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Toast;

import com.amima.app.MyApp;
import com.amima.pic.R;
import com.my.util.Constants;
import com.my.util.SystemOut;
import com.my.util.Tools;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {
	public MyApp app;
	private NetCheckReceiver netCheckReceiver;
	protected Activity context;

	Handler mhandler = new Handler() {

		public void handleMessage(Message msg) {

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		MyApp.addActivity(this);

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		app = (MyApp) this.getApplication();
		context = this;
		
		
		if(Tools.isDebug){
			MobclickAgent.setDebugMode(true);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApp.popActivityRemove(this);
		unregisterCheckReceiver();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		MobclickAgent.onPageEnd("SplashScreen"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		MobclickAgent.onPageStart("SplashScreen"); //统计页面
	    MobclickAgent.onResume(this);          //统计时长
	    
		registerNetCheckReceiver();
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		registerNetCheckReceiver();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		unregisterCheckReceiver();
		// if (serviceBinder != null) {
		// unbindService(serviceConnection);
		// }
		super.onStop();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);

	}

	public void registerNetCheckReceiver() {
		if (netCheckReceiver == null) {
			netCheckReceiver = new NetCheckReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constants.CONNECTIVITY_CHANGE_ACTION);
			filter.addAction(Constants.SPORTS_MESSAGE);

			registerReceiver(netCheckReceiver, filter);
		}
	}

	public void unregisterCheckReceiver() {
		if (netCheckReceiver != null) {
			unregisterReceiver(netCheckReceiver);
			netCheckReceiver = null;
		}
	}

	class NetCheckReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// System.out.println("ACTION:" + intent.getAction());
			if (intent.getAction().equals(Constants.CONNECTIVITY_CHANGE_ACTION)) {
				SystemOut
						.out("CONNECTIVITY_CHANGE_ACTION--------------------------");
				ConnectivityManager mConnMgr = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				if (mConnMgr != null) {
					NetworkInfo aActiveInfo = mConnMgr.getActiveNetworkInfo();
					// System.out.println("aActiveInfo=" + aActiveInfo);
					if (aActiveInfo == null) {
						Toast.makeText(BaseActivity.this, R.string.net_err,
								Toast.LENGTH_SHORT).show();
					} else {
					}

				}
			} else if (intent.getAction().equals(Constants.SPORTS_MESSAGE)) {

				String str = intent.getStringExtra("sports_mag");
				Toast.makeText(BaseActivity.this, str, Toast.LENGTH_SHORT)
						.show();
			}

		}
	}

	public void copyfile() {
	}

}
