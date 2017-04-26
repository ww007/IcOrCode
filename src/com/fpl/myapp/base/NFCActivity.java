package com.fpl.myapp.base;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.widget.Toast;

public class NFCActivity extends Activity {

	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	public void init() {
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mAdapter == null) {
			Toast.makeText(this, "�豸��֧��NFC��", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		if (mAdapter != null && !mAdapter.isEnabled()) {
			Toast.makeText(this, "����ϵͳ������������NFC���ܣ�", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		// ��������MIME������ǲһ����ͼ������
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		try {
			ndef.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		mFilters = new IntentFilter[] { ndef };
		// ����Ϊ����MifareClassic��ǩ�����б�
		mTechLists = new String[][] { new String[] { MifareClassic.class.getName() } };
	}

	@Override
	protected void onResume() {
		super.onResume();
		// ʹ��ǰ̨����ϵͳ
		mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAdapter.disableForegroundDispatch(this);
	}

}
