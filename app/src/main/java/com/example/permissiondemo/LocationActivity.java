package com.example.permissiondemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class LocationActivity extends AppCompatActivity {

    private static final String TAG = LocationActivity.class.getSimpleName();
    public static final int PERMISSION_LOCATION_CODE = 1;

    private GPSModel mGPSModel;

    private TextView mMessageTextView;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private AlertDialog.Builder mDialogBuilder;
    private AlertDialog mDialog;

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

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        clear();
        mGPSModel.getLngAndLat(locationResultListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGPSModel != null) {
            Log.d(TAG, "onDestroy to release");
            mGPSModel.removeListener();
            mGPSModel = null;
        }

        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            mDialog = null;
            mDialogBuilder = null;
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

                        mMessageTextView.setText("請至設置介面打開權限");
                    }else {
                        //mMessageTextView.setText("請至設置介面打開權限");
                        //dialog to user
                        if (mDialogBuilder == null) {
                            mDialogBuilder = new AlertDialog.Builder(this);
                            mDialogBuilder.setTitle("需要定位權限");
                            mDialogBuilder.setMessage("");
                            mDialogBuilder.setPositiveButton("前往設定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(LocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION_CODE);
                                }
                            });
                            mDialogBuilder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mMessageTextView.setText("請至設置介面打開權限");
                                    dialog.dismiss();
                                }
                            });
                        }

                        if (mDialog == null) {
                            mDialog = mDialogBuilder.create();
                        }

                        if (!mDialog.isShowing()) {
                            mDialog.show();
                        }
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
