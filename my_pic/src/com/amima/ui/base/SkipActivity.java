package com.amima.ui.base;

import android.content.Intent;
import android.os.Bundle;

import com.amima.pic.ui.Pic_InitActivity;

/**
 * @author Administrator
 * 
 */
public class SkipActivity extends BaseActivity {
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent sq_Intent = new Intent(this, Pic_InitActivity.class);
		startActivity(sq_Intent);
		this.finish();

		// switch (EdjTools.APPTYPE) {
		// case 0:
		// Intent shb_Intent = new Intent(this,
		// com.emotte.shb.SHB_InitActivity.class);
		// EdjTools.CURRENTTYPE=0;
		// startActivity(shb_Intent);
		// this.finish();
		// break;
		// case 1:
		// /* Intent edj_Intent = new Intent(this,
		// com.emotte.edj.Update.class);
		// EdjTools.CURRENTTYPE=1;
		// startActivity(edj_Intent);
		// this.finish();*/
		// break;
		// case 2:
		// // Intent jz_Intent = new Intent(this,
		// // com.emotte.jzb.JZ_Update.class);
		// // EdjTools.CURRENTTYPE=2;
		// // startActivity(jz_Intent);
		// // this.finish();
		// break;
		// case 3:
		// /* Intent jk_Intent = new Intent(this,
		// com.emotte.jkb.JK_New_Update.class);
		// EdjTools.CURRENTTYPE=3;
		// startActivity(jk_Intent);
		// this.finish();*/
		// break;
		// case 4:
		// // Intent nIntent3 = new Intent(this,
		// // DW_InitActivity.class);
		// // EdjTools.CURRENTTYPE=4;
		// // startActivity(nIntent3);
		// // this.finish();
		// break;
		// case 5:
		// // Intent ycbIntent = new Intent(this,
		// // YCBWelcomeActivity.class);
		// // EdjTools.CURRENTTYPE=5;
		// // startActivity(ycbIntent);
		// // this.finish();
		// break;
		// case 6:
		// // Intent dcb_Intent = new Intent(this,
		// // com.emotte.dcb.Dcb_Update.class);
		// // EdjTools.CURRENTTYPE=6;
		// // startActivity(dcb_Intent);
		// // this.finish();
		// break;
		// case 8:
		// /* Intent jkb_Intent = new Intent(this,
		// com.emotte.jkb.sqb.JK_BloodUpdate.class);
		// EdjTools.CURRENTTYPE=8;
		// startActivity(jkb_Intent);
		// this.finish();*/
		// break;
		//
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

}
