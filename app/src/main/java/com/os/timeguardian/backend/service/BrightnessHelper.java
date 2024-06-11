package com.os.timeguardian.backend.service;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.os.timeguardian.MainActivity;
// This class with his methods was created with the help of ChatGpt.
public class BrightnessHelper {

    private static final int WRITE_SETTINGS_PERMISSION_REQUEST_CODE = 100;
    private Context context;

    public BrightnessHelper(Context context) {
        this.context = context;
    }

    public void setSystemBrightness(int brightnessValue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                requestWriteSettingsPermission();
            } else {
                applySystemBrightness(brightnessValue);
            }
        } else {
            applySystemBrightness(brightnessValue);
        }
    }

    private void requestWriteSettingsPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        if (context instanceof MainActivity) {
            ((MainActivity) context).startActivityForResult(intent, WRITE_SETTINGS_PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(context, "Unable to request permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void applySystemBrightness(int brightnessValue) {
        ContentResolver contentResolver = context.getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightnessValue);
    }

    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode == WRITE_SETTINGS_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(context)) {
                // Permission granted, do nothing special
            }
        }
    }
}

/*
BrightnessHelper brightnessHelper = new BrightnessHelper(context);
brightnessHelper.setSystemBrightness(150);
 */