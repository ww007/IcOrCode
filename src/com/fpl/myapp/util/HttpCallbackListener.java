package com.fpl.myapp.util;

/**
 * http����ص��ӿ�
 * 
 * @author ww
 *
 */
public interface HttpCallbackListener {
	// ��������ɹ�
	void onFinish(String response);

	// ��������ʧ��
	void onError(Exception e);
}
