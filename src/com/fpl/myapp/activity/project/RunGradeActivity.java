package com.fpl.myapp.activity.project;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.fpl.myapp2.R;
import com.fpl.myapp.adapter.ChengjiAdapter;
import com.fpl.myapp.base.NFCActivity;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.db.SaveDBUtil;
import com.fpl.myapp.entity.RunGrade;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp.util.NetUtil;
import com.wnb.android.nfc.dataobject.entity.IC_ItemResult;
import com.wnb.android.nfc.dataobject.entity.IC_Result;
import com.wnb.android.nfc.dataobject.entity.Student;
import com.wnb.android.nfc.dataobject.service.IItemService;
import com.wnb.android.nfc.dataobject.service.impl.NFCItemServiceImpl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class RunGradeActivity extends NFCActivity {

	private Bundle bd;
	private ArrayList<RunGrade> runGrades;
	private ChengjiAdapter adapter;
	private TextView tvTitle;
	private String title;
	private ListView lvGrade;
	private Button btnQuit;
	private Button btnSure;
	private String currentName;
	private int currentPosition;
	private Context context;

	private Logger log = Logger.getLogger(RunGradeActivity.class);
	private Student student = new Student();

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run_grade);
		context = this;

		Intent intent = getIntent();
		bd = intent.getExtras();
		title = intent.getStringExtra("title");
		runGrades = (ArrayList<RunGrade>) bd.getSerializable("grades");
		log.info(title + "�ɼ���" + runGrades.toString());

		initView();
		setListener();

	}

	@Override
	protected void onNewIntent(Intent intent) {
		writeCard(intent);
		readCard(intent);
	}

	/**
	 * ��������
	 * 
	 * @param intent
	 */
	private void readCard(Intent intent) {

		try {
			IItemService itemService = new NFCItemServiceImpl(intent);
			student = itemService.IC_ReadStuInfo();
			currentName = student.getStuName();

			log.info("��ǰ����λ��=>" + lvGrade.getFirstVisiblePosition() + "=>" + student.toString());

			updateView(currentPosition, student);

			currentPosition++;
			adapter.setSelectItem(currentPosition);
			adapter.notifyDataSetInvalidated();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * д������
	 * 
	 * @param intent
	 */
	private void writeCard(Intent intent) {

		try {
			Log.i("title=", title);
			if (title.equals("50����")) {
				IItemService itemService = new NFCItemServiceImpl(intent);
				String time = runGrades.get(currentPosition).getTime();
				int result1 = Integer.parseInt(time.subSequence(0, 2).toString()) * 60 * 1000
						+ Integer.parseInt(time.subSequence(3, 5).toString()) * 1000
						+ Integer.parseInt(time.substring(6, 9).toString());
				log.info("50����д��=>" + "�ɼ���" + result1 + "��ѧ����" + student.toString());
				IC_Result[] result50M = new IC_Result[4];
				result50M[0] = new IC_Result(result1, 1, 0, 0);// �ɼ�1
				IC_ItemResult ItemResult50M = new IC_ItemResult(Constant.RUN50, 0, 0, result50M);
				boolean is50MResult = itemService.IC_WriteItemResult(ItemResult50M);
				log.info("50����д��=>" + is50MResult);
			} else if (title.equals("800/1000����")) {
				IItemService itemService = new NFCItemServiceImpl(intent);
				String time = runGrades.get(currentPosition).getTime();
				int result1 = Integer.parseInt(time.subSequence(0, 2).toString()) * 60 * 1000
						+ Integer.parseInt(time.subSequence(3, 5).toString()) * 1000
						+ Integer.parseInt(time.substring(6, 9).toString());
				log.info("800/1000����д��=>" + "�ɼ���" + result1 + "��ѧ����" + student.toString());
				IC_Result[] resultMiddleRace = new IC_Result[4];
				resultMiddleRace[0] = new IC_Result(result1, 1, 0, 0);// �ɼ�1
				IC_ItemResult ItemResultMiddleRace = new IC_ItemResult(Constant.MIDDLE_RACE, 0, 0, resultMiddleRace);
				boolean isMiddleRaceResult = itemService.IC_WriteItemResult(ItemResultMiddleRace);
				log.info("800/1000����д��=>" + isMiddleRaceResult);
			} else if (title.equals("50��x8������")) {
				IItemService itemService = new NFCItemServiceImpl(intent);
				String time = runGrades.get(currentPosition).getTime();
				int result1 = Integer.parseInt(time.subSequence(0, 2).toString()) * 60 * 1000
						+ Integer.parseInt(time.subSequence(3, 5).toString()) * 1000
						+ Integer.parseInt(time.substring(6, 9).toString());
				log.info("50��x8������д��=>" + "�ɼ���" + result1 + "��ѧ����" + student.toString());
				IC_Result[] resultZFP = new IC_Result[4];
				resultZFP[0] = new IC_Result(result1, 1, 0, 0);// �ɼ�1
				IC_ItemResult ItemResultZFP = new IC_ItemResult(Constant.SHUTTLE_RUN, 0, 0, resultZFP);
				boolean isZFPResult = itemService.IC_WriteItemResult(ItemResultZFP);
				log.info("50��x8������д��=>" + isZFPResult);
			}

		} catch (Exception e) {
			log.error(title + "д������ʧ��");
		}
	}

	private ArrayList<RunGrade> datas;
	private TextView tv;

	private void setListener() {
		lvGrade.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				currentPosition = position;
				adapter.setSelectItem(position);
				adapter.notifyDataSetInvalidated();
			}
		});

		btnSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(RunGradeActivity.this).setTitle("����ɼ�").setMessage("�ɼ�����������˳����Ƿ�ȷ�ϣ�")
						.setPositiveButton("ȡ��", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub

							}
						}).setNegativeButton("ȷ��", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								datas = ChengjiAdapter.datas;
								if (DbService.getInstance(context).loadAllItem().isEmpty()) {
									NetUtil.showToast(context, "�������δ���أ����ܱ���");
									return;
								}
								if (title.equals("50����")) {
									for (RunGrade runGrade : datas) {
										if (!runGrade.getName().isEmpty()) {
											int result = Integer.parseInt(
													runGrade.getTime().subSequence(0, 2).toString()) * 60 * 1000
													+ Integer.parseInt(runGrade.getTime().subSequence(3, 5).toString())
															* 1000
													+ Integer.parseInt(runGrade.getTime().substring(6, 9).toString());
											SaveDBUtil.saveGradesDB(context, runGrade.getStuCode(), result + "", 0,
													Constant.RUN50 + "", "50����");
										}
									}

								} else if (title.equals("800/1000����")) {
									for (RunGrade runGrade : datas) {
										if (!runGrade.getName().isEmpty()) {
											int result = Integer.parseInt(
													runGrade.getTime().subSequence(0, 2).toString()) * 60 * 1000
													+ Integer.parseInt(runGrade.getTime().subSequence(3, 5).toString())
															* 1000
													+ Integer.parseInt(runGrade.getTime().substring(6, 9).toString());
											String proName = null;
											if (runGrade.getSex() == 1) {
												proName = "1000����";
											} else {
												proName = "800����";
											}
											SaveDBUtil.saveGradesDB(context, runGrade.getStuCode(), result + "", 0,
													Constant.MIDDLE_RACE + "", proName);
										}
									}
								} else if (title.equals("50��x8������")) {
									for (RunGrade runGrade : datas) {
										if (!runGrade.getName().isEmpty()) {
											int result = Integer.parseInt(
													runGrade.getTime().subSequence(0, 2).toString()) * 60 * 1000
													+ Integer.parseInt(runGrade.getTime().subSequence(3, 5).toString())
															* 1000
													+ Integer.parseInt(runGrade.getTime().substring(6, 9).toString());
											SaveDBUtil.saveGradesDB(context, runGrade.getStuCode(), result + "", 0,
													Constant.SHUTTLE_RUN + "", "50��x8������");
										}
									}
								}
								finish();
							}
						}).show();
				// lvGrade.setEnabled(false);
				// btnSure.setEnabled(false);
			}
		});
		btnQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(RunGradeActivity.this).setTitle("ȷ��").setMessage("�ɼ�δ���棬�Ƿ��˳���")
						.setPositiveButton("ȡ��", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub

							}
						}).setNegativeButton("ȷ��", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						}).show();
			}
		});
	}

	/**
	 * ���µ�ǰlistView��ʾ������Ϣ
	 * 
	 * @param itemIndex
	 * @param student
	 */
	private void updateView(int itemIndex, Student student) {
		runGrades.get(itemIndex).setName(currentName);
		currentName = "";
		// �õ���һ������ʾ�ؼ���λ��
		int visiblePosition = lvGrade.getFirstVisiblePosition();
		// ֻ�е�Ҫ���µ�view�ڿɼ���λ��ʱ�Ÿ��£����ɼ�ʱ������������
		if (itemIndex - visiblePosition >= 0) {
			// �õ�Ҫ���µ�item��view
			View view = lvGrade.getChildAt(itemIndex - visiblePosition);
			// ����adapter���½���
			adapter.updateView(view, itemIndex, student);
		}

	}

	private void initView() {
		lvGrade = (ListView) findViewById(R.id.lv_run_grade);
		lvGrade.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// ���õ�ѡģʽ

		tvTitle = (TextView) findViewById(R.id.tv_title_runGrade);
		btnQuit = (Button) findViewById(R.id.btn_grade_quit);
		btnSure = (Button) findViewById(R.id.btn_grade_sure);
		tv = (TextView) findViewById(R.id.tv_runGrade);

		tvTitle.setText(title);
		adapter = new ChengjiAdapter(this, runGrades);
		lvGrade.setAdapter(adapter);
	}

}
