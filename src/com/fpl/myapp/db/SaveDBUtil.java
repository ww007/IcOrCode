package com.fpl.myapp.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fpl.myapp2.R;
import com.fpl.myapp.entity.PH_Class;
import com.fpl.myapp.entity.PH_Grade;
import com.fpl.myapp.entity.PH_School;
import com.fpl.myapp.entity.PH_Student;
import com.fpl.myapp.entity.PH_StudentItem;
import com.fpl.myapp.util.NetUtil;
import com.wnb.android.nfc.dataobject.entity.IC_Result;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import de.greenrobot.dao.async.AsyncSession;
import ww.greendao.dao.Classes;
import ww.greendao.dao.DaoSession;
import ww.greendao.dao.Grade;
import ww.greendao.dao.RoundResult;
import ww.greendao.dao.School;
import ww.greendao.dao.Student;
import ww.greendao.dao.StudentItem;

public class SaveDBUtil {
	private static List<PH_School> schools = new ArrayList<>();
	private static List<PH_Grade> grades = new ArrayList<>();
	private static List<PH_Class> classes = new ArrayList<>();
	private static long flag = -1;
	public static int over = 0;
	private static NotificationManager notificationManager;
	private static ArrayList<Student> mStudents;
	private static AsyncSession mAsyncSession = DbService.mDaoSession.startAsyncSession();
	private static ArrayList<StudentItem> mStudentItems;
	private static ArrayList<Classes> mClasses;
	private static ArrayList<Grade> mGrade;

	/**
	 * ���ݿ��в�ѯ���гɼ�������IC��
	 * 
	 * @param context
	 * @param roundResults
	 * @param result
	 */
	public static void queryRoundResults(List<RoundResult> roundResults, IC_Result[] result) {
		List<Integer> list = new ArrayList<>();
		for (RoundResult roundResult : roundResults) {
			list.add(roundResult.getResult());
		}
		// �����ɼ����浽result[0]
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == Collections.max(list)) {
				result[0] = new IC_Result(list.get(i), 1, 0, 0);
				list.remove(i);
			}
		}
		// ����ȥ���ɼ��������ɼ�����
		for (int i = 0; i < list.size(); i++) {
			result[i + 1] = new IC_Result(list.get(i), 1, 0, 0);
		}
	}

	/**
	 * �洢ѧ����Ϣ�����ݿ�
	 * 
	 * @param response
	 *            ��ȡ����ѧ����Ϣ
	 * @param students
	 *            ѧ������
	 * @param context
	 * @return
	 */
	public static int saveStudentDB(String response, List<PH_Student> students, Context context) {
		mStudents = new ArrayList<Student>();
		mClasses = new ArrayList<Classes>();
		mGrade = new ArrayList<Grade>();
		// ������ȡ��Json����
		JSONObject jsonObject = JSON.parseObject(response);
		JSONArray jsonSchool = jsonObject.getJSONArray("school");
		JSONArray jsonClass = jsonObject.getJSONArray("class");
		JSONArray jsonGrade = jsonObject.getJSONArray("grade");
		schools = JSON.parseArray(jsonSchool.toJSONString(), PH_School.class);
		classes = JSON.parseArray(jsonClass.toJSONString(), PH_Class.class);
		grades = JSON.parseArray(jsonGrade.toJSONString(), PH_Grade.class);

		// ����ѧУ��Ϣ
		for (int i = 0; i < schools.size(); i++) {
			School school = new School(null, schools.get(i).getSchoolName(), schools.get(i).getSchoolYear(), null);
			DbService.getInstance(context).saveSchool(school);
		}
		Log.i("----------", "����ѧУ��Ϣ���");
		// �����꼶��Ϣ
		for (int i = 0; i < grades.size(); i++) {
			Long SchoolID = DbService.getInstance(context).querySchoolByName(grades.get(i).getSchoolName().toString())
					.get(0).getSchoolID();
			Grade grade = new Grade(null, SchoolID, grades.get(i).getGradeCode(), grades.get(i).getGradeName(), null);
			mGrade.add(grade);
		}
		mAsyncSession.runInTx(new Runnable() {
			@Override
			public void run() {
				DbService.gradeDao.insertOrReplaceInTx(mGrade);
				Log.i("mGrade", mGrade.size() + "");
				Log.i("----------", "�����꼶��Ϣ���");
			}
		});
		// ����༶��Ϣ
		for (int i = 0; i < classes.size(); i++) {
			Long GradeID = DbService.getInstance(context).queryGradeByCode(classes.get(i).getGradeCode()).get(0)
					.getGradeID();
			Classes newClass = new Classes(null, GradeID, classes.get(i).getClassCode(), classes.get(i).getClassName(),
					null);
			mClasses.add(newClass);
		}
		mAsyncSession.runInTx(new Runnable() {
			@Override
			public void run() {
				DbService.classesDao.insertOrReplaceInTx(mClasses);
				Log.i("mClasses", mClasses.size() + "");
				Log.i("----------", "����༶��Ϣ���");
			}
		});
		// ����ѧ����Ϣ
		for (int i = 0; i < students.size(); i++) {
			Student student = new Student(null, students.get(i).getStudentCode(), students.get(i).getStudentName(),
					students.get(i).getSex(), students.get(i).getClassCode(), students.get(i).getGradeCode(),
					students.get(i).getIDCardNo(), students.get(i).getICCardNo(), students.get(i).getDownloadTime(),
					null, null, null);
			mStudents.add(student);
			// flag = DbService.getInstance(context).saveStudent(student);
		}
		mAsyncSession.runInTx(new Runnable() {
			@Override
			public void run() {
				DbService.studentDao.insertOrReplaceInTx(mStudents);
				Log.i("students", mStudents.size() + "");
				Log.i("----------", "����ѧ����Ϣ���");
			}
		});
		if (mStudents.size() != 0 && flag != -1) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * �洢ѧ����Ŀ��Ϣ�����ݿ�
	 * 
	 * @param studentItems
	 *            ��ȡ����ѧ����Ŀ��Ϣ
	 * @param context
	 */
	public static void saveStudentItemDB(List<PH_StudentItem> studentItems, Context context) {
		Log.i("studentItems.size()=", studentItems.size() + "");
		mStudentItems = new ArrayList<>();
		for (PH_StudentItem stuItem : studentItems) {
			String itemCode = stuItem.getItemCode();
			String studentCode = stuItem.getStudentCode();
			StudentItem studentItem = new StudentItem(null, studentCode, itemCode, null, 0, null, 0, null, null, null);
			mStudentItems.add(studentItem);
		}
		mAsyncSession.runInTx(new Runnable() {
			@Override
			public void run() {
				DbService.studentItemDao.insertOrReplaceInTx(mStudentItems);
				Log.i("mStudentItems", mStudentItems.size() + "");
				Log.i("----------", "����ѧ����Ŀ��Ϣ���");
			}
		});
		Notification.Builder builder = new Notification.Builder(context);
		builder.setSmallIcon(R.drawable.app);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app));
		builder.setAutoCancel(true);
		builder.setContentTitle("MyApp֪ͨ");
		builder.setContentText("���ݳ�ʼ�����");
		builder.setTicker("���ݳ�ʼ�����,���Կ�ʼ����");
		builder.setDefaults(Notification.DEFAULT_SOUND);
		// ���õ����ת
		Intent hangIntent = new Intent();
		// hangIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// hangIntent.setClass(context, MainActivity.class);
		// ���������PendingIntent�Ѿ����ڣ����ڲ����µ�Intent֮ǰ����ȡ������ǰ��
		PendingIntent hangPendingIntent = PendingIntent.getActivity(context, 0, hangIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		builder.setFullScreenIntent(hangPendingIntent, true);
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(2, builder.build());
	}

	/**
	 * �洢�ɼ������ݿ�
	 * 
	 * @param context
	 * @param stuCode
	 *            ѧ������
	 * @param etChengji
	 *            �ɼ�
	 * @param resultState
	 * @param code
	 *            ��Ŀ��������
	 * @param tvTitle
	 *            ��Ŀ����
	 * @return
	 */
	public static int saveGradesDB(Context context, String stuCode, String etChengji, int resultState, String code,
			String tvTitle) {
		int RoundNo = 1;
		// long studentID =
		// DbService.getInstance(context).queryStudentByCode(stuCode).get(0).getStudentID();
		// long ItemID = -1;
		String itemCode = "";
		if (tvTitle.equals("���") || tvTitle.equals("����") || tvTitle.equals("1000����") || tvTitle.equals("800����")
				|| tvTitle.equals("��������") || tvTitle.equals("��������")) {
			itemCode = DbService.getInstance(context).queryItemByName(tvTitle).get(0).getItemCode();
		} else {
			itemCode = DbService.getInstance(context).queryItemByMachineCode(code).get(0).getItemCode();
		}

		List<StudentItem> studentItems = DbService.getInstance(context).queryStudentItemByCode(stuCode, itemCode);
		if (studentItems.isEmpty()) {
			return 0;
		}
		Long studentItemID = studentItems.get(0).getStudentItemID();
		List<RoundResult> round = DbService.getInstance(context).queryRoundResultByID(studentItemID);
		List<Integer> rounds = new ArrayList<>();

		if (round.size() != 0) {
			for (int i = 0; i < round.size(); i++) {
				rounds.add(round.get(i).getRoundNo());
			}
			Log.i("rounds", rounds.toString());
			RoundNo = Collections.max(rounds) + 1;
		}

		Integer currentResult = Integer.parseInt(etChengji);
		// ��ȡ��ǰϵͳʱ��
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		Date curDate = new Date(java.lang.System.currentTimeMillis());// ��ȡ��ǰʱ��
		String currentTime = formatter.format(curDate);
		Log.i("currentTime--->", currentTime);
		// �洢��ǰ�����ִ�
		String macAddress = NetUtil.getLocalMacAddressFromWifiInfo(context);
		RoundResult roundResult = new RoundResult(null, studentItemID, currentResult, RoundNo, currentTime, resultState,
				0, macAddress, null, null);
		DbService.getInstance(context).saveRoundResult(roundResult);

		return 1;

	}
}
