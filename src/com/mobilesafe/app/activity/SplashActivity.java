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
				Toast.makeText(SplashActivity.this, "网址错误!", Toast.LENGTH_LONG).show();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "网络错误!", Toast.LENGTH_LONG).show();
				break;
			case CODE_JSON_ERROR:
				Toast.makeText(SplashActivity.this, "解析错误!", Toast.LENGTH_LONG).show();
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
	 * 从服务器获取版本信息进行校验
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
					//最多让用户等5s
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					conn.connect();
					if(conn.getResponseCode() == 200){
						InputStream is = conn.getInputStream();
						String readFromStream = StreamUtil.readFromStream(is);
//						
						LogUtil.i("MobileSafe", "网络返回:"+readFromStream);
						
						//解析json
						JSONObject jo = new JSONObject(readFromStream);
						
						mVersionName = jo.getString("versionName");
						mVersionCode = jo.getInt("versionCode");
						mDesc = jo.getString("description");
						mDownloadUrl = jo.getString("downloadUrl");
						
						LogUtil.i("MobileSafe", "版本描述:"+mDesc);
						
						if(mVersionCode > getVersionCode()){
							//server>local说明有更新,弹出升级对话框
							msg.what = CODE_UPDATE_DIALOG;
//							showUpdateDialog();
						}
					}
					
				} catch (MalformedURLException e) {
					// url错误
					e.printStackTrace();
					msg.what = CODE_URL_ERROR;
				} catch (IOException e) {
					// 网络错误
					e.printStackTrace();
					msg.what = CODE_NET_ERROR;
				} catch (JSONException e) {
					// json解析失败
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
	 * 升级的对话框
	 */
	private void showUpdateDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder dialog = new Builder(this);
		dialog.setTitle("最新版本:"+mVersionName);
		dialog.setMessage(mDesc);
		dialog.setPositiveButton("立即更新", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		dialog.setNegativeButton("下次再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		dialog.show();
	}

	/**获取版本名
	 * @return
	 */
	
	private String getVersionName(){
		//每个程序都用包来管理
		//包管理器
		PackageManager packageManager = getPackageManager();
		try {
			//获取此程序包的信息
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			LogUtil.i("MobileSafe", "versionCode:"+packageInfo.versionCode+",versionName:"+packageInfo.versionName);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// 没有找到包名时走此异常
			e.printStackTrace();
		}
		return "";
	}
	
	/**获取版本号
	 * @return
	 */
	
	private int getVersionCode(){
		//每个程序都用包来管理
		//包管理器
		PackageManager packageManager = getPackageManager();
		try {
			//获取此程序包的信息
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// 没有找到包名时走此异常
			e.printStackTrace();
		}
		return 0;
	}
}
