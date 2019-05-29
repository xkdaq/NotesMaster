package cn.com.caoyue.tinynote.vest.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationHelper implements LocationListener {

    private static final String TAG = LocationHelper.class.getSimpleName();

    private LocationManager mLocationManager;
    private MyLocationListener mListener;
    private Context mContext;

    public interface MyLocationListener {
        void updateLastLocation(Location location);
        void updateLocation(Location location);// 位置信息发生改变
        void updateStatus(String provider, int status, Bundle extras);// 位置状态发生改变
        void updateGpsStatus(GpsStatus gpsStatus);// GPS状态发生改变
    }

    public LocationHelper(Context context) {
        mContext = context.getApplicationContext();
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    public void initLocation(MyLocationListener listener) {
        mListener = listener;
        Location location;

        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                mListener.updateLastLocation(location);
            }
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } else if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                mListener.updateLastLocation(location);
            }
             mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    public void removeLocationUpdatesListener() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
    }

    // 定位坐标发生改变
    @Override
    public void onLocationChanged(Location location) {
        if (mListener != null) {
            mListener.updateLocation(location);
        }
    }

    // 定位服务状态改变
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (mListener != null) {
            mListener.updateStatus(provider, status, extras);
        }
    }

    // 定位服务开启
    @Override
    public void onProviderEnabled(String provider) {
    }

    // 定位服务关闭
    @Override
    public void onProviderDisabled(String provider) {
    }
}
