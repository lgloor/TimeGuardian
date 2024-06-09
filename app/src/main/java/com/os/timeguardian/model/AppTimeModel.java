package com.os.timeguardian.model;

public class AppTimeModel {
    private final String packageName;
    private final long appTime;

    public AppTimeModel(String packageName, long appTime) {
        this.packageName = packageName;
        this.appTime = appTime;
    }

    public String getPackageName() {
        return packageName;
    }

    public long getAppTime() {
        return appTime;
    }
}
