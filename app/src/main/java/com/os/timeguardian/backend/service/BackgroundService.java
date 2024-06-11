package com.os.timeguardian.backend.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class BackgroundService extends Service {
    private Runnable runnable;
    private Handler handler;
    private Context context;
    private FileHelper fileHelper;
    private String currentApp;
    private BrightnessHelper brightnessHelper;
    private VolumeHelper volumeHelper;
    private NotificationHelper notificationHelper;
    public BackgroundService(Context context, FileHelper fileHelper) {
        this.context = context;
        handler = new Handler();
        currentApp = "nothing";
        brightnessHelper = new BrightnessHelper(context);
        volumeHelper = new VolumeHelper(context);
        notificationHelper = new NotificationHelper(context);
        startTracking();
    }

    public void startTracking() {
        runnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "Hintergrundprozess läuft.", Toast.LENGTH_SHORT).show();
                Log.e("Hintergrundprozess", "Läuft");
                handler.postDelayed(this, 5000);

            }
        };
        handler.postDelayed(runnable, 5000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
