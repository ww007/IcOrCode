package com.fpl.myapp.entity;

import java.util.List;

public class First_StudentItem {

	private int totalCount; // �ܼ�¼����
	private int totalPage; // ��ҳ��
	private int pageSize; // ҳ���С
	private int pageNo; // ��ǰ�ڼ�ҳ
	private List<PH_StudentItem> result; // ����ѧ����Ŀ���
	private int startRow; // ��ʼ��
	private int endRow; // ������

	public First_StudentItem() {
		// TODO Auto-generated constructor stub
	}

	public First_StudentItem(int totalCount, int totalPage, int pageSize, int pageNo, List<PH_StudentItem> result,
			int startRow, int endRow) {
		super();
		this.totalCount = totalCount;
		this.totalPage = totalPage;
		this.pageSize = pageSize;
		this.pageNo = pageNo;
		this.result = result;
		this.startRow = startRow;
		this.endRow = endRow;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<PH_StudentItem> getResult() {
		return result;
	}

	public void setResult(List<PH_StudentItem> result) {
		this.result = result;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	@Override
	public String toString() {
		return "First_StudentItem [totalCount=" + totalCount + ", totalPage=" + totalPage + ", pageSize=" + pageSize
				+ ", pageNo=" + pageNo + ", result=" + result + ", startRow=" + startRow + ", endRow=" + endRow + "]";
	}

}
