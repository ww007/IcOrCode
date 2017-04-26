package com.fpl.myapp.activity.information;

import java.util.ArrayList;
import java.util.List;
import com.fpl.myapp2.R;
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
	private ICInfo icInfo;
	private String sex;
	private TextView tvNumber;
	private TextView tvName;
	private TextView tvGender;
	private ListView lvIcInfo;
	private TextView tvShow;
	private SharedPreferences sharedPreferences;
	public ArrayList<String> projects = new ArrayList<>();
	private String[] newProject;
	private String[] newValue;
	private int number50, numberH, numberW, numberFHL, numberLDTY, numberYWQZ, number800, number1000, numberFWC,
			numberZWTQQ, numberTS, numberLSL, numberRSL, numberYTXS, numberMG, numberPQ, numberHWSXQ, numberLQYQ,
			numberZFP, numberTJZ, numberZQYQ, numberYY;
	private List<Item> items;
	private ImageButton ibQuit;

	Handler mHandler = new Handler();
	Runnable updateTv = new Runnable() {
		@Override
		public void run() {
			tvShow.setText("��ȡ��...");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_icinformation);
		// ��ȡ���ش洢��ѡ����Ŀ
		sharedPreferences = getSharedPreferences("projects", Activity.MODE_PRIVATE);
		int selected = sharedPreferences.getInt("size", 0);
		Log.i("selected=", selected + "");

		items = DbService.getInstance(this).loadAllItem();

		if (items.isEmpty()) {
			projects.add("���");
			projects.add("����");
			projects.add("�λ���");
			projects.add("50����");
			projects.add("������Զ");
			projects.add("��������");
			projects.add("��λ��ǰ��");
			projects.add("��������");
			projects.add("800����");
			projects.add("1000����");
		} else {
			for (Item item : items) {
				if (item.getMachineCode().equals("9")) {
					projects.add("��������");
					projects.add("��������");
				} else {
					projects.add(item.getItemName());
				}
			}
		}

		Log.i("projects=", projects.toString());

		initView();
		setListener();
	}

	@Override
	public void onNewIntent(final Intent intent) {
		tvShow.setText("��ȡ�жϣ�������ˢ����");
		readCard(intent);

	}

	/**
	 * ����
	 * 
	 * @param intent
	 */
	private void readCard(final Intent intent) {
		try {
			NFCItemServiceImpl itemService = new NFCItemServiceImpl(intent);
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

			if (items.isEmpty()) {
				// ��ȡ�������
				IC_ItemResult itemResultHW = itemService.IC_ReadItemResult(Constant.HEIGHT_WEIGHT);
				Log.i("��ȡ������ز���", itemResultHW.toString());
				if (itemResultHW.getResult()[0].getResultFlag() != 1) {
					newValue[0] = "��δ�⣩";
					newValue[1] = "��δ�⣩";
				} else {
					double height = itemResultHW.getResult()[0].getResultVal();
					double weight = itemResultHW.getResult()[2].getResultVal();
					newValue[0] = height / 10 + " cm";
					newValue[1] = weight / 1000 + " kg";
				}
				readOne(itemService, 2, Constant.VITAL_CAPACITY, " ml");
				readOne(itemService, 3, Constant.RUN50, " ms");
				readOne(itemService, 4, Constant.BROAD_JUMP, " cm");
				readOne(itemService, 5, Constant.SIT_UP, " ��");
				readOne(itemService, 6, Constant.SIT_AND_REACH, " mm");
				if (sex.equals("Ů")) {
					newValue[7] = "���ޣ�";
				} else {
					readOne(itemService, 7, Constant.PULL_UP, " ��");
				}

				IC_ItemResult itemResultMiddleRace = itemService.IC_ReadItemResult(Constant.MIDDLE_RACE);
				Log.i("��ȡ�г��ܲ���", itemResultMiddleRace.toString());
				if (sex.equals("Ů")) {
					newValue[9] = "���ޣ�";
					if (itemResultMiddleRace.getResult()[0].getResultFlag() != 1) {
						newValue[8] = "��δ�⣩";
					} else {
						newValue[8] = itemResultMiddleRace.getResult()[0].getResultVal() + " ms";
					}
				} else {
					newValue[8] = "���ޣ�";
					if (itemResultMiddleRace.getResult()[0].getResultFlag() != 1) {
						newValue[9] = "��δ�⣩";
					} else {
						newValue[9] = itemResultMiddleRace.getResult()[0].getResultVal() + " ms";
					}
				}
			} else {

				for (int i = 0; i < items.size(); i++) {
					switch (items.get(i).getMachineCode()) {
					case "" + Constant.RUN50:
						number50 = i;
						// ��ȡ50��
						readOne(itemService, number50, Constant.RUN50, " ms");
						break;
					case "" + Constant.HEIGHT_WEIGHT:
						if (items.get(i).getItemName().equals("���")) {
							numberH = i;
						} else {
							numberW = i;
						}
						break;
					case "" + Constant.VITAL_CAPACITY:
						numberFHL = i;
						// ��ȡ�λ���
						readOne(itemService, numberFHL, Constant.VITAL_CAPACITY, " ml");
						break;
					case "" + Constant.BROAD_JUMP:
						numberLDTY = i;
						// ��ȡ������Զ
						readOne(itemService, numberLDTY, Constant.BROAD_JUMP, " cm");
						break;
					case "" + Constant.SIT_UP:
						numberYWQZ = i;
						// ��ȡ��������
						if (sex.equals("��")) {
							newValue[numberYWQZ] = "���ޣ�";
						} else {
							readOne(itemService, numberYWQZ, Constant.SIT_UP, " ��");
						}
						break;
					case "" + Constant.MIDDLE_RACE:
						if (items.get(i).getItemName().equals("800����")) {
							number800 = i;
						} else {
							number1000 = i;
						}
						// ��ȡ�г���
						readMiddleRun(itemService);
						break;
					case "" + Constant.PUSH_UP:
						numberFWC = i;
						// ��ȡ���Գ�
						readOne(itemService, numberFWC, Constant.PUSH_UP, " ��");
						break;
					case "" + Constant.SIT_AND_REACH:
						numberZWTQQ = i;
						// ��ȡ��λ��ǰ��
						readOne(itemService, numberZWTQQ, Constant.SIT_AND_REACH, " cm");
						break;
					case "" + Constant.ROPE_SKIPPING:
						numberTS = i;
						// ��ȡ����
						readOne(itemService, numberTS, Constant.ROPE_SKIPPING, " ��");
						break;
					case "" + Constant.VISION:
						if (items.get(i).getItemName().equals("��������")) {
							numberLSL = i;
						} else {
							numberRSL = i;
						}
						readVision(itemService);
						break;
					case "" + Constant.PULL_UP:
						numberYTXS = i;
						// ��ȡ��������
						if (sex.equals("Ů")) {
							newValue[numberYTXS] = "���ޣ�";
						} else {
							readOne(itemService, numberYTXS, Constant.PULL_UP, " ��");
						}
						break;
					case "" + Constant.JUMP_HEIGHT:
						numberMG = i;
						// ��ȡ����
						readOne(itemService, numberMG, Constant.JUMP_HEIGHT, " cm");
						break;
					case "" + Constant.VOLLEYBALL:
						numberPQ = i;
						// ��ȡ����
						readOne(itemService, numberPQ, Constant.VOLLEYBALL, " ��");
						break;
					case "" + Constant.INFRARED_BALL:
						numberHWSXQ = i;
						// ��ȡ����ʵ����
						readOne(itemService, numberHWSXQ, Constant.INFRARED_BALL, " cm");
						break;
					case "" + Constant.BASKETBALL_SKILL:
						numberLQYQ = i;
						// ��ȡ��������
						readOne(itemService, numberLQYQ, Constant.BASKETBALL_SKILL, " ms");
						break;
					case "" + Constant.SHUTTLE_RUN:
						numberZFP = i;
						// ��ȡ�۷���
						readOne(itemService, numberZFP, Constant.SHUTTLE_RUN, " ms");
						break;
					case "" + Constant.KICKING_SHUTTLECOCK:
						numberTJZ = i;
						// ��ȡ�����
						readOne(itemService, numberTJZ, Constant.KICKING_SHUTTLECOCK, " ms");
						break;
					case "" + Constant.FOOTBALL_SKILL:
						numberZQYQ = i;
						// ��ȡ��������
						readOne(itemService, numberZQYQ, Constant.FOOTBALL_SKILL, " ms");
						break;
					case "" + Constant.SWIM:
						numberYY = i;
						// ��ȡ��Ӿ
						readOne(itemService, numberYY, Constant.SWIM, " ms");
						break;
					default:
						break;
					}

				}
				readHW(itemService);
			}

			icInfos.clear();
			updateView();

			mAdapter.notifyDataSetChanged();
			lvIcInfo.invalidate();
			tvShow.setText("��ȡ���!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ����
	 * 
	 * @param itemService
	 * @throws Exception
	 */
	private void readVision(NFCItemServiceImpl itemService) throws Exception {
		// ��ȡ����
		IC_ItemResult itemResultVision = itemService.IC_ReadItemResult(Constant.VISION);
		Log.i("��ȡ��������", itemResultVision.toString());
		if (itemResultVision.getResult()[0].getResultFlag() != 1) {
			newValue[numberLSL] = "��δ�⣩";
			newValue[numberRSL] = "��δ�⣩";
		} else {
			double left = itemResultVision.getResult()[0].getResultVal();
			double right = itemResultVision.getResult()[2].getResultVal();
			newValue[numberLSL] = left + "";
			newValue[numberRSL] = right + "";
		}
	}

	/**
	 * ��IC����ȡ�г��ܳɼ�
	 * 
	 * @param itemService
	 * @throws Exception
	 */
	private void readMiddleRun(IItemService itemService) throws Exception {
		IC_ItemResult itemResultMiddleRace = itemService.IC_ReadItemResult(Constant.MIDDLE_RACE);
		Log.i("��ȡ�г��ܲ���", itemResultMiddleRace.toString());
		if (sex.equals("Ů")) {
			newValue[number1000] = "���ޣ�";
			if (itemResultMiddleRace.getResult()[0].getResultFlag() != 1) {
				newValue[number800] = "��δ�⣩";
			} else {
				newValue[number800] = itemResultMiddleRace.getResult()[0].getResultVal() + " ms";
			}
		} else {
			newValue[number800] = "���ޣ�";
			if (itemResultMiddleRace.getResult()[0].getResultFlag() != 1) {
				newValue[number1000] = "��δ�⣩";
			} else {
				newValue[number1000] = itemResultMiddleRace.getResult()[0].getResultVal() + " ms";
			}
		}
	}

	/**
	 * ��IC����ȡ�������
	 * 
	 * @param itemService
	 * @throws Exception
	 */
	private void readHW(IItemService itemService) throws Exception {
		// ��ȡ�������
		IC_ItemResult itemResultHW = itemService.IC_ReadItemResult(Constant.HEIGHT_WEIGHT);
		Log.i("��ȡ������ز���", itemResultHW.toString());
		if (itemResultHW.getResult()[0].getResultFlag() != 1) {
			newValue[numberH] = "��δ�⣩";
			newValue[numberW] = "��δ�⣩";
		} else {
			double height = itemResultHW.getResult()[0].getResultVal();
			double weight = itemResultHW.getResult()[2].getResultVal();
			newValue[numberH] = height / 10 + " cm";
			newValue[numberW] = weight / 1000 + " kg";
		}
	}

	/**
	 * ��ȡֻ��һ�γɼ�����Ŀ
	 * 
	 * @param itemService
	 * @param number
	 *            ���
	 * @param code
	 *            ��Ŀ��������
	 * @param unit
	 *            ��λ
	 * @throws Exception
	 */
	private void readOne(IItemService itemService, int number, int code, String unit) throws Exception {
		IC_ItemResult itemResult = itemService.IC_ReadItemResult(code);
		Log.d(code + "һ�γɼ���", itemResult.toString());
		if (itemResult.getResult()[0].getResultFlag() != 1) {
			newValue[number] = "��δ�⣩";
		} else {
			newValue[number] = itemResult.getResult()[0].getResultVal() + unit;
		}
	}

	private void initView() {
		newProject = new String[projects.size()];
		newValue = new String[projects.size()];

		for (int i = 0; i < projects.size(); i++) {
			newProject[i] = projects.get(i).toString();
		}

		tvShow = (TextView) findViewById(R.id.tv_icinfo_show);
		lvIcInfo = (ListView) findViewById(R.id.lv_icinfo);
		icInfos = new ArrayList<ICInfo>();

		updateView();

		tvNumber = (TextView) findViewById(R.id.tv_icinfo_number_show);
		tvName = (TextView) findViewById(R.id.tv_icinfo_name_show);
		tvGender = (TextView) findViewById(R.id.tv_icinfo_gender_show);
		ibQuit = (ImageButton) findViewById(R.id.ib_quit);

	}

	private void updateView() {
		// ��ȡ����
		getData();
		mAdapter = new ICInfoAdapter(this, icInfos);
		Log.i("icInfos", icInfos + "");
		lvIcInfo.setAdapter(mAdapter);
	}

	public void getData() {

		for (int i = 0; i < newProject.length; i++) {
			icInfo = new ICInfo();
			icInfo.setProjectTitle(newProject[i]);
			icInfo.setProjectValue(newValue[i]);
			icInfos.add(icInfo);
		}
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
