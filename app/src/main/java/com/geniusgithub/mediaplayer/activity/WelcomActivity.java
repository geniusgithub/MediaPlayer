package com.geniusgithub.mediaplayer.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import com.geniusgithub.mediaplayer.AllShareApplication;
import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.BaseActivity;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.LogFactory;
import com.geniusgithub.mediaplayer.util.PermissionsUtil;

public class WelcomActivity extends BaseActivity {

	private static final CommonLog log = LogFactory.createLog();

	private Handler mHandle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setupViews();	
		initData();
	}
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	 
	
	private void setupViews(){
		setContentView(R.layout.welcome_layout);
	}
	
	private void initData(){
		mHandle = new Handler();
		boolean flag = AllShareApplication.getInstance().getEnterFlag();
		if (flag){
			goMainActivity();
		}else{
			mHandle.postDelayed(new Runnable() {
				@Override
				public void run() {
					goMainActivity();
				}
			}, 1000);
		}

	}
	


	@Override
	public void onBackPressed() {
		exit();
	}
	
	
	private void exit(){
		finish();
		System.exit(0);
	}

	private void goMainActivity(){



		if (PermissionsUtil.hasNecessaryRequiredPermissions(this)){
			Intent intent = new Intent();
			intent.setClass(this, MainFrameActivity.class);
			startActivity(intent);
			finish();
		/*	String deviceInfo = getDeviceInfo(this);
			AlwaysLog.i(WelcomActivity.class.getSimpleName(), "deviceInfo = " + deviceInfo);*/
		}else{
			requestNecessaryRequiredPermissions();
		}



	}

	private final int REQUEST_STORAGE_PERMISSION =  0X0001;
	private final int REQUEST_AUDIORECORD_PERMISSION =  0X0002;
	private final int REQUEST_PHONE_PERMISSION =  0X0003;
	private void requestNecessaryRequiredPermissions(){
		requestSpecialPermissions(PermissionsUtil.STORAGE, REQUEST_STORAGE_PERMISSION);
	}


	private void requestSpecialPermissions(String permission, int requestCode){
		String []permissions = new String[]{permission};
		requestPermissions(permissions, requestCode);
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

		switch(requestCode){
			case REQUEST_STORAGE_PERMISSION:
				doStoragePermission(grantResults);
				break;
			case REQUEST_AUDIORECORD_PERMISSION:
				doAudioRecordPermission(grantResults);
				break;
			case REQUEST_PHONE_PERMISSION:
				doPhonePermission(grantResults);
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
				break;
		}


	}

	private void doStoragePermission(int[] grantResults){
		if (grantResults[0] == PackageManager.PERMISSION_DENIED){
			log.e("doStoragePermission is denied!!!" );
			Dialog dialog = PermissionsUtil.createPermissionSettingDialog(this, "存储权限");
			dialog.show();
		}else if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
			log.i("doStoragePermission, is granted!!!" );
			requestSpecialPermissions(PermissionsUtil.MICROPHONE, REQUEST_AUDIORECORD_PERMISSION);
		}

	}

	private void doAudioRecordPermission(int[] grantResults){
		if (grantResults[0] == PackageManager.PERMISSION_DENIED){
			log.e("doAudioRecordPermission is denied!!!" );
			Dialog dialog = PermissionsUtil.createPermissionSettingDialog(this, "录音权限");
			dialog.show();
		}else if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
			log.i("doAudioRecordPermission, is granted!!!" );
			requestSpecialPermissions(PermissionsUtil.PHONE, REQUEST_PHONE_PERMISSION);
		}

	}

	private void doPhonePermission(int[] grantResults){
		if (grantResults[0] == PackageManager.PERMISSION_DENIED){
			log.e("doPhonePermission is denied!!!" );
			Dialog dialog = PermissionsUtil.createPermissionSettingDialog(this, "读电话权限");
			dialog.show();
		}else if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
			log.i("doPhonePermission, is granted!!!" );
			goMainActivity();

		}

	}


/*
	public static String getDeviceInfo(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String device_id = null;
			device_id = tm.getDeviceId();

			String mac = null;
			FileReader fstream = null;
			try {
				fstream = new FileReader("/sys/class/net/wlan0/address");
			} catch (FileNotFoundException e) {
				fstream = new FileReader("/sys/class/net/eth0/address");
			}
			BufferedReader in = null;
			if (fstream != null) {
				try {
					in = new BufferedReader(fstream, 1024);
					mac = in.readLine();
				} catch (IOException e) {
				} finally {
					if (fstream != null) {
						try {
							fstream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			json.put("mac", mac);
			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}
			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}
			json.put("device_id", device_id);
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
*/


}
