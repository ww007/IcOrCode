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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
	private int constant;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_information);
		context = this;

		// 加载配置
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
				Toast.makeText(context, "查无此人", Toast.LENGTH_SHORT).show();
				stuData = "";
			}
		}

		if (title.equals("800/1000米跑")) {
			constant = Constant.MIDDLE_RACE;
		} else if (title.equals("50米跑")) {
			constant = Constant.RUN50;
		} else if (title.equals("50米x8往返跑")) {
			constant = Constant.SHUTTLE_RUN;
		} else if (title.equals("篮球运球")) {
			constant = Constant.BASKETBALL_SKILL;
		} else if (title.equals("足球运球")) {
			constant = Constant.FOOTBALL_SKILL;
		} else if (title.equals("游泳")) {
			constant = Constant.SWIM;
		}

		items = DbService.getInstance(context).queryItemByMachineCode(constant + "");

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
	 * 写卡
	 * 
	 * @param intent
	 */
	private void writeCard(Intent intent) {
		try {
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
			log.info("写入跑步成绩=>" + isRunResult + "成绩：" + result1 + "，学生：" + tvNumber.getText().toString());
			if (isRunResult) {
				tvShow1.setText("成绩写卡完成");
				tvShow.setText("请刷卡");
			} else {
				Toast.makeText(this, "写卡出错", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			log.error(title + "写卡失败");
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
			log.info(title + "读卡=>" + student.toString());

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
			log.error(title + "读卡失败");
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
		rb2.setText("中退");
		rb3.setText("弃权");

		tvInfoTitle.setText(title);
		tvInfoChengji.setText("成绩");
		tvInfoUnit.setVisibility(View.VISIBLE);
		tvInfoUnit.setText("毫秒");
		tvName.setText(name);
		tvGender.setText(sex);
		etMax.setText(max);
		etMin.setText(min);
		etS.setText("");
		etMs.setText("");
		etSec.setText("");
		btnCancel.setVisibility(View.GONE);
		btnSave.setVisibility(View.GONE);
		tvShow.setText("请输入成绩");
		tvShow1.setVisibility(View.GONE);

		if (readStyle == 1) {
			tvNumber.setText(stuData);
			tvShow.setVisibility(View.GONE);
			if (stuByCode.get(0).getSex() == 1) {
				sex = "男";
			} else {
				sex = "女";
			}
			tvName.setText(stuByCode.get(0).getStudentName());
			tvGender.setText(sex);
			btnScan.setVisibility(View.GONE);
			tvShow.setText("请输入成绩");
			tvShow.setVisibility(View.VISIBLE);
			initOne();
		} else {
			tvNumber.setText(number);
			stuData = "";
		}
	}

	private int flag = 0;
	private String checkedBtn = "正常";
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
		etSec.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etSec.requestFocus();
				etSec.setInputType(EditorInfo.TYPE_CLASS_PHONE);
				InputMethodManager imm = (InputMethodManager) etSec.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			}
		});
		etMs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etMs.requestFocus();
				etMs.setInputType(EditorInfo.TYPE_CLASS_PHONE);
				InputMethodManager imm = (InputMethodManager) etMs.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			}
		});
		etS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etS.requestFocus();
				etS.setInputType(EditorInfo.TYPE_CLASS_PHONE);
				InputMethodManager imm = (InputMethodManager) etS.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			}
		});
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
				tvShow.setText("请输入成绩");
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
				if (checkedBtn.equals("正常")) {
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
				} else if (checkedBtn.equals("犯规")) {
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
				} else if (checkedBtn.equals("中退")) {
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
				} else if (checkedBtn.equals("弃权")) {
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
					Toast.makeText(context, "请先获取项目相关数据", Toast.LENGTH_SHORT).show();
					return;
				}
				if (getChengJi() == 0 && checkedBtn.equals("正常")) {
					Toast.makeText(RunGradeInputActivity.this, "成绩为空", Toast.LENGTH_SHORT).show();
					return;
				} else if (checkedBtn.equals("犯规")) {
					resultState = -1;
				} else if (checkedBtn.equals("中退")) {
					resultState = -2;
				} else if (checkedBtn.equals("弃权")) {
					resultState = -3;
				} else if (checkedBtn.equals("正常")) {
					if (getChengJi() > Integer.parseInt(max) || getChengJi() < Integer.parseInt(min)) {
						Toast.makeText(context, "不在输入范围，请重新输入", Toast.LENGTH_SHORT).show();
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
				if (title.equals("50米跑")) {
					// 查询数据库中保存的该学生项目成绩的轮次
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
						Toast.makeText(context, "当前学生项目不存在", Toast.LENGTH_SHORT).show();
						return;
					} else {
						resultState = 0;
					}
					flag = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(), chengji, resultState,
							Constant.RUN50 + "", "50米跑");
					log.info("保存50米跑" + tvNumber.getText().toString() + "成绩：" + chengji);
				} else if (title.equals("800/1000米跑")) {
					String itemName;
					String itemCode;
					if (sex.equals("男")) {
						itemName = "1000米跑";
					} else {
						itemName = "800米跑";
					}
					itemCode = DbService.getInstance(context).queryItemByName(itemName).getItemCode();
					// 查询数据库中保存的该学生项目成绩的轮次
					studentItems = DbService.getInstance(context).queryStudentItemByCode(tvNumber.getText().toString(),
							itemCode);
					if (studentItems == null) {
						Toast.makeText(context, "当前学生项目不存在", Toast.LENGTH_SHORT).show();
						return;
					} else {
						resultState = 0;
					}
					flag = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(), chengji, resultState,
							Constant.MIDDLE_RACE + "", itemName);
					log.info("保存800/1000米跑" + tvNumber.getText().toString() + "成绩：" + chengji);
				} else if (title.equals("50米x8往返跑")) {
					// 查询数据库中保存的该学生项目成绩的轮次
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
						Toast.makeText(context, "当前学生项目不存在", Toast.LENGTH_SHORT).show();
						return;
					} else {
						resultState = 0;
					}
					flag = SaveDBUtil.saveGradesDB(context, tvNumber.getText().toString(), chengji, resultState,
							Constant.SHUTTLE_RUN + "", "50米x8往返跑");
					log.info("保存50米x8往返跑" + tvNumber.getText().toString() + "成绩：" + chengji);
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
						tvShow1.setText("成绩保存成功");
						tvShow.setVisibility(View.VISIBLE);
						tvShow.setText("请刷卡");
					} else {
						tvShow.setVisibility(View.GONE);
						tvShow1.setVisibility(View.GONE);
						etMs.setText("");
						etSec.setText("");
						etS.setText("");
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

	}
}
