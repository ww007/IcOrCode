package com.fpl.myapp.activity.information;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fpl.myapp2.R;
import com.fpl.myapp.activity.project.BroadJumpActivity;
import com.fpl.myapp.adapter.ICInfoAdapter;
import com.fpl.myapp.base.NFCActivity;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.entity.ICInfo;
import com.fpl.myapp.util.Constant;
import com.wnb.android.nfc.dataobject.entity.IC_ItemResult;
import com.wnb.android.nfc.dataobject.entity.Student;
import com.wnb.android.nfc.dataobject.service.IItemService;
import com.wnb.android.nfc.dataobject.service.impl.NFCItemServiceImpl;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import ww.greendao.dao.Item;

public class ICInformationActivity extends NFCActivity {
	private ICInfoAdapter mAdapter;
	private ArrayList<ICInfo> icInfos;
	private String sex;
	private TextView tvNumber;
	private TextView tvName;
	private TextView tvGender;
	private ListView lvIcInfo;
	private TextView tvShow;
	private SharedPreferences sharedPreferences;

	public ArrayList<String> projects = new ArrayList<>();
	// private String[] newProject;
	// private String[] newValue;
	// private int number50, numberH, numberW, numberFHL, numberLDTY,
	// numberYWQZ, number800, number1000, numberFWC,
	// numberZWTQQ, numberTS, numberLSL, numberRSL, numberYTXS, numberMG,
	// numberPQ, numberHWSXQ, numberLQYQ,
	// numberZFP, numberTJZ, numberZQYQ, numberYY;
	private List<Item> items;
	private ImageButton ibQuit;
	private Logger log = Logger.getLogger(ICInformationActivity.class);

	Handler mHandler = new Handler();
	Runnable updateTv1 = new Runnable() {
		@Override
		public void run() {
			tvShow.setText("��ȡ��...");
		}
	};
	Runnable updateTv2 = new Runnable() {
		@Override
		public void run() {
			tvShow.setText("��ȡ���!");
		}
	};
	private ICInfo icInfo1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_icinformation);
		// ��ȡ���ش洢��ѡ����Ŀ
		sharedPreferences = getSharedPreferences("projects", Activity.MODE_PRIVATE);
		int selected = sharedPreferences.getInt("size", 0);
		Log.i("selected=", selected + "");

		icInfo1 = new ICInfo();

		initView();
		setListener();
	}

	@Override
	public void onNewIntent(final Intent intent) {
		tvShow.setText("��ȡ�жϣ�������ˢ����");
		readCard(intent);

	}

	private void readCard(Intent intent) {
		NFCItemServiceImpl itemService;
		try {
			icInfos.clear();
			itemService = new NFCItemServiceImpl(intent);
			Student student = itemService.IC_ReadStuInfo();
			Log.i("StudentTest===", student.toString());
			if (1 == student.getSex()) {
				sex = "��";
			} else {
				sex = "Ů";
			}

			tvGender.setText(sex);
			tvName.setText(student.getStuName().toString());
			tvNumber.setText(student.getStuCode().toString());

			readHW(itemService);
			readMiddleRun(itemService);
			readVision(itemService);
			readCommon(itemService, Constant.BASKETBALL_SKILL, "ms", "��������");
			readCommon(itemService, Constant.BROAD_JUMP, "cm", "������Զ");
			readCommon(itemService, Constant.FOOTBALL_SKILL, "ms", "��������");
			readCommon(itemService, Constant.INFRARED_BALL, "cm", "ʵ����");
			readCommon(itemService, Constant.JUMP_HEIGHT, "cm", "����");
			readCommon(itemService, Constant.KICKING_SHUTTLECOCK, "��", "�����");
			readCommon(itemService, Constant.PULL_UP, "��", "��������");
			readCommon(itemService, Constant.PUSH_UP, "��", "���Գ�");
			readCommon(itemService, Constant.ROPE_SKIPPING, "��", "����");
			readCommon(itemService, Constant.RUN50, "ms", "50����");
			readCommon(itemService, Constant.SHUTTLE_RUN, "ms", "�۷���");
			readCommon(itemService, Constant.SIT_AND_REACH, "mm", "��λ��ǰ��");
			readCommon(itemService, Constant.SIT_UP, "��", "��������");
			readCommon(itemService, Constant.SWIM, "ms", "��Ӿ");
			readCommon(itemService, Constant.VITAL_CAPACITY, "ml", "�λ���");
			readCommon(itemService, Constant.VOLLEYBALL, "ms", "����");
			readCommon(itemService, Constant.WALKING1500, "ms", "1500�׽�����");
			readCommon(itemService, Constant.WALKING2000, "ms", "2000�׽�����");

			updateView();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void readCommon(NFCItemServiceImpl itemService, int code, String unit, String name) {
		IC_ItemResult itemResult;
		ICInfo icInfo = new ICInfo();
		try {
			itemResult = itemService.IC_ReadItemResult(code);
			Log.d(name + "��", itemResult.toString());
			if (itemResult.getResult()[0].getResultFlag() != 1) {
				Log.i(code + "", name + "û�гɼ�");
			} else {
				icInfo.setProjectTitle(name);
				icInfo.setProjectValue(itemResult.getResult()[0].getResultVal() + unit);
				icInfos.add(icInfo);
			}

		} catch (Exception e) {
			log.debug("��IC����û��" + name + "��Ŀ");
		}

	}

	private void updateView() {
		// ��ȡ����
		mHandler.post(updateTv2);
		mAdapter = new ICInfoAdapter(this, icInfos);
		Log.i("icInfos", icInfos + "");
		lvIcInfo.setAdapter(mAdapter);
		// tvShow.setText("��ȡ���!");
	}

	/**
	 * ��ȡ����
	 * 
	 * @param itemService
	 * @throws Exception
	 */
	private void readVision(NFCItemServiceImpl itemService) {
		// ��ȡ����
		IC_ItemResult itemResultVision;
		ICInfo icInfo = new ICInfo();
		try {
			itemResultVision = itemService.IC_ReadItemResult(Constant.VISION);
			if (itemResultVision.getResult()[0].getResultFlag() != 1) {
				Log.i("", "����û������");
			} else {
				double left = itemResultVision.getResult()[0].getResultVal();
				double right = itemResultVision.getResult()[2].getResultVal();
				icInfo.setProjectTitle("��������");
				icInfo.setProjectValue(left + "");
				icInfo1.setProjectTitle("��������");
				icInfo1.setProjectValue(right + "");
				icInfos.add(icInfo);
				icInfos.add(icInfo1);
			}
		} catch (Exception e) {
			log.debug("��IC����û��������Ŀ");
			e.printStackTrace();
		}

	}

	/**
	 * ��IC����ȡ�г��ܳɼ�
	 * 
	 * @param itemService
	 * @throws Exception
	 */
	private void readMiddleRun(IItemService itemService) {
		IC_ItemResult itemResultMiddleRace;
		ICInfo icInfo = new ICInfo();
		try {
			itemResultMiddleRace = itemService.IC_ReadItemResult(Constant.MIDDLE_RACE);
			Log.i("��ȡ�г��ܲ���", itemResultMiddleRace.toString());
			if (sex.equals("Ů")) {
				if (itemResultMiddleRace.getResult()[0].getResultFlag() != 1) {
					Log.i("", "800����û�гɼ�");
				} else {
					icInfo.setProjectTitle("800����");
					icInfo.setProjectValue(itemResultMiddleRace.getResult()[0].getResultVal() + " ms");
					icInfos.add(icInfo);
				}
			} else {
				if (itemResultMiddleRace.getResult()[0].getResultFlag() != 1) {
					Log.i("", "1000����û�гɼ�");
				} else {
					icInfo.setProjectTitle("1000����");
					icInfo.setProjectValue(itemResultMiddleRace.getResult()[0].getResultVal() + " ms");
					icInfos.add(icInfo);
				}
			}
		} catch (Exception e) {
			log.debug("��IC����û���г�����Ŀ");
			e.printStackTrace();
		}

	}

	/**
	 * ��IC����ȡ�������
	 * 
	 * @param itemService
	 */
	private void readHW(IItemService itemService) {
		// ��ȡ�������
		
		IC_ItemResult itemResultHW;
		ICInfo icInfo = new ICInfo();
		try {
			itemResultHW = itemService.IC_ReadItemResult(Constant.HEIGHT_WEIGHT);
			Log.i("��ȡ������ز���", itemResultHW.toString());
			if (itemResultHW.getResult()[0].getResultFlag() != 1) {
				Log.i("", "�������û������");
			} else {
				double height = itemResultHW.getResult()[0].getResultVal();
				double weight = itemResultHW.getResult()[2].getResultVal();
				icInfo.setProjectTitle("���");
				icInfo.setProjectValue(height / 10 + " cm");
				icInfo1.setProjectTitle("����");
				icInfo1.setProjectValue(weight / 1000 + " kg");
				icInfos.add(icInfo);
				icInfos.add(icInfo1);
			}
		} catch (Exception e) {
			log.debug("��IC����û�����������Ŀ");
			e.printStackTrace();
		}

	}
	
	private void initView() {
		tvShow = (TextView) findViewById(R.id.tv_icinfo_show);
		lvIcInfo = (ListView) findViewById(R.id.lv_icinfo);
		icInfos = new ArrayList<ICInfo>();

		tvNumber = (TextView) findViewById(R.id.tv_icinfo_number_show);
		tvName = (TextView) findViewById(R.id.tv_icinfo_name_show);
		tvGender = (TextView) findViewById(R.id.tv_icinfo_gender_show);
		ibQuit = (ImageButton) findViewById(R.id.ib_quit);

	}

	private void setListener() {

		ibQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
