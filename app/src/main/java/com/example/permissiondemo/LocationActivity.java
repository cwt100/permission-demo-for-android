package com.example.permissiondemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LocationActivity extends AppCompatActivity {

    private static final String TAG = LocationActivity.class.getSimpleName();
    private static final int PERMISSION_LOCATION_CODE = 1;

    private GPSModel mGPSModel;

    private TextView mMessageTextView;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initialUI();
        mGPSModel = GPSModel.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        clear();
        boolean isOK = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                isOK = false;
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION_CODE);
            }
        }

        if (isOK) {
            mGPSModel.getLngAndLat(locationResultListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGPSModel != null) {
            Log.d(TAG, "onDestroy to release");
            mGPSModel.removeListener();
            mGPSModel = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        clear();
        switch (requestCode) {
            case PERMISSION_LOCATION_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    mGPSModel.getLngAndLat(locationResultListener);
                }else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                            || !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        Toast.makeText(this, "請至設置介面打開權限", Toast.LENGTH_SHORT).show();
                    }else {
                        mMessageTextView.setText("請至設置介面打開權限");
                    }
                }
                break;
        }
    }

    private void initialUI() {
        mMessageTextView = (TextView) findViewById(R.id.textViewMessage);
        mLatitudeTextView = (TextView) findViewById(R.id.textViewLatitude);
        mLongitudeTextView = (TextView) findViewById(R.id.textViewLongitude);
        clear();
    }

    private void clear() {
        mMessageTextView.setText("");
        mLatitudeTextView.setText("--");
        mLongitudeTextView.setText("--");
    }

    private void updateLocation(Location location) {
        String latitudeString = String.format("%.2f", location.getLatitude());
        String longitudeString = String.format("%.2f", location.getLongitude());
        mLatitudeTextView.setText(latitudeString);
        mLongitudeTextView.setText(longitudeString);
    }

    private final GPSModel.OnLocationResultListener locationResultListener = new GPSModel.OnLocationResultListener() {
        @Override
        public void onLocationResult(Location location) {
            updateLocation(location);
        }

        @Override
        public void onLocationChange(Location location) {
            updateLocation(location);
        }
    };
}
