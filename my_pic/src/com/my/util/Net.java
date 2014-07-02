package com.my.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.amima.app.MyApp;

public class Net {

	private InputStream inputstream;
	private DefaultHttpClient httpClient;
	private boolean isStop = false;

	public byte[] downloadResource(Context context, String url)
			throws Exception {
		isStop = false;
		ByteArrayBuffer buffer = null;
		byte[] bufferbt = null;
		if (Tools.isDebug)
			SystemOut.out("net-url:" + url);
		HttpGet hp = new HttpGet(url);
		httpClient = new DefaultHttpClient();
		String netType = isNetType(context);
		if (netType != null & netType.equals("cmwap")) {
			HttpHost proxy = new HttpHost("10.0.0.172", 80);
			httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
					proxy);
		}
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
				5 * 1000);
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 60 * 1000);
		HttpResponse response = httpClient.execute(hp);
		if (response.getStatusLine().getStatusCode() == 200) {
			inputstream = response.getEntity().getContent();
			if (inputstream != null) {
				int i = (int) response.getEntity().getContentLength();
				buffer = new ByteArrayBuffer(1024);
				byte[] tmp = new byte[1024];
				int len;
				int totalRead = 0;
				while (((len = inputstream.read(tmp)) != -1)
						&& (false == isStop)) {
					totalRead += len;

					buffer.append(tmp, 0, len);
				}

				// bufferbt =readStream(inputstream);

				DecimalFormat df = new DecimalFormat("0.00");
				MyApp.getInstance().allTotalRead += totalRead;
				String fileLen_str = df.format(totalRead / 1024);
				SystemOut.out("下载图片的流量：" + fileLen_str + "K");
			}
			cancel(url);
		}
		return buffer.toByteArray();
	}

	public static byte[] readStream(InputStream in) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = in.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		outputStream.close();
		in.close();
		return outputStream.toByteArray();
	}

	/**
	 * ǿ�ƹر�����
	 * 
	 * @throws IOException
	 */
	public synchronized void cancel(String url) throws IOException {
		if (null != httpClient) {
			isStop = true;
			httpClient.getConnectionManager().shutdown();
			httpClient = null;
		}
		if (inputstream != null) {
			inputstream.close();
		}
		Tools.downImageMap.remove(url);
	}

	/**
	 * �жϽ��������
	 * 
	 * @return
	 */
	public static String isNetType(Context context) {
		String nettype = null;
		if (context == null) {
			return null;
		}
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectivityManager.getActiveNetworkInfo();
		if (mobNetInfo != null) {
			if (mobNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				nettype = mobNetInfo.getTypeName(); // ��ǰj��������WIFI
			} else {
				nettype = mobNetInfo.getExtraInfo();// ��ǰj��������cmnet/cmwap
			}
		}

		return nettype;
	}

}
