package com.os.timeguardian.backend.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

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
        punishments.put(packageName, level);
    }

    public HashMap<String, String> getAllPunishments() {
        return this.punishments;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
