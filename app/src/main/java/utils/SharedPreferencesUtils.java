package utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

	public static final String SP_NAME = "config";
	private static SharedPreferences sp;

	/**
	 * 保存字符串
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveString(Context context, String key, String value) {
		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, 0);
		}

		sp.edit().putString(key, value).commit();
	}
	public static void saveInt(Context context, String key, Integer value) {
		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, 0);
		}
		
		sp.edit().putInt(key, value).commit();
	}

	public static void saveBoolean(Context context, String key, boolean value) {
		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, 0);
		}

		sp.edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(Context context, String key,
                                     boolean defValue) {
		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, 0);
		}

		return sp.getBoolean(key, defValue);
	}
	public static int getInt(Context context, String key,
                             Integer defValue) {
		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, 0);
		}
		
		return sp.getInt(key, 0);
	}

	/**
	 * 获取字符串
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(Context context, String key, String defValue) {
		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, 0);
		}

		return sp.getString(key, defValue);
	}

}
