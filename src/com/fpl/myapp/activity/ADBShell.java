package com.fpl.myapp.activity;

import java.io.OutputStream;

/**
 * adb������root��δ�ã�
 * @author ww
 *
 */
public class ADBShell {
	private OutputStream os;

	/**
	 * <b>���������� </b>
	 * <dd>�������ã� ִ��adb����
	 * ע�⣬Runtime.getRuntime().exec("su").getOutputStream();
	 * 
	 * @param cmd
	 *            �����������
	 * @since Met 1.0
	 * @see
	 */
	public final void execute(String cmd) {
		try {
			if (os == null) {
				os = Runtime.getRuntime().exec("su").getOutputStream();
			}
			os.write(cmd.getBytes());
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <b>����������ģ�ⰴ���������� </b>
	 * @param keyCode
	 *            ��Ӧ�İ�������
	 * @since Met 1.0
	 * @see
	 */
	public final void simulateKey(int keyCode) {
		execute("input keyevent --longpress " + keyCode + "\n");
	}
}
