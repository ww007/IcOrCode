package com.fpl.myapp.util;

/**
 * @author ww
 *
 */
public interface Constant {
	/**
	 * ��ά�������type
	 */
	public static final String REQUEST_SCAN_TYPE = "type";
	/**
	 * ��ͨ���ͣ�ɨ�꼴�ر�
	 */
	public static final int REQUEST_SCAN_TYPE_COMMON = 0;
	/**
	 * �����̵Ǽ����ͣ�ɨ��
	 */
	public static final int REQUEST_SCAN_TYPE_REGIST = 1;

	/**
	 * ɨ������ ��������߶�ά�룺REQUEST_SCAN_MODE_ALL_MODE �����룺
	 * REQUEST_SCAN_MODE_BARCODE_MODE ��ά�룺REQUEST_SCAN_MODE_QRCODE_MODE
	 *
	 */
	public static final String REQUEST_SCAN_MODE = "ScanMode";
	/**
	 * �����룺 REQUEST_SCAN_MODE_BARCODE_MODE
	 */
	public static final int REQUEST_SCAN_MODE_BARCODE_MODE = 0X100;
	/**
	 * ��ά�룺REQUEST_SCAN_MODE_ALL_MODE
	 */
	public static final int REQUEST_SCAN_MODE_QRCODE_MODE = 0X200;
	/**
	 * ��������߶�ά�룺REQUEST_SCAN_MODE_ALL_MODE
	 */
	public static final int REQUEST_SCAN_MODE_ALL_MODE = 0X300;

	public static final String STUDENT_URL = "http://192.168.0.65:8080/sys_tccx/phone/studentInfo/getInfo.action";
	public static final String STUDENT_Page_URL = "http://192.168.0.65:8080/sys_tccx/phone/studentInfo/getStuPage.action";
	public static final String ITEM_URL = "http://192.168.0.65:8080/sys_tccx/phone/item/getItems.action";
	public static final String STUDENT_ITEM_URL = "http://192.168.0.65:8080/sys_tccx/phone/StuItem/getStuItems.action";
	public static final String STUDENT_ITEM_SAVE_URL = "http://192.168.0.65:8080/sys_tccx/phone/StuItem/saveStuItem.action";
	public static final String ROUND_RESULT_SAVE_URL = "/sys_tccx/phone/RoundResult/savePage.action";

	public static final String TOKEN = "fpl@*!";

	public static final int HEIGHT_WEIGHT = 1;// �������
	public static final int VITAL_CAPACITY = 2;// �λ���
	public static final int BROAD_JUMP = 3;// ������Զ
	public static final int JUMP_HEIGHT = 4;// ����
	public static final int PUSH_UP = 5;// ���Գ�
	public static final int SIT_UP = 6;// ��������
	public static final int SIT_AND_REACH = 7;// ��λ��ǰ��
	public static final int ROPE_SKIPPING = 8;// ����
	public static final int VISION = 9;// ����
	public static final int PULL_UP = 10;// ��������
	public static final int INFRARED_BALL = 11;// ����ʵ����
	public static final int MIDDLE_RACE = 12;// �г���
	public static final int VOLLEYBALL = 13;// ����
	public static final int BASKETBALL_SKILL = 14;// ��������
	public static final int SHUTTLE_RUN = 15;// �۷���
	public static final int WALKING1500 = 16;// 1500�׽�����
	public static final int WALKING2000 = 17;// 2000�׽�����
	public static final int RUN50 = 18;// 50����
	public static final int FOOTBALL_SKILL = 19;// ��������
	public static final int KICKING_SHUTTLECOCK = 20;// �����
	public static final int SWIM = 21;// ��Ӿ
	public static final String runGradeInput = "11111";

}
