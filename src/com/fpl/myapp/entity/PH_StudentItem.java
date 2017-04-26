package com.fpl.myapp.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PH_StudentItem implements Serializable{
	private String studentCode; // ѧ��
	private int resultState;// �ɼ�״̬
	private int lastResult;// ���ճɼ�
	private String lastTestTime;// ���ճɼ�����ʱ��
	private String itemCode; // ��Ŀ����

	public PH_StudentItem() {
		// TODO Auto-generated constructor stub
	}

	public PH_StudentItem(String studentCode, int resultState, int lastResult, String lastTestTime, String itemCode) {
		super();
		this.studentCode = studentCode;
		this.resultState = resultState;
		this.lastResult = lastResult;
		this.lastTestTime = lastTestTime;
		this.itemCode = itemCode;
	}

	public int getResultState() {
		return resultState;
	}

	public void setResultState(int resultState) {
		this.resultState = resultState;
	}

	public int getLastResult() {
		return lastResult;
	}

	public void setLastResult(int lastResult) {
		this.lastResult = lastResult;
	}

	public String getLastTestTime() {
		return lastTestTime;
	}

	public void setLastTestTime(String lastTestTime) {
		this.lastTestTime = lastTestTime;
	}

	public String getStudentCode() {
		return studentCode;
	}

	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	@Override
	public String toString() {
		return "PH_StudentItem [studentCode=" + studentCode + ", resultState=" + resultState + ", lastResult="
				+ lastResult + ", lastTestTime=" + lastTestTime + ", itemCode=" + itemCode + "]";
	}

}
