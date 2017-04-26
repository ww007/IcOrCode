package com.fpl.myapp.activity.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fpl.myapp2.R;
import com.fpl.myapp.adapter.BasicProjectAdapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FragmentLeft extends Fragment {
	private int[] projectXuhao = { 1, 2, 3, 4, 5, 6, 7, 8 };
	private String[] projectName = { "50����", "800/1000����", "�������", "�λ���", "������Զ", "��������", "��λ��ǰ��", "��������" };
	private ArrayList<Map<String, Object>> basicList=new ArrayList<>();
	private BasicProjectAdapter basicAdapter;
	private ListView lvProjectBasic;
	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_fragment_left, container, false);
		context = getActivity();

		getBasicDate();
		lvProjectBasic = (ListView) view.findViewById(R.id.lv_project_basic);
		basicAdapter = new BasicProjectAdapter(context, basicList);
		lvProjectBasic.setAdapter(basicAdapter);
		return view;
	}

	private List<Map<String, Object>> getBasicDate() {

		for (int i = 0; i < projectXuhao.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("xuhao", projectXuhao[i]);
			map.put("name", projectName[i]);
			basicList.add(map);
		}
		return basicList;
	}
}
