package com.os.timeguardian.model;

public class AppTimeModel {
    private final String appName;
    private final String appTime;

    public AppTimeModel(String appName, String appTime) {
        this.appName = appName;
        this.appTime = appTime;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppTime() {
        return appTime;
    }
}
