package com.mobilesafe.app.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import com.mobilesafe.app.R;
import com.mobilesafe.app.util.LogUtil;
import com.mobilesafe.app.util.StreamUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {

	protected static final int CODE_NET_ERROR = 0;
	protected static final int CODE_JSON_ERROR = 1;
	protected static final int CODE_UPDATE_DIALOG = 2;
	protected static final int CODE_URL_ERROR = 3;
	private TextView tvVersion;
	private String mVersionName;
	private int mVersionCode;
	private String mDesc;
	private String mDownloadUrl;
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			case CODE_URL_ERROR:
				Toast.makeText(SplashActivity.this, "��ַ����!", Toast.LENGTH_LONG).show();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "�������!", Toast.LENGTH_LONG).show();
				break;
			case CODE_JSON_ERROR:
				Toast.makeText(SplashActivity.this, "��������!", Toast.LENGTH_LONG).show();
				break;

			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		tvVersion = (TextView) findViewById(R.id.tv_version);
		String versionName = getVersionName();
		tvVersion.setText(versionName);
		checkVersion();
	}
	
	/**
	 * �ӷ�������ȡ�汾��Ϣ����У��
	 */
	private void checkVersion(){
		new Thread(){

			public void run() {
				Message msg = new Message();
				String address = "http://192.168.56.1:8080/update.json";
				HttpURLConnection conn = null;
				try {
					URL url = new URL(address);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					//������û���5s
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					conn.connect();
					if(conn.getResponseCode() == 200){
						InputStream is = conn.getInputStream();
						String readFromStream = StreamUtil.readFromStream(is);
//						
						LogUtil.i("MobileSafe", "���緵��:"+readFromStream);
						
						//����json
						JSONObject jo = new JSONObject(readFromStream);
						
						mVersionName = jo.getString("versionName");
						mVersionCode = jo.getInt("versionCode");
						mDesc = jo.getString("description");
						mDownloadUrl = jo.getString("downloadUrl");
						
						LogUtil.i("MobileSafe", "�汾����:"+mDesc);
						
						if(mVersionCode > getVersionCode()){
							//server>local˵���и���,���������Ի���
							msg.what = CODE_UPDATE_DIALOG;
//							showUpdateDialog();
						}
					}
					
				} catch (MalformedURLException e) {
					// url����
					e.printStackTrace();
					msg.what = CODE_URL_ERROR;
				} catch (IOException e) {
					// �������
					e.printStackTrace();
					msg.what = CODE_NET_ERROR;
				} catch (JSONException e) {
					// json����ʧ��
					e.printStackTrace();
					msg.what = CODE_JSON_ERROR;
				} finally{
					handler.sendMessage(msg);
					if(conn != null){
						conn.disconnect();
					}
				}
			};
		}.start();
	}
	

	/**
	 * �����ĶԻ���
	 */
	private void showUpdateDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder dialog = new Builder(this);
		dialog.setTitle("���°汾:"+mVersionName);
		dialog.setMessage(mDesc);
		dialog.setPositiveButton("��������", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		dialog.setNegativeButton("�´���˵", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		dialog.show();
	}

	/**��ȡ�汾��
	 * @return
	 */
	
	private String getVersionName(){
		//ÿ�������ð�������
		//��������
		PackageManager packageManager = getPackageManager();
		try {
			//��ȡ�˳��������Ϣ
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			LogUtil.i("MobileSafe", "versionCode:"+packageInfo.versionCode+",versionName:"+packageInfo.versionName);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// û���ҵ�����ʱ�ߴ��쳣
			e.printStackTrace();
		}
		return "";
	}
	
	/**��ȡ�汾��
	 * @return
	 */
	
	private int getVersionCode(){
		//ÿ�������ð�������
		//��������
		PackageManager packageManager = getPackageManager();
		try {
			//��ȡ�˳��������Ϣ
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// û���ҵ�����ʱ�ߴ��쳣
			e.printStackTrace();
		}
		return 0;
	}
}
