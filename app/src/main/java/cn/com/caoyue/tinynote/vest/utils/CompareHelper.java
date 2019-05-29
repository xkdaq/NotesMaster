package cn.com.caoyue.tinynote.vest.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class CompareHelper {

    private static String gSignatureKey;
    private static String gBoundId;
    private static String gRequestUrl;
    private Context mContext;

    public interface OnComparisonListener {
        void onComparisonSuccess(double similarity, double refTh);
        void onComparisonFailed(int code, String msg);
    }

    public CompareHelper(Context context) {
        assert (context != null);
        mContext = context;
        gSignatureKey = CommonUtils.isRelease() ? "bioauthA399A626DAF04D05A6E01241A9BCF638" : "bioauthB74908CD6BDF4789802192C26931D308";
        gBoundId = CommonUtils.isRelease() ? "com.paic.xface-acs" : "com.paic.xface-test";
        gRequestUrl = CommonUtils.isRelease() ? "https://hk-xface-int.yun.pingan.com/bioauth/api01/face/compare" : "http://hk-xface-int-stg.pingan.com/bioauth/api01/face/compare";
    }

    public void compare(String photoPath1, String photoPath2, OnComparisonListener listener) {
        assert (listener != null);
        try {
            String p1 = encodeBase64File(photoPath1);
            String p2 = encodeBase64File(photoPath2);
            JSONObject obj = new JSONObject();
            JSONObject image1obj = new JSONObject();
            JSONObject image2obj = new JSONObject();
            image1obj.put("width", getWidth(photoPath1));
            image1obj.put("height", getHeight(photoPath1));
            image1obj.put("content_type", getImageType(photoPath1));
            image1obj.put("data", p1);
            image1obj.put("token", getTokenValue(p1, gSignatureKey));
            image1obj.put("mark", 0);
            image1obj.put("check_quality", 0);
            image2obj.put("width", getWidth(photoPath2));
            image2obj.put("height", getHeight(photoPath2));
            image2obj.put("content_type", getImageType(photoPath2));
            image2obj.put("data", p2);
            image2obj.put("token", getTokenValue(p2, gSignatureKey));
            image2obj.put("mark", 0);
            image2obj.put("check_quality", 0);
            obj.put("image1", image1obj);
            obj.put("image2", image2obj);
            obj.put("person_id", DeviceInfoUtils.getAndroidID(mContext));
            obj.put("app_id", gBoundId);

            try {
                String res = service(obj);
                JSONObject jsonObject = CommonUtils.getJsonObject(res);
                int code = jsonObject.getInt("errorcode");
                String msg = jsonObject.getString("errormsg");
                double similarity = jsonObject.getDouble("similarity");
                double refTh = jsonObject.getDouble("ref_thres");
                if (code != 0) {
                    listener.onComparisonFailed(code, msg);
                } else {
                    listener.onComparisonSuccess(similarity, refTh);
                }
            } catch (Exception e) {
                e.printStackTrace();
                listener.onComparisonFailed(-1, e.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            listener.onComparisonFailed(-1, e.toString());
        }
    }

    public String encodeBase64File(String path) throws Exception {
        byte[] buffer = readFile(path);
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    private String getImageType(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        String type = options.outMimeType;
        return type.split("/")[1];
    }

    private String getWidth(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);
        return String.valueOf(options.outWidth);
    }

    private String getHeight(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);
        return String.valueOf(options.outHeight);
    }

    private static String service(JSONObject jsonobj)
            throws Exception {

        URL url = new URL(gRequestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        long timestamp = new Timestamp(System.currentTimeMillis()).getTime();
        int none = new Random().nextInt(9999999);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty(
                "Authorization",
                getSignature(String.valueOf(timestamp), String.valueOf(none),
                        gSignatureKey));
        connection.setRequestProperty("x-bi-timestamp",
                String.valueOf(timestamp));
        connection.setRequestProperty("x-bi-none", String.valueOf(none));
        connection.setRequestProperty("x-bi-boundid", gBoundId);
        connection.connect();
        DataOutputStream out = new DataOutputStream(
                connection.getOutputStream());
        out.writeBytes(jsonobj.toString());
        out.flush();
        out.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String lines;

        StringBuffer sb = new StringBuffer("");
        while ((lines = reader.readLine()) != null) {
            lines = new String(lines.getBytes(), "UTF-8");
            sb.append(lines);
        }

        reader.close();
        connection.disconnect();
        return sb.toString();

    }

    private static String getSignature(String timestamp, String none,
                                       String signatureKey) throws Exception {
        String hmacSha1 = null;
        try {
            String message = URLEncoder.encode(timestamp.concat(none), "UTF-8");
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec = new SecretKeySpec(
                    signatureKey.getBytes("UTF-8"), "HmacSHA1");
            mac.init(spec);
            byte[] byteHMAC = mac.doFinal(message.getBytes("UTF-8"));
            hmacSha1 = byteArray2String(byteHMAC);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hmacSha1;
    }

    private static String byteArray2String(byte[] bs) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bs.length; i++) {
            String inTmp = null;
            String text = Integer.toHexString(bs[i]);
            if (text.length() >= 2) {
                inTmp = text.substring(text.length() - 2, text.length());
            } else {
                char[] array = new char[2];
                Arrays.fill(array, 0, 2 - text.length(), '0');
                System.arraycopy(text.toCharArray(), 0, array,
                        2 - text.length(), text.length());
                inTmp = new String(array);
            }
            sb.append(inTmp);
        }
        return sb.toString().toUpperCase();
    }

    private static byte[] readFile(String filePath) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        File f = new File(filePath);
        int fSize = (int) f.length();
        byte[] buff = new byte[fSize];
        // byte[] buf = new byte[1024 * 50];
        InputStream in = null;
        try {
            in = new FileInputStream(filePath);
            int position = 0;
            while ((position = in.read(buff)) != -1) {
                bos.write(buff, 0, position);
            }
            in.close();

            return bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String getTokenValue(String imageStr, String SIGNATURE_KEY) {
        String hmacSha = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec spec = new SecretKeySpec(
                    SIGNATURE_KEY.getBytes("UTF-8"), "HmacSHA256");
            mac.init(spec);
            byte[] byteHMAC = mac.doFinal(imageStr.getBytes("UTF-8"));
            StringBuffer sbLogRet = new StringBuffer();
            for (int i = 0; i < byteHMAC.length; i++) {
                String inTmp = null;
                String text = Integer.toHexString(byteHMAC[i]);
                if (text.length() >= 2) {
                    inTmp = text.substring(text.length() - 2, text.length());
                } else {
                    char[] array = new char[2];
                    Arrays.fill(array, 0, 2 - text.length(), '0');
                    System.arraycopy(text.toCharArray(), 0, array,
                            2 - text.length(), text.length());
                    inTmp = new String(array);
                }
                sbLogRet.append(inTmp);
            }
            hmacSha = sbLogRet.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hmacSha;
    }
}
