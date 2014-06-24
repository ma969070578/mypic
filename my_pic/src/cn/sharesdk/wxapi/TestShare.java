package cn.sharesdk.wxapi;

import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.utils.WechatClientNotExistException;
import cn.sharesdk.wechat.utils.WechatTimelineNotSupportedException;

import com.amima.pic.R;
import com.my.activity.BaseActivity;
import com.my.util.Tools;

public class TestShare extends  BaseActivity  implements OnClickListener{

	private RelativeLayout sina;
	private RelativeLayout sms;
	private RelativeLayout qq;
	private RelativeLayout chat;
	private TextView invite_code;

	private Context context;
	private String[] items = new String[] { "分享给好友", "分享到朋友圈" };
	private String share_content;
	private String share_content_sms;
	private String user_id;
	private TextView share_name;
	
	private Button left_btn;
	private Button right_btn;
	private TextView title;
	
	private static final int SHARE_SUCCESS = 30;
	private static final int SHARE_FAIL = 31;

	private LinearLayout ll_head;
	private RelativeLayout rl_yqcode;
	private String flag_share;
	private String yymenu_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.test_share);

		/**
		 * 手册内容 分享111111111112222223333
		 */
		Intent intent = getIntent();
		if (intent != null) {
			flag_share = intent.getStringExtra("yyb_share");
			yymenu_content = intent.getStringExtra("content");
		}
		context = TestShare.this;

		ShareSDK.initSDK(this);
		left_btn = (Button) this.findViewById(R.id.butt_left);
		title = (TextView) this.findViewById(R.id.title);
		sina = (RelativeLayout) this.findViewById(R.id.share_sina);
		qq = (RelativeLayout) this.findViewById(R.id.share_qq);
		chat = (RelativeLayout) this.findViewById(R.id.share_chat);
		sms = (RelativeLayout) this.findViewById(R.id.share_sms);
		 
 
			title.setText("分享");
		 
	 
		}
 
		 
//		left_btn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				TestShare.this.finish();
//			}
//		});
//		sina.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				// 分享到新浪微博
//				// showShare(true, SinaWeibo.NAME,TestShare.this);
//				if ("yyb_share".equals(flag_share)) {
//					share(yymenu_content, null, SinaWeibo.NAME);
//				} else {
////					share(share_content, null, SinaWeibo.NAME);
//					Intent sinaIntent = new Intent(TestShare.this, WeiBoShareActivity.class);
//					sinaIntent.putExtra("share_msg", share_content);
//					sinaIntent.putExtra("shareType", 0);
//					TestShare.this.startActivity(sinaIntent);
//					
//				}
//
//			}
//		});
//
//		qq.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				// 分享到腾讯微博
//				// showShare(true, TencentWeibo.NAME, TestShare.this);
//				if ("yyb_share".equals(flag_share)) {
//					share(yymenu_content, null, TencentWeibo.NAME);
//				} else {
//					//share(share_content, null, TencentWeibo.NAME);
//					Intent tentIntent = new Intent(TestShare.this, WeiBoShareActivity.class);
//					tentIntent.putExtra("share_msg", share_content);
//					tentIntent.putExtra("shareType", 1);
//					TestShare.this.startActivity(tentIntent);
//				}
//
//			}
//		});
//
//		chat.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if ("yyb_share".equals(flag_share)) { // 分享内容
//					showChatDialogContent();
//				} else {
//					showChatDialog();
//				}
//
//			}
//		});
//
//		sms.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if ("yyb_share".equals(flag_share)) { // 分享内容
//					Tools.sendSms(TestShare.this, yymenu_content, "");
//				} else {
//					Tools.sendSms(TestShare.this, share_content_sms, "");
//				}
//
//			}
//		});
//
//		ll_head = (LinearLayout) findViewById(R.id.ll_head);
//		rl_yqcode = (RelativeLayout) findViewById(R.id.rl_yqcode);
//		if ("yyb_share".equals(flag_share)) {
//			ll_head.setVisibility(View.GONE);
//			rl_yqcode.setVisibility(View.GONE);
//		}
//	}

	// 新浪微博分享 腾讯微博等只需修改 NAME
	public void share(String text, String photopath, String sharename) {
		Platform.ShareParams sp = new SinaWeibo.ShareParams();
		sp.text = text;
		if (!Tools.isNull(photopath)) {
			// sp.imagePath = "/mnt/sdcard/测试分享的图片.jpg";
			sp.imagePath = photopath;
		}
		Platform weibo = ShareSDK.getPlatform(context, sharename);
		// 设置分享事件回调
		weibo.setPlatformActionListener(new PlatformActionListener() {
			public void onError(Platform platform, int action, Throwable t) {
				// 操作失败的处理代码
//				Message m = handler.obtainMessage();
//				m.what = SHARE_FAIL;
//				TestShare.this.handler.sendMessage(m);
				
				Message msg =  handler.obtainMessage();
				msg.what = SHARE_FAIL;
				msg.arg1 = action;
				msg.obj = t;
				handler.sendMessage(msg);
			}

			public void onComplete(Platform platform, int action,
					HashMap<String, Object> res) {
				// 操作成功的处理代码
				Message m = handler.obtainMessage();
				m.what = SHARE_SUCCESS;
				TestShare.this.handler.sendMessage(m);

				
			}

			public void onCancel(Platform platform, int action) {
				// 操作取消的处理代码
			}
		});
		// 执行图文分享
		weibo.share(sp);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}

	/**
	 * 显示选择对话框
	 */
	private void showChatDialog() {

		new AlertDialog.Builder(this).setTitle("分享到")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
//						case 0:
//							share(share_content, null, Wechat.NAME);
//							break;
//						case 1:
//
//							share(share_content, null, WechatMoments.NAME);
//							break;
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

	 

	private Handler handler = new Handler() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {
	        case SHARE_SUCCESS:
				
				String successtext = getResources().getString(R.string.share_completed);
				Tools.showShortToast(successtext,TestShare.this);

				break;
			case SHARE_FAIL:
				String failtext="";
				
				if (msg.obj instanceof WechatClientNotExistException) {
					failtext = getResources().getString(R.string.wechat_client_inavailable);
				}
				else if (msg.obj instanceof WechatTimelineNotSupportedException) {
					failtext = getResources().getString(R.string.wechat_client_inavailable);
				}
//				java.lang.Throwable: {"ret":5,"seqid":5950018181724704141,"detailerrinfo":
//				{"timestamp":1385346563,"proctime":41,"cmd":1472,"accesstoken":"","clientip":"219.143.8.242","apiname":"weibo.t.add","ret2":5,"appkey":"801400858","ret1":20,"ret4":3515057674,"ret3":75},"msg":"prevent duplicate publication","errcode":75}
				else if (msg.obj instanceof java.lang.Throwable &&  !Tools.isNull(msg.obj.toString())&&msg.obj.toString().contains("prevent duplicate publication")) {
				
					failtext = getResources().getString(R.string.prevent_duplicate);
				}
				else {
					failtext = getResources().getString(R.string.share_failed);
				}
				Tools.showShortToast(failtext,TestShare.this);
				break;


			}
		}

	};


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}}
