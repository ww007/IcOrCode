package com.fpl.myapp.entity;

public class PH_Grade {
	private String SchoolName; // ѧУ���� --ѧУID
	private String GradeCode; // �꼶����
	private String GradeName; // �꼶����

	public PH_Grade() {
		// TODO Auto-generated constructor stub
	}

	public PH_Grade(String schoolName, String gradeCode, String gradeName) {
		super();
		SchoolName = schoolName;
		GradeCode = gradeCode;
		GradeName = gradeName;
	}

	public String getSchoolName() {
		return SchoolName;
	}

	public void setSchoolName(String schoolName) {
		SchoolName = schoolName;
	}

	public String getGradeCode() {
		return GradeCode;
	}

	public void setGradeCode(String gradeCode) {
		GradeCode = gradeCode;
	}

	public String getGradeName() {
		return GradeName;
	}

	public void setGradeName(String gradeName) {
		GradeName = gradeName;
	}

	@Override
	public String toString() {
		return "PH_Grade [SchoolName=" + SchoolName + ", GradeCode=" + GradeCode + ", GradeName=" + GradeName + "]";
	}

}
