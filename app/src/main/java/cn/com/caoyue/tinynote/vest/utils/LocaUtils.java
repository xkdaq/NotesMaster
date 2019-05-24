package cn.com.caoyue.tinynote.vest.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocaUtils implements LocationListener {

    private LocationManager mLocationManager;
    private MyLocationListener mListener;
    private Context mContext;

    public interface MyLocationListener {
        abstract void updateLastLocation(Location location);
        void updateLocation(Location location);
        void updateStatus(String provider, int status, Bundle extras);
        void updateGpsStatus(GpsStatus gpsStatus);
    }

    public LocaUtils(Context context) {
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

    @Override
    public void onLocationChanged(Location location) {
        if (mListener != null) {
            mListener.updateLocation(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (mListener != null) {
            mListener.updateStatus(provider, status, extras);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
