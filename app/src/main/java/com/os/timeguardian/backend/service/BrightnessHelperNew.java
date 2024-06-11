package com.os.timeguardian.backend.service;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.os.timeguardian.ui.timeplan.TimeplanFragment;

public class BrightnessHelperNew {

    private static final String TAG = "BrightnessHelper";
    private Context context;
    private ContentResolver contentResolver;

    public BrightnessHelperNew(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    public void setSystemBrightness(int brightness) {
        if (Settings.System.canWrite(context)) {
            try {
                // Deaktivieren der automatischen Helligkeit
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

                // Setzen der Helligkeit
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
                Log.d(TAG, "Screen brightness was set to: " + brightness);
            } catch (Exception e) {
                Log.e(TAG, "Error setting screen brightness", e);
            }
        } else {
            Log.w(TAG, "Permission to write settings not granted.");
        }
    }

    public void requestWriteSettingsPermission() {
        if (!Settings.System.canWrite(context)) {
            Log.d(TAG, "You are allowed to change brightness");
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}