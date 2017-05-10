package com.fpl.myapp.activity.project;

import java.util.List;

import org.apache.log4j.Logger;

import com.fpl.myapp2.R;
import com.fpl.myapp.activity.CaptureActivity;
import com.fpl.myapp.base.NFCActivity;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.db.SaveDBUtil;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp.util.NetUtil;
import com.wnb.android.nfc.dataobject.entity.IC_ItemResult;
import com.wnb.android.nfc.dataobject.entity.IC_Result;
import com.wnb.android.nfc.dataobject.entity.Student;
import com.wnb.android.nfc.dataobject.service.IItemService;
import com.wnb.android.nfc.dataobject.service.impl.NFCItemServiceImpl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import ww.greendao.dao.Item;
import ww.greendao.dao.StudentItem;

public class RunGradeInputActivity extends NFCActivity {
	private TextView tvInfoTitle;
	private TextView tvName;
	private TextView tvGender;
	private TextView tvNumber;
	private TextView tvShow1;
	private TextView tvShow;
	private EditText etMax;
	private EditText etMin;
	private TextView tvInfoChengji;
	private TextView tvInfoUnit;
	private Button btnSave;
	private Button btnCancel;

	private String sex;
	private String number;
	private String name;
	private String title = "";
	private Student student;
	private Context context;

	private Logger log = Logger.getLogger(RunGradeInputActivity.class);
	private RadioGroup rg;
	private RadioButton rb0;
	private RadioButton rb1;
	private RadioButton rb2;
	private RadioButton rb3;
	private String max;
	private String min;
	private Item items;
	private EditText etMs;
	private EditText etS;
	private EditText etSec;
	private TextView tvMs;
	private TextView tvS;
	private TextView tvSec;
	private LinearLayout llRunChengji;
	private LinearLayout llInfoChengji;
	private String stuData;
	private List<ww.greendao.dao.Student> stuByCode;
	private SharedPreferences mSharedPreferences;
	private int readStyle;
	private Button btnScan;
	private String title2 = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_information);
		context = this;

		// ��������
		Intent intent = getIntent();
		number = intent.getStringExtra("number");
		name = intent.getStringExtra("name");
		sex = intent.getStringExtra("sex");
		title = intent.getStringExtra("title");
		title2 = intent.getStringExtra("title2");
		if ("".equals(title)) {
			title = title2;
		}
		mSharedPreferences = getSharedPreferences("readStyles", Activity.MODE_PRIVATE);
		readStyle = mSharedPreferences.getInt("readStyle", 0);

		stuData = getIntent().getStringExtra("data");
		if (stuData != null && stuData.length() != 0) {
			stuByCode = DbService.getInstance(context).queryStudentByCode(stuData);
			if (stuByCode.isEmpty()) {
				Toast.makeText(context, "���޴���", Toast.LENGTH_SHORT).show();
				stuData = "";
			}
		}

		if (title.equals("800/1000����")) {
			items = DbService.getInstance(context).queryItemByMachineCode("12");
		} else if (title.equals("50����")) {
			items = DbService.getInstance(context).queryItemByMachineCode("18");
		} else if (title.equals("50��x8������")) {
			items = DbService.getInstance(context).queryItemByMachineCode("15");
		}

		if (items == null) {
			max = "";
			min = "";
		} else {
			max = items.getMaxValue().toString();
			min = items.getMinValue().toString();
		}

		initView();
		setListener();

	}

	@Override
	public void onNewIntent(Intent intent) {
		if (readStyle == 0) {
			if (View.VISIBLE == tvShow1.getVisibility() && "�ɼ�����ɹ�".equals(tvShow1.getText().toString())) {
				writeCard(intent);
			} else {
				readCard(intent);
			}
		} else {
			NetUtil.showToast(context, "��ǰѡ���IC��״̬��������");
		}
	}

	private int getChengJi() {
		int sec = 0;
		int s = 0;
		int ms = 0;
		if ("".equals(etSec.getText().toString())) {
			sec = 0;
		} else {
			sec = Integer.parseInt(etSec.getText().toString());
		}
		if ("".equals(etS.getText().toString())) {
			s = 0;
		} else {
			s = Integer.parseInt(etS.getText().toString());
		}
		if ("".equals(etMs.getText().toString())) {
			ms = 0;
		} else {
			ms = Integer.parseInt(etMs.getText().toString());
		}

		int etChengji = sec * 60 * 1000 + s * 1000 + ms;
		return etChengji;
	}

	/**
	 * д��
	 * 
	 * @param intent
	 */
	private void writeCard(Intent intent) {
		try {

			int constant = 0;
			if (title.equals("50����")) {
				constant = Constant.RUN50;
			} else if (title.equals("800/1000����")) {
				constant = Constant.MIDDLE_RACE;
			} else if (title.equals("50��x8������")) {
				constant = Constant.SHUTTLE_RUN;
			}
			IItemService itemService = new NFCItemServiceImpl(intent);

			IC_Result[] resultRun = new IC_Result[4];
			String chengji = "";
			if ("".equals(etMs.getText().toString()) && "".equals(etS.getText().toString())
					&& "".equals(etSec.getText().toString())) {
				chengji = "0";
			} else {
				chengji = getChengJi() + "";
			}
			int result1 = Integer.parseInt(chengji);
			resultRun[0] = new IC_Result(result1, 1, 0, 0);
			IC_ItemResult ItemResultRun = new IC_ItemResult(constant, 0, 0, resultRun);
			boolean isRunResult = itemService.IC_WriteItemResult(ItemResultRun);
			log.info("д���ܲ��ɼ�=>" + isRunResult + "�ɼ���" + result1 + "��ѧ����" + tvNumber.getText().toString());
			if (isRunResult) {
				tvShow1.setText("�ɼ�д�����");
				tvShow.setText("��ˢ��");
			} else {
				Toast.makeText(this, "д������", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			log.error(title + "д��ʧ��");
			e.printStackTrace();
		}

	}

	/**
	 * ����
	 */
	private void readCard(Intent intent) {
		try {
			IItemService itemService = new NFCItemServiceImpl(intent);
			student = itemService.IC_ReadStuInfo();
			log.info(title + "����=>" + student.toString());

			if (1 == student.getSex()) {
				sex = "��";
			} else {
				sex = "Ů";
			}
			tvGender.setText(sex);
			tvName.setText(student.getStuName().toString());
			tvNumber.setText(student.getStuCode().toString());
			initOne();
		} catch (Exception e) {
			log.error(title + "����ʧ��");
			e.printStackTrace();
		}

	}

	private void initOne() {
		etMs.setText("");
		etS.setText("");
		etSec.setText("");
		tvShow1.setVisibility(View.GONE);
		btnCancel.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		etMs.setEnabled(true);
		etS.setEnabled(true);
		etSec.setEnabled(true);
		rb0.setChecked(true);

		tvShow.setText("������ɼ�");
		tvShow.setVisibility(View.VISIBLE);
	}

	protected void initView() {
		btnScan = (Button) findViewById(R.id.btn_scanCode);
		tvInfoTitle = (TextView) findViewById(R.id.tv_info_title);
		tvInfoChengji = (TextView) findViewById(R.id.tv_info_chengji);
		tvInfoUnit = (TextView) findViewById(R.id.tv_info_unit);
		tvName = (TextView) findViewById(R.id.tv_name_edit);
		tvGender = (TextView) findViewById(R.id.tv_gender_edit);
		tvNumber = (TextView) findViewById(R.id.tv_number_edit);
		tvShow1 = (TextView) findViewById(R.id.tv_infor_show1);
		tvShow = (TextView) findViewById(R.id.tv_infor_show);
		tvMs = (TextView) findViewById(R.id.tv_unit_ms);
		tvS = (TextView) findViewById(R.id.tv_unit_s);
		tvSec = (TextView) findViewById(R.id.tv_unit_sec);
		etMs = (EditText) findViewById(R.id.et_run_ms);
		etS = (EditText) findViewById(R.id.et_run_s);
		etSec = (EditText) findViewById(R.id.et_run_sec);
		etS.setInputType(InputType.TYPE_NULL);
		etMs.setInputType(InputType.TYPE_NULL);
		etSec.setInputType(InputType.TYPE_NULL);
		llRunChengji = (LinearLayout) findViewById(R.id.ll_run_chengji);
		llRunChengji.setVisibility(View.VISIBLE);
		llInfoChengji = (LinearLayout) findViewById(R.id.ll_info_chengji);
		llInfoChengji.setVisibility(View.GONE);
		etMax = (EditText) findViewById(R.id.et_info_max);
		etMin = (EditText) findViewById(R.id.et_info_min);
		btnSave = (Button) findViewById(R.id.btn_info_save);
		btnCancel = (Button) findViewById(R.id.btn_info_cancel);
		rg = (RadioGroup) findViewById(R.id.radioGroup);
		rb0 = (RadioButton) findViewById(R.id.radio0);
		rb1 = (RadioButton) findViewById(R.id.radio1);
		rb2 = (RadioButton) findViewById(R.id.radio2);
		rb3 = (RadioButton) findViewById(R.id.radio3);
		rb2.setText("����");
		rb3.setText("��Ȩ");

		tvInfoTitle.setText(title);
		tvInfoChengji.setText("�ɼ�");
		tvInfoUnit.setVisibility(View.VISIBLE);
		tvInfoUnit.setText("����");
		tvName.setText(name);
		tvGender.setText(sex);
		etMax.setText(max);
		etMin.setText(min);
		etS.setText("");
		etMs.setText("");
		etSec.setText("");
		btnCancel.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		tvShow.setText("������ɼ�");
		tvShow1.setVisibility(View.GONE);

		if (readStyle == 1) {
			tvNumber.setText(stuData);
			tvShow.setVisibility(View.GONE);
			if (stuByCode.get(0).getSex() == 1) {
				sex = "��";
			} else {
				sex = "Ů";
			}
			tvName.setText(stuByCode.get(0).getStudentName());
			tvGender.setText(sex);
			btnScan.setVisibility(View.GONE);
			tvShow.setText("������ɼ�");
			tvShow.setVisibility(View.VISIBLE);
			initOne();
		} else {
			tvNumber.setText(number);
			stuData = "";
		}
	}

	private int flag = 0;
	private String checkedBtn = "����";
	private int resultState;
	private String chengji;
	private StudentItem studentItems;
	private Long stuId;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 66 || keyCode == 135 || keyCode == 136) {
			Intent intent = new Intent(RunGradeInputActivity.this, CaptureActivity.class);
			intent.putExtra("className", Constant.runGradeInput);
			intent.putExtra("title2", title);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setListener() {
		btnScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RunGradeInputActivity.this, CaptureActivity.class);
				intent.putExtra("className", Constant.runGradeInput);
				intent.putExtra("title2", title);
				startActivity(intent);
				finish();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etS.setText("");
				etMs.setText("");
				etSec.setText("");
				btnCancel.setVisibility(View.GONE);
				btnSave.setVisibility(View.GONE);
				tvShow.setText("������ɼ�");
				tvShow.setVisibility(View.VISIBLE);
			}
		});

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				btnCancel.setVisibility(View.VISIBLE);
				btnSave.setVisibility(View.VISIBLE);
				RadioButton radioButton = (RadioButton) RunGradeInputActivity.this
						.findViewById(group.getCheckedRadioButtonId());
				checkedBtn = radioButton.getText().toString();
				if (checkedBtn.equals("����")) {
					tvMs.setVisibility(View.VISIBLE);
					tvS.setVisibility(View.VISIBLE);
					tvSec.setVisibility(View.VISIBLE);
					etMs.setVisibility(View.VISIBLE);
					etS.setVisibility(View.VISIBLE);
					etS.setText("");
					etMs.setText("");
					etSec.setText("");
					etSec.setTextSize(18);
					etS.setTextSize(18);
					etMs.setTextSize(18);
					etS.setTextColor(getResources().getColor(android.R.color.black));
					etMs.setTextColor(getResources().getColor(android.R.color.black));
					etSec.setTextColor(getResources().getColor(android.R.color.black));
					if (tvNumber.getText().toString().isEmpty()) {
						etS.setEnabled(false);
						etMs.setEnabled(false);
						etSec.setEnabled(false);
					} else {
						etSec.setEnabled(true);
						etMs.setEnabled(true);
						etS.setEnabled(true);
					}
				} else if (checkedBtn.equals("����")) {
					tvMs.setVisibility(View.GONE);
					tvS.setVisibility(View.GONE);
					tvSec.setVisibility(View.GONE);
					etMs.setVisibility(View.GONE);
					etS.setVisibility(View.GONE);
					etSec.setText("DQ");
					etSec.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
					etSec.setEnabled(false);
					etSec.setTextSize(23);
					chengji = "0";
				} else if (checkedBtn.equals("����")) {
					tvMs.setVisibility(View.GONE);
					tvS.setVisibility(View.GONE);
					tvSec.setVisibility(View.GONE);
					etMs.setVisibility(View.GONE);
					etS.setVisibility(View.GONE);
					etSec.setText("DNF");
					etSec.setTextColor(getResources().getColor(android.R.color.darker_gray));
					etSec.setEnabled(false);
					etSec.setTextSize(23);
					chengji = "0";
				} else if (checkedBtn.equals("��Ȩ")) {
					tvMs.setVisibility(View.GONE);
					tvS.setVisibility(View.GONE);
					tvSec.setVisibility(View.GONE);
					etMs.setVisibility(View.GONE);
					etS.setVisibility(View.GONE);
					etSec.setText("DNS");
					etSec.setTextColor(getResources().getColor(android.R.color.darker_gray));
					etSec.setEnabled(false);
					etSec.setTextSize(23);
					chengji = "0";
				}
			}
		});

		etMs.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (tvNumber.getText().toString().isEmpty()) {
					tvShow.setVisibility(View.VISIBLE);
					btnCancel.setVisibility(View.GONE);
					btnSave.setVisibility(View.GONE);
				} else {
					tvShow.setVisibility(View.GONE);
					btnCancel.setVisibility(View.VISIBLE);
					btnSave.setVisibility(View.VISIBLE);
				}
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (DbService.getInstance(context).loadAllItem().isEmpty()) {
					Toast.makeText(context, "���Ȼ�ȡ��Ŀ�������", Toast.LENGTH_SHORT).show();
					return;
				}
				if (getChengJi() == 0 && checkedBtn.equals("����")) {
					Toast.makeText(RunGradeInputActivity.this, "�ɼ�Ϊ��", Toast.LENGTH_SHORT).show();
					return;
				} else if (checkedBtn.equals("����")) {
					resultState = -1;
				} else if (checkedBtn.equals("����")) {
					resultState = -2;
				} else if (checkedBtn.equals("��Ȩ")) {
					resultState = -3;
				} else if (checkedBtn.equals("����")) {
					if (getChengJi() > Integer.parseInt(max) || getChengJi() < Integer.parseInt(min)) {
						Toast.makeText(context, "�������뷶Χ������������", Toast.LENGTH_SHORT).show();
						etS.setText("");
						etMs.setText("");
						etSec.setText("");
						return;
					}
					chengji = getChengJi() + "";
					// stuId =
					// DbService.getInstance(context).queryStudentByCode(tvNumber.getText().toString()).get(0)
					// .getStudentID();
				}
				if (title.equals("50����")) {
					// ��ѯ���ݿ��б���ĸ�ѧ����Ŀ�ɼ����ִ�
					String itemCode = DbService.getInstance(context).queryItemByMachineCode(Constant.RUN50 + "")
							.getItemCode();
					// long stuID =
					// DbService.getInstance(context).queryStudentByCode(tvNumber.getText().toString()).get(0)
					// .getStudentID();
					// long itemID =
					// DbService.getInstance(context).queryItemByCode(itemCode).getItemID();
					studentItems = DbService.getInstance(context).queryStudentItemByCode(tvNumber.getText().toString(),
							itemCode);
					if (studentItems == null) {
						Toast.makeText(context, "��ǰѧ����Ŀ������", Toast.LENGTH_SHORT).show();
						return;
					} else {
						resultState = 0;
					}
					flag = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(), chengji, resultState,
							Constant.RUN50 + "", "50����");
					log.info("����50����" + tvNumber.getText().toString() + "�ɼ���" + chengji);
				} else if (title.equals("800/1000����")) {
					String itemName;
					String itemCode;
					if (sex.equals("��")) {
						itemName = "1000����";
					} else {
						itemName = "800����";
					}
					itemCode = DbService.getInstance(context).queryItemByName(itemName).getItemCode();
					// ��ѯ���ݿ��б���ĸ�ѧ����Ŀ�ɼ����ִ�
					studentItems = DbService.getInstance(context).queryStudentItemByCode(tvNumber.getText().toString(),
							itemCode);
					if (studentItems == null) {
						Toast.makeText(context, "��ǰѧ����Ŀ������", Toast.LENGTH_SHORT).show();
						return;
					} else {
						resultState = 0;
					}
					flag = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(), chengji, resultState,
							Constant.MIDDLE_RACE + "", itemName);
					log.info("����800/1000����" + tvNumber.getText().toString() + "�ɼ���" + chengji);
				} else if (title.equals("50��x8������")) {
					// ��ѯ���ݿ��б���ĸ�ѧ����Ŀ�ɼ����ִ�
					String itemCode = DbService.getInstance(context).queryItemByMachineCode(Constant.SHUTTLE_RUN + "")
							.getItemCode();
					// long stuID =
					// DbService.getInstance(context).queryStudentByCode(tvNumber.getText().toString()).get(0)
					// .getStudentID();
					// long itemID =
					// DbService.getInstance(context).queryItemByCode(itemCode).getItemID();
					studentItems = DbService.getInstance(context).queryStudentItemByCode(tvNumber.getText().toString(),
							itemCode);
					if (studentItems == null) {
						Toast.makeText(context, "��ǰѧ����Ŀ������", Toast.LENGTH_SHORT).show();
						return;
					} else {
						resultState = 0;
					}
					flag = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(), chengji, resultState,
							Constant.SHUTTLE_RUN + "", "50��x8������");
					log.info("����50��x8������" + tvNumber.getText().toString() + "�ɼ���" + chengji);
				}
				btnCancel.setVisibility(View.GONE);
				btnSave.setVisibility(View.GONE);
				tvShow1.setVisibility(View.VISIBLE);
				if (readStyle == 1) {
					tvShow.setVisibility(View.GONE);
				} else {
					tvShow.setVisibility(View.VISIBLE);
				}

				if (flag == 1) {
					if (stuData.isEmpty()) {
						tvShow1.setText("�ɼ�����ɹ�");
						tvShow.setVisibility(View.VISIBLE);
						tvShow.setText("��ˢ��");
					} else {
						tvShow.setVisibility(View.GONE);
						tvShow1.setVisibility(View.GONE);
						etMs.setText("");
						etSec.setText("");
						etS.setText("");
						Toast.makeText(context, "�ɼ�����ɹ�", Toast.LENGTH_SHORT).show();
						btnSave.setVisibility(View.GONE);
						btnCancel.setVisibility(View.GONE);
						btnScan.setVisibility(View.VISIBLE);
					}
				} else {
					if (stuData.isEmpty()) {
						tvShow1.setText("�ɼ�����ʧ��");
						tvShow.setVisibility(View.VISIBLE);
						tvShow.setText("��ˢ��");
					} else {
						tvShow1.setVisibility(View.GONE);
						Toast.makeText(context, "�ɼ�����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
						btnSave.setVisibility(View.GONE);
						btnCancel.setVisibility(View.GONE);
						tvShow.setVisibility(View.GONE);
						btnScan.setVisibility(View.VISIBLE);
					}
				}
			}
		});

	}
}
