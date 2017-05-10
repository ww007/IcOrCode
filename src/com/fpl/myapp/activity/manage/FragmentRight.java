package com.fpl.myapp.activity.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.fpl.myapp2.R;
import com.fpl.myapp.adapter.ProjectAdapter;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp.util.HttpCallbackListener;
import com.fpl.myapp.util.HttpUtil;
import com.fpl.myapp.util.NetUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import ww.greendao.dao.Item;

public class FragmentRight extends Fragment {
	private Context context;
	private SharedPreferences sharedPreferences;
	private String[] projectName = { "50����", "800/1000����", "�������", "�λ���", "������Զ", "��������", "��λ��ǰ��", "��������" };
	private ArrayList<Map<String, Object>> dataList = new ArrayList<>();
	private ArrayList<String> strings = new ArrayList<>();
	private ArrayList<String> nameData = new ArrayList<>();
	private ArrayList<String> names = new ArrayList<>();
	private ArrayList<String> names1 = new ArrayList<>();
	private List<Item> itemList = new ArrayList<>();
	private ArrayList<String> projects = new ArrayList<>();
	private List<Item> newList;
	private ListView lvProject;
	private ProjectAdapter adapter;
	private Button btn;

	Handler mHandler = new Handler();
	Runnable updateItem = new Runnable() {
		@Override
		public void run() {
			getItems();
		}
	};
	Runnable getStudent = new Runnable() {
		@Override
		public void run() {
			HttpUtil.getStudentInfo(context);
		}
	};
	// Runnable getStudentItem = new Runnable() {
	// @Override
	// public void run() {
	// HttpUtil.getStudentItemInfo(context);
	// }
	// };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_fragment_right, container, false);
		context = getActivity();

		// ��ȡ�����ڱ��ص�ѡ����Ŀ
		sharedPreferences = context.getSharedPreferences("projects", Activity.MODE_PRIVATE);
		int selected = sharedPreferences.getInt("size", 0);
		for (int i = 0; i < selected; i++) {
			strings.add(sharedPreferences.getString(i + "", ""));
			Log.i("strings=", strings.toString());
		}

		newList = DbService.getInstance(context).loadAllItem();
		for (int i = 0; i < newList.size(); i++) {
			names1.add(newList.get(i).getItemName());
		}

		initView(view);
		setListener();

		// getItems();
		return view;
	}

	private void getItems() {
		sendItemRequest();
		// �жϻ�ȡ����Ŀ�Ƿ����
		if (nameData.size() == names.size()) {
			for (int i = 0; i < nameData.size(); i++) {
				if (nameData.get(i).toString().equals(names.get(i).toString())) {
					NetUtil.showToast(context, "��Ϊ������Ŀ");
				} else {
					Log.i("����", "------------");
					dataList = getNewDate(names);
					showList();
				}
			}

		} else {
			Log.i("����", "------------");
			dataList = getNewDate(names);
			showList();
		}
	}

	private CheckBox cb;

	private void setListener() {
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isWifiConnected(NetUtil.netState(context));
			}
		});

		lvProject.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				cb = (CheckBox) view.findViewById(R.id.cb_project);
				cb.toggle();
				ProjectAdapter.getIsSelected().put(position, cb.isChecked());
			}

		});
	}

	private void initView(View view) {
		lvProject = (ListView) view.findViewById(R.id.lv_project);
		btn = (Button) view.findViewById(R.id.btn_getInfo);

		removeName(names1);
		dataList = getNewDate(names1);
		showList();

	}

	private void showList() {

		Log.i("dataList=", dataList.toString());
		adapter = new ProjectAdapter(context, dataList);
		lvProject.setAdapter(adapter);
		for (int i = 0; i < dataList.size(); i++) {
			for (int j = 0; j < strings.size(); j++) {
				if (dataList.get(i).get("name").equals(strings.get(j))) {
					ProjectAdapter.getIsSelected().put(i, true);
				}
			}
		}
	}

	/**
	 * ���ݷ���ֵ�жϽ������Ĳ���
	 * 
	 * @param result
	 */
	protected void isWifiConnected(boolean result) {

		if (true == result) {
			mHandler.post(updateItem);
			if (DbService.getInstance(context).loadAllStudent().isEmpty()) {
				mHandler.post(getStudent);
			}
		} else {
			NetUtil.checkNetwork(getActivity());
		}
	}

	/**
	 * ��ȡ������Ŀ��Ϣ
	 */
	private void sendItemRequest() {

		try {
			HttpUtil.sendOkhttp(Constant.ITEM_URL, null, new HttpCallbackListener() {

				@Override
				public void onFinish(String response) {
					names.clear();
					// ������ȡ��Json����
					itemList = JSON.parseArray(response, Item.class);
					Log.i("item--->", itemList.get(0).getItemName());
					// ��ȡ��Ŀ���ּ���
					for (int j = 0; j < itemList.size(); j++) {
						names.add(itemList.get(j).getItemName());
					}
					if (DbService.getInstance(context).loadAllItem().isEmpty()) {
						DbService.getInstance(context).saveItemLists(itemList);
						Log.i("success", "������Ŀ��Ϣ�ɹ�");
					}
					removeName(names);
					Log.i("2.names=", names + "");
					nameData.clear();
					for (int i = 0; i < dataList.size(); i++) {
						nameData.add(dataList.get(i).get("name").toString());
						Log.i("nameData=", nameData + "");
					}
				}

				@Override
				public void onError(Exception e) {
					Log.i("error", "��������ʧ��");
					NetUtil.showToast(context, "��������ʧ��");
				}
			});
		} catch (Exception e) {
			NetUtil.showToast(context, "���ӷ������쳣");
		}
	}

	private ArrayList<Map<String, Object>> getNewDate(List<String> strings) {
		dataList.clear();
		for (int i = 0; i < strings.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("xuhao", i + 9);
			map.put("name", strings.get(i));
			dataList.add(map);
		}
		return dataList;
	}

	/**
	 * �Ƴ���Ŀ�����л�����Ŀ
	 * 
	 * @param stringList
	 */
	private void removeName(ArrayList<String> stringList) {
		for (int i = 0; i < projectName.length; i++) {
			stringList.remove(projectName[i]);
		}
		for (Iterator<String> it = stringList.iterator(); it.hasNext();) {
			String item = it.next();
			if (item.equals("���") || item.equals("����") || item.equals("һ������������") || item.equals("1000����")
					|| item.equals("800����")) {
				it.remove();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("--", "resume");
		getItems();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// �˳�ǰ��ȡѡ����Ŀ
		for (int i = 0; i < dataList.size(); i++) {
			if (ProjectAdapter.getIsSelected().get(i)) {
				projects.add(dataList.get(i).get("name").toString());
			}
		}
		// SharedPreferences����ѡ�е���Ŀ��Ϣ
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("size", projects.size());
		for (int i = 0; i < projects.size(); i++) {
			editor.putString(i + "", projects.get(i));
		}
		editor.commit();
		Log.i("���ݳɹ�д��SharedPreferences��", editor + "");
	}
}
