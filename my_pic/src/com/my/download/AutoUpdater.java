/**
 * @author 魏兴波 
 * @version 创建时间：2012-8-15 上午10:08:52
 * 类说明
 */
package com.my.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amima.app.MyApp;
import com.amima.pic.R;
import com.my.android.http.AsyncHttpResponseHandler;
import com.my.android.http.RequestParams;
import com.my.util.HttpConnection;
import com.my.util.Protocol;
import com.my.util.SystemOut;
import com.my.util.Tools;

/**
 * @author Administrator
 * 
 */
public class AutoUpdater {

	private String TAG = "AutoUpdater";
	Context tcontext;

	private Resources mResources = null;
	private boolean backstage = true;// 默认系统自动更新 手动为false
	public ProgressDialog progress;
	// ProgressDialog pBar;
	private DownloadDialogHandler mDownloadHandler = null;

	private static final int GET_SUCCESS = 4;
	private static final int GET_FAIL = 5;
	private static final int TIME_OUT = 6;

	private String updateUrl;

	private AutoUpdater(Context context, boolean backstage) {
		this.tcontext = context;
		this.backstage = backstage;
		this.mResources = context.getResources();
		mDownloadHandler = new DownloadDialogHandler(new DownloadDialog(
				tcontext, "软件更新", "正在下载最新版本安装包..."));
	}

	private void checkForUpdate() {
		if (!backstage) {
			if (progress == null) {
				progress = new ProgressDialog(tcontext);
			}
			progress.setMessage("正在检查更新...");
			progress.show();
		}
		String version = MyApp.localVersion;

		SystemOut.out(version);
		if (!Tools.isNull(version)) {
			checkUpdate(version);
		}

	}

	private void checkUpdate(String version) {

		RequestParams params = new RequestParams();
		params.put("v", version);

		String url = Tools.getUrl(Protocol.URL_CHECK_UPDATE);
		HttpConnection
				.HttpClientPost(url, params, listAsyncHttpResponseHandler);
	}

	AsyncHttpResponseHandler listAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			SystemOut.out("开始");
			super.onStart();
		}

		@Override
		public void onFinish() {
			SystemOut.out("完成");
			super.onFinish();
		}

		@Override
		public void onSuccess(String content) {

			try {
				JSONObject json = new JSONObject(content);

				if (json != null) {

					String code = Tools.JSONString(json, "code");

					if ("0".equals(code)) {
						Message msg = handler.obtainMessage();
						msg.what = GET_SUCCESS;
						msg.obj = json;
						handler.sendMessage(msg);
					} else {
						Message msg = handler.obtainMessage();
						msg.what = GET_FAIL;
						handler.sendMessage(msg);
						SystemOut.out("失败：" + content);
					}
				} else {
					Message msg = handler.obtainMessage();
					msg.what = GET_FAIL;
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
			msg.what = GET_FAIL;
			handler.sendMessage(msg);
			SystemOut.out("失败：" + content);
			super.onFailure(error, content);
		}
	};

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case GET_SUCCESS:

				if (progress != null && progress.isShowing()) {
					progress.cancel();
				}

				JSONObject mobj = (JSONObject) msg.obj;
				String forceUpdate = "1"; // 是否强制更新 0 是 1 否
				String newVersion = "";
				String remark = "";

				try {
					newVersion = Tools.JSONString(mobj, "newVersion");
					remark = Tools.JSONString(mobj, "remark");
					updateUrl = Tools.JSONString(mobj, "url");
					forceUpdate = Tools.JSONString(mobj, "forceUpdate");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (!Tools.isNull(updateUrl)&&updateUrl.contains(".apk")) {
					showUpdateDialog(newVersion, remark, updateUrl, forceUpdate);
					
				} else {
					if (!backstage) {
						Toast.makeText(tcontext, "当前已是最新版本", Toast.LENGTH_SHORT)
								.show();
					}
				}

				break;

			case GET_FAIL:
				if (progress != null && progress.isShowing()) {
					progress.cancel();
				}
				if (!backstage) {
					Toast.makeText(tcontext, "暂无最新版本", Toast.LENGTH_SHORT)
							.show();
				}

				break;

			}
		}

		

	};
	
	
	private void showUpdateDialog(String newVersion, String remark,
			final String updateUrl, String forceUpdate) {
		// TODO Auto-generated method stub
		Activity activity = (Activity) tcontext;
		if (activity.isFinishing()) {
			return;
		}
		LayoutInflater inflater = LayoutInflater.from(MyApp
				.getInstance());
		// LayoutInflater inflater = getLayoutInflater();;
		View view = inflater.inflate(R.layout.updata_dialog, null);
		Builder builder = new AlertDialog.Builder(tcontext);
		builder.setTitle(R.string.updatadialog_title)
				.setPositiveButton(
						Html.fromHtml("<font color=#288245>立即更新</font>"),
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog,
									int which) {

								dialog.dismiss();
								Message msg = Message.obtain();
								msg.obj = DownloadStates.MESSAGE_DOWNLOAD_STARTING;
								Bundle data = new Bundle();
								data.putString("msg",
										"开始下载最新版本安装包...");
								msg.setData(data);
								mDownloadHandler.sendMessage(msg);

								downFile(updateUrl);

								MyApp.isAutoUpate = true;
							}
						});

		// 强制更新 不显示
		if (!"0".equals(forceUpdate)) {
			builder.setNegativeButton(Html
					.fromHtml("<font color=#666666>暂不更新</font>"),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// 点击"取消"按钮之后退出程序
							dialog.cancel();

							MyApp.getInstance().isAutoUpate = false;

						}
					});
		}
		builder.setView(view);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		AlertDialog dialog = builder.create();
		dialog.show();
		TextView dialogTitle = (TextView) dialog
				.findViewById(R.id.dialogtitle);
		dialogTitle.setText(remark);
	}

	void downFile(final String url) {
		// pBar.show();

		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				Message msg = Message.obtain();
				try {
					String fileName = getFileName(url);
					File f1 = getFile(fileName);
					if (!f1.exists()) {
						f1.mkdirs();
					}

					if (f1.exists()) {
						f1.delete();
					}

					response = client.execute(get);

					if (response.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = response.getEntity();
						InputStream is = entity.getContent();
						int fileSize = (int) entity.getContentLength();
						FileOutputStream fileOutputStream = null;
						int count = 0;
						if (is != null) {
							fileOutputStream = new FileOutputStream(f1);

							byte[] buf = new byte[1024];
							int ch = -1;
							int totalRead = 0;
							while ((ch = is.read(buf)) != -1) {
								msg = Message.obtain();
								msg.obj = DownloadStates.MESSAGE_DOWNLOAD_PROGRESS;
								totalRead += ch;
								msg.arg1 = totalRead;// / 1024;
								msg.arg2 = fileSize;// / 1024;
								float fileLen = (float) msg.arg2 / 1024;
								float readLen = (float) msg.arg1 / 1024;
								DecimalFormat df = new DecimalFormat("0.00");
								String fileLen_str = df.format(fileLen);
								String readLen_str = df.format(readLen);
								if (Tools.isDebug)
									Log.i("AutoUpdater", "msg.arg1" + msg.arg1
											+ "msg.arg2" + msg.arg2);
								// String
								// title="\n下载更新"+"\n"+readLen_str+"K"+"/"+fileLen_str+"K";
								String title = "下载更新中....";
								// System.out.println("fileLen="+fileLen);
								// System.out.println("fileLen_str="+fileLen_str);
								Bundle data = new Bundle();
								data.putString("msg", title);
								msg.setData(data);
								mDownloadHandler.sendMessage(msg);

								fileOutputStream.write(buf, 0, ch);
								count += ch;
							}

						} else {
							Toast.makeText(tcontext, "is null",
									Toast.LENGTH_SHORT).show();
						}
						fileOutputStream.flush();
						if (fileOutputStream != null) {
							fileOutputStream.close();
						}
						if (is != null) {
							is.close();
						}

						downUI(url);

					} else {
						fail();
					}
				} catch (ClientProtocolException e) {
					Toast.makeText(tcontext, "ClientProtocolException!",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					fail();
				} catch (IOException e) {
					Toast.makeText(tcontext, "IOException!" + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					fail();
				}

			}
		}.start();

	}

	void downUI(final String url) {
		handler.post(new Runnable() {
			public void run() {

				Message msg = Message.obtain();
				msg.obj = DownloadStates.MESSAGE_DOWNLOAD_COMPLETE;
				mDownloadHandler.sendMessage(msg);
				updateStart(url);
			}
		});
		// finish();
	}

	File getFile(String filename) {
		File updateDir = tcontext.getFilesDir();
		File file = new File(updateDir.getPath(), filename);

		return file;
	}

	String getFileName(String path) {
		if (path != null && path.indexOf("/") > -1) {
			return path.substring(path.lastIndexOf("/") + 1);
		}
		return "1.apk";
	}

	void updateStart(String url) {
		String fileName = getFileName(url);
		File f = getFile(fileName);
		String cmd = "chmod 777 " + f.getPath();
		try {
			Runtime.getRuntime().exec(cmd);
			// Toast.makeText(Update.this, "cmd:" + cmd,
			// Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Toast.makeText(tcontext, "cmd error:" + e.getMessage(),
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(f),
				"application/vnd.android.package-archive");
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		tcontext.startActivity(intent);
	}

	public void fail() {
		// pBar.cancel();
		Toast.makeText(MyApp.getInstance(), "更新失败！", Toast.LENGTH_LONG).show();
		/*
		 * Intent intent = new Intent(this, InitActivity.class);
		 * startActivity(intent); finish();
		 */
	}

	// checkBySys false 手动更新
	public static void CheckForUpdate(Context context, boolean checkBySys) {
		AutoUpdater autoUpdater = new AutoUpdater(context, checkBySys);
		autoUpdater.checkForUpdate();
	}

	// public interface CheckForUpdateFinish {
	// public static final int SUCESS = 1;
	// public static final int FAIL = 0;
	// public abstract void callBack(String updatastr);
	// }
}
