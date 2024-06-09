package com.os.timeguardian.ui.time;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.os.timeguardian.databinding.FragmentTimeBinding;
import com.os.timeguardian.model.AppTimeModel;
import com.os.timeguardian.utils.HourValueFormatter;
import com.os.timeguardian.utils.MapUtil;
import com.os.timeguardian.utils.MillisecondsToHoursFormatter;
import com.os.timeguardian.utils.WeekdayValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TimeFragment extends Fragment {

    private FragmentTimeBinding binding;
    private AppTimeService service;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        service = new AppTimeService(requireContext());
        binding = FragmentTimeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeRecyclerView();
        initializeSpinnerAndBarChart();

        return root;
    }

    private void initializeRecyclerView() {
        recyclerView = binding.recycler;
        List<Map<String, Long>> result = service.getUsageStatsTodayGroupByHours();
        Map<String, Long> currentHourMap = result.get(result.size() - 1);
        updateRecyclerViewItems(currentHourMap);
    }

    private void initializeSpinnerAndBarChart() {
        Spinner spinner = binding.spinner1;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.spinner_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    List<Map<String, Long>> result = service.getUsageStatsTodayGroupByHours();
                    updateRecyclerViewItems(result.get(result.size() - 1));
                    BarData data = getBarData(result);
                    getFormattedBarChart(data, result, new HourValueFormatter());
                } else {
                    List<Map<String, Long>> result = service.getUsageStatsPastSevenDays();
                    updateRecyclerViewItems(result.get(result.size() - 1));
                    BarData data = getBarData(result);
                    getFormattedBarChart(data, result, new WeekdayValueFormatter());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    private void getFormattedBarChart(BarData data, List<Map<String, Long>> appTimes, ValueFormatter formatter) {
        BarChart barChart = binding.idBarChart;
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setHighlightFullBarEnabled(true);
        barChart.setDrawValueAboveBar(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.highlightValue(new Highlight(appTimes.size() - 1, 0, 0));
        barChart.setOnClickListener(v -> updateRecyclerView(appTimes, barChart));

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.rgb(128, 128, 128));

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setValueFormatter(new MillisecondsToHoursFormatter());
        yAxis.setGranularity(1000 * 60 * 60);
    }

    private void updateRecyclerView(List<Map<String, Long>> appTimes, BarChart barChart) {
        Optional.ofNullable(barChart.getHighlighted())
                .filter(highlighted -> highlighted.length > 0)
                .map(highlighted -> highlighted[0])
                .ifPresent(highlight -> {
                    int x = (int) highlight.getX();
                    updateRecyclerViewItems(appTimes.get(x));
                });
    }

    private BarData getBarData(List<Map<String, Long>> data) {
        List<BarEntry> barEntries = new ArrayList<>(data.size());
        int count = 0;
        for (Map<String, Long> map : data) {
            List<Map.Entry<String, Long>> entries = MapUtil.sortMapByValueDesc(map);
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

    private BarEntry formatBigBarData(int count, List<Map.Entry<String, Long>> entries) {
        Long longestTime = entries.get(0).getValue();
        Long secondLongestTime = entries.get(1).getValue();
        Long thirdLongestTime = entries.get(2).getValue();
        Long otherTimes = 0L;
        for (int i = 3; i < entries.size(); i++) {
            otherTimes += entries.get(i).getValue();
        }
        return new BarEntry(count, new float[]{longestTime, secondLongestTime, thirdLongestTime, otherTimes});
    }

    private BarEntry formatSmallBarData(int count, List<Map.Entry<String, Long>> entries) {
        float[] openings = new float[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            openings[i] = entries.get(i).getValue();
        }
        return new BarEntry(count, openings);
    }

    private void updateRecyclerViewItems(Map<String, Long> map) {
        List<Map.Entry<String, Long>> entries = MapUtil.sortMapByValueDesc(map);
        List<AppTimeModel> models = setupRecyclerModels(entries);
        TimeRecyclerAdapter adapter = new TimeRecyclerAdapter(requireContext(), models);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private List<AppTimeModel> setupRecyclerModels(List<Map.Entry<String, Long>> entries) {
       List<AppTimeModel> models = new ArrayList<>(entries.size());
        for (Map.Entry<String, Long> entry : entries) {
            models.add(new AppTimeModel(entry.getKey(), entry.getValue()));
        }
        return models;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}