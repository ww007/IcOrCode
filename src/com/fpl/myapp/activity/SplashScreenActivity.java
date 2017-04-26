package com.fpl.myapp.activity;

import com.fpl.myapp2.R;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.util.HttpUtil;
import com.fpl.myapp.util.NetUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

/**
 * ������������
 *
 */
public class SplashScreenActivity extends Activity {

	private int SPLASH_TIME_OUT = 2000;
	private Handler mHandle = new Handler();;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		context = this;

		// ����NetUtil�е������жϷ���
		boolean result = NetUtil.netState(context);
		isWifiConnected(result);

	}


	private void isWifiConnected(boolean result) {
		if (true == result) {
			if (DbService.getInstance(context).loadAllStudentItem().isEmpty()) {

				HttpUtil.getItemInfo(context);

				HttpUtil.getStudentInfo(context);

				HttpUtil.getStudentItemInfo(context);
				if (HttpUtil.okFlag == 3000) {
					SPLASH_TIME_OUT = 2000;
				} else {
					SPLASH_TIME_OUT = 60000;
				}
			} else {
				SPLASH_TIME_OUT = 2000;
			}
			handlePost();
		} else {
			if (DbService.getInstance(context).loadAllStudentItem().isEmpty()) {
				Toast.makeText(context, "��ʾ����ǰδ������·�������������ʧ��", Toast.LENGTH_SHORT).show();
			}
			handlePost();
		}

	}

	private void handlePost() {
		mHandle.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
				startActivity(i);
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

}