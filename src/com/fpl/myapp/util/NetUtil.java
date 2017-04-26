package com.fpl.myapp.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import com.fpl.myapp2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * �ж��������Ӱ�����
 * 
 * @author ww
 *
 */
public class NetUtil {
	/**
	 * ����Wifi��Ϣ��ȡ����Mac
	 * 
	 * @param context
	 * @return
	 */
	public static String getLocalMacAddressFromWifiInfo(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}
	
	public static String getLocalMacAddress() {
		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// ȥ�ո�
					break;
				}
			}
		} catch (IOException ex) {
			// ����Ĭ��ֵ
			ex.printStackTrace();
		}
		return macSerial;
	}

	/**
	 * �ж��������
	 * 
	 * @param context
	 *            ������
	 * @return false ��ʾû������ true ��ʾ������
	 */
	public static boolean isNetworkAvalible(Context context) {
		// �������״̬������
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
		if (activeNetwork != null) { // connected to the internet
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
				// connected to wifi
			} else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
				// connected to the mobile provider's data plan
			}
			return true;
		} else {
			// not connected to the internet
			return false;
		}
	}

	/**
	 * ���û�����磬�򵯳��������öԻ���
	 * 
	 * @param activity
	 */
	public static void checkNetwork(final Activity activity) {
		if (!NetUtil.isNetworkAvalible(activity)) {
			TextView msg = new TextView(activity);
			msg.setText("��ǰû�п���ʹ�õ����磬���������磡");
			msg.setTextSize(18);
			new AlertDialog.Builder(activity).setIcon(R.drawable.super_tip).setTitle("����״̬��ʾ").setView(msg)
					.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whichButton) {
							// ��ת�����ý���
							if (android.os.Build.VERSION.SDK_INT > 10) {
								activity.startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
							} else {
								activity.startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), 0);
							}

						}
					}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					}).create().show();
		}
		return;
	}

	/**
	 * �ж������Ƿ�����
	 **/
	public static boolean netState(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// ��ȡ��������״̬��NetWorkInfo����
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		// ��ȡ��ǰ�����������Ƿ����
		boolean available = false;
		try {
			available = networkInfo.isAvailable();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (available) {
			Log.i("֪ͨ", "��ǰ���������ӿ���");
			return true;
		} else {
			Log.i("֪ͨ", "��ǰ���������ӿ���");
			return false;
		}
	}
	
	private static Toast toast;

    public static void showToast(Context context, 
        String content) {
        if (toast == null) {
            toast = Toast.makeText(context,
                         content, 
                         Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
