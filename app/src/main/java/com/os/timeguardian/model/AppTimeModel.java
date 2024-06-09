package com.os.timeguardian.model;

public class AppTimeModel {
    private final String appName;
    private final long appTime;

    public AppTimeModel(String appName, long appTime) {
        this.appName = appName;
        this.appTime = appTime;
    }

    public String getAppName() {
        return appName;
    }

    public long getAppTime() {
        return appTime;
    }
}
