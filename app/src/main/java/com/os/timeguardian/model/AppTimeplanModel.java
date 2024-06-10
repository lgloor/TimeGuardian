package com.os.timeguardian.model;

public class AppTimeplanModel {

    private final String packageName;
    private final String punishmentLevel;

    public AppTimeplanModel(String packageName, String punishmentLevel) {
        this.packageName = packageName;
        this.punishmentLevel = punishmentLevel;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getPunishmentLevel() {
        return this.punishmentLevel;
    }
}
