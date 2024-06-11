package com.os.timeguardian.backend.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PunishmentService extends Service {

    private static final HashMap<String, String> punishments = new HashMap<>();
    private Context context;
    private FileHelper fileHelper;
    private BackgroundService backgroundService;

    public PunishmentService(Context context) {
        this.context = context;
        this.fileHelper = new FileHelper(context);
        this.backgroundService = new BackgroundService(context, this.fileHelper);
        updateLocalList();
        //startBackgroundService();
    }


    public void addNewPunishment(String packageName, String level) {
        if (level.equals("Nothing")) return;
        fileHelper.saveData(packageName, level);
        updateLocalList();
        //punishments.put(packageName, level);
    }

    public void deletePunishment(String packageName) {
        fileHelper.removeData(packageName);
        updateLocalList();
        //punishments.remove(packageName);
    }

    public HashMap<String, String> getAllPunishments() {
        return punishments;
    }

    public void takeAwaySound() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    }

    private void updateLocalList() {
        punishments.clear();
        List<Map.Entry<String,String>>inputData = fileHelper.readAllData();
        for (Map.Entry<String, String> entry : inputData) {
            punishments.put(entry.getKey(), entry.getValue());
        }
    }

    /*public void startBackgroundService() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent startServiceIntent = new Intent(context, this.getClass());
        PendingIntent startService = PendingIntent.getService(context, 0, startServiceIntent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + 1000*5);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*5, startService);
    }*/
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
