package com.os.timeguardian.ui.time;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.os.timeguardian.backend.service.AppTimeService;
import com.os.timeguardian.databinding.FragmentTimeBinding;
import com.os.timeguardian.model.AppTimeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimeFragment extends Fragment {

    private FragmentTimeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TimeViewModel timeViewModel =
                new ViewModelProvider(this).get(TimeViewModel.class);

        binding = FragmentTimeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recycler;
        List<AppTimeModel> appTimeModels = setupRecyclerModels();

        RecyclerAdapter adapter = new RecyclerAdapter(requireContext(), appTimeModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        //final RecyclerView recyclerView = binding.recycleView;
        //timeViewModel.getText().observe(getViewLifecycleOwner(), );
        //final TextView textView = binding.textHome;
        //timeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private List<AppTimeModel> setupRecyclerModels() {
        AppTimeService service = new AppTimeService(requireContext());
        requireActivity().startService(new Intent(requireContext(), AppTimeService.class));
        Map<String, Long> usageStatsToday = service.getUsageStatsToday();
        List<AppTimeModel> models = new ArrayList<>(usageStatsToday.size());
        for (Map.Entry<String, Long> entry : usageStatsToday.entrySet()) {
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