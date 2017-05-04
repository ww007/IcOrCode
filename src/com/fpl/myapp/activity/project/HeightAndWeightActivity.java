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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import ww.greendao.dao.Item;
import ww.greendao.dao.StudentItem;

public class HeightAndWeightActivity extends NFCActivity {

	private TextView tvNumber;
	private TextView tvGender;
	private TextView tvName;
	private TextView tvShow1;
	private TextView tvShow;
	private EditText etHeight;
	private EditText etWeight;
	private Button btnSave;
	private Button btnCancel;
	private ImageButton ibQuit;
	private Student student;
	private String sex;
	private TextView tvHUnit;
	private TextView tvWUnit;
	private TextView tvLeft;
	private TextView tvRight;
	private TextView tvTitle;
	private Context context;
	private StudentItem studentItems;

	private Logger log = Logger.getLogger(HeightAndWeightActivity.class);
	private EditText etHMax;
	private EditText etHMin;
	private EditText etWMax;
	private EditText etWMin;
	private String hMax;
	private String hMin;
	private String wMax;
	private String wMin;
	private SharedPreferences mSharedPreferences;
	private int readStyle;
	private String stuData;
	private List<ww.greendao.dao.Student> stuByCode;
	private Button btnScan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_height_and_weight);
		context = this;

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

		List<Item> items = DbService.getInstance(context).queryItemByCode("E01");
		List<Item> items2 = DbService.getInstance(context).queryItemByCode("E02");

		if (items.isEmpty()) {
			hMax = "";
			hMin = "";
			wMax = "";
			wMin = "";
		} else {
			hMax = items.get(0).getMaxValue() / 10 + "";
			hMin = items.get(0).getMinValue() / 10 + "";
			wMax = items2.get(0).getMaxValue() / 1000 + "";
			wMin = items2.get(0).getMinValue() / 1000 + "";
		}
		initView();
		setListener();
	}

	@Override
	public void onNewIntent(Intent intent) {
		if (readStyle == 0) {
			if (View.VISIBLE == tvShow1.getVisibility() && "����ɹ�".equals(tvShow1.getText().toString())) {
				writeCard(intent);
			} else {
				readCard(intent);
			}
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
			log.info("������ض���->" + student.toString());

			if (1 == student.getSex()) {
				sex = "��";
			} else {
				sex = "Ů";
			}

			initOne();
			tvGender.setText(sex);
			tvName.setText(student.getStuName().toString());
			tvNumber.setText(student.getStuCode().toString());
		} catch (Exception e) {
			log.error("������ض���ʧ��");
			e.printStackTrace();
		}

	}

	/**
	 * д��
	 * 
	 * @param intent
	 */
	private void writeCard(Intent intent) {

		try {
			int hResult1 = (int) (Double.parseDouble(etHeight.getText().toString()) * 10);
			int wResult1 = (int) (Double.parseDouble(etWeight.getText().toString()) * 1000);
			IItemService itemService = new NFCItemServiceImpl(intent);
			IC_Result[] HWresult = new IC_Result[4];
			HWresult[0] = new IC_Result(hResult1, 1, 0, 0);// ���1
			// HWresult[1] = new IC_Result(1710, 1, 0, 0);// ���2
			HWresult[2] = new IC_Result(wResult1, 1, 0, 0);// ����1
			IC_ItemResult HWItemResult = new IC_ItemResult(Constant.HEIGHT_WEIGHT, 0, 0, HWresult);
			boolean isHWResult = itemService.IC_WriteItemResult(HWItemResult);
			log.info("д���������=>" + isHWResult + "��ߣ�" + hResult1 + "���أ�" + wResult1 + "��ѧ����" + student.toString());
			if (isHWResult) {
				tvShow1.setText("д�����");
				tvShow.setText("��ˢ��");
			} else {
				Toast.makeText(this, "д������", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			log.error("�������д��ʧ��");
		}

	}

	private void initOne() {
		etHeight.setText("");
		etWeight.setText("");
		tvShow1.setVisibility(View.GONE);
		btnCancel.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		etHeight.setEnabled(true);
		etWeight.setEnabled(true);

		tvShow.setText("������");
		tvShow.setVisibility(View.VISIBLE);
	}

	/**
	 * ��ʼ��View
	 */
	private void initView() {
		btnScan = (Button) findViewById(R.id.btn_hw_scanCode);
		tvHUnit = (TextView) findViewById(R.id.tv_h_unit);
		tvWUnit = (TextView) findViewById(R.id.tv_w_unit);
		tvLeft = (TextView) findViewById(R.id.tv_hw_height);
		tvRight = (TextView) findViewById(R.id.tv_hw_weight);
		tvTitle = (TextView) findViewById(R.id.tv_hw_title);
		tvNumber = (TextView) findViewById(R.id.tv_number_edit_HW);
		tvGender = (TextView) findViewById(R.id.tv_gender_edit_HW);
		tvName = (TextView) findViewById(R.id.tv_name_edit_HW);
		tvShow1 = (TextView) findViewById(R.id.tv_hw_show1);
		tvShow = (TextView) findViewById(R.id.tv_hw_show);
		etHeight = (EditText) findViewById(R.id.et_height_edit);
		etWeight = (EditText) findViewById(R.id.et_weight_edit);
		etHeight.setInputType(InputType.TYPE_NULL);
		etWeight.setInputType(InputType.TYPE_NULL);
		etHMax = (EditText) findViewById(R.id.et_h_max);
		etHMin = (EditText) findViewById(R.id.et_h_min);
		etWMax = (EditText) findViewById(R.id.et_w_max);
		etWMin = (EditText) findViewById(R.id.et_w_min);
		btnSave = (Button) findViewById(R.id.btn_hw_save);
		btnCancel = (Button) findViewById(R.id.btn_hw_cancel);
		ibQuit = (ImageButton) findViewById(R.id.ib_hw_quit);
		etHeight.setEnabled(false);
		etWeight.setEnabled(false);

		tvTitle.setText("�������");
		tvLeft.setText("���");
		tvRight.setText("����");
		tvHUnit.setText("����");
		tvWUnit.setText("ǧ��");
		tvGender.setText("");
		tvName.setText("");
		etHeight.setText("");
		etWeight.setText("");
		etHMax.setText(hMax);
		etHMin.setText(hMin);
		etWMax.setText(wMax);
		etWMin.setText(wMin);
		btnCancel.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		tvShow.setText("��ˢ��");
		tvShow1.setVisibility(View.GONE);
		tvNumber.setText(stuData);
		if (readStyle == 1) {
			btnScan.setVisibility(View.VISIBLE);
			tvShow.setVisibility(View.GONE);
		}
		if ("".equals(tvNumber.getText().toString())) {
			stuData = "";
		} else {
			if (stuByCode.get(0).getSex() == 1) {
				sex = "��";
			} else {
				sex = "Ů";
			}
			tvName.setText(stuByCode.get(0).getStudentName());
			tvGender.setText(sex);
			etHeight.setEnabled(true);
			etWeight.setEnabled(true);
			btnScan.setVisibility(View.GONE);
			tvShow.setText("������");
			tvShow.setVisibility(View.VISIBLE);
			initOne();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 66 || keyCode == 135 || keyCode == 136) {
			Intent intent = new Intent(HeightAndWeightActivity.this, CaptureActivity.class);
			intent.putExtra("className", Constant.HEIGHT_WEIGHT + "");
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setListener() {
		btnScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HeightAndWeightActivity.this, CaptureActivity.class);
				intent.putExtra("className", Constant.HEIGHT_WEIGHT + "");
				startActivity(intent);
				finish();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etHeight.setText("");
				etWeight.setText("");
				btnCancel.setVisibility(View.GONE);
				btnSave.setVisibility(View.GONE);
				tvShow.setText("������");
				tvShow.setVisibility(View.VISIBLE);
			}
		});

		ibQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		etWeight.addTextChangedListener(new TextWatcher() {

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
				if ("".equals(etHeight.getText().toString()) || "".equals(etWeight.getText().toString())) {
					Toast.makeText(HeightAndWeightActivity.this, "��߻�����Ϊ��", Toast.LENGTH_SHORT).show();
				} else {
					if (Double.parseDouble(etHeight.getText().toString()) > Double.parseDouble(hMax)
							|| Double.parseDouble(etHeight.getText().toString()) < Integer.parseInt(hMin)
							|| Double.parseDouble(etWeight.getText().toString()) > Double.parseDouble(wMax)
							|| Double.parseDouble(etWeight.getText().toString()) < Integer.parseInt(wMin)) {
						Toast.makeText(context, "�������뷶Χ������������", Toast.LENGTH_SHORT).show();
						etHeight.setText("");
						etWeight.setText("");
						return;
					}
					studentItems = DbService.getInstance(context).queryStudentItemByCode(tvNumber.getText().toString(),
							"E01");
					if (studentItems == null) {
						Toast.makeText(context, "��ǰѧ����Ŀ������", Toast.LENGTH_SHORT).show();
					} else {
						int hResult = (int) (Double.parseDouble(etHeight.getText().toString()) * 10);
						int wResult = (int) (Double.parseDouble(etWeight.getText().toString()) * 1000);
						// �����������
						int flag1 = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(), hResult + "", 0,
								Constant.HEIGHT_WEIGHT + "", "���");
						int flag2 = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(), wResult + "", 0,
								Constant.HEIGHT_WEIGHT + "", "����");

						btnCancel.setVisibility(View.GONE);
						btnSave.setVisibility(View.GONE);
						tvShow1.setVisibility(View.VISIBLE);
						if (readStyle == 1) {
							tvShow.setVisibility(View.GONE);
						} else {
							tvShow.setVisibility(View.VISIBLE);
						}
						if (flag1 == 1 && flag2 == 1) {
							if (stuData.isEmpty()) {
								tvShow1.setText("����ɹ�");
								tvShow.setVisibility(View.VISIBLE);
								tvShow.setText("��ˢ��");
							} else {
								tvShow.setVisibility(View.GONE);
								tvShow1.setVisibility(View.GONE);
								etHeight.setText("");
								etWeight.setText("");
								Toast.makeText(context, "����ɹ�", Toast.LENGTH_SHORT).show();
								btnCancel.setVisibility(View.GONE);
								btnSave.setVisibility(View.GONE);
								btnScan.setVisibility(View.VISIBLE);
							}
						} else {
							if (stuData.isEmpty()) {
								tvShow1.setText("����ʧ��");
								tvShow.setVisibility(View.VISIBLE);
								tvShow.setText("��ˢ��");
							} else {
								tvShow1.setVisibility(View.GONE);
								Toast.makeText(context, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
								btnCancel.setVisibility(View.GONE);
								btnSave.setVisibility(View.GONE);
								tvShow.setVisibility(View.GONE);
								btnScan.setVisibility(View.VISIBLE);
							}
						}
					}
				}
			}
		});

	}

}
