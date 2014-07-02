package com.my.util;

//
////网络类型
//		String networkType =PhoneInfo.getConnectTypeName(ShowPhoneInfo.this);
//		//系统版本
//		String sysVersion = android.os.Build.VERSION.RELEASE;
//		//手机型号：
//		String mobileModel = android.os.Build.MODEL;
//		
//		TextView text2=(TextView)findViewById(R.id.useinfo);
//		text2.setText("常用：\n networkType:"+networkType+"\n"+"sysVersion:"+sysVersion+"\n"+"mobileModel:"+mobileModel);
//		
//		
//		DisplayMetrics dm=new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);

//http://wenku.baidu.com/view/dd4e6ef2f61fb7360b4c6595.html

//1.	Build.BOARD // 主板   
//2.	Build.BRAND // android系统定制商   
//3.	Build.CPU_ABI // cpu指令集   
//4.	Build.DEVICE // 设备参数   
//5.	Build.DISPLAY // 显示屏参数   
//6.	Build.FINGERPRINT // 硬件名称   
//7.	Build.HOST   
//8.	Build.ID // 修订版本列表   
//9.	Build.MANUFACTURER // 硬件制造商   
//10.	Build.MODEL // 版本   
//11.	Build.PRODUCT // 手机制造商   
//12.	Build.TAGS // 描述build的标签   
//13.	Build.TIME   
//14.	Build.TYPE // builder类型   
//15.	Build.USER
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.amima.app.MyApp;

public class PhoneUtils {
	private static final String TAG = PhoneUtils.class.getSimpleName();
	private static final String FILE_MEMORY = "/proc/meminfo";
	private static final String FILE_CPU = "/proc/cpuinfo";
	public String mIMEI;
	public int mPhoneType;
	public int mSysVersion;
	public String mNetWorkCountryIso;
	public String mNetWorkOperator;
	public String mNetWorkOperatorName;
	public int mNetWorkType;
	public boolean mIsOnLine;
	public String mConnectTypeName;
	public long mFreeMem;
	public long mTotalMem;
	public String mCupInfo;
	public String mProductName;
	public String mModelName;
	public String mManufacturerName;

	private PhoneUtils() {

	}

	public static String getIMEI(Context context) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Activity.TELEPHONY_SERVICE);
		// check if has the permission
		if (PackageManager.PERMISSION_GRANTED == context.getPackageManager()
				.checkPermission(Manifest.permission.READ_PHONE_STATE,
						context.getPackageName())) {
			return manager.getDeviceId();
		} else {
			return null;
		}
	}

	public static int getPhoneType(Context context) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Activity.TELEPHONY_SERVICE);
		return manager.getPhoneType();
	}

	public static int getSysVersion() {
		return Build.VERSION.SDK_INT;
	}

	public static String getNetWorkCountryIso(Context context) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Activity.TELEPHONY_SERVICE);
		return manager.getNetworkCountryIso();
	}

	public static String getNetWorkOperator(Context context) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Activity.TELEPHONY_SERVICE);
		return manager.getNetworkOperator();
	}

	public static String getNetWorkOperatorName(Context context) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Activity.TELEPHONY_SERVICE);
		return manager.getNetworkOperatorName();
	}

	public static int getNetworkType(Context context) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Activity.TELEPHONY_SERVICE);
		return manager.getNetworkType();
	}

	// 是否连接到网络
	public static boolean isOnline(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		}
		return false;
	}

	public static String getConnectTypeName(Context context) {
		if (!isOnline(context)) {
			return "OFFLINE";
		}
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null) {
			return info.getTypeName();
		} else {
			return "OFFLINE";
		}
	}

	public static long getFreeMem(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Activity.ACTIVITY_SERVICE);
		MemoryInfo info = new MemoryInfo();
		manager.getMemoryInfo(info);
		long free = info.availMem / 1024 / 1024;
		return free;
	}

	public static long getTotalMem(Context context) {
		try {
			FileReader fr = new FileReader(FILE_MEMORY);
			BufferedReader br = new BufferedReader(fr);
			String text = br.readLine();
			String[] array = text.split("\\s+");
			Log.w(TAG, text);
			return Long.valueOf(array[1]) / 1024;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static String getCpuInfo() {
		try {
			FileReader fr = new FileReader(FILE_CPU);
			BufferedReader br = new BufferedReader(fr);
			String text = br.readLine();
			String[] array = text.split(":\\s+", 2);
			for (int i = 0; i < array.length; i++) {
				Log.w(TAG, " .....  " + array[i]);
			}
			Log.w(TAG, text);
			return array[1];
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getProductName() {
		return Build.PRODUCT;
	}

	public static String getModelName() {
		return Build.MODEL;
	}

	public static String getManufacturerName() {
		return Build.MANUFACTURER;
	}

	public static PhoneUtils getPhoneInfo(Context context) {
		PhoneUtils result = new PhoneUtils();
		result.mIMEI = getIMEI(context);
		result.mPhoneType = getPhoneType(context);
		result.mSysVersion = getSysVersion();
		result.mNetWorkCountryIso = getNetWorkCountryIso(context);
		result.mNetWorkOperator = getNetWorkOperator(context);
		result.mNetWorkOperatorName = getNetWorkOperatorName(context);
		result.mNetWorkType = getNetworkType(context);
		result.mIsOnLine = isOnline(context);
		result.mConnectTypeName = getConnectTypeName(context);
		result.mFreeMem = getFreeMem(context);
		result.mTotalMem = getTotalMem(context);
		result.mCupInfo = getCpuInfo();
		result.mProductName = getProductName();
		result.mModelName = getModelName();
		result.mManufacturerName = getManufacturerName();
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IMEI : " + mIMEI + "\n");
		builder.append("mPhoneType : " + mPhoneType + "\n");
		builder.append("mSysVersion : " + mSysVersion + "\n");
		builder.append("mNetWorkCountryIso : " + mNetWorkCountryIso + "\n");
		builder.append("mNetWorkOperator : " + mNetWorkOperator + "\n");
		builder.append("mNetWorkOperatorName : " + mNetWorkOperatorName + "\n");
		builder.append("mNetWorkType : " + mNetWorkType + "\n");
		builder.append("mIsOnLine : " + mIsOnLine + "\n");
		builder.append("mConnectTypeName : " + mConnectTypeName + "\n");
		builder.append("mFreeMem : " + mFreeMem + "M\n");
		builder.append("mTotalMem : " + mTotalMem + "M\n");
		builder.append("mCupInfo : " + mCupInfo + "\n");
		builder.append("mProductName : " + mProductName + "\n");
		builder.append("mModelName : " + mModelName + "\n");
		builder.append("mManufacturerName : " + mManufacturerName + "\n");
		return builder.toString();
	}

	// 获取手机MAC地址：
	public static String getMacAddress(Context context) {
		String result = "";
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		result = wifiInfo.getMacAddress();
		Log.i(TAG, "macAdd:" + result);
		return result;
	}

	// 手机CPU信息
	public static String[] getCpuInfo(Context context) {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		String[] cpuInfo = { "", "" }; // 1-cpu型号 //2-cpu频率
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
			}
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			cpuInfo[1] += arrayOfString[2];
			localBufferedReader.close();
		} catch (IOException e) {
		}
		Log.i(TAG, "cpuinfo:" + cpuInfo[0] + " " + cpuInfo[1]);
		return cpuInfo;
	}

	// // 获取手机可用内存和总内存：
	// public static String[] getTotalMemory(Context context) {
	// String[] result = {"",""}; //1-total 2-avail
	// ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
	// ActivityManager mActivityManager;
	// mActivityManager.getMemoryInfo(mi);
	// long mTotalMem = 0;
	// long mAvailMem = mi.availMem;
	// String str1 = "/proc/meminfo";
	// String str2;
	// String[] arrayOfString;
	// try {
	// FileReader localFileReader = new FileReader(str1);
	// BufferedReader localBufferedReader = new BufferedReader(localFileReader,
	// 8192);
	// str2 = localBufferedReader.readLine();
	// arrayOfString = str2.split("\\s+");
	// mTotalMem = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
	// localBufferedReader.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// result[0] = Formatter.formatFileSize(this, mTotalMem);
	// result[1] = Formatter.formatFileSize(this, mAvailMem);
	// Log.i(TAG, "meminfo total:" + result[0] + " used:" + result[1]);
	// return result;
	// }

	// 获取手机安装的应用信息（排除系统自带）：
	public static String getAllApp(Context contex) {
		String result = "";
		List<PackageInfo> packages = contex.getPackageManager()
				.getInstalledPackages(0);
		for (PackageInfo i : packages) {
			if ((i.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				result += i.applicationInfo.loadLabel(
						contex.getPackageManager()).toString()
						+ ",";
			}
		}
		return result.substring(0, result.length() - 1);
	}

	/**
	 * 获取手机唯一识别码 userkey
	 * 
	 * @param context
	 * @return
	 */
	public static String getUserKey(Context context) {

		String userKey = SharedPreTools.readShare(Constants.DEVICE_INFO,
				Constants.DEVICE_INFO_PHONE);
		if (!Tools.isNull(userKey)) {
			return userKey;
		}

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		// 先获取手机号
		String mobile = tm.getLine1Number();
		if (mobile != null && !"".equals(mobile)) {
			return mobile;
		}

		/*
		 * String simSerialNumber = tm.getSimSerialNumber(); if (simSerialNumber
		 * != null && !"".equals(simSerialNumber)) { return simSerialNumber; }
		 * 
		 * String subscriberId = tm.getSubscriberId(); if (subscriberId != null
		 * && !"".equals(subscriberId)) { return subscriberId; }
		 */

		String imei = tm.getDeviceId();
		if (imei != null && !"".equals(imei)) {
			return imei;
		}
		try {
			String android_id = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);
			if (!Tools.isNull(android_id)) {
				return android_id;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		String mac = getLocalMacAddress(context);
		if (mac != null && !"".equals(mac)) {
			return mac;
		}

		userKey = "" + new Date().getTime() + ((int) (Math.random() * 1000000));
		SharedPreTools.writeShare(Constants.DEVICE_INFO,
				Constants.DEVICE_INFO_USERKEY, userKey);

		return userKey;
	}

	private static String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	private static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}

	/**
	 * 获取手机号
	 * 
	 * @param context
	 * @return
	 */
	public static String getTelephone(Context context) {

		String telephone = SharedPreTools.readShare(Constants.DEVICE_INFO,
				Constants.DEVICE_INFO_PHONE);

		if (telephone != null && !"".equals(telephone)) {
			return telephone;
		} else {

			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			String mobile = tm.getLine1Number();
			SystemOut.out(mobile + "..................mobile");
			if (mobile != null && !"".equals(mobile)) {
				SharedPreTools.writeShare(Constants.DEVICE_INFO,
						Constants.DEVICE_INFO_PHONE, mobile);
				return mobile;
			}
		}
		return null;
	}

	private static final String NET_TYPE_WIFI = "WIFI";
	public static final int NET_NOT_AVAILABLE = 0;
	public static final int NET_WIFI = 1;
	public static final int NET_PROXY = 2;
	public static final int NET_NORMAL = 3;

	/**
	 * 网络类型
	 */
	private volatile static int networkType = NET_NOT_AVAILABLE;

	/**
	 * 判断网络连接是否可用
	 */
	public static boolean checkNetworkAvailable1(Context inContext) {
		int type = getNetworkType(inContext);
		if (type == NET_NOT_AVAILABLE) {
			return false;
		}
		return true;
	}

	/**
	 * 获取网络连接类型
	 */
	public synchronized static int getNetworkTypeCode(Context inContext) {
		Context context = inContext.getApplicationContext();

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			// 当前网络不可用
			networkType = NET_NOT_AVAILABLE;
		} else {
			// 如果当前是WIFI连接
			if (NET_TYPE_WIFI.equals(networkinfo.getTypeName())) {
				networkType = NET_WIFI;
			}
			// 非WIFI联网
			else {
				String proxyHost = android.net.Proxy.getDefaultHost();
				// 代理模式
				if (proxyHost != null) {
					networkType = NET_PROXY;
				}
				// 直连模式
				else {
					networkType = NET_NORMAL;
				}
			}
		}
		return networkType;
	}

	// 获取设备信息
	public static void getDevInfo(Context con) {

		MyApp.getInstance().networkType = getConnectTypeName(con);
		// MyApp.getInstance().clientVersion = clientVersion;
		MyApp.getInstance().sysVersion = android.os.Build.VERSION.RELEASE;
		MyApp.getInstance().mobileModel = android.os.Build.MODEL;
		MyApp.getInstance().userKey = getUserKey(con);
		MyApp.getInstance().telephone = getTelephone(con);
	}

}
