package com.os.timeguardian.model;

public class AppOpeningsModel {
    private final String packageName;
    private final int appOpenings;

    public AppOpeningsModel(String packageName, int appOpenings) {
        this.packageName = packageName;
        this.appOpenings = appOpenings;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getAppOpenings() {
        return appOpenings;
    }
}
