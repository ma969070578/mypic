package cn.sharesdk.wxapi;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.utils.WechatClientNotExistException;
import cn.sharesdk.wechat.utils.WechatTimelineNotSupportedException;

import com.amima.pic.R;
import com.amima.ui.base.BaseActivity;
import com.my.dialog.ProgressDlg;
import com.my.util.Tools;

public class WeiBoShareActivity extends BaseActivity {

	
	private static final int SHARE_SUCCESS = 30;
	private static final int SHARE_FAIL = 31;
	private static final int SHARE_CANCEL = 32;

	private Button left_btn;
	private Button right_btn;
	private TextView title;
	private TextView share_content_size;
	private String message = "";  //分享内容
	private String sharename = "";
	private EditText shareEdit;
	private int shareType;//分享类型  //  0、新浪微博 1、腾讯微博


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Intent loaclIntent  = this.getIntent();
		if(loaclIntent!=null){
			message = loaclIntent.getStringExtra("share_msg");
			shareType = loaclIntent.getIntExtra("shareType",0);
		}
		ShareSDK.initSDK(this);

		setContentView(R.layout.shb_weibo_share);
		initView();
		
		
	}

	public void initView(){
		left_btn = (Button) this.findViewById(R.id.butt_left);
		left_btn.setVisibility(View.VISIBLE);
		right_btn = (Button) this.findViewById(R.id.butt_right1);
		right_btn.setVisibility(View.VISIBLE);
		
		title = (TextView) this.findViewById(R.id.title);
		shareEdit = (EditText) this.findViewById(R.id.share_editText);
		share_content_size = (TextView) this.findViewById(R.id.share_content_size);
		title.setText("编辑内容");
		right_btn.setText("发送");
		shareEdit.setText(message);
		shareEdit.addTextChangedListener(watcher);

		share_content_size.setText(message.length()+"/140");
		
		left_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WeiBoShareActivity.this.finish();
			}
		});
		right_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 message = shareEdit.getText().toString();
				 if(!Tools.isNull(message)&&message.length()<=140){
					 if(shareType==0){
						 share(message, null, SinaWeibo.NAME);
						 
					 }else if(shareType==1){
						 share(message, null, TencentWeibo.NAME);
					 }
						ProgressDlg.showDlg(WeiBoShareActivity.this, "正在发送中.......");
						ProgressDlg.getProgress().setCancelable(false);

				 }else if(!Tools.isNull(message)&&message.length()>140){
					 Tools.showShortToast("内容大于140字数", WeiBoShareActivity.this);
				 }
			}
		});
		
		
	}
	
	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		    	if(s.length()<=140){
		    		share_content_size.setTextColor(context.getResources().getColor(R.color.black));
				  share_content_size.setText(s.length()+"/140");
				}else{
		    		share_content_size.setTextColor(context.getResources().getColor(R.color.red));
				  share_content_size.setText(s.length()+"/140");
				}

		}
	};
	
	
	
	// 新浪微博分享 腾讯微博等只需修改 NAME
		public void share(String text, String photopath, String sharename) {
			Platform.ShareParams sp = new SinaWeibo.ShareParams();
			sp.text = text;
			if (!Tools.isNull(photopath)) {
				// sp.imagePath = "/mnt/sdcard/测试分享的图片.jpg";
				sp.imagePath = photopath;
			}
			Platform weibo = ShareSDK.getPlatform(context, sharename);
//			if(SinaWeibo.NAME.equals(sharename)){
//				weibo.SSOSetting(true);
//			}
			
			// 设置分享事件回调
			weibo.setPlatformActionListener(new PlatformActionListener() {
				public void onError(Platform platform, int action, Throwable t) {
					// 操作失败的处理代码
//					Message m = handler.obtainMessage();
//					m.what = SHARE_FAIL;
//					TestShare.this.handler.sendMessage(m);
					
					Message msg =  handler.obtainMessage();
					msg.what = SHARE_FAIL;
					msg.arg1 = action;
					msg.obj = t;
					WeiBoShareActivity.this.handler.sendMessage(msg);
				}

				public void onComplete(Platform platform, int action,
						HashMap<String, Object> res) {
					// 操作成功的处理代码
					Message m = handler.obtainMessage();
					m.what = SHARE_SUCCESS;
					WeiBoShareActivity.this.handler.sendMessage(m);

					
				}

				public void onCancel(Platform platform, int action) {
					// 操作取消的处理代码
					Message m = handler.obtainMessage();
					m.what = SHARE_CANCEL;
					WeiBoShareActivity.this.handler.sendMessage(m);
				}
			});
			// 执行图文分享
			weibo.share(sp);
		}
		
		
		
		
		private Handler handler = new Handler() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				ProgressDlg.cancleDlg(WeiBoShareActivity.this);
				switch (msg.what) {
		        case SHARE_SUCCESS:
					String successtext = getResources().getString(R.string.share_completed);
					Tools.showShortToast(successtext,WeiBoShareActivity.this);
		        	WeiBoShareActivity.this.finish();
                   
					break;
				case SHARE_FAIL:
					String failtext="";
					
					if (msg.obj instanceof WechatClientNotExistException) {
						failtext = getResources().getString(R.string.wechat_client_inavailable);
					}
					else if (msg.obj instanceof WechatTimelineNotSupportedException) {
						failtext = getResources().getString(R.string.wechat_client_inavailable);
					}
//					java.lang.Throwable: {"ret":5,"seqid":5950018181724704141,"detailerrinfo":
//					{"timestamp":1385346563,"proctime":41,"cmd":1472,"accesstoken":"","clientip":"219.143.8.242","apiname":"weibo.t.add","ret2":5,"appkey":"801400858","ret1":20,"ret4":3515057674,"ret3":75},"msg":"prevent duplicate publication","errcode":75}
					else if (msg.obj instanceof java.lang.Throwable &&  !Tools.isNull(msg.obj.toString())&&msg.obj.toString().contains("prevent duplicate publication")) {
					
						failtext = getResources().getString(R.string.prevent_duplicate);
					}else if(msg.obj.toString().contains("error")){
						failtext = getResources().getString(R.string.share_failed_error);

					}else {
						failtext = getResources().getString(R.string.share_failed);
					}
					Tools.showShortToast(failtext,WeiBoShareActivity.this);
					break;
				case SHARE_CANCEL:
					
					break;


				}
			}

		};
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		ProgressDlg.cancleDlg(WeiBoShareActivity.this);

		//ShareSDK.stopSDK(this);

		super.onDestroy();
	}
}
