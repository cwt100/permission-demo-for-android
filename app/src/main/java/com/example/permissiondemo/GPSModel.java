package com.example.permissiondemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class GPSModel {
    private static final String TAG = GPSModel.class.getSimpleName();
    private static GPSModel mGPSModel;

    private Context mContext;
    private LocationManager mLocationManager;

    private OnLocationResultListener mOnLocationResultListener;

    public interface OnLocationResultListener {
        void onLocationResult(Location location);

        void onLocationChange(Location location);
    }

    private GPSModel(Context context) {
        this.mContext = context;
    }

    public static GPSModel getInstance(Context context) {
        if (mGPSModel == null) {
            mGPSModel = new GPSModel(context);
        }
        return mGPSModel;
    }

    public String getLngAndLat(OnLocationResultListener onLocationResultListener) {
        double latitude = 0.0;
        double longitude = 0.0;
        mOnLocationResultListener = onLocationResultListener;

        String locationProvider = null;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(intent);
            return null;
        }

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }

        Location location = mLocationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            if (mOnLocationResultListener != null) {
                mOnLocationResultListener.onLocationResult(location);
            }
        }
        mLocationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
        return null;
    }

    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (mOnLocationResultListener != null) {
                mOnLocationResultListener.onLocationChange(location);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void removeListener() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(locationListener);
        }
    }
}
