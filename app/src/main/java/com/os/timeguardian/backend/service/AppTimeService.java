package com.os.timeguardian.backend.service;

import static android.app.usage.UsageStatsManager.INTERVAL_MONTHLY;
import static android.app.usage.UsageStatsManager.INTERVAL_WEEKLY;
import static android.app.usage.UsageStatsManager.INTERVAL_YEARLY;
import static android.content.ContentValues.TAG;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageEvents.Event;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class AppTimeService extends Service{

    private final PackageManager packageManager;
    private final UsageStatsManager statsManager;

    public AppTimeService(Context context) {
        statsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);
        packageManager = context.getPackageManager();
    }

    public Map<String, Integer> getUsageStatsToday() {
        long endTime = LocalDate.now()
                .atTime(23, 59, 59)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        long startTime = LocalDate.now().minusDays(1)
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        List<String> allPackageNames = getAllPackageNames();
        return getUsageTimeForRange(allPackageNames, startTime, endTime);
    }

    public List<Map<String, Integer>> getUsageStatsPastSevenDays() {
        //TODO: Add Multi-threading
        long stopWatchBegin = System.currentTimeMillis();
        ArrayList<Future<Map<String, Integer>>> futures = new ArrayList<>(7);
        ArrayList<Map<String, Integer>> weekList = new ArrayList<>(7);
        LocalDate date = LocalDate.now();

        List<String> allPackageNames = getAllPackageNames();

        ExecutorService executorService = Executors.newFixedThreadPool(7);

        for (int i = 0; i < 7; i++) {
            final long startTime = date.minusDays(i + 1)
                    .atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            final long endTime = date.minusDays(i)
                    .atTime(23, 59, 59)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            Future<Map<String, Integer>> future = executorService.submit(() -> getUsageTimeForRange(allPackageNames, startTime, endTime));
            futures.add(future);
        }

        executorService.shutdown();

        for (Future<Map<String, Integer>> future : futures) {
            try {
                weekList.add(future.get());
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "getUsageStatsPastSevenDays: ", e);
                return Collections.emptyList();
            }
        }

        long stopWatchEnd = System.currentTimeMillis();
        System.out.println(stopWatchEnd - stopWatchBegin + " ms");
        return weekList;
    }

    @NonNull
    private List<String> getAllPackageNames() {
        return packageManager.getInstalledApplications(0)
                .stream()
                .filter(applicationInfo -> (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1)
                .map(applicationInfo -> applicationInfo.packageName)
                .collect(Collectors.toList());
    }

    public List<UsageStats> getUsageStatsWeek() {
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 1000;
        return statsManager.queryUsageStats(INTERVAL_WEEKLY, startTime, endTime);
    }

    public List<UsageStats> getUsageStatsMonth() {
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 1000;
        return statsManager.queryUsageStats(INTERVAL_MONTHLY, startTime, endTime);
    }

    public List<UsageStats> getUsageStatsYear() {
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 1000;
        return statsManager.queryUsageStats(INTERVAL_YEARLY, startTime, endTime);
    }

    public Map<String, Integer> getUsageTimeForRange(List<String> packageNames, long startTime, long endTime) {
        UsageEvents usageEvents = statsManager.queryEvents(startTime, endTime);
        Map<String, List<Event>> eventsByPackage = new HashMap<>();

        while (usageEvents.hasNextEvent()) {
            Event currentEvent = new Event();
            usageEvents.getNextEvent(currentEvent);
            String packageName = currentEvent.getPackageName();
            if (currentEvent.getEventType() != Event.ACTIVITY_RESUMED
                    && currentEvent.getEventType() != Event.ACTIVITY_PAUSED) {
                continue;
            }
            if (packageNames.contains(packageName)) {
                eventsByPackage.putIfAbsent(packageName, new ArrayList<>());
                Objects.requireNonNull(eventsByPackage.get(packageName)).add(currentEvent);
            }
        }

        return getUsageTimeFromEvents(packageNames, eventsByPackage);
    }

    @NonNull
    private Map<String, Integer> getUsageTimeFromEvents(List<String> packageNames, Map<String, List<Event>> eventsByPackage) {
        Map<String, Integer> usageTimeMap = new HashMap<>();
        for (String packageName : packageNames) {
            String userFriendlyAppName;
            try {
                userFriendlyAppName = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0)).toString();
            } catch (PackageManager.NameNotFoundException e) {
                userFriendlyAppName = packageName;
            }
            int time = getTotalTime(Objects.requireNonNull(eventsByPackage.getOrDefault(packageName, new ArrayList<>())));
            if (time > 0) {
                usageTimeMap.put(userFriendlyAppName, time);
            }
        }
        return usageTimeMap;
    }

    private static int getTotalTime(List<Event> allEvents) {
        int totalTime = 0;
        for (int i = 0; i < allEvents.size() - 1; i++) {
            Event E0 = allEvents.get(i);
            Event E1 = allEvents.get(i + 1);

            if (E0.getEventType() == Event.ACTIVITY_RESUMED
                    && E1.getEventType() == Event.ACTIVITY_PAUSED
                    && E0.getClassName().equals(E1.getClassName())) {
                totalTime += (int) (E1.getTimeStamp() - E0.getTimeStamp());
            }
        }
        return totalTime;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
