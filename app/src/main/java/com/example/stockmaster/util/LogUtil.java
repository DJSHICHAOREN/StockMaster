package com.example.stockmaster.util;

import android.util.Log;

import com.example.stockmaster.BuildConfig;


/**
 * Log类，加个开关.
 * 控制只在DEBUG模式下显示Log
 */
public class LogUtil {

	public static boolean DEBUG = BuildConfig.DEBUG; //开关
	public static final String TAG = "lwd";

	public static int v(String msg) {
		return v(TAG,msg);
	}

	public static int d(String msg) {
		return d(TAG,msg);
	}

	public static int i(String msg) {
		return i(TAG,msg);
	}

	public static int w(String msg) {
		return w(TAG,msg);
	}

	public static int e(String msg) {
		return e(TAG,msg);
	}

	public static int v(String tag, String msg) {
		if(msg==null){
			return -1;
		}
		return DEBUG ? Log.v(tag, msg) : -1;
	}

	public static int d(String tag, String msg) {
		if(msg==null){
			return -1;
		}
		return DEBUG ? Log.d(tag, msg) : -1;
	}

	public static int i(String tag, String msg) {
		if(msg==null){
			return -1;
		}
		return DEBUG ? Log.i(tag, msg) : -1;
	}

	public static int w(String tag, String msg) {
		if(msg==null){
			return -1;
		}
		return DEBUG ? Log.w(tag, msg) : -1;
	}

	public static int e(String tag, String msg) {
		if(msg==null){
			return -1;
		}
		return DEBUG ? Log.e(tag, msg) : -1;
	}

	public static void setDebug(boolean isDebug){
		DEBUG = isDebug;
	}

}
