package com.os.timeguardian.backend.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PunishmentService extends Service {

    private HashMap<String, String> punishments;
    private Context context;

    public PunishmentService(Context context) {
        this.context = context;
        punishments = new HashMap<>();
    }


    public void addNewPunishment(String packageName, String level) {
        if (level.equals("Nothing")) return;
        punishments.put(packageName, level);
    }

    public void deletePunishment(String packageName) {
        punishments.remove(packageName);
    }

    public HashMap<String, String> getAllPunishments() {
        return this.punishments;
    }

    public void takeAwaySound() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    }

    public void setScreenBrightness(int brightness) {
        ContentResolver cr = getContentResolver();
        //Window window = getWindow();
    }

    public void createNotification() {
       // NotificationCompat.Builder nbuilder = (NotificationCompat.Builder) new NotificationCompat(getApplicationContext());
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
