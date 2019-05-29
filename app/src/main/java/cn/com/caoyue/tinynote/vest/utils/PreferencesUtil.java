package cn.com.caoyue.tinynote.vest.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtil {

    static private PreferencesUtil instance = null;

    static private String PREFERENCE_NAME = "xname";

    private SharedPreferences pref = null;
    private SharedPreferences.Editor editor = null;

    static public PreferencesUtil getInstance() {
        if (instance == null) {
            synchronized (PreferencesUtil.class) {
                if (instance == null) {
                    instance = new PreferencesUtil();
                }
            }
        }
        return instance;
    }

    public static void init(Context context) {
        instance = new PreferencesUtil();
        instance.pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        instance.editor = instance.pref.edit();
    }


    private void setStringValue(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    private String getStringValue(String key) {
        return getStringValue(key, "");
    }

    private String getStringValue(String key, String defaultStr) {
        return pref.getString(key, defaultStr);
    }

    /**
     * 保存tab类型
     */
    public void saveTabType(String status) {
        editor.putString("tab_type", status);
        editor.commit();
    }

    /**
     * 获取tab类型
     */
    public String getTabType() {
        return pref.getString("tab_type", "");
    }
}
