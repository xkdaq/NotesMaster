package cn.com.caoyue.tinynote;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;

import java.util.List;
import java.util.Map;

import cn.com.caoyue.tinynote.vest.utils.PreferencesUtil;

public class MyApplication extends Application {

    private static Context mContext;
    private static String mPackageName;
    private static MyApplication application;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        mPackageName = getPackageName();
        super.onCreate();
        final String processName = getCurrProcessName(getApplicationContext());

        if (processName == null || processName.equals(mPackageName)) {
            init();
        }
    }

    public static MyApplication getInstance() {
        if (application == null) {
            application = (MyApplication) mContext.getApplicationContext();
        }
        return application;
    }

    private void init() {
        PreferencesUtil.init(this);
        initAppsflyer();
    }


    private void initAppsflyer() {
        try {
            AppsFlyerLib.getInstance().startTracking(this, "DbyCWpY9BNde7soACQz7Zi");
            AppsFlyerLib.getInstance().registerConversionListener(this, new AppsFlyerConversionListener() {
                @Override
                public void onInstallConversionDataLoaded(Map<String, String> conversionData) {
                    for (String attrName : conversionData.keySet()) {
                        Log.e("xuke111", AppsFlyerLib.LOG_TAG + ": attribute: " + attrName + " = " + conversionData.get(attrName));
                        if ("af_status".equals(attrName)) {
                            String status = conversionData.get(attrName);
                            PreferencesUtil.getInstance().saveTabType(status);
                        }
                    }

                }

                @Override
                public void onInstallConversionFailure(String errorMessage) {
                }

                @Override
                public void onAppOpenAttribution(Map<String, String> conversionData) {
                }

                @Override
                public void onAttributionFailure(String errorMessage) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getCurrProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        if (processInfos != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                if (processInfo.pid == pid) {
                    return processInfo.processName;
                }
            }
        }
        return null;
    }

}
