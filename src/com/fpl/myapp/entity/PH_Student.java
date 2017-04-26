package com.fpl.myapp.entity;

public class PH_Student {
	private String StudentCode; // ѧ��
	private String StudentName; // ����
	private int Sex; // �Ա�
	private String ClassCode; // �༶��� --�༶id
	private String GradeCode; // �꼶���� --�꼶id
	private String IDCardNo; // ���֤��
	private String ICCardNo; // IC����
	private String DownloadTime; // ����ʱ��

	public PH_Student() {
		// TODO Auto-generated constructor stub
	}

	public PH_Student(String studentCode, String studentName, int sex, String classCode, String gradeCode,
			String iDCardNo, String iCCardNo, String downloadTime) {
		super();
		StudentCode = studentCode;
		StudentName = studentName;
		Sex = sex;
		ClassCode = classCode;
		GradeCode = gradeCode;
		IDCardNo = iDCardNo;
		ICCardNo = iCCardNo;
		DownloadTime = downloadTime;
	}

	public String getStudentCode() {
		return StudentCode;
	}

	public void setStudentCode(String studentCode) {
		StudentCode = studentCode;
	}

	public String getStudentName() {
		return StudentName;
	}

	public void setStudentName(String studentName) {
		StudentName = studentName;
	}

	public int getSex() {
		return Sex;
	}

	public void setSex(int sex) {
		Sex = sex;
	}

	public String getClassCode() {
		return ClassCode;
	}

	public void setClassCode(String classCode) {
		ClassCode = classCode;
	}

	public String getGradeCode() {
		return GradeCode;
	}

	public void setGradeCode(String gradeCode) {
		GradeCode = gradeCode;
	}

	public String getIDCardNo() {
		return IDCardNo;
	}

	public void setIDCardNo(String iDCardNo) {
		IDCardNo = iDCardNo;
	}

	public String getICCardNo() {
		return ICCardNo;
	}

	public void setICCardNo(String iCCardNo) {
		ICCardNo = iCCardNo;
	}

	public String getDownloadTime() {
		return DownloadTime;
	}

	public void setDownloadTime(String downloadTime) {
		DownloadTime = downloadTime;
	}

	@Override
	public String toString() {
		return "PH_Student [StudentCode=" + StudentCode + ", StudentName=" + StudentName + ", Sex=" + Sex
				+ ", ClassCode=" + ClassCode + ", GradeCode=" + GradeCode + ", IDCardNo=" + IDCardNo + ", ICCardNo="
				+ ICCardNo + ", DownloadTime=" + DownloadTime + "]";
	}

}
