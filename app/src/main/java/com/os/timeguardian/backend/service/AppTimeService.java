package com.os.timeguardian.backend.service;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageEvents.Event;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.os.timeguardian.utils.PackageUtil;

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
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AppTimeService extends Service {
    private static final long TEN_MINUTES = 10 * 60 * 60 * 1000L;
    private static Pair<Long, Map<String, Long>> usageStatsTodayCache;
    private static Pair<Long, List<Map<String, Long>>> usageStatsTodayGroupByHoursCache;
    private static Pair<Long, List<Map<String, Long>>> usageStatsPastSevenDaysCache;
    private static Pair<Long, List<Map<String, Integer>>> openingAmountsTodayGroupByHoursCache;
    private static Pair<Long, List<Map<String, Integer>>> openingAmountsPastSevenDaysCache;
    private static List<String> allPackageNames;
    private static final String TAG = "AppTimeService";
    private final UsageStatsManager statsManager;

    public AppTimeService(Context context) {
        statsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);
        allPackageNames = PackageUtil.getAllPackageNames(context);
    }

    public List<Map<String, Long>> getUsageStatsTodayGroupByHours() {
        if (cacheValid(usageStatsTodayGroupByHoursCache)) {
            return usageStatsTodayGroupByHoursCache.second;
        }

        int hoursToShow = LocalDateTime.now().getHour() + 1;
        List<Future<Map<String, Long>>> futures = new ArrayList<>(hoursToShow);
        LocalDateTime maxTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(hoursToShow, 0));

        ExecutorService executorService = Executors.newFixedThreadPool(hoursToShow);

        for (int i = 0; i < hoursToShow; i++) {
            final long startTime = convertToEpochMilli(maxTime.minusHours(hoursToShow - i));
            final long endTime =  convertToEpochMilli(maxTime.minusHours(hoursToShow - i - 1));

            Future<Map<String, Long>> future = executorService.submit(() -> getUsageTimeForRange(startTime, endTime));
            futures.add(i, future);
        }

        executorService.shutdown();

        List<Map<String, Long>> hourList = getValuesFromFutures(hoursToShow, futures);
        usageStatsTodayGroupByHoursCache = new Pair<>(System.currentTimeMillis(), hourList);
        return hourList;
    }

    public List<Map<String, Long>> getUsageStatsPastSevenDays() {
        if (cacheValid(usageStatsPastSevenDaysCache)) {
            return usageStatsPastSevenDaysCache.second;
        }

        int numberOfDays = 7;
        List<Future<Map<String, Long>>> futures = new ArrayList<>(numberOfDays);
        LocalDate today = LocalDate.now();

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfDays);

        for (int i = 0; i < numberOfDays; i++) {
            final long startTime = getStartTime(today.minusDays(numberOfDays - i - 1));
            final long endTime = getEndTime(today.minusDays(numberOfDays - i - 1));

            Future<Map<String, Long>> future = executorService.submit(() -> getUsageTimeForRange(startTime, endTime));
            futures.add(i, future);
        }

        executorService.shutdown();

        List<Map<String, Long>> weekList = getValuesFromFutures(numberOfDays, futures);
        usageStatsPastSevenDaysCache = new Pair<>(System.currentTimeMillis(), weekList);
        return weekList;
    }

    public synchronized Pair<String, Long> getUsageTimeOfCurrentForegroundForToday() {
        String appInForeground = getAppInForeground();
        LocalDate today = LocalDate.now();
        long startTime = getStartTime(today);
        long endTime = getEndTime(today);
        Map<String, Long> usageTimeForRange = getUsageTimeForRange(startTime, endTime);

        return new Pair<>(appInForeground, usageTimeForRange.getOrDefault(appInForeground, 0L));
    }

    /**
     * copied from <a href="https://stackoverflow.com/a/30778294">StackOverflow</a>
     */
    private String getAppInForeground() {
        String currentApp = "NULL";
        long time = System.currentTimeMillis();
        List<UsageStats> appList = statsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
        if (appList != null && !appList.isEmpty()) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (!mySortedMap.isEmpty()) {
                currentApp = Objects.requireNonNull(mySortedMap.get(mySortedMap.lastKey())).getPackageName();
            }
        }

        return currentApp;
    }

    private static boolean cacheValid(Pair<Long, ?> cache) {
        return cache != null && cache.first >= System.currentTimeMillis() - TEN_MINUTES;
    }

    @NonNull
    private static <T,S> List<Map<T, S>> getValuesFromFutures(int hoursToShow, List<Future<Map<T, S>>> futures) {
        List<Map<T, S>> statsList = new ArrayList<>(hoursToShow);
        for (Future<Map<T, S>> future : futures) {
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
     * created with the help of <a href="https://stackoverflow.com/a/67753802">StackOverflow</a>
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
    private Map<String, Long> getUsageTimeFromEvents(List<String> packageNames, Map<String, List<Event>> eventsByPackage) {
        Map<String, Long> usageTimeMap = new HashMap<>();
        for (String packageName : packageNames) {
            long time = getTotalTimeEvents(Objects.requireNonNull(eventsByPackage.getOrDefault(packageName, new ArrayList<>())));
            if (time > 0) {
                usageTimeMap.put(packageName, time);
            }
        }
        return usageTimeMap;
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

    public List<Map<String, Integer>> getOpeningAmountsTodayGroupByHours() {
        if (cacheValid(openingAmountsTodayGroupByHoursCache)) {
            return openingAmountsTodayGroupByHoursCache.second;
        }

        int hoursToShow = LocalDateTime.now().getHour() + 1;
        List<Future<Map<String, Integer>>> futures = new ArrayList<>(hoursToShow);
        LocalDateTime maxTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(hoursToShow, 0));
        ExecutorService executorService = Executors.newFixedThreadPool(hoursToShow);

        for (int i = 0; i < hoursToShow; i++) {
            final long startTime = convertToEpochMilli(maxTime.minusHours(hoursToShow - i));
            final long endTime =  convertToEpochMilli(maxTime.minusHours(hoursToShow - i - 1));

            Future<Map<String, Integer>> future = executorService.submit(() -> getOpeningAmountsForTimeRange(startTime, endTime));
            futures.add(i, future);
        }
        executorService.shutdown();

        List<Map<String, Integer>> hourList = getValuesFromFutures(hoursToShow, futures);
        openingAmountsTodayGroupByHoursCache = new Pair<>(System.currentTimeMillis(), hourList);
        return hourList;
    }

    public List<Map<String, Integer>> getOpeningAmountsPastSevenDays() {
        if (cacheValid(openingAmountsPastSevenDaysCache)) {
            return openingAmountsPastSevenDaysCache.second;
        }

        int numberOfDays = 7;
        List<Future<Map<String, Integer>>> futures = new ArrayList<>(numberOfDays);
        LocalDate today = LocalDate.now();

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfDays);

        for (int i = 0; i < numberOfDays; i++) {
            final long startTime = getStartTime(today.minusDays(numberOfDays - i - 1));
            final long endTime = getEndTime(today.minusDays(numberOfDays - i - 1));

            Future<Map<String, Integer>> future = executorService.submit(() -> getOpeningAmountsForTimeRange(startTime, endTime));
            futures.add(i, future);
        }

        executorService.shutdown();

        List<Map<String, Integer>> weekList = getValuesFromFutures(numberOfDays, futures);
        openingAmountsPastSevenDaysCache = new Pair<>(System.currentTimeMillis(), weekList);
        return weekList;
    }

    /**
     * created with inspiration from <a href="https://stackoverflow.com/a/67753802">StackOverflow</a>
     */
    private Map<String, Integer> getOpeningAmountsForTimeRange(long startTime, long endTime) {
        UsageEvents usageEvents = statsManager.queryEvents(startTime, endTime);

        Map<String, Integer> openingAmounts = new HashMap<>();

        while (usageEvents.hasNextEvent()) {
            Event currentEvent = new Event();
            usageEvents.getNextEvent(currentEvent);
            String packageName = currentEvent.getPackageName();
            if (!allPackageNames.contains(packageName)) {
                continue;
            }
            if (currentEvent.getEventType() == Event.ACTIVITY_RESUMED && isNotUselessEvent(currentEvent)) {
                openingAmounts.merge(packageName, 1, Integer::sum);
            }
        }
        return openingAmounts;
    }

    private boolean isNotUselessEvent(Event currentEvent) {
        String className = currentEvent.getClassName();
        return className != null
            && (className.toLowerCase().contains("main")
            || className.toLowerCase().contains("home"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
