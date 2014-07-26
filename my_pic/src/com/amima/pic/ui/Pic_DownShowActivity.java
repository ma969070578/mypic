package com.amima.pic.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amima.pic.R;
import com.amima.pic.download.DownloadLib;
import com.amima.pic.download.UpdateListener;
import com.amima.ui.base.BaseActivity;
import com.my.util.Files;
import com.nostra13.example.universalimageloader.Constants.Extra;

public class Pic_DownShowActivity extends BaseActivity {

	private final static String TAG = "IcsTestActivity";
 
	private GifImageView mImageView;

	 
	private Button less_5mb_btn, less_10mb_btn, remove_btn, pause_btn;
	private ProgressBar progress_bar;
	private TextView percentage_view, present_view;

	int filesize;

	DownloadLib fd1;
	private String msave_file_path;
	private String murl;

	InputStream is = null;
	private final int FILE = 11;
	private final int ASSETS = 12;
	private final int URL = 13;
	WebView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_gif_downshow);

		 

		progress_bar = (ProgressBar) this.findViewById(R.id.downloadbar);
		present_view = (TextView) this.findViewById(R.id.present_view);
		percentage_view = (TextView) this.findViewById(R.id.percentage_view);
		mImageView = (GifImageView) this.findViewById(R.id.imgSource);

		progress_bar.setMax(0);
		progress_bar.setProgress(0);

		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		String[] imageUrls = bundle.getStringArray(Extra.IMAGES);
		int pagerPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);
		murl = imageUrls[pagerPosition];
		if (murl.contains("http")) {
			downloadFile(murl);
		} else if (murl.contains("file")) {
			File mfile = new File(murl);

			Message mssage = new Message();
			mssage.what = FILE;
			connectHanlder.sendMessage(mssage);

		} else if (murl.contains("assets")) {

			Message mssage = new Message();
			mssage.what = ASSETS;
			connectHanlder.sendMessage(mssage);

		}

	}

	private void downloadFile(String url) {
		String download_url = "http://dj.95081.com/4.gif";
		File save_file_path = Environment.getExternalStorageDirectory();
		final DownloadLib fd = new DownloadLib(context, download_url,
				save_file_path);

		Bundle b = new Bundle();
		b.putString("param_name1", "param_value1");
		// fd.set_notification(TargetActivity.class, b);
		// filesize = fd.get_filesize();
		// Log.i("文件总大�?", Integer.toString(filesize));
		fd.download(new UpdateListener() {
			public void on_update(int downloaded_size) {
				Log.i("UI界面已经下载的大�?", Integer.toString(downloaded_size));

				int filesize = fd.get_filesize();

				progress_bar.setMax(filesize);
				progress_bar.setProgress(downloaded_size);

				percentage_view.setText(Files.get_percent(downloaded_size,
						filesize));
				present_view.setText(Files.get_size(downloaded_size) + "/"
						+ Files.get_size(filesize));

				System.out.println("downloaded_size..." + downloaded_size);
				if (downloaded_size == filesize) {
					System.out.println("111111111");

					msave_file_path = fd.get_filepath();

					Message mssage = new Message();
					mssage.what = URL;
					connectHanlder.sendMessage(mssage);
				}
			}
		});

	}

	private Handler connectHanlder = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "display image");
			// 更新UI，显示图片

			switch (msg.what) {
			case FILE:

				File mfile0 = new File("/mnt/sdcard", "2.gif");

				System.out.println("0000");

				if (mfile0.exists()) {
					GifDrawable gifFromFile;
					try {
						gifFromFile = new GifDrawable(mfile0);
						mImageView.setBackgroundDrawable(gifFromFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				}

				break;

			case ASSETS:
				GifDrawable gifFromAssets;
				try {
					gifFromAssets = new GifDrawable(getAssets(), "4.gif");
					mImageView.setBackgroundDrawable(gifFromAssets);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				break;
			case URL:

				String full_file_path = Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ msave_file_path;
				File mfile = new File(full_file_path);

				System.out.println("111111");

				if (mfile.exists()) {
					GifDrawable gifFromFile;
					try {
						gifFromFile = new GifDrawable(mfile);
						mImageView.setBackgroundDrawable(gifFromFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				}

			}
		};

	};
}