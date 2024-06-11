package com.os.timeguardian.backend.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.os.timeguardian.ui.timeplan.TimeplanFragment;
import com.os.timeguardian.utils.PackageUtil;

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
    private Long currentTime;
    private BrightnessHelperNew brightnessHelper;
    private VolumeHelper volumeHelper;
    private NotificationHelper notificationHelper;
    private int tracker;
    private String lastUsedApp;
    private AppTimeService appTimeService;

    public BackgroundService(Context context, FileHelper fileHelper) {
        tracker = 0;
        this.context = context;
        handler = new Handler();
        currentApp = "nothing";
        lastUsedApp = "nothing";
        this.fileHelper = fileHelper;
        appTimeService = new AppTimeService(context);
        brightnessHelper = new BrightnessHelperNew(context);
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
                //currentApp = "Docs"; //TODO: currentApp aktualisieren mit aktuell genutzter App.
                Pair<String, Long> usagePair = appTimeService.getUsageTimeOfCurrentForegroundForToday();
                currentApp = usagePair.first;
                currentApp = PackageUtil.getUserFriendlyAppName(currentApp,context);
                currentTime = usagePair.second;
                System.out.println(currentTime);
                HashMap<String, String> cmp = getPunishmentEntries();
                if (cmp.containsKey(currentApp) && currentApp != null) {
                    String punishment = cmp.get(currentApp);
                    if (!lastUsedApp.equals(currentApp)) {
                        tracker = 0;
                    }
                    lastUsedApp = currentApp;
                    tracker+=5;
                    Log.e("Aktueller Zeitmesser", String.valueOf(tracker));
                    if (currentTime>=30000 && punishment.equals("Soft")) {
                        notificationHelper.sendHighPriorityNotification("Soft punishment", "Maybe take a look at your screen time.");
                        tracker = 0;
                    } else if (currentTime>=30000 && punishment.equals("Middle")) {
                        notificationHelper.sendHighPriorityNotification("Middle punishment", "That is too much screen time. ");
                        brightnessHelper.requestWriteSettingsPermission();
                        brightnessHelper.setSystemBrightness(0);
                        tracker = 0;
                    } else if(currentTime>=30000 && punishment.equals("Hard")) {
                        notificationHelper.sendHighPriorityNotification("Hard punishment", "YOU GET THE ULTIMATE PUNISH !!!");
                        brightnessHelper.requestWriteSettingsPermission();
                        brightnessHelper.setSystemBrightness(0);
                        volumeHelper.setMaxVolume(AudioManager.STREAM_MUSIC);
                        tracker = 0;
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
