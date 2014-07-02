package com.google.zxing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amima.app.MyApp;
import com.amima.pic.R;
import com.amima.ui.base.BaseActivity;
import com.google.zxing.camera.CameraManager;
import com.google.zxing.decoding.CaptureActivityHandler;
import com.google.zxing.decoding.InactivityTimer;
import com.google.zxing.view.ViewfinderView;
import com.my.activity.sq.bean.SqInfoBean;
import com.my.android.http.AsyncHttpResponseHandler;
import com.my.android.http.RequestParams;
import com.my.dialog.ProgressDlg;
import com.my.util.HttpConnection;
import com.my.util.MD5;
import com.my.util.Protocol;
import com.my.util.SystemOut;
import com.my.util.Tools;

/**
 * Initial the camera
 * 
 * @ClassName: CaptureActivity
 * @Description: TODO
 * @author yeungeek
 * @date 2013-4-28 下午12:59:44
 */
public class CaptureActivity extends BaseActivity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	private TextView mTitle;
	private ImageView mGoHome;
	private static final int BIND_SUCCESS = 141;
	private static final int BIND_FAIL = 142;
	private static final int UNBIND_SUCCESS = 143;
	private static final int UNBIND_FAIL = 144;

	LayoutInflater ininflater;
	Display display;
	private String unbindname;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.qr_code_scan);

		CameraManager.init(getApplication());
		initControl();

		ininflater = LayoutInflater.from(CaptureActivity.this);
		display = this.getWindowManager().getDefaultDisplay();

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	private void initControl() {
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	public void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();

		final String resultString = result.getText();

		// String[] aa = resultString.split("/");
		// String sqname = aa[4];
		int pos = resultString.indexOf("/app/");

		String sqname = "";
		if (pos > -1) {
			String sq1 = resultString
					.substring(resultString.indexOf("/app/") + 5);
			int pos1 = sq1.indexOf("/");
			if (pos1 > 0)
				sqname = sq1.substring(0, pos1);
			else
				sqname = sq1;
		}

		if (Tools.isNull(sqname)) {
			Tools.showShortToast(
					getResources().getString(R.string.eroor_sq_name),
					CaptureActivity.this);
			return;
		}
		if (!Tools.isNull(app.user.getId())) {
			Boolean isBind = true;

			if (app.user.getListSq() != null) {

				for (SqInfoBean melement : app.user.getListSq()) {
					if (sqname.equals(melement.getName())) {
						isBind = false;
					}
				}
			}
			creatButtonPrompt(sqname, isBind);
			// BindingBind(app.user.getId(), sqname);

		}
		// Intent resultIntent = new Intent();
		// Bundle bundle = new Bundle();
		// bundle.putString("result", resultString);
		// resultIntent.putExtras(bundle);
		// this.setResult(RESULT_OK, resultIntent);
		//
		// finish();
	}

	/**
	 * 显示绑定/解除 对话框
	 * 
	 * @param name
	 * @param isBind
	 */
	public void creatButtonPrompt(final String name, final Boolean isBind) {
		String message = "";
		if (isBind) {
			message = "是否绑定这个社区";
		} else {
			message = "是否解除绑定这个社区";
		}

		final Dialog backDialog = new Dialog(CaptureActivity.this,
				R.style.ThemeDialog);
		View dialogView =null;
	 
	  dialogView = ininflater.inflate(R.layout.bind_sq, null);
		 
	 
		Button confirmButton = (Button) dialogView.findViewById(R.id.bind_yes);

		Button cancleButton = (Button) dialogView.findViewById(R.id.bind_no);
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isBind) {
					BindingBind(app.user.getId(), name);
				} else {
					UNBindingBind(app.user.getId(), name);
				}
				backDialog.dismiss();
			}
		});
		cancleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				backDialog.dismiss();
			}
		});

		TextView titleText = (TextView) dialogView.findViewById(R.id.name);
		titleText.setText(name);
		TextView valueText = (TextView) dialogView.findViewById(R.id.message);
		valueText.setText(message);
		backDialog.setContentView(dialogView);
		backDialog.show();
		Window dialogWindow = backDialog.getWindow();
		WindowManager.LayoutParams p = dialogWindow.getAttributes();
		p.width = (int) (display.getWidth() * 0.9);
		p.height = (int) (display.getHeight() * 0.3);
		dialogWindow.setAttributes(p);
		backDialog.show();
	}

	// -------------------------------------
	/**
	 * 绑定社区
	 * 
	 * @param id
	 * @param result
	 */
	private void BindingBind(String id, String name) {
		RequestParams params = new RequestParams();
		params.put("id", id);

		params.put("communityName", name);
		String sign = null;
		try {
			sign = MD5.str2MD5("mykk#@!321" + app.user.getId()
					+ URLDecoder.decode(name, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.put("sign", sign);
		String url = Tools.getUrl(Protocol.URL_COMMUNITY_BIND);
		HttpConnection
				.HttpClientPost(url, params, bindAsyncHttpResponseHandler);

	}

	AsyncHttpResponseHandler bindAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			ProgressDlg.showDlg(CaptureActivity.this, "提交中...");
			SystemOut.out("开始");
			super.onStart();
		}

		@Override
		public void onFinish() {
			SystemOut.out("完成");
			ProgressDlg.cancleDlg(CaptureActivity.this);
			super.onFinish();
		}

		@Override
		public void onSuccess(String content) {
			ProgressDlg.cancleDlg(CaptureActivity.this);
			try {

				JSONObject obj = new JSONObject(content);
				String result = obj.optString("code");
				if ("0".equals(result)) {
					String name = "";
					JSONObject mobj = obj.optJSONObject("data");
					if (mobj != null) {
						String sqid = mobj.optString("id");
						name = Tools.JSONString(mobj, "name");

						if (!Tools.isNull(sqid)) {
							SqInfoBean minfo = new SqInfoBean();
							minfo.setId(sqid);
							minfo.setName(name);
							MyApp.user.getListSq().add(minfo);
						}

						SystemOut.out("size.." + MyApp.user.getListSq().size());
					}

					if (MyApp.user.getUserName() == null
							|| MyApp.user.getUserName().length() < 1) {
						MyApp.user.setUserName(name + "用户");
					}

					Message m = mhandler.obtainMessage();
					m.what = BIND_SUCCESS;

					mhandler.sendMessage(m);
				} else {
					Message m = mhandler.obtainMessage();
					m.what = BIND_FAIL;
					mhandler.sendMessage(m);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			SystemOut.out("成功：" + content);
			super.onSuccess(content);
		}

		@Override
		public void onFailure(Throwable error, String content) {
			Message msg = mhandler.obtainMessage();
			msg.what = BIND_FAIL;
			handler.sendMessage(msg);

			super.onFailure(error, content);
		}
	};

	// -------------------------------------
	/**
	 * 解除绑定社区
	 * 
	 * @param id
	 * @param result
	 */
	private void UNBindingBind(String id, String name) {
		RequestParams params = new RequestParams();
		params.put("id", id);

		params.put("communityName", name);
		unbindname = name;
		String sign = null;
		try {
			sign = MD5.str2MD5("mykk#@!321" + app.user.getId()
					+ URLDecoder.decode(name, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.put("sign", sign);
		String url = Tools.getUrl(Protocol.URL_COMMUNITY_UNBIND);
		HttpConnection.HttpClientPost(url, params,
				unbindAsyncHttpResponseHandler);

	}

	AsyncHttpResponseHandler unbindAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			ProgressDlg.showDlg(CaptureActivity.this, "提交中...");
			SystemOut.out("开始");
			super.onStart();
		}

		@Override
		public void onFinish() {
			SystemOut.out("完成");
			ProgressDlg.cancleDlg(CaptureActivity.this);
			super.onFinish();
		}

		@Override
		public void onSuccess(String content) {
			ProgressDlg.cancleDlg(CaptureActivity.this);
			try {

				JSONObject obj = new JSONObject(content);
				String result = obj.optString("code");
				if ("0".equals(result)) {
					

					for (SqInfoBean melement : app.user.getListSq()) {
						if (unbindname.equals(melement.getName())) {
							app.user.getListSq().remove(melement);

						}
					}
					Message m = mhandler.obtainMessage();
					m.what = UNBIND_SUCCESS;
					mhandler.sendMessage(m);
				} else {
					Message m = mhandler.obtainMessage();
					m.what = UNBIND_FAIL;
					mhandler.sendMessage(m);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			SystemOut.out("成功：" + content);
			super.onSuccess(content);
		}

		@Override
		public void onFailure(Throwable error, String content) {
			Message msg = mhandler.obtainMessage();
			msg.what = UNBIND_FAIL;
			handler.sendMessage(msg);

			super.onFailure(error, content);
		}
	};

	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case BIND_SUCCESS:
				Tools.showShortToast("绑定成功", CaptureActivity.this);
				finish();

				break;

			case BIND_FAIL:
				Tools.showShortToast("绑定失败", CaptureActivity.this);
				break;
			case UNBIND_SUCCESS:
				Tools.showShortToast("解除绑定成功", CaptureActivity.this);
				finish();

				break;

			case UNBIND_FAIL:
				Tools.showShortToast("解除绑定失败", CaptureActivity.this);
				break;
			}
		}

	};

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	/**
	 * 扫描正确后的震动声音,如果感觉apk大了,可以删除
	 */
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/*
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}
