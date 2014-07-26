package com.my.activity.sq;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amima.app.widget.HeadImageView;
import com.amima.pic.R;
import com.amima.ui.base.BaseActivity;
import com.my.android.http.AsyncHttpResponseHandler;
import com.my.android.http.RequestParams;
import com.my.dialog.ProgressDlg;
import com.my.download.AutoUpdater;
import com.my.util.Constants;
import com.my.util.Files;
import com.my.util.HttpConnection;
import com.my.util.Protocol;
import com.my.util.SystemOut;
import com.my.util.Tools;

public class Sq_SettingActivity extends BaseActivity implements OnClickListener {

	private HeadImageView mMyAvatar;
	private LinearLayout mHead_photo_layout;
	private LinearLayout mBinding_community_layout;
	private LinearLayout mCheck_update_layout;
	private LinearLayout mAboutus_layout;
	private Button mswitch_roler;

	private static String IMAGE_FILE_PATH = "";
	private String[] items = new String[] { "从手机相册选择", "拍照" };
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private static final int GET_PHOTO_SUCCESS = 41;
	private static final int GET_PHOTO_FAIL = 42;
	private static final int UPDATESUCCESS = 11;
	private static final int UPDATEFAIL = 12;

	private static final int BIND_SUCCESS = 141;
	private static final int BIND_FAIL = 142;
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = System.currentTimeMillis()
			+ ".jpg";

	private String headurlnet;
	private String headurl;

	private ImageView image_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_user);
		bindViews();

		IMAGE_FILE_PATH = Environment.getExternalStorageDirectory() + "/"
				+ Files.path;
	}

	private void bindViews() {

		image_back = (ImageView) findViewById(R.id.image_back);
		image_back.setImageDrawable(getResources().getDrawable(
				R.drawable.user_image_back));
		image_back.setOnClickListener(this);

		mswitch_roler = (Button) findViewById(R.id.swicth_roler);
		mHead_photo_layout = (LinearLayout) findViewById(R.id.head_photo_layout);

		mMyAvatar = (HeadImageView) findViewById(R.id.myAvatar);
		mBinding_community_layout = (LinearLayout) findViewById(R.id.binding_community_layout);
		mCheck_update_layout = (LinearLayout) findViewById(R.id.check_update_layout);
		mAboutus_layout = (LinearLayout) findViewById(R.id.aboutus_layout);

		mswitch_roler.setOnClickListener(this);
		mHead_photo_layout.setOnClickListener(this);
		mBinding_community_layout.setOnClickListener(this);
		mCheck_update_layout.setOnClickListener(this);
		mAboutus_layout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.image_back:

			finish();

			break;
		case R.id.head_photo_layout:

			if (Files.ExistSDCard()) {
				Files.mkdirByPath(IMAGE_FILE_PATH);
				showDialog();
			} else {
				Tools.showShortToast("暂无SD卡，无法修改头像！", this);
			}

			break;
		case R.id.binding_community_layout:

			startActivity(new Intent(Sq_SettingActivity.this,
					Sq_SqListActivity.class));

			break;
		case R.id.check_update_layout:

			AutoUpdater.CheckForUpdate(this, false);

			break;
		case R.id.aboutus_layout:

			Intent intent1 = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("url", Protocol.URL_ABOUTUS);
			intent1.putExtras(bundle);

			intent1.setClass(context, Sq_WebActivity.class);
			context.startActivity(intent1);

			break;

		case R.id.swicth_roler:
//			startActivity(new Intent(this, Sq_DocLoginActivity.class));
			break;
		default:
			break;
		}

	}

	/**
	 * 显示选择对话框
	 */
	private void showDialog() {
		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (Files.ExistSDCard()) {
								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT, Uri
												.fromFile(new File(
														IMAGE_FILE_PATH,
														IMAGE_FILE_NAME)));
							}
							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (Files.ExistSDCard()) {
					File tempFile = new File(IMAGE_FILE_PATH + IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Tools.showShortToast("未找到存储卡，无法存储照片！", this);
				}
				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 100);
		intent.putExtra("outputY", 120);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			if (Files.ExistSDCard()) {
				try {
					Boolean save = Files.saveMyBitmap(IMAGE_FILE_PATH
							+ IMAGE_FILE_NAME, photo);
					if (save) {
						String pic = IMAGE_FILE_PATH + IMAGE_FILE_NAME;
						String filename = pic.substring(
								pic.lastIndexOf("/") + 1, pic.length());
						if (!Tools.isNull(app.user.getId()))
							saveUserphoto1(app.user.getId(), pic);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Drawable drawable = new BitmapDrawable(photo);

			mMyAvatar.setImageDrawable(drawable);
		}
	}

	/**
	 * 保存图片
	 * 
	 * @param id
	 * @param path
	 */
	private void saveUserphoto1(String id, String path) {
		RequestParams params = new RequestParams();
		params.put("id", id + "");
		byte[] image = null;
		String filename = path.substring(path.lastIndexOf("/") + 1,
				path.length());
		try {
			image = Files.getFileByte(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		params.put("file", new ByteArrayInputStream(image), "1.jpg");

		String url = Tools.getUrl(Protocol.URL_USER_AVATR);
		HttpConnection.HttpClientPost(url, params,
				userPhotoAsyncHttpResponseHandler);

	}

	AsyncHttpResponseHandler userPhotoAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			ProgressDlg.showDlg(Sq_SettingActivity.this, "提交中...");
			SystemOut.out("开始");
			super.onStart();
		}

		@Override
		public void onFinish() {
			SystemOut.out("完成");
			ProgressDlg.cancleDlg(Sq_SettingActivity.this);
			super.onFinish();
		}

		@Override
		public void onSuccess(String content) {
			ProgressDlg.cancleDlg(Sq_SettingActivity.this);
			try {

				JSONObject obj = new JSONObject(content);
				String result = obj.optString("code");
				if ("0".equals(result)) {
					Message m = handler.obtainMessage();
					m.what = GET_PHOTO_SUCCESS;
					headurlnet = obj.optString("url");

					Sq_SettingActivity.this.handler.sendMessage(m);
				} else {
					Message m = handler.obtainMessage();
					m.what = GET_PHOTO_FAIL;
					Sq_SettingActivity.this.handler.sendMessage(m);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			SystemOut.out("成功：" + content);
			super.onSuccess(content);
		}

		@Override
		public void onFailure(Throwable error, String content) {
			Message msg = handler.obtainMessage();
			msg.what = GET_PHOTO_FAIL;
			handler.sendMessage(msg);
			SystemOut.out("失败：" + content);
			super.onFailure(error, content);
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_PHOTO_SUCCESS:
				Tools.showShortToast("头像修改成功", Sq_SettingActivity.this);
				headurl = "http://" + Constants.HOST + "/" + headurlnet;
				mMyAvatar.setImageUrl(headurl, 0, null, 2);
				break;

			case GET_PHOTO_FAIL:
				Tools.showShortToast("头像修改失败", Sq_SettingActivity.this);
				break;

			case UPDATESUCCESS:
				Tools.showShortToast("修改成功", getApplicationContext());
				// 需要重设全局
				break;

			case UPDATEFAIL:
				Tools.showShortToast("修改失败，请稍后重试", getApplicationContext());
				break;

			}
		}

	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		headurl = app.user.getAvatar();
		mMyAvatar.setImageUrl(headurl, 0, null, 2);

		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
