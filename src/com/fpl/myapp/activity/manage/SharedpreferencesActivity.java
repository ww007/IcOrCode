package com.fpl.myapp.activity.manage;

import com.fpl.myapp2.R;
import com.fpl.myapp2.R.layout;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SharedpreferencesActivity extends Activity {

	private RadioGroup rgIcOrCode;
	private RadioGroup rgImeiOrMac;
	private EditText etIp;
	private EditText etNumber;
	private SharedPreferences mSharedPreferences;
	private int getStyle;
	private int readStyle = 0;
	private SharedPreferences m1SharedPreferences;
	private String ip;
	private String number;
	private int OnlyNumber;
	private RadioButton rbIC;
	private RadioButton rbCode;
	private RadioButton rbImei;
	private RadioButton rbMac;
	private ImageButton ibQuit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sharedpreferences);

		mSharedPreferences = this.getSharedPreferences("readStyles", Activity.MODE_PRIVATE);
		getStyle = mSharedPreferences.getInt("readStyle", 0);

		m1SharedPreferences = this.getSharedPreferences("ipAddress", Activity.MODE_PRIVATE);

		// SharedPreferences��ȡ������ϴ���ַ
		ip = m1SharedPreferences.getString("ip", "");
		number = m1SharedPreferences.getString("number", "");
		OnlyNumber = m1SharedPreferences.getInt("macorimei", 0);

		initView();
		setListeners();
	}

	private void setListeners() {
		ibQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		rgIcOrCode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int radioButtonId = group.getCheckedRadioButtonId();
				RadioButton rbIcOrCode = (RadioButton) findViewById(radioButtonId);
				if (rbIcOrCode.getText().toString().equals("IC��")) {
					readStyle = 0;
					// SharedPreferences�����ȡ��ʽ
					SharedPreferences.Editor editor = mSharedPreferences.edit();
					editor.putInt("readStyle", readStyle);
					editor.commit();
				} else if (rbIcOrCode.getText().toString().equals("������")) {
					readStyle = 1;
					// SharedPreferences�����ȡ��ʽ
					SharedPreferences.Editor editor1 = mSharedPreferences.edit();
					editor1.putInt("readStyle", readStyle);
					editor1.commit();
				}

			}
		});

		rgImeiOrMac.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int radioButtonId = group.getCheckedRadioButtonId();
				RadioButton rbImeiOrMac = (RadioButton) findViewById(radioButtonId);
				if (rbImeiOrMac.getText().toString().equals("IMEI")) {
					SharedPreferences.Editor editor = m1SharedPreferences.edit();
					editor.putInt("macorimei", 0);
					editor.commit();
				} else if (rbImeiOrMac.getText().toString().equals("MAC")) {
					SharedPreferences.Editor editor1 = m1SharedPreferences.edit();
					editor1.putInt("macorimei", 1);
					editor1.commit();
				}
			}
		});
	}

	private void initView() {
		ibQuit = (ImageButton) findViewById(R.id.ib_shared_quit);
		rgIcOrCode = (RadioGroup) findViewById(R.id.radioGroup1);
		rgImeiOrMac = (RadioGroup) findViewById(R.id.radioGroup2);
		rbIC = (RadioButton) findViewById(R.id.radio_ic);
		rbCode = (RadioButton) findViewById(R.id.radio_code);
		rbImei = (RadioButton) findViewById(R.id.radio_imei);
		rbMac = (RadioButton) findViewById(R.id.radio_mac);
		etIp = (EditText) findViewById(R.id.et_shared_ip);
		etNumber = (EditText) findViewById(R.id.et_shared_number);
		etIp.setText(ip);
		etNumber.setText(number);

		if (OnlyNumber == 0) {
			rbImei.setChecked(true);
		} else {
			rbMac.setChecked(true);
		}
		if (getStyle == 0) {
			rbIC.setChecked(true);
		} else {
			rbCode.setChecked(true);
		}

	}

	@Override
	protected void onDestroy() {
		String saveIp = etIp.getText().toString();
		String saveNumber = etNumber.getText().toString();
		// SharedPreferences����������ϴ���ַ
		SharedPreferences.Editor mEditor = m1SharedPreferences.edit();
		mEditor.putString("ip", saveIp);
		mEditor.putString("number", saveNumber);
		mEditor.commit();
		super.onDestroy();
	}

}
