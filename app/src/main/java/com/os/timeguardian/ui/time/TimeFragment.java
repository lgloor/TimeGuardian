package com.os.timeguardian.ui.time;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.os.timeguardian.backend.service.AppTimeService;
import com.os.timeguardian.databinding.FragmentTimeBinding;
import com.os.timeguardian.model.AppTimeModel;
import com.os.timeguardian.utils.MapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        return root;
    }

    private void initializeRecyclerView() {
        recyclerView = binding.recycler;
        List<Map<String, Long>> result = service.getUsageStatsTodayGroupByHours();
        Map<String, Long> currentHourMap = result.get(result.size() - 1);
        updateRecyclerViewItems(currentHourMap);
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