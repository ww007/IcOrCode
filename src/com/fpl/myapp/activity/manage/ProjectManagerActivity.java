package com.fpl.myapp.activity.manage;

import com.fpl.myapp2.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class ProjectManagerActivity extends FragmentActivity implements OnClickListener {

	private Button btnLeft;
	private Button btnRight;
	private Fragment tabLeft;
	private Fragment tabRight;
	private ImageButton ibQuit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_manager);

		initView();// ��ʼ�����е�view
		initEvents();
		setSelect(0);// Ĭ����ʾ����
	}

	private void initView() {
		btnLeft = (Button) findViewById(R.id.btn_left);
		btnRight = (Button) findViewById(R.id.btn_right);
		ibQuit = (ImageButton) findViewById(R.id.ib_top_quit);
		resetBackgraound();
	}

	private void initEvents() {
		btnLeft.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		ibQuit.setOnClickListener(this);
	}

	private void setSelect(int i) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();// ����һ������
		hideFragment(transaction);// �Ȱ����е�Fragment�����ˣ�Ȼ�������ٿ�ʼ�������Ҫ��ʾ��Fragment
		switch (i) {
		case 0:
			if (tabLeft == null) {
				tabLeft = new FragmentLeft();
				transaction.add(R.id.id_content, tabLeft);// ��Fragment��ӵ�Activity��
			} else {
				transaction.show(tabLeft);
			}
			btnLeft.setBackgroundResource(R.color.fragment);
			break;
		case 1:
			if (tabRight == null) {
				tabRight = new FragmentRight();
				transaction.add(R.id.id_content, tabRight);
			} else {
				transaction.show(tabRight);
			}
			btnRight.setBackgroundResource(R.color.fragment);
			break;
		default:
			break;
		}
		transaction.commit();// �ύ����
	}

	@Override
	public void onClick(View v) {
		resetBackgraound();
		switch (v.getId()) {
		case R.id.btn_left:
			setSelect(0);
			break;
		case R.id.btn_right:
			setSelect(1);
			break;
		case R.id.ib_top_quit:
			finish();
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		resetBackgraound();
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			setSelect(1);
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			setSelect(0);
			return true;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void resetBackgraound() {
		btnLeft.setBackgroundResource(0);
		btnRight.setBackgroundResource(0);
	}

	/*
	 * �������е�Fragment
	 */
	private void hideFragment(FragmentTransaction transaction) {
		if (tabLeft != null) {
			transaction.hide(tabLeft);
		}
		if (tabRight != null) {
			transaction.hide(tabRight);
		}
	}
}
