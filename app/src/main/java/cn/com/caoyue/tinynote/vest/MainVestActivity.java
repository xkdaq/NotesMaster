package cn.com.caoyue.tinynote.vest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.caoyue.tinynote.BuildConfig;
import cn.com.caoyue.tinynote.vest.manager.CameraActivity;
import cn.com.caoyue.tinynote.vest.manager.FaceDetectActivity;
import cn.com.caoyue.tinynote.vest.manager.Managers;
import cn.com.caoyue.tinynote.vest.utils.CommonUtils;
import cn.com.caoyue.tinynote.vest.utils.CompareHelper;
import cn.com.caoyue.tinynote.vest.utils.Constants;
import cn.com.caoyue.tinynote.vest.utils.DeviceInfoUtils;
import cn.com.caoyue.tinynote.vest.utils.OssService;

public class MainVestActivity extends AppCompatActivity {

    private static final int REQ_PERMS_CODE = 100;
    private static final int REQ_CONTACT = 101;
    private CoreWebView mWebView;
    private ConditionVariable mConditionVariable = new ConditionVariable();
    private boolean mPermissionsGranted = false;
    private File mCameraFile;
    private OssService mOssService;
    private CompareHelper mCompareHelper;
    private boolean mPhotoTaken = false;
    private String mCompareObjKey;
    private double mCompareTh;
    private String mAliveResultPath;
    private double mConfidence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQ_PERMS_CODE);
        initViews();
        initData();
        setContentView(mWebView);
        start();
//        test();

        mCameraFile = new File(getCacheDir(), "IMAGE_FILE.jpg");
        mOssService = OssService.initOSS(this, CommonUtils.decodeStr(Constants.OSS_END_POINT), CommonUtils.decodeStr(Constants.OSS_BUCKET));
        mCompareHelper = new CompareHelper(this);
    }

    private void initViews() {
        mWebView = new CoreWebView(this);
        mWebView.addJavascriptInterface(new JsObject(), "Android");
    }

    private void initData() {
        Managers.getInstance().init(getApplicationContext());
        // Init Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);

        // Init AppsFlyer
        //AppsFlyerLib.getInstance().startTracking(getApplication(), decodeStr("RGJ5Q1dwWTlCTmRlN3NvQUNRejdaaQ=="));  // DbyCWpY9BNde7soACQz7Zi
    }

    private void start() {
        //String format = "http%s://%sshare.okex.id/tangan_zeus";
        String format = CommonUtils.decodeStr(Constants.START_URL_FORMAT);
        String url;
        if (BuildConfig.DEBUG) {
            url = String.format(format, "", "test");
        } else {
            url = String.format(format, "s", "");
        }
        //url = String.format(format, "", "test");
        Log.e("xuke", "url=" + url);
        mWebView.loadUrl(url);
    }

    private void test() {
//        mWebView.loadData("", "text/html", null);
//        mWebView.loadUrl("javascript:alert(Android.getDeviceInfo())");
        mWebView.loadUrl("http://192.168.1.21/WebVest/index.html");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String link = "market://details?id=id.danarupiah.weshare.jiekuan&referrer=af_tranid%3DsHNipoE5hT6XsNx8N9Wo_Q%26pid%3Dcashcash_int%26c%3DCashCash_1070228004%26af_click_lookback%3D7d%26clickid%3Dd9e2208b21cac6692a6316474ed47a31%26android_id%3D21b0ee826f6332f4%26advertising_id%3D2dd4fc95-f675-4294-89ec-46ab25f2caa0%26imei%3D868735039268307%26af_siteid%3D7ced3a25b3bb54dd";
//                openMarketView(MainVestActivity.this, link);
            }
        }, 3000);
    }

    class JsObject {
        @JavascriptInterface
        public String getDeviceInfo(String types, int offset, int length) {
            if (TextUtils.equals(types, "basic")) {
                return DeviceInfoUtils.getBasic(getApplicationContext()).toString();
            }
            return DeviceInfoUtils.getDeviceInfo(getApplicationContext(), CommonUtils.split(types, ";"), true);
        }

        @JavascriptInterface
        public String getPackageName() {
            return "com.fs.webvest";
        }

        @JavascriptInterface
        public String get(String key) {
            return getPref().getString(key, "");
        }

        @JavascriptInterface
        public void set(String key, String value) {
            getPref().edit().putString(key, value).commit();
        }

        @JavascriptInterface
        public void event(String event) {
            trackEvent(event);
        }


        @JavascriptInterface
        public int takePhoto(int type) {
            if (!CommonUtils.checkPermission(getApplicationContext(), Manifest.permission.CAMERA))
                return -1;

            switch (type) {
                case Constants.JS_ID_FRONT:
                    CameraActivity.captureForResult(MainVestActivity.this, mCameraFile, CameraActivity.TYPE_IDCARD_FRONT, type);
                    break;
                case Constants.JS_ID_BACK:
                    CameraActivity.captureForResult(MainVestActivity.this, mCameraFile, CameraActivity.TYPE_IDCARD_BACK, type);
                    break;
                case Constants.JS_CAMERA:
                    CameraActivity.captureForResult(MainVestActivity.this, mCameraFile, CameraActivity.TYPE_DEFAULT, type);
                    break;
                default:
                    return -2;
            }
            mConditionVariable.block();
            mConditionVariable.close();
            return mPhotoTaken ? 0 : -3;
        }

        @JavascriptInterface
        public int chooseContact() {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, REQ_CONTACT);
            return 0;
        }

        @JavascriptInterface
        public int detectAlive(int type, String json) {
            if (!CommonUtils.checkPermission(getApplicationContext(), Manifest.permission.CAMERA))
                return -1;

            switch (type) {
                case Constants.JS_LIVE_PINAN:
                    startActivityForResult(new Intent(MainVestActivity.this, FaceDetectActivity.class), type);
                    break;
                case Constants.JS_LIVE_PINAN_FACE_COMPARE:
                    JSONObject jsonObject = CommonUtils.getJsonObject(json);
                    try {
                        mCompareObjKey = jsonObject.getString("image_url").substring(CommonUtils.decodeStr(Constants.IMAGE_FIX).length());
                        mCompareTh = jsonObject.getDouble("threshold");
                        startActivityForResult(new Intent(MainVestActivity.this, FaceDetectActivity.class), type);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return 0;
        }

    }

    public SharedPreferences getPref() {
        return getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
    }

    public void trackEvent(String value) {
        // Facebook
        AppEventsLogger.newLogger(getApplicationContext()).logEvent(value);

        // Appsflyer
        Map<String, Object> eventValue = new HashMap<String, Object>();
        eventValue.put(AFInAppEventParameterName.SUCCESS, value);
        AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), value, eventValue);
    }

    @JavascriptInterface
    public int requestPermissions(String permissions) {
        List<String> perms = CommonUtils.split(permissions, ";");
        ActivityCompat.requestPermissions(MainVestActivity.this, perms.toArray(new String[perms.size()]), REQ_PERMS_CODE);
        mConditionVariable.block(10000);
        mConditionVariable.close();
        return mPermissionsGranted ? 0 : -1;
    }


    public static String getMapToString(Map<String, Object> map) {
        try {
            return new JSONObject(map).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String decodeStr(String encodeStr) {
        byte[] decodeStr = Base64.decode(encodeStr.getBytes(), Base64.DEFAULT);
        return new String(decodeStr);
    }


    private void upload(File file, final int requestCode) {
        if (!file.exists() || file.length() == 0)
            return;

        String objName = Constants.OSS_DIRNAME + "loan-" + CommonUtils.genUniqueID(DeviceInfoUtils.getAndroidID(getApplicationContext())) + "." +
                CommonUtils.getSuffix(file.getName());
        mOssService.asyncPutImage(this, objName, file.getAbsolutePath(), new OssService.OnProfileCallback() {
            @Override
            public void onSuccess(String objectKey) {
                Map<String, Object> map = new HashMap<>();
                map.put("image_path", CommonUtils.decodeStr(Constants.IMAGE_FIX) + objectKey);
                switch (requestCode) {
                    case Constants.JS_ID_FRONT:
                        CommonUtils.sendEvaluateJavascript(mWebView, "GetTakePhotoUrl", String.valueOf(requestCode), CommonUtils.getMapToString(map));
                        break;
                    case Constants.JS_ID_BACK:
                        CommonUtils.sendEvaluateJavascript(mWebView, "GetTakePhotoUrl", String.valueOf(requestCode), CommonUtils.getMapToString(map));
                        break;
                    case Constants.JS_CAMERA:
                        CommonUtils.sendEvaluateJavascript(mWebView, "GetTakePhotoUrl", String.valueOf(requestCode), CommonUtils.getMapToString(map));
                        break;
                    case Constants.JS_LIVE_PINAN:
                    case Constants.JS_LIVE_PINAN_FACE_COMPARE:
                        map.put("code", 0);
                        map.put("confidence", mConfidence);
                        CommonUtils.sendEvaluateJavascript(mWebView, "GetlivenessImg", String.valueOf(requestCode), CommonUtils.getMapToString(map));
                        break;
                }
            }

            @Override
            public void onFailure(String msg) {
                Log.e("onFailure", msg);
            }
        });
    }

    private void download(String objectKey, final int requestCode) {
        mOssService.downLoad(this, "photo.jpg", CommonUtils.decodeStr(Constants.OSS_BUCKET), objectKey, new OssService.OnDownLoadCallback() {
            @Override
            public void onSuccess(String objectKey) {
                String downLoadFilePath = getFilesDir() + "/photo.jpg";
                mCompareHelper.compare(mAliveResultPath, downLoadFilePath, new CompareHelper.OnComparisonListener() {
                    @Override
                    public void onComparisonSuccess(double similarity, double refTh) {
                        if (similarity < (mCompareTh > 0 ? mCompareTh : refTh)) {
                            compareFailed(requestCode, 1);
                        } else {
                            mConfidence = similarity;
                            upload(new File(mAliveResultPath), requestCode);
                        }
                    }

                    @Override
                    public void onComparisonFailed(int code, String msg) {
                        compareFailed(requestCode, -1);
                    }
                });
            }

            @Override
            public void onProgress(long currentSize, long totalSize) {
                int progress = (int) (100 * currentSize / totalSize);
                Log.e("onProgress", "progress:" + progress);
            }

            @Override
            public void onFailure(String msg) {
                Log.e("onFailure", msg);
            }
        });
    }

    private void compareFailed(int requestCode, int code) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        CommonUtils.sendEvaluateJavascript(mWebView, "GetlivenessImg", String.valueOf(requestCode), CommonUtils.getMapToString(map));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_PERMS_CODE) {
            mPermissionsGranted = true;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    mPermissionsGranted = false;
                    break;
                }
            }
            mConditionVariable.open();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.JS_ID_FRONT || requestCode == Constants.JS_ID_BACK || requestCode == Constants.JS_CAMERA) {
            if (resultCode != Activity.RESULT_OK || !mCameraFile.exists() || mCameraFile.length() == 0) {
                mPhotoTaken = false;
                mConditionVariable.open();
                return;
            }
            mPhotoTaken = true;
            mConditionVariable.open();
            upload(mCameraFile, requestCode);
        } else if (requestCode == REQ_CONTACT) {
            if (resultCode != Activity.RESULT_OK || data == null)
                return;

            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
            Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
            if (cursor == null)
                return;

            if (cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberIndex);

                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameIndex);

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(number)) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("user_name", name);
                    map.put("user_number", number);
                    CommonUtils.sendEvaluateJavascript(mWebView, "GetDefaultPhone", CommonUtils.getMapToString(map));
                }
            }
            cursor.close();
        } else if (requestCode == Constants.JS_LIVE_PINAN || requestCode == Constants.JS_LIVE_PINAN_FACE_COMPARE) {
            if (resultCode != Activity.RESULT_OK)
                return;

            Bundle bundle = data.getExtras();
            mAliveResultPath = bundle.getString(Constants.LIVENESS_FILEPATH);
            if (requestCode == Constants.JS_LIVE_PINAN) {
                upload(new File(mAliveResultPath), requestCode);
            } else if (requestCode == Constants.JS_LIVE_PINAN_FACE_COMPARE) {
                download(mCompareObjKey, requestCode);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }


}
