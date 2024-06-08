package com.os.timeguardian.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class PackageUtil {
    public static List<String> getAllPackageNames(Context context) {
        return context.getPackageManager()
                .getInstalledApplications(0)
                .stream()
                .filter(applicationInfo -> (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1)
                .map(applicationInfo -> applicationInfo.packageName)
                .collect(Collectors.toList());
    }

    @NonNull
    public static String getUserFriendlyAppName(String packageName, Context context) {
        PackageManager packageManager = context.getPackageManager();
        String userFriendlyAppName;
        try {
            userFriendlyAppName = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            userFriendlyAppName = packageName;
        }
        return userFriendlyAppName;
    }
}
