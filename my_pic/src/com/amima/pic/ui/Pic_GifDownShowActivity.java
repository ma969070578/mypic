package com.amima.pic.ui;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amima.pic.R;
import com.amima.ui.base.BaseActivity;
import com.ant.liao.GifView;
import com.nostra13.example.universalimageloader.Constants.Extra;

public class Pic_GifDownShowActivity extends BaseActivity {

	private final static String TAG = "IcsTestActivity";
	private final static String ALBUM_PATH = Environment
			.getExternalStorageDirectory() + "/download_test/";
	private GifView mImageView;

	private ProgressDialog mSaveDialog = null;
	private Bitmap mBitmap;
	private String mFileName;
	private String mSaveMessage;
	private ProgressBar progressBar = null;
	private Message message = null;
	private boolean flag = true;
	private int size = 1;
	private int hasRead = 0;
	private int len = 0;
	private byte buffer[] = new byte[1024 * 4];
	private int index = 0;
	private String murl;

	String mfilepath = Environment.getExternalStorageDirectory() + "/1234.gif";
	InputStream is = null;
	private final int FILE = 11;
	private final int ASSETS = 12;
	private final int URL = 13;
	 WebView view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_gif_downshow);

		progressBar = (ProgressBar) findViewById(R.id.progress_horizontal);
		mImageView = (GifView) findViewById(R.id.imgSource);
		
		view = (WebView) this.findViewById(R.id.main_webView);
        view.setBackgroundColor(0);

		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		String[] imageUrls = bundle.getStringArray(Extra.IMAGES);
		int pagerPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);
		murl = imageUrls[pagerPosition];
		if (murl.contains("http")) {
			new Thread(connectNet).start();
		} else if (murl.contains("file")) {
			File mfile = new File(murl);
			try {
				is = new FileInputStream(mfile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (is != null) {
				Message mssage = new Message();
				mssage.what = FILE;
				connectHanlder.sendMessage(mssage);
			}
		} else if (murl.contains("assets")) {
			try {
				is = context.getAssets().open("4.gif");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (is != null) {
				Message mssage = new Message();
				mssage.what = ASSETS;
				connectHanlder.sendMessage(mssage);
			}
		}

	}

	/**
	 * Get image from newwork
	 * 
	 * @param path
	 *            The path of image
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] getImage(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		InputStream inStream = conn.getInputStream();
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return readStream(inStream);
		}
		return null;
	}

	/**
	 * Get image from newwork
	 * 
	 * @param path
	 *            The path of image
	 * @return InputStream
	 * @throws Exception
	 */
	public InputStream getImageStream(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return conn.getInputStream();
		}
		return null;
	}

	/**
	 * Get data from stream
	 * 
	 * @param inStream
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	/**
	 * 保存文件
	 * 
	 * @param bm
	 * @param fileName
	 * @throws IOException
	 */
	public void saveFile(Bitmap bm, String fileName) throws IOException {
		File dirFile = new File(ALBUM_PATH);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		File myCaptureFile = new File(ALBUM_PATH + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		bos.flush();
		bos.close();
	}

	// private Runnable saveFileRunnable = new Runnable() {
	// @Override
	// public void run() {
	// try {
	// saveFile(mBitmap, mFileName);
	// mSaveMessage = "图片保存成功！";
	// } catch (IOException e) {
	// mSaveMessage = "图片保存失败！";
	// e.printStackTrace();
	// }
	// messageHandler.sendMessage(messageHandler.obtainMessage());
	// }
	//
	// };

	private Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mSaveDialog.dismiss();
			Log.d(TAG, mSaveMessage);
			Toast.makeText(Pic_GifDownShowActivity.this, mSaveMessage,
					Toast.LENGTH_SHORT).show();
		}
	};

	/*
	 * 连接网络 由于在4.0中不允许在主线程中访问网络，所以需要在子线程中访问
	 */
	private Runnable connectNet = new Runnable() {
		@Override
		public void run() {
			try {

				// 以下是取得图片的两种方法
				// ////////////// 方法1：取得的是byte数组, 从byte数组生成bitmap
				// byte[] data = getImage(filePath);
				// if (data != null) {
				// mBitmap = BitmapFactory.decodeByteArray(data, 0,
				// data.length);// bitmap
				// } else {
				// Toast.makeText(IcsTestActivity.this, "Image error!", 1)
				// .show();
				// }
				// // //////////////////////////////////////////////////////
				//
				// // ******** 方法2：取得的是InputStream，直接从InputStream生成bitmap
				// // ***********/
				// mBitmap =
				// BitmapFactory.decodeStream(getImageStream(filePath));
				// ********************************************************************/

				// 发送消息，通知handler在主线程中更新UI
				// connectHanlder.sendEmptyMessage(0);

				String filename = "1234.gif";
				URL url = new URL(murl);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				size = connection.getContentLength();
				InputStream inputStream = connection.getInputStream();

				OutputStream outputStream = new FileOutputStream(mfilepath);

				while ((len = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer);
					hasRead += len;
					index = (int) (hasRead * 100) / size;
					message = new Message();
					message.what = 1;
					handler.sendMessage(message);
					Log.d(TAG, "index = " + index);
					System.out.println("has = " + hasRead + " size = " + size
							+ " index = " + index);
				}

				inputStream.close();
				outputStream.close();
				Log.d(TAG, "set image ...");
			} catch (Exception e) {
				Toast.makeText(Pic_GifDownShowActivity.this, "无法链接网络！", 1).show();
				e.printStackTrace();
			}

		}

	};

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				progressBar.setProgress(index);
				Log.d(TAG, "setProgress index:" + index);
				if (index >= 99) {
					progressBar.setVisibility(View.GONE);
					String rs = "下载完成";
					Toast.makeText(Pic_GifDownShowActivity.this, rs, Toast.LENGTH_SHORT)
							.show();
					Message mssage = new Message();
					mssage.what = URL;
					connectHanlder.sendMessage(mssage);

				}
			}

			super.handleMessage(msg);
		}

	};

	private Handler connectHanlder = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "display image");
			// 更新UI，显示图片

			switch (msg.what) {
			case FILE:

				mImageView.setGifImage(is);

				break;

			case ASSETS:
				mImageView.setGifImage(is);

				break;
			case URL:
				
				File mfile = new File(mfilepath);
				if(mfile.exists()){
					
					view.loadDataWithBaseURL(null, "<center><img src='"+mfilepath+"'></center>", "text/html", "utf-8", 
							  null);}
					
				
                               
//				File mfile = new File(mfilepath);

//				FileInputStream fin = null;
//				try {
//					fin = new FileInputStream(mfile);
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				mImageView.setGifImage(fin);

				break;
			}

		}
	};

}