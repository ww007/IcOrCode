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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import ww.greendao.dao.Item;
import ww.greendao.dao.RoundResult;
import ww.greendao.dao.StudentItem;

public class SitAndReachActivity extends NFCActivity {
	private TextView tvInfoTitle;
	private TextView tvName;
	private TextView tvGender;
	private TextView tvNumber;
	private TextView tvShow1;
	private TextView tvShow;
	private EditText etChengji;
	private EditText etMax;
	private EditText etMin;
	private TextView tvInfoChengji;
	private TextView tvInfoUnit;
	private Button btnSave;
	private Button btnCancel;
	private ImageButton ibQuit;
	private Student student;
	private String sex;
	private Context context;
	private String max;
	private String min;
	private StudentItem studentItems;
	private Logger log = Logger.getLogger(SitAndReachActivity.class);
	private RadioGroup rg;
	private RadioButton rb0;
	private RadioButton rb2;
	private RadioButton rb1;
	private RadioButton rb3;
	private Button btnScan;
	private String stuData;
	private List<ww.greendao.dao.Student> stuByCode;
	private SharedPreferences mSharedPreferences;
	private int readStyle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_information);
		context = this;

		mSharedPreferences = getSharedPreferences("readStyles", Activity.MODE_PRIVATE);
		readStyle = mSharedPreferences.getInt("readStyle", 0);

		stuData = getIntent().getStringExtra("data");
		if (stuData != null && stuData.length() != 0) {
			stuByCode = DbService.getInstance(context).queryStudentByCode(stuData);
			if (stuByCode.isEmpty()) {
				Toast.makeText(context, "查无此人", Toast.LENGTH_SHORT).show();
				stuData = "";
			}
		}

		Item items = DbService.getInstance(context).queryItemByName("坐位体前屈");

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
			if (View.VISIBLE == tvShow1.getVisibility() && "成绩保存成功".equals(tvShow1.getText().toString())) {
				writeCard(intent);
			} else {
				readCard(intent);
			}
		} else {
			NetUtil.showToast(context, "当前选择非IC卡状态，请设置");
		}
	}

	/**
	 * 写卡
	 * 
	 * @param intent
	 */
	private void writeCard(Intent intent) {
		try {
			IItemService itemService = new NFCItemServiceImpl(intent);
			if (itemService.IC_ReadStuInfo().getStuCode().equals(tvNumber.getText().toString())) {
				IC_Result[] resultSitReach = new IC_Result[4];
				String chengji = "";
				if (checkedBtn.equals("犯规") || checkedBtn.equals("弃权")) {
					chengji = "0";
				} else {
					chengji = etChengji.getText().toString();
				}
				int result1 = Integer.parseInt(chengji);
				resultSitReach[0] = new IC_Result(result1, 1, 0, 0);
				IC_ItemResult ItemResultSitReach = new IC_ItemResult(Constant.SIT_AND_REACH, 0, 0, resultSitReach);
				boolean isSitReachResult = itemService.IC_WriteItemResult(ItemResultSitReach);
				log.info("写入坐位体前屈成绩=>" + isSitReachResult + "成绩：" + result1 + "，学生：" + student.toString());
				if (isSitReachResult) {
					tvShow1.setText("成绩写卡完成");
					tvShow.setText("请刷卡");
				} else {
					Toast.makeText(this, "写卡出错", Toast.LENGTH_SHORT).show();
				}
			} else {
				NetUtil.showToast(context, "写卡失败，此卡非当前记录");
			}
		} catch (Exception e) {
			log.error("坐位体前屈写卡失败");
			e.printStackTrace();
		}

	}

	/**
	 * 读卡
	 */
	private void readCard(Intent intent) {
		try {
			IItemService itemService = new NFCItemServiceImpl(intent);
			student = itemService.IC_ReadStuInfo();
			log.info("坐位体前屈读卡=>" + student.toString());

			if (1 == student.getSex()) {
				sex = "男";
			} else {
				sex = "女";
			}
			tvGender.setText(sex);
			tvName.setText(student.getStuName().toString());
			tvNumber.setText(student.getStuCode().toString());
			initOne();
		} catch (Exception e) {
			log.error("坐位体前屈读卡失败");
			e.printStackTrace();
		}
	}

	private void initOne() {
		etChengji.setText("");
		tvShow1.setVisibility(View.GONE);
		btnCancel.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		etChengji.setEnabled(true);
		rb0.setChecked(true);

		tvShow.setText("请输入成绩");
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
		etChengji = (EditText) findViewById(R.id.et_info_chengji);
		etChengji.setInputType(InputType.TYPE_NULL);
		etMax = (EditText) findViewById(R.id.et_info_max);
		etMin = (EditText) findViewById(R.id.et_info_min);
		btnSave = (Button) findViewById(R.id.btn_info_save);
		btnCancel = (Button) findViewById(R.id.btn_info_cancel);
		ibQuit = (ImageButton) findViewById(R.id.ib_quit);
		rg = (RadioGroup) findViewById(R.id.radioGroup);
		rb0 = (RadioButton) findViewById(R.id.radio0);
		rb1 = (RadioButton) findViewById(R.id.radio1);
		rb2 = (RadioButton) findViewById(R.id.radio2);
		rb3 = (RadioButton) findViewById(R.id.radio3);
		rb2.setVisibility(View.GONE);
		rb3.setText("弃权");
		etChengji.setEnabled(false);

		tvInfoTitle.setText("坐位体前屈");
		tvInfoChengji.setText("坐位体前屈");
		tvInfoUnit.setVisibility(View.VISIBLE);
		tvInfoUnit.setText("mm");
		tvName.setText("");
		tvGender.setText("");
		etChengji.setText("");
		etMax.setText(max);
		etMin.setText(min);
		btnCancel.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		tvShow.setText("请刷卡");
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
				sex = "男";
			} else {
				sex = "女";
			}
			tvName.setText(stuByCode.get(0).getStudentName());
			tvGender.setText(sex);
			etChengji.setEnabled(true);
			btnScan.setVisibility(View.GONE);
			tvShow.setText("请输入成绩");
			tvShow.setVisibility(View.VISIBLE);
			initOne();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 66 || keyCode == 135 || keyCode == 136) {
			Intent intent = new Intent(SitAndReachActivity.this, CaptureActivity.class);
			intent.putExtra("className", Constant.SIT_AND_REACH + "");
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

	private int resultState;
	private String grade;
	private String checkedBtn = "正常";

	private void setListener() {
		etChengji.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etChengji.requestFocus();
				etChengji.setInputType(EditorInfo.TYPE_CLASS_PHONE);
				InputMethodManager imm = (InputMethodManager) etChengji.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			}
		});
		btnScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SitAndReachActivity.this, CaptureActivity.class);
				intent.putExtra("className", Constant.SIT_AND_REACH + "");
				startActivity(intent);
				finish();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etChengji.setText("");
				btnCancel.setVisibility(View.GONE);
				btnSave.setVisibility(View.GONE);
				tvShow.setText("请输入成绩");
				tvShow.setVisibility(View.VISIBLE);
			}
		});

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				btnCancel.setVisibility(View.VISIBLE);
				btnSave.setVisibility(View.VISIBLE);
				RadioButton radioButton = (RadioButton) SitAndReachActivity.this
						.findViewById(group.getCheckedRadioButtonId());
				checkedBtn = radioButton.getText().toString();
				if (checkedBtn.equals("正常")) {
					etChengji.setText("");
					if (tvNumber.getText().toString().isEmpty()) {
						etChengji.setEnabled(false);
					} else {
						etChengji.setEnabled(true);
					}
				} else if (checkedBtn.equals("犯规")) {
					etChengji.setText("DQ");
					etChengji.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
					etChengji.setEnabled(false);
					etChengji.setTextSize(23);
					grade = "0";
				} else if (checkedBtn.equals("弃权")) {
					etChengji.setText("DNS");
					etChengji.setTextColor(getResources().getColor(android.R.color.darker_gray));
					etChengji.setEnabled(false);
					etChengji.setTextSize(23);
					grade = "0";
				}
			}
		});

		ibQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (DbService.getInstance(context).loadAllItem().isEmpty()) {
					Toast.makeText(context, "请先获取项目相关数据", Toast.LENGTH_SHORT).show();
					return;
				}
				if ("".equals(etChengji.getText().toString()) && checkedBtn.equals("正常")) {
					Toast.makeText(SitAndReachActivity.this, "成绩为空", Toast.LENGTH_SHORT).show();
					return;
				} else if (checkedBtn.equals("犯规")) {
					resultState = -1;
				} else if (checkedBtn.equals("弃权")) {
					resultState = -3;
				} else if (checkedBtn.equals("正常")) {
					if (Double.parseDouble(etChengji.getText().toString()) > Double.parseDouble(max)
							|| Double.parseDouble(etChengji.getText().toString()) < Double.parseDouble(min)) {
						Toast.makeText(context, "不在输入范围，请重新输入", Toast.LENGTH_SHORT).show();
						etChengji.setText("");
						return;
					}
					grade = etChengji.getText().toString();
					// 查询数据库中保存的该学生项目成绩的轮次
					String itemCode = DbService.getInstance(context).queryItemByMachineCode(Constant.SIT_AND_REACH + "")
							.getItemCode();
					// long stuID =
					// DbService.getInstance(context).queryStudentByCode(tvNumber.getText().toString()).get(0)
					// .getStudentID();
					// long itemID =
					// DbService.getInstance(context).queryItemByCode(itemCode).getItemID();
					studentItems = DbService.getInstance(context).queryStudentItemByCode(tvNumber.getText().toString(),
							itemCode);

					if (studentItems == null) {
						Toast.makeText(context, "当前学生项目不存在", Toast.LENGTH_SHORT).show();
						return;
					} else {
						resultState = 0;
					}
				}
				int flag = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(), grade, resultState,
						Constant.SIT_AND_REACH + "", "坐位体前屈");

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
						tvShow1.setText("成绩保存成功");
						tvShow.setVisibility(View.VISIBLE);
						tvShow.setText("请刷卡");
					} else {
						tvShow.setVisibility(View.GONE);
						tvShow1.setVisibility(View.GONE);
						etChengji.setText("");
						Toast.makeText(context, "成绩保存成功", Toast.LENGTH_SHORT).show();
						btnSave.setVisibility(View.GONE);
						btnCancel.setVisibility(View.GONE);
						btnScan.setVisibility(View.VISIBLE);
					}
				} else {
					if (stuData.isEmpty()) {
						tvShow1.setText("成绩保存失败");
						tvShow.setVisibility(View.VISIBLE);
						tvShow.setText("请刷卡");
					} else {
						tvShow1.setVisibility(View.GONE);
						Toast.makeText(context, "成绩保存失败！", Toast.LENGTH_SHORT).show();
						btnSave.setVisibility(View.GONE);
						btnCancel.setVisibility(View.GONE);
						tvShow.setVisibility(View.GONE);
						btnScan.setVisibility(View.VISIBLE);
					}
				}
			}

		});
		etChengji.addTextChangedListener(new TextWatcher() {
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
	}
}
