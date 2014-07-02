package com.my.activity.sq;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amima.app.MyApp;
import com.amima.pic.R;
import com.amima.ui.base.BaseActivity;
import com.my.util.SystemOut;
import com.my.util.Tools;

//传入到js中的数据不可以带标签（自己测试的），
//如果带会报 EventHub.removeMessages(int what = 107) is not supported before the WebViewCore is set up. 

public class Sq_WebActivity extends BaseActivity {
	 
	/** Called when the activity is first created. */
	 
	WebView webView;
	 
	ProgressDialog pd;
	 
	Handler handler;
 
	private Button mBack;
	private TextView mTitle;
 
	public void onCreate(Bundle savedInstanceState) {
		 
		super.onCreate(savedInstanceState);
		 
		setContentView(R.layout.news);
		mBack=(Button) findViewById(R.id.butt_left);
		mTitle=(TextView) findViewById(R.id.title);
		mTitle.setText("");
		mTitle.setTextSize(13.0f);
		mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(pd!=null&&pd.isShowing())
					pd.cancel();
				Sq_WebActivity.this.finish();
			}
		});
		
		Bundle data=this.getIntent().getExtras();
		
		if (MyApp.user.getType()==2){
			RelativeLayout layout=(RelativeLayout)findViewById(R.id.news_title_layout);
			layout.setBackgroundColor(Color.argb(0xff, 0x02, 0x87, 0xc5));
		}
		
		
		final String url=Tools.getUrl(data.getString("url"));
		handler = new Handler() {

			public void handleMessage(Message msg)

			{// 定义一个Handler，用于处理下载线程与UI间通讯

				if (!Thread.currentThread().isInterrupted())

				{

					switch (msg.what)

					{

					case 0:

						pd.show();// 显示进度对话框

						break;

					case 1:

						pd.hide();// 隐藏进度对话框，不可使用dismiss()、cancel(),否则再次调用show()时，显示的对话框小圆圈不会动。

						break;
						
					case 2:
						SystemOut.out("准备载入网页...................."+url);
						webView.loadUrl(url);// 载入网页
						break;

					}

				}

				super.handleMessage(msg);

			}

		};
		 
		init();
		
		

		loadurl();
		 
    }
 

 
	public void init() {// 初始化

		webView = (WebView) findViewById(R.id.webView);

		webView.getSettings().setJavaScriptEnabled(true);// 可用JS

		webView.setScrollBarStyle(0);// 滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
//		WebSettings settings = webView.getSettings();
//		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); 
		webView.setWebViewClient(new WebViewClient() {

			public boolean shouldOverrideUrlLoading(final WebView view,
					final String url) {

				loadurl();// 载入网页

				return true;

			}// 重写点击动作,用webview载入

		});

		webView.setWebChromeClient(new WebChromeClient() {

			public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发

				if (progress == 100) {

					handler.sendEmptyMessage(1);// 如果全部载入,隐藏进度对话框

				}

				super.onProgressChanged(view, progress);

			}

		});

		pd = new ProgressDialog(Sq_WebActivity.this);

		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		pd.setMessage("数据载入中，请稍候！");

	}
 
;
 
	public boolean onKeyDown(int keyCode, KeyEvent event) {// 捕捉返回键

		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {

			webView.goBack();

			return true;

		} else if (keyCode == KeyEvent.KEYCODE_BACK) {

			//ConfirmExit();// 按了返回键，但已经不能返回，则执行退出确认
			if(pd!=null&&pd.isShowing())
				pd.cancel();
            this.finish();
			return true;

		}

		return super.onKeyDown(keyCode, event);

	}
 


 

 
	public void loadurl() {

		Message msg = handler.obtainMessage();
		msg.what = 2;
		handler.sendMessage(msg);

	}
 
}

