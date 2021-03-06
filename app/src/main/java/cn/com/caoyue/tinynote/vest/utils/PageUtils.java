package cn.com.caoyue.tinynote.vest.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class PageUtils {

    /**
     * Base64加密
     */
    public static String base64(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    /**
     * Base64解密
     */
    public static String decodeStr(String encodeStr) {
        return new String(Base64.decode(encodeStr.getBytes(), Base64.NO_WRAP));
    }


    /**
     * 打开gooleplay
     */
    public static void openMarketView(Context context, String link) {
        assert (context != null);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(link);
        intent.setData(content_url);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        boolean flag = isLocalApp(context, "com.android.vending");
        if (flag) {
            intent.setPackage("com.android.vending");
        }
        context.startActivity(intent);
    }


    /**
     * 判断是否是本地的app
     */
    private static boolean isLocalApp(Context context, String packageName) {
        assert (context != null);
        if (TextUtils.isEmpty(packageName))
            return false;

        boolean flag = false;
        try {
            if (context.getPackageManager().getLaunchIntentForPackage(packageName) != null) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 获取缓存目录
     */
    public static File getCacheDir(Context context) {
        File cache;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cache = context.getExternalCacheDir();
        } else {
            cache = context.getCacheDir();
        }
        if (!cache.exists())
            cache.mkdirs();
        return cache;
    }

    /**
     * json转jsonobjet
     */
    public static JSONObject getJsonObject(String json) {
        JSONObject result = new JSONObject();
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * map转string
     */
    public static String getMapToString(Map<String, Object> map) {
        try {
            return new JSONObject(map).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 解压zip
     */
    public static byte[] gzip(String unGzipStr) {
        if (TextUtils.isEmpty(unGzipStr))
            return null;

        ByteArrayOutputStream baos = null;
        GZIPOutputStream gzip = null;
        try {
            baos = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(baos);
            gzip.write(unGzipStr.getBytes());
            gzip.close();
            byte[] encode = baos.toByteArray();
            baos.flush();
            baos.close();
            return encode;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(baos);
            closeSilently(gzip);
        }
        return null;
    }

    /**
     * 关流
     */
    public static void closeSilently(Closeable closeable) {
        if (closeable != null)
            return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isPhoneStateGranted(Context context) {
        return checkPermission(context, decodeStr(Constants.READ_PHONE_STATE));
    }

    public static boolean isCoarseLocationGranted(Context context) {
        return checkPermission(context, decodeStr(Constants.ACCESS_COARSE_LOCATION));
    }

    public static List removeDuplicate(List list) {
        List listTemp = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (!listTemp.contains(list.get(i))) {
                listTemp.add(list.get(i));
            }
        }
        return listTemp;
    }

    public static List<String> split(String origin, String sep) {
        List<String> result = new ArrayList<>();
        String[] parts = origin.split(sep);
        for (String part : parts) {
            if (!TextUtils.isEmpty(part.trim())) {
                result.add(part.trim());
            }
        }
        return result;
    }

    public static void copy(File src, File dst) throws IOException {
        if (!dst.exists()) {
            dst.createNewFile();
        }
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public static String getFileDirectoryName() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        return dateNowStr;
    }

    public static String genUniqueID(String uid) {
        StringBuffer sb = new StringBuffer();
        sb.append(uid);
        sb.append("-");
        sb.append(System.currentTimeMillis());
        sb.append("-");
        sb.append(new SecureRandom().nextInt(8999) + 1000);
        sb.append("-");
        sb.append(Thread.currentThread().hashCode());
        return sb.toString();
    }

    public static String getSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static void sendEvaluateJavascript(final WebView webView, String function, final String... params) {
        String jsParams = "";
        for (String param : params) {
            jsParams += param + ",";
        }
        final String url = String.format("javascript:%s(%s)", function, jsParams.substring(0, jsParams.length() - 1));
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(url, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                        }
                    });
                    return;
                }
                webView.loadUrl(url);
            }
        });
    }

    public static String saveJPGFile(Context mContext, byte[] data, String fileName) {
        if (data == null)
            return null;
        File mediaStorageDir = new File(getLocalCacheDir(mContext), fileName);
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mediaStorageDir);
            bos = new BufferedOutputStream(fos);
            bos.write(data);
            return mediaStorageDir.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    public static File getLocalCacheDir(Context context) {
        File cache = context.getCacheDir();
        if (!cache.exists())
            cache.mkdirs();
        return cache;
    }

    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static int dip2px(Context context, float dip) {
        final float DOT_FIVE = 0.5f;
        return (int) (dip * getDensity(context) + DOT_FIVE);
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }


}
