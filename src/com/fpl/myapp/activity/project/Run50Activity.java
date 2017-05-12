package com.fpl.myapp.activity.project;

import org.apache.log4j.Logger;

import com.fpl.myapp2.R;
import com.fpl.myapp.activity.CaptureActivity;
import com.fpl.myapp.base.NFCActivity;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp.util.NetUtil;
import com.wnb.android.nfc.dataobject.entity.Student;
import com.wnb.android.nfc.dataobject.service.IItemService;
import com.wnb.android.nfc.dataobject.service.impl.NFCItemServiceImpl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Run50Activity extends NFCActivity {

	private TextView tvTitle;
	private Button btnStart;
	private Button btnQuit;
	private Student student;
	private String sex;

	private Logger log = Logger.getLogger(Run50Activity.class);
	private Button btnScan;
	private SharedPreferences mSharedPreferences;
	private int readStyle;
	private Context context;
	private TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run);// 50��800����һ������
		context = this;

		mSharedPreferences = getSharedPreferences("readStyles", Activity.MODE_PRIVATE);
		readStyle = mSharedPreferences.getInt("readStyle", 0);

		initView();
		setListeners();
	}

	@Override
	public void onNewIntent(Intent intent) {
		if (readStyle == 0) {
			readCard(intent);
		} else {
			NetUtil.showToast(context, "��ǰѡ���IC��״̬��������");
		}
	}

	/**
	 * ����
	 */
	private void readCard(Intent intent) {
		try {
			IItemService itemService = new NFCItemServiceImpl(intent);
			student = itemService.IC_ReadStuInfo();
			log.info("50���ܶ���=>" + student.toString());

			if (1 == student.getSex()) {
				sex = "��";
			} else {
				sex = "Ů";
			}
			if (student.getStuCode() != null) {
				Intent intent2 = new Intent(Run50Activity.this, RunGradeInputActivity.class);
				intent2.putExtra("number", student.getStuCode());
				intent2.putExtra("name", student.getStuName());
				intent2.putExtra("sex", sex);
				intent2.putExtra("title", "50����");
				startActivity(intent2);
			} else {
				Toast.makeText(this, "�˿���Ч", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			log.error("50���ܶ���ʧ��");
			e.printStackTrace();
		}

	}

	private void initView() {
		btnScan = (Button) findViewById(R.id.btn_run_scanCode);
		tvTitle = (TextView) findViewById(R.id.tv_title_run);
		tv = (TextView) findViewById(R.id.tv_run);
		tvTitle.setText("50����");
		btnStart = (Button) findViewById(R.id.btn_start);
		btnQuit = (Button) findViewById(R.id.btn_quit);

		if (readStyle == 0) {
			btnScan.setVisibility(View.GONE);
		} else {
			tv.setVisibility(View.INVISIBLE);
			btnScan.setVisibility(View.VISIBLE);
		}
	}

	private void setListeners() {
		btnScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent1 = new Intent(Run50Activity.this, CaptureActivity.class);
				intent1.putExtra("className", Constant.RUN50 + "");
				startActivity(intent1);
				finish();
			}
		});
		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (readStyle == 0) {
					Intent intent = new Intent(Run50Activity.this, RunMeteringActivity.class);
					intent.putExtra("title", tvTitle.getText().toString());
					startActivity(intent);
				} else {
					NetUtil.showToast(context, "��ǰΪɨ��ģʽ");
				}
			}
		});

		btnQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			Intent intent = new Intent(Run50Activity.this, RunMeteringActivity.class);
			intent.putExtra("title", tvTitle.getText().toString());
			startActivity(intent);
			return true;
		case 135:
			Intent intent1 = new Intent(Run50Activity.this, CaptureActivity.class);
			intent1.putExtra("className", Constant.RUN50 + "");
			startActivity(intent1);
			finish();
			return true;
		default:
			break;
		}

		return super.onKeyDown(keyCode, event);

	}

}
