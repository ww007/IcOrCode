package com.fpl.myapp.activity;

import java.io.IOException;
import java.util.List;

import com.fpl.myapp.activity.project.BroadJumpActivity;
import com.fpl.myapp.activity.project.HeightAndWeightActivity;
import com.fpl.myapp.activity.project.InfraredBallActivity;
import com.fpl.myapp.activity.project.JumpHeightActivity;
import com.fpl.myapp.activity.project.PullUpActivity;
import com.fpl.myapp.activity.project.PushUpActivity;
import com.fpl.myapp.activity.project.RopeSkippingActivity;
import com.fpl.myapp.activity.project.Run50Activity;
import com.fpl.myapp.activity.project.Run800Activity;
import com.fpl.myapp.activity.project.RunGradeInputActivity;
import com.fpl.myapp.activity.project.ShuttleRunActivity;
import com.fpl.myapp.activity.project.ShuttlecockKickingActivity;
import com.fpl.myapp.activity.project.SitAndReachActivity;
import com.fpl.myapp.activity.project.SitUpActivity;
import com.fpl.myapp.activity.project.VisionActivity;
import com.fpl.myapp.activity.project.VitalCapacityActivity;
import com.fpl.myapp.activity.project.VolleyballActivity;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp2.R;
import com.zkc.io.LightEmGpio;
import com.zkc.zbar.CameraPreview;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class CaptureActivity extends Activity {

	private static final float BEEP_VOLUME = 0.10f;
	public static Camera mCamera;
	public static boolean isScanOpen = false;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;
	private MediaPlayer mediaPlayer;
	private boolean playBeep = true;
	ImageScanner scanner;
	private boolean previewing = true;
	public static LightEmGpio gpio;
	private static final String TAG = "CaptureActivity";
	boolean isStartScan = false;

	int time = 0;
	FrameLayout preview;
	private String codeMessage = null;
	private String classNo;
	private Class<?> mClass;
	private String title = "";
	private String title2 = "";

	static {
		System.loadLibrary("iconv");
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȥ��������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ȫ��
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.zbar_capture);

		classNo = getIntent().getStringExtra("className");
		title2 = getIntent().getStringExtra("title2");

		initView();
		preview = (FrameLayout) findViewById(R.id.cameraPreview);

		gpio = new LightEmGpio();
		isScanOpen = true;

	}

	private void initView() {
		switch (classNo) {
		case "" + Constant.HEIGHT_WEIGHT:
			mClass = HeightAndWeightActivity.class;
			break;
		case "" + Constant.VITAL_CAPACITY:
			mClass = VitalCapacityActivity.class;
			break;
		case "" + Constant.BROAD_JUMP:
			mClass = BroadJumpActivity.class;
			break;
		case "" + Constant.JUMP_HEIGHT:
			mClass = JumpHeightActivity.class;
			break;
		case "" + Constant.PUSH_UP:
			mClass = PushUpActivity.class;
			break;
		case "" + Constant.SIT_UP:
			mClass = SitUpActivity.class;
			break;
		case "" + Constant.SIT_AND_REACH:
			mClass = SitAndReachActivity.class;
			break;
		case "" + Constant.ROPE_SKIPPING:
			mClass = RopeSkippingActivity.class;
			break;
		case "" + Constant.VISION:
			mClass = VisionActivity.class;
			break;
		case "" + Constant.PULL_UP:
			mClass = PullUpActivity.class;
			break;
		case "" + Constant.INFRARED_BALL:
			mClass = InfraredBallActivity.class;
			break;
		case "" + Constant.MIDDLE_RACE:
			mClass = RunGradeInputActivity.class;
			title = "800/1000����";
			break;
		case "" + Constant.VOLLEYBALL:
			mClass = VolleyballActivity.class;
			break;
		case "" + Constant.BASKETBALL_SKILL:
			break;
		case "" + Constant.SHUTTLE_RUN:
			mClass = RunGradeInputActivity.class;
			title = "50��x8������";
			break;
		case "" + Constant.WALKING1500:
			break;
		case "" + Constant.WALKING2000:
			break;
		case "" + Constant.RUN50:
			mClass = RunGradeInputActivity.class;
			title = "50����";
			break;
		case "" + Constant.FOOTBALL_SKILL:
			break;
		case "" + Constant.KICKING_SHUTTLECOCK:
			mClass = ShuttlecockKickingActivity.class;
			break;
		case Constant.runGradeInput:
			mClass = RunGradeInputActivity.class;
			break;
		case "" + Constant.SWIM:
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		isStartScan = true;
		try {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			autoFocusHandler = new Handler();

			/* Instance barcode scanner */
			scanner = new ImageScanner();
			scanner.setConfig(0, Config.X_DENSITY, 3);
			scanner.setConfig(0, Config.Y_DENSITY, 3);
			preview.removeAllViews();

			mCamera = getCameraInstance();

			mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);

			preview.addView(mPreview);

			mCamera.setPreviewCallback(previewCb);

			mCamera.startPreview();
			previewing = true;
			mCamera.autoFocus(autoFocusCB);

			// ��ʼ������
			AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);

			// ��ʼ������
			initBeepSound();
			// Ĭ���ֶ����ò���
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}

	/**
	 * 10��رհ׵��߳�
	 * 
	 * @author zkc-soft2
	 * 
	 */
	class TimeThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			time = 0;

			while (time++ < 10000) {
				if (codeMessage != null) {

					break;
				} else if (time == 9999) {
					// �رհ׵�
					try {
						gpio.RedLightPower(false);
						isExposure = false;
					} catch (Exception e) {
						// TODO: handle exception
					}
					break;
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == 135 || keyCode == 136 || keyCode == 66) {
			codeMessage = null;
			gpio.GreenLightPower(false);
			if (time > 0) {
				// �ر�ʱ������߳�
				time = 10000;
			}
			// ����ʱ���������10������ɨ�赽������Ϣ���رհ׵�
			TimeThread timeThread = new TimeThread();
			timeThread.start();

			isStartScan = true;// �������ɨ��
		}
		return super.onKeyUp(keyCode, event);
	}

	boolean isExposure = true;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 66 || keyCode == 135 || keyCode == 136) {
			gpio.RedLightPower(true);
			gpio.GreenLightPower(true);
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onPause() {
		releaseCamera();
		gpio.RedLightPower(false);
		isScanOpen = false;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		gpio.RedLightPower(false);
		isScanOpen = false;
		super.onDestroy();
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
		} catch (Exception e) {
			c = Camera.open(CameraInfo.CAMERA_FACING_BACK);
		}
		return c;
	}

	private void releaseCamera() {
		if (mCamera != null) {
			isStartScan = false;
			previewing = false;
		}
	}

	// �Զ��Խ�
	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getPreviewSize();
			if (isStartScan) {
				Image barcode = new Image(size.width, size.height, "Y800");
				barcode.setData(data);

				int result = scanner.scanImage(barcode);

				if (result != 0) {

					previewing = false;
					// ��ȡɨ������
					SymbolSet syms = scanner.getResults();
					for (Symbol sym : syms) {
						codeMessage = sym.getData();
						if (codeMessage != "") {
							isStartScan = false;
							gpio.RedLightPower(false);
							// Toast.makeText(getApplicationContext(),
							// codeMessage, 300).show();
							/*
							 * Intent intentBroadcast = new Intent();
							 * intentBroadcast.setAction("com.zkc.scancode");
							 * intentBroadcast.putExtra("code", codeMessage);
							 * sendBroadcast(intentBroadcast);
							 */
							playBeepSoundAndVibrate();// �����������񶯴���ɹ���ȡ��ά��
							Intent mIntent = new Intent(CaptureActivity.this, mClass);
							mIntent.putExtra("data", codeMessage);
							mIntent.putExtra("title", title);
							mIntent.putExtra("title2", title2);
							startActivity(mIntent);
							CaptureActivity.this.finish();
							break;
						}
					}
				}
			}
		}
	};

	/**
	 * �Զ��Խ��ص�
	 */
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

	/**
	 * ��ʼ������
	 */
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);
			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	/**
	 * ��������
	 */
	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	/**
	 * Convert char to byte
	 * 
	 * @param char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static int[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		int[] d = new int[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (int) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/*
	 * �������
	 */
	public static void turnLightOn(Camera mCamera) {
		if (mCamera == null) {
			return;
		}
		Parameters parameters = mCamera.getParameters();
		if (parameters == null) {
			return;
		}
		List<String> flashModes = parameters.getSupportedFlashModes();
		// Check if camera flash exists
		if (flashModes == null) {
			// Use the screen as a flashlight (next best thing)
			return;
		}
		String flashMode = parameters.getFlashMode();
		if (!Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
			// Turn on the flash
			if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
				parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
				mCamera.setParameters(parameters);
			} else {

			}
		}
	}

	/*
	 * �ر������
	 */
	public static void turnLightOff(Camera mCamera) {
		if (mCamera == null) {
			return;
		}
		Parameters parameters = mCamera.getParameters();
		if (parameters == null) {
			return;
		}
		List<String> flashModes = parameters.getSupportedFlashModes();
		String flashMode = parameters.getFlashMode();
		// Check if camera flash exists
		if (flashModes == null) {
			return;
		}
		if (!Parameters.FLASH_MODE_OFF.equals(flashMode)) {
			// Turn off the flash
			if (flashModes.contains(Parameters.FLASH_MODE_OFF)) {
				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(parameters);
			} else {
				Log.e(TAG, "FLASH_MODE_OFF not supported");
			}
		}
	}
}
