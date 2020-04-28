package com.example.permissiondemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_CAMERA_CODE = 1;

    private Button mCameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraButton = (Button) findViewById(R.id.buttonCamera);
        updateCameraPermissionStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCameraPermissionStatus();
    }

    public void onClickLocationPermission(View view) {
        Intent intent = new Intent(MainActivity.this, LocationActivity.class);
        startActivity(intent);
    }

    public void onClickCameraPermission(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("需要相機權限");
                builder.setMessage("");
                builder.setPositiveButton("前往設定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_CODE);
                    }
                });
                builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateCameraPermissionStatus();
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();

            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_CODE);
            }
        }
    }

    public void onClickPhonePermission(View view) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CAMERA_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateCameraPermissionStatus();
                }else {
                    updateCameraPermissionStatus();
                }
                break;
        }
    }

    public void updateCameraPermissionStatus() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mCameraButton.setEnabled(false);
            mCameraButton.setText("Camera permission is OK");
        }else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                mCameraButton.setEnabled(false);
                mCameraButton.setText("請至設置介面打開權限");
            }else {
                mCameraButton.setEnabled(true);
                mCameraButton.setText("Camera");
            }
        }
    }
}
