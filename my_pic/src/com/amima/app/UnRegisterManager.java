package com.amima.app;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.widget.Toast;

import com.amima.pic.R;
import com.my.util.Tools;

public class UnRegisterManager {
	public ProgressDialog progress = null;
	Timer outtime = null;
	Handler mhandler;
	Activity con;

	public UnRegisterManager(Activity con, Handler mhandler) {
		this.mhandler = mhandler;
		this.con = con;
	}

	public void handlerLogic(int what) {
		switch (what) {
		case 1:
			timeCancel(progress);
			break;
		case 2:
			timeCancel(progress);
			Toast.makeText(con, "注销失败", Toast.LENGTH_LONG).show();
			break;
		case 3:
			timeCancel(progress);
			Toast.makeText(con, "连接超时，请检查网络...", Toast.LENGTH_LONG).show();
			break;
		case 4:
			timeCancel(progress);

			if (con.isFinishing()) {
				return;
			}
			if (progress == null) {
				progress = new ProgressDialog(con);
			}
			if (progress != null && !progress.isShowing()) {
				progress.setCancelable(false);
				progress.setMessage("正在注销中.......");
				progress.show();
			}
			break;
		case 5:
			timeCancel(progress);
			Toast.makeText(con, "注销失败，请检查网络连接...", Toast.LENGTH_LONG).show();
			break;
		case 6:
			timeCancel(progress);
			AlertDialog.Builder promptDialog = new AlertDialog.Builder(con);
			promptDialog.setTitle("网络异常");
			promptDialog.setMessage("网络不可用 请检查网络状态");
			promptDialog.setPositiveButton("网络设置",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
							con.startActivity(new Intent(
									Settings.ACTION_WIFI_SETTINGS)); // 直接进入手机中的wifi网络设置界面
						}
					});
			promptDialog.setNegativeButton("退出程序",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Tools.appExit(con);
							dialog.cancel();
						}
					});

			promptDialog.show();
			break;
		}
	}

	public void unRegister() {

		if (progress == null) {
			progress = new ProgressDialog(con);
		}
		new AlertDialog.Builder(con).setIcon(R.drawable.icon).setTitle("注销")
				.setMessage("您确认退出登录吗？")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				})

				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}

	public void timeCancel(ProgressDialog progress) {
		if (outtime != null) {
			try {
				outtime.cancel();
			} catch (Exception e) {

			}
			outtime = null;
		}
		if (progress != null) {
			progress.dismiss();
		}
	}

	public void timeOut(final Handler handler) {
		if (outtime != null) {
			try {
				outtime.cancel();
			} catch (Exception e) {

			}
			outtime = null;
		}
		outtime = new Timer();
		outtime.schedule(new TimerTask() {
			public void run() {
				Message msg = new Message();
				msg.what = 3;

				handler.sendMessage(msg);

			}
		}, 12000);
	}
}
