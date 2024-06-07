package com.os.timeguardian.backend.service;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageEvents.Event;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

public class AppTimeService extends Service {
    private static final long TEN_MINUTES = 10 * 60 * 60 * 1000L;
    private static Pair<Long, Map<String, Long>> usageStatsTodayCache;
    private static Pair<Long, List<Map<String, Long>>> usageStatsTodayGroupByHoursCache;
    private static Pair<Long, List<Map<String, Long>>> usageStatsPastSevenDaysCache;
    private static List<String> allPackageNames;
    private static final String TAG = "AppTimeService";
    private final PackageManager packageManager;
    private final UsageStatsManager statsManager;

    public AppTimeService(Context context) {
        statsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);
        packageManager = context.getPackageManager();
        allPackageNames = getAllPackageNames();
    }

    public Map<String, Long> getUsageStatsToday() {
        if (usageStatsTodayCache != null
                && usageStatsTodayCache.first >= System.currentTimeMillis() - TEN_MINUTES) {
            return usageStatsTodayCache.second;
        }

        LocalDate today = LocalDate.now();
        long startTime = getStartTime(today.minusDays(1));
        long endTime = getEndTime(today);
        Map<String, Long> usageStatsToday = getUsageTimeForRange(startTime, endTime);
        usageStatsTodayCache = new Pair<>(System.currentTimeMillis(), usageStatsToday);
        return usageStatsToday;
    }

    public List<Map<String, Long>> getUsageStatsTodayGroupByHours() {
        if (usageStatsTodayGroupByHoursCache != null
                && usageStatsTodayGroupByHoursCache.first >= System.currentTimeMillis() - TEN_MINUTES) {
            return usageStatsTodayGroupByHoursCache.second;
        }

        int hoursToShow = LocalDateTime.now().getHour() + 1;
        List<Future<Map<String, Long>>> futures = new ArrayList<>(hoursToShow);
        LocalDateTime maxTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(hoursToShow, 0));

        ExecutorService executorService = Executors.newFixedThreadPool(hoursToShow);

        for (int i = 0; i < hoursToShow; i++) {
            final long startTime = convertToEpochMilli(maxTime.minusHours(i + 1));
            final long endTime =  convertToEpochMilli(maxTime.minusHours(i));

            Future<Map<String, Long>> future = executorService.submit(() -> getUsageTimeForRange(startTime, endTime));
            futures.add(i, future);
        }

        executorService.shutdown();

        List<Map<String, Long>> hourList = getValuesFromFutures(hoursToShow, futures);
        usageStatsTodayGroupByHoursCache = new Pair<>(System.currentTimeMillis(), hourList);
        return hourList;
    }

    public List<Map<String, Long>> getUsageStatsPastSevenDays() {
        if (usageStatsPastSevenDaysCache != null
                && usageStatsPastSevenDaysCache.first >= System.currentTimeMillis() - TEN_MINUTES) {
            return usageStatsPastSevenDaysCache.second;
        }

        int numberOfDays = 7;
        List<Future<Map<String, Long>>> futures = new ArrayList<>(numberOfDays);
        LocalDate today = LocalDate.now();

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfDays);

        for (int i = 0; i < numberOfDays; i++) {
            final long startTime = getStartTime(today.minusDays(i + 1));
            final long endTime = getEndTime(today.minusDays(i));

            Future<Map<String, Long>> future = executorService.submit(() -> getUsageTimeForRange(startTime, endTime));
            futures.add(i, future);
        }

        executorService.shutdown();

        List<Map<String, Long>> weekList = getValuesFromFutures(numberOfDays, futures);
        usageStatsPastSevenDaysCache = new Pair<>(System.currentTimeMillis(), weekList);
        return weekList;
    }

    @NonNull
    private static List<Map<String, Long>> getValuesFromFutures(int hoursToShow, List<Future<Map<String, Long>>> futures) {
        List<Map<String, Long>> statsList = new ArrayList<>(hoursToShow);
        for (Future<Map<String, Long>> future : futures) {
            try {
                statsList.add(future.get());
            } catch (ExecutionException | InterruptedException e) {
                Log.e(AppTimeService.TAG, "Error getting value from Future: ", e);
                statsList.add(Collections.emptyMap());
            }
        }
        return statsList;
    }

    private static long getStartTime(LocalDate date) {
        return convertToEpochMilli(date.atStartOfDay());
    }

    private static long getEndTime(LocalDate date) {
        return convertToEpochMilli(date.atTime(23, 59, 59));
    }

    private static long convertToEpochMilli(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * DO NOT USE FOR DATE RANGES THAT ARE MORE THAN 1 WEEK AGO! <br>
     * Events are only stored by the system for a few days.
     *
     * @param startTime Start time of evaluation in milliseconds
     * @param endTime   End time of evaluation in milliseconds
     * @return Map of package names to usage time in milliseconds for the given time range
     */
    private Map<String, Long> getUsageTimeForRange(long startTime, long endTime) {
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
            if (allPackageNames.contains(packageName)) {
                eventsByPackage.putIfAbsent(packageName, new ArrayList<>());
                Objects.requireNonNull(eventsByPackage.get(packageName)).add(currentEvent);
            }
        }

        return getUsageTimeFromEvents(allPackageNames, eventsByPackage);
    }

    @NonNull
    private List<String> getAllPackageNames() {
        return packageManager.getInstalledApplications(0)
                .stream()
                .filter(applicationInfo -> (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1)
                .map(applicationInfo -> applicationInfo.packageName)
                .collect(Collectors.toList());
    }

    @NonNull
    private Map<String, Long> getUsageTimeFromEvents(List<String> packageNames, Map<String, List<Event>> eventsByPackage) {
        Map<String, Long> usageTimeMap = new HashMap<>();
        for (String packageName : packageNames) {
            String userFriendlyAppName;
            userFriendlyAppName = getUserFriendlyAppName(packageName);
            long time = getTotalTimeEvents(Objects.requireNonNull(eventsByPackage.getOrDefault(packageName, new ArrayList<>())));
            if (time > 0) {
                usageTimeMap.put(userFriendlyAppName, time);
            }
        }
        return usageTimeMap;
    }

    @NonNull
    private String getUserFriendlyAppName(String packageName) {
        String userFriendlyAppName;
        try {
            userFriendlyAppName = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            userFriendlyAppName = packageName;
        }
        return userFriendlyAppName;
    }

    private static long getTotalTimeEvents(List<Event> allEvents) {
        long totalTime = 0;
        for (int i = 0; i < allEvents.size() - 1; i++) {
            Event E0 = allEvents.get(i);
            Event E1 = allEvents.get(i + 1);

            if (E0.getEventType() == Event.ACTIVITY_RESUMED
                    && E1.getEventType() == Event.ACTIVITY_PAUSED
                    && E0.getClassName().equals(E1.getClassName())) {
                totalTime += (E1.getTimeStamp() - E0.getTimeStamp());
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
