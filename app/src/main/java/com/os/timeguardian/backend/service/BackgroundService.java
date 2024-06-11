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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackgroundService extends Service {
    private Runnable runnable;
    private Handler handler;
    private Context context;
    private FileHelper fileHelper;
    private String currentApp;
    private BrightnessHelper brightnessHelper;
    private VolumeHelper volumeHelper;
    private NotificationHelper notificationHelper;
    private int tracker;
    private String lastUsedApp;

    public BackgroundService(Context context, FileHelper fileHelper) {
        tracker = 0;
        this.context = context;
        handler = new Handler();
        currentApp = "nothing";
        lastUsedApp = "nothing";
        this.fileHelper = fileHelper;
        brightnessHelper = new BrightnessHelper(context);
        volumeHelper = new VolumeHelper(context);
        notificationHelper = new NotificationHelper(context);
        startTracking();
    }

    public void startTracking() {
        runnable = new Runnable() {
            @Override
            public void run() {
                /*Toast.makeText(context, "Hintergrundprozess läuft.", Toast.LENGTH_SHORT).show();
                Log.e("Hintergrundprozess", "Läuft");
                handler.postDelayed(this, 5000);*/
                currentApp = "Docs"; //TODO: currentApp aktualisieren mit aktuell genutzter App.
                HashMap<String, String> cmp = getPunishmentEntries();
                if (cmp.containsKey(currentApp) && currentApp != null) {
                    lastUsedApp = currentApp;
                    tracker+=5;
                    Log.e("Aktueller Zeitmesser", String.valueOf(tracker));
                    if (tracker>=30) {
                        notificationHelper.sendHighPriorityNotification("Soft punishment", "Achte mal auf deine Bildschirmzeit");
                    }
                }
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(runnable, 5000);
    }

    private HashMap<String, String> getPunishmentEntries() {
        List<Map.Entry<String, String>> allData = fileHelper.readAllData();
        HashMap<String, String> temp = new HashMap<>();
        for (Map.Entry<String,String> entry : allData) {
            temp.put(entry.getKey(), entry.getValue());
        }
        return temp;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
