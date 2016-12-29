package com.mobilesafe.app.util;

import android.util.Log;

public class LogUtil {
	private static final int VERBOSE = 0;
	private static final int DEBUG = 1;
	private static final int INFO = 2;
	private static final int WARN = 3;
	private static final int ERROR = 4;
	/**
	 * ����ʱ��LEVLEL��Ϊ5,���е�log������ӡ��
	 */
	private static final int LEVEL = VERBOSE;
	
	public static void v(String tag,String msg){
		if(LEVEL <= VERBOSE){
			Log.v(tag, msg);
		}
	}
	
	public static void d(String tag,String msg){
		if(LEVEL <= DEBUG){
			Log.d(tag, msg);
		}
	}

	public static void i(String tag,String msg){
		if(LEVEL <= INFO){
			Log.i(tag, msg);
		}
	}
	
	public static void w(String tag,String msg){
		if(LEVEL <= WARN){
			Log.w(tag, msg);
		}
	}
	
	public static void e(String tag,String msg){
		if(LEVEL <= ERROR){
			Log.e(tag, msg);
		}
	}
	
}
