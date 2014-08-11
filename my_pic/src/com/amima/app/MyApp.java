package com.amima.app;

import java.util.Stack;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.my.activity.sq.bean.UserBean;
import com.my.android.http.AsyncHttpClient;
import com.my.util.ImageCache;
import com.my.util.Tools;
import com.nostra13.example.universalimageloader.Constants.Config;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.analytics.MobclickAgent;

public class MyApp extends Application {

	public static String TAG = "my";
	public ImageCache mImageCache;
	public SharedPreferences prefs = null;
	public static String charSet = "utf-8";

	public static MyApp mApp;
	public int allTotalRead;
	public static UserBean user = new UserBean();


	private ImageLoader imageLoader = ImageLoader.getInstance();
	public AsyncHttpClient asyncHttpClient;
	public static boolean isAutoUpate = true;
	

	public LocationClient mLocationClient;
	public GeofenceClient mGeofenceClient;
	private MyLocationListener mMyLocationListener;
	
	public static String userKey;
	public String sysVersion;// 系统版本
	public String mobileModel;// 设备名称
	public static String localVersion; // 客户端版本
	public String networkType;// 网络类型
	public String telephone;// 用户电话

	public static double lon;
	public static double lat;
	public static String naddr;
	public TextView mLocationResult;
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		Tools.loadProperties(this);
		mApp = this;
		myexcatch();
		mImageCache = new ImageCache();
		asyncHttpClient = new AsyncHttpClient();
		
		if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
		}
		initImageLoader(getApplicationContext());

		
		//baidu map
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		// mGeofenceClient = new GeofenceClient(getApplicationContext());
		
		
		//jpush
		if(Tools.isDebug)
	    JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        
        
        /**
         * umeng在线参数
         */
        MobclickAgent.updateOnlineConfig( this );
        
		super.onCreate();

	}

	public static MyApp getInstance() {
		return mApp;
	}

	public ImageCache getImageCache() {
		return mImageCache;
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static Stack<Activity> stackActivity;

	public static void addActivity(Activity activity) {
		if (stackActivity == null)
			stackActivity = new Stack<Activity>();
		stackActivity.add(activity);
	}

	public static void popActivityRemove(Activity activity) {
		if (stackActivity != null && stackActivity.contains(activity)) {
			stackActivity.remove(activity);
		}
	}

	public static void finishAll() {
		if (stackActivity != null) {
			for (Activity a : MyApp.stackActivity) {
				a.finish();
			}
			MyApp.stackActivity.clear();
		}
	}

	public static void finishOne(String clazzName) {
		if (stackActivity != null) {
			for (Activity activity : stackActivity) {
				if (activity.getComponentName().getClassName()
						.equals(clazzName)) {
					activity.finish();
				}
			}
		}
	}

	private void myexcatch() {
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append(location.getDirection());
				naddr = location.getAddrStr();

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				// 运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());

				naddr = location.getAddrStr();
			}
			logMsg(naddr);
			lon = location.getLongitude();
			lat = location.getLatitude();

			// Log.i("BaiduLocationApiDem", sb.toString());
		}

	}

	/**
	 * 显示请求字符串
	 * 
	 * @param str
	 */
	public void logMsg(String str) {
		try {
			if (mLocationResult != null)
				mLocationResult.setText(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
