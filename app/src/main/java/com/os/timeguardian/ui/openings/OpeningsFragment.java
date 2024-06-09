package com.os.timeguardian.ui.openings;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.os.timeguardian.R;
import com.os.timeguardian.backend.service.AppTimeService;
import com.os.timeguardian.databinding.FragmentOpeningsBinding;
import com.os.timeguardian.utils.HourValueFormatter;
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

        Spinner spinner1 = binding.spinner1;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.spinner_options_opening,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    List<Map<String, Integer>> openingAmountsTodayGroupByHours = service.getOpeningAmountsTodayGroupByHours();
                    BarData data = getBarData(openingAmountsTodayGroupByHours);
                    getFormattedBarChart(data, openingAmountsTodayGroupByHours, new HourValueFormatter());
                    //todo: update barchart
                } else {
                    List<Map<String, Integer>> openingAmountsPastSevenDays = service.getOpeningAmountsPastSevenDays();
                    BarData data = getBarData(openingAmountsPastSevenDays);
                    getFormattedBarChart(data, openingAmountsPastSevenDays, new WeekdayValueFormatter());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        return root;
    }

    private void getFormattedBarChart(BarData data, List<Map<String, Integer>> openingAmounts, ValueFormatter formatter) {
        BarChart barChart = binding.idBarChart;
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setHighlightFullBarEnabled(true);
        barChart.setDrawValueAboveBar(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.highlightValue(new Highlight(openingAmounts.size() - 1, 0, 0));
        barChart.setOnClickListener(v -> updateRecyclerView(openingAmounts, barChart));

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.rgb(128, 128, 128));

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
    }

    private static void updateRecyclerView(List<Map<String, Integer>> openingAmounts, BarChart barChart) {
        Optional.ofNullable(barChart.getHighlighted())
                .filter(highlighted -> highlighted.length > 0)
                .map(highlighted -> highlighted[0])
                .ifPresent(highlight -> {
                    int x = (int) highlight.getX();
                    List<Entry<String, Integer>> entries = sortMapByValueDesc(openingAmounts.get(x));
                    //TODO: update recyclerView with entries
                });
    }

    private BarData getBarData(List<Map<String, Integer>> data) {
        List<BarEntry> barEntries = new ArrayList<>(data.size());
        int count = 0;
        for (Map<String, Integer> map : data) {
            List<Entry<String, Integer>> entries = sortMapByValueDesc(map);
            if (entries.size() > 3) {
                barEntries.add(formatBigBarData(count++, entries));
            } else {
                barEntries.add(formatSmallBarData(count++, entries));
            }
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setDrawValues(false);

        barDataSet.setColors(Color.rgb(217, 80, 138), Color.rgb(254, 149, 7),
                Color.rgb(254, 247, 120), Color.rgb(160, 160, 160));
        return new BarData(barDataSet);
    }

    private BarEntry formatSmallBarData(int count, List<Entry<String, Integer>> entries) {
        float[] openings = new float[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            openings[i] = entries.get(i).getValue();
        }
        return new BarEntry(count, openings);
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