package com.os.timeguardian.backend.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.os.timeguardian.utils.PackageUtil;

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
    private AppTimeService appTimeService;

    public BackgroundService(Context context, FileHelper fileHelper) {
        this.context = context;
        handler = new Handler();
        currentApp = "nothing";
        this.fileHelper = fileHelper;
        appTimeService = new AppTimeService(context);
        brightnessHelper = new BrightnessHelperNew(context);
        volumeHelper = new VolumeHelper(context);
        startTracking();
    }

    public void startTracking() {
        runnable = new Runnable() {
            @Override
            public void run() {
                Pair<String, Long> usagePair = appTimeService.getUsageTimeOfCurrentForegroundForToday();
                currentApp = PackageUtil.getUserFriendlyAppName(usagePair.first, context);
                currentTime = usagePair.second;
                HashMap<String, String> punishmentEntries = getPunishmentEntries();
                if (punishmentEntries.containsKey(currentApp) && currentApp != null) {
                    String punishment = punishmentEntries.get(currentApp);
                    if (currentTime>=30000 && "Soft".equals(punishment)) {
                        Toast.makeText(context, "Maybe take a look at your screen time.", Toast.LENGTH_SHORT).show();
                    } else if (currentTime>=30000 && "Middle".equals(punishment)) {
                        Toast.makeText(context, "That is too much screen time.", Toast.LENGTH_SHORT).show();
                        brightnessHelper.requestWriteSettingsPermission();
                        brightnessHelper.setSystemBrightness(0);
                    } else if(currentTime>=30000 && "Hard".equals(punishment)) {
                        Toast.makeText(context, "YOU GET THE ULTIMATE PUNISH!!!", Toast.LENGTH_SHORT).show();
                        brightnessHelper.requestWriteSettingsPermission();
                        brightnessHelper.setSystemBrightness(0);
                        volumeHelper.setMaxVolume(AudioManager.STREAM_MUSIC);
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
