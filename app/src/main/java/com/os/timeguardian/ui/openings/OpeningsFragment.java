package com.os.timeguardian.ui.openings;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.os.timeguardian.backend.service.AppTimeService;
import com.os.timeguardian.databinding.FragmentOpeningsBinding;
import com.os.timeguardian.utils.WeekdayValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class OpeningsFragment extends Fragment {
    private AppTimeService service;

    private FragmentOpeningsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        service = new AppTimeService(requireContext());

        OpeningsViewModel openingsViewModel =
                new ViewModelProvider(this).get(OpeningsViewModel.class);

        binding = FragmentOpeningsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        openingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        requireActivity().startService(new Intent(requireContext(), AppTimeService.class));

//        Map<String, Long> usageStatsToday = service.getUsageStatsToday();
//        List<Map<String, Long>> usageStatsPastSevenDays = service.getUsageStatsPastSevenDays();
        //List<Map<String, Long>> usageStatsTodayGroupByHours = service.getUsageStatsTodayGroupByHours();
        //Map<String, Integer> openingAmountsToday = service.getOpeningAmountsToday();
        List<Map<String, Integer>> openingAmountsPastSevenDays = service.getOpeningAmountsPastSevenDays();
        Collections.reverse(openingAmountsPastSevenDays);
        BarData data = getBarData(openingAmountsPastSevenDays);
        getFormattedBarChart(data, openingAmountsPastSevenDays);
        return root;
    }

    private BarChart getFormattedBarChart(BarData data, List<Map<String, Integer>> openingAmountsPastSevenDays) {
        BarChart barChart = binding.idBarChart;
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setHighlightFullBarEnabled(true);
        barChart.setDrawValueAboveBar(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.highlightValue(new Highlight(6, 0, 0));
        barChart.setOnClickListener(v -> updateRecyclerView(openingAmountsPastSevenDays, barChart));

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new WeekdayValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.rgb(128, 128, 128));

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        return barChart;
    }

    private static void updateRecyclerView(List<Map<String, Integer>> openingAmountsPastSevenDays, BarChart barChart) {
        Optional.ofNullable(barChart.getHighlighted())
                .filter(highlighted -> highlighted.length > 0)
                .map(highlighted -> highlighted[0])
                .ifPresent(highlight -> {
                    int x = (int) highlight.getX();
                    List<Entry<String, Integer>> entries = sortMapByValueDesc(openingAmountsPastSevenDays.get(x));
                    //TODO: update recyclerView with entries
                });
    }

    private BarData getBarData(List<Map<String, Integer>> data) {
        List<BarEntry> barEntries = new ArrayList<>();
        int count = 0;
        for (Map<String, Integer> map : data) {
            List<Entry<String, Integer>> entries = sortMapByValueDesc(map);
            if (entries.size() > 3) {
                barEntries.add(formatBigBarData(count++, entries));
            }
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColors(Color.rgb(217, 80, 138), Color.rgb(254, 149, 7),
                Color.rgb(254, 247, 120), Color.rgb(160, 160, 160));
        return new BarData(barDataSet);
    }

    private BarEntry formatBigBarData(int count, List<Entry<String, Integer>> entries) {
        Integer mostOpens = entries.get(0).getValue();
        Integer secondMostOpens = entries.get(1).getValue();
        Integer thirdMostOpens = entries.get(2).getValue();
        Integer otherOpens = 0;
        for (int i = 3; i < entries.size(); i++) {
            otherOpens += entries.get(i).getValue();
        }
        return new BarEntry(count, new float[]{mostOpens, secondMostOpens, thirdMostOpens, otherOpens});
    }

    public static <K, V extends Comparable<? super V>> List<Entry<K, V>> sortMapByValueDesc(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());
        Collections.reverse(list);
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}