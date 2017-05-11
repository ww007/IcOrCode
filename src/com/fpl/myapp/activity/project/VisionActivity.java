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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ww.greendao.dao.Item;
import ww.greendao.dao.StudentItem;

/**
 * ����
 * 
 * @author ww
 *
 */
public class VisionActivity extends NFCActivity {

	private TextView tvNumber;
	private TextView tvGender;
	private TextView tvName;
	private TextView tvShow1;
	private TextView tvShow;
	private EditText etMax;
	private EditText etMin;
	private Button btnSave;
	private Button btnCancel;
	private ImageButton ibQuit;
	private Student student;
	private String sex;
	private TextView tvHUnit;
	private TextView tvWUnit;
	private TextView tvTitle;
	private TextView tvLeft;
	private TextView tvRight;
	private EditText etLeft;
	private EditText etRight;
	private Context context;
	private String max;
	private String min;

	private Logger log = Logger.getLogger(VisionActivity.class);
	private LinearLayout llHW;
	private LinearLayout llHWVison;
	private SharedPreferences mSharedPreferences;
	private int readStyle;
	private String stuData;
	private List<ww.greendao.dao.Student> stuByCode;
	private Button btnScan;
	private StudentItem studentItems;

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

		Item items = DbService.getInstance(context).queryItemByName("����");

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

	/**
	 * д��
	 * 
	 * @param intent
	 */
	private void writeCard(Intent intent) {
		try {
			int hResult1 = Integer.parseInt(etLeft.getText().toString());
			int wResult1 = Integer.parseInt(etRight.getText().toString());
			IItemService itemService = new NFCItemServiceImpl(intent);
			IC_Result[] HWresult = new IC_Result[4];
			HWresult[0] = new IC_Result(hResult1, 1, 0, 0);// ��������
			HWresult[2] = new IC_Result(wResult1, 1, 0, 0);// ��������
			IC_ItemResult HWItemResult = new IC_ItemResult(Constant.VISION, 0, 0, HWresult);
			boolean isHWResult = itemService.IC_WriteItemResult(HWItemResult);
			log.info("д�������ɼ�=>" + isHWResult + "���ۣ�" + hResult1 + "�����ۣ�" + wResult1 + "��ѧ����" + student.toString());
			if (isHWResult) {
				tvShow1.setText("�ɼ�д�����");
				tvShow.setText("��ˢ��");
			} else {
				Toast.makeText(this, "д������", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			log.error("����д��ʧ��");
		}
	}

	/**
	 * ����
	 */
	private void readCard(Intent intent) {
		try {
			IItemService itemService = new NFCItemServiceImpl(intent);
			student = itemService.IC_ReadStuInfo();
			log.info("��������=>" + student.toString());
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
			log.error("��������ʧ��");
			e.printStackTrace();
		}

	}

	private void initOne() {
		etLeft.setText("");
		etRight.setText("");
		tvShow1.setVisibility(View.GONE);
		btnCancel.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		etLeft.setEnabled(true);
		etRight.setEnabled(true);

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
		tvTitle = (TextView) findViewById(R.id.tv_hw_title);
		tvNumber = (TextView) findViewById(R.id.tv_number_edit_HW);
		tvGender = (TextView) findViewById(R.id.tv_gender_edit_HW);
		tvLeft = (TextView) findViewById(R.id.tv_hw_height);
		tvRight = (TextView) findViewById(R.id.tv_hw_weight);
		tvName = (TextView) findViewById(R.id.tv_name_edit_HW);
		tvShow1 = (TextView) findViewById(R.id.tv_hw_show1);
		tvShow = (TextView) findViewById(R.id.tv_hw_show);
		etLeft = (EditText) findViewById(R.id.et_height_edit);
		etRight = (EditText) findViewById(R.id.et_weight_edit);
		etLeft.setInputType(InputType.TYPE_NULL);
		etRight.setInputType(InputType.TYPE_NULL);
		llHW = (LinearLayout) findViewById(R.id.ll_hw);
		llHWVison = (LinearLayout) findViewById(R.id.ll_hw_Vison);
		etMax = (EditText) findViewById(R.id.et_hw_max);
		etMin = (EditText) findViewById(R.id.et_hw_min);
		btnSave = (Button) findViewById(R.id.btn_hw_save);
		btnCancel = (Button) findViewById(R.id.btn_hw_cancel);
		ibQuit = (ImageButton) findViewById(R.id.ib_hw_quit);
		llHW.setVisibility(View.GONE);
		llHWVison.setVisibility(View.VISIBLE);
		etLeft.setEnabled(false);
		etRight.setEnabled(false);

		tvTitle.setText("����");
		tvLeft.setText("��������");
		tvRight.setText("��������");
		tvHUnit.setText("");
		tvWUnit.setText("");
		tvGender.setText("");
		tvName.setText("");
		etLeft.setText("");
		etRight.setText("");
		etMax.setText(max);
		etMin.setText(min);
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
			etLeft.setEnabled(true);
			etRight.setEnabled(true);
			btnScan.setVisibility(View.GONE);
			tvShow.setText("������");
			tvShow.setVisibility(View.VISIBLE);
			initOne();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 66 || keyCode == 135 || keyCode == 136) {
			Intent intent = new Intent(VisionActivity.this, CaptureActivity.class);
			intent.putExtra("className", Constant.VISION + "");
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setListener() {
		etLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etLeft.requestFocus();
				etLeft.setInputType(EditorInfo.TYPE_CLASS_PHONE);
				InputMethodManager imm = (InputMethodManager) etLeft.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			}
		});
		etRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etRight.requestFocus();
				etRight.setInputType(EditorInfo.TYPE_CLASS_PHONE);
				InputMethodManager imm = (InputMethodManager) etRight.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			}
		});
		btnScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(VisionActivity.this, CaptureActivity.class);
				intent.putExtra("className", Constant.VISION + "");
				startActivity(intent);
				finish();
			}
		});

		ibQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		etRight.addTextChangedListener(new TextWatcher() {

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
				tvShow.setVisibility(View.GONE);
				btnCancel.setVisibility(View.VISIBLE);
				btnSave.setVisibility(View.VISIBLE);
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (DbService.getInstance(context).loadAllItem().isEmpty()) {
					Toast.makeText(context, "���Ȼ�ȡ��Ŀ�������", Toast.LENGTH_SHORT).show();
					return;
				}
				if ("".equals(etLeft.getText().toString()) || "".equals(etRight.getText().toString())) {
					Toast.makeText(VisionActivity.this, "����Ϊ��", Toast.LENGTH_SHORT).show();
				} else {
					if (Double.parseDouble(etLeft.getText().toString()) > Double.parseDouble(max)
							|| Double.parseDouble(etLeft.getText().toString()) < Integer.parseInt(min)
							|| Double.parseDouble(etRight.getText().toString()) > Double.parseDouble(max)
							|| Double.parseDouble(etRight.getText().toString()) < Integer.parseInt(min)) {
						Toast.makeText(context, "�������뷶Χ������������", Toast.LENGTH_SHORT).show();
						etLeft.setText("");
						etRight.setText("");
						return;
					}

					String itemCode = DbService.getInstance(context).queryItemByMachineCode(Constant.VISION + "")
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
					} else {
						int flag1 = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(),
								etLeft.getText().toString(), 0, "" + Constant.VISION, "��������");
						int flag2 = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(),
								etRight.getText().toString(), 0, "" + Constant.VISION, "��������");

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
								etLeft.setText("");
								etRight.setText("");
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
