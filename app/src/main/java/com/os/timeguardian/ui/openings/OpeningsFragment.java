package com.os.timeguardian.ui.openings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.os.timeguardian.backend.service.AppTimeService;
import com.os.timeguardian.databinding.FragmentOpeningsBinding;

import java.util.List;
import java.util.Map;

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

        Map<String, Long> usageStatsToday = service.getUsageStatsToday();
        List<Map<String, Long>> usageStatsPastSevenDays = service.getUsageStatsPastSevenDays();
        List<Map<String, Long>> usageStatsTodayGroupByHours = service.getUsageStatsTodayGroupByHours();

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}