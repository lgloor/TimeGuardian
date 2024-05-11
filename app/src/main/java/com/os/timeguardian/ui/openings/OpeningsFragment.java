package com.os.timeguardian.ui.openings;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.os.timeguardian.R;
import com.os.timeguardian.backend.service.AppTimeService;
import com.os.timeguardian.databinding.FragmentOpeningsBinding;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OpeningsFragment extends Fragment {
    private AppTimeService service;

    private FragmentOpeningsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        String appName = getString(R.string.app_name);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(appName, Context.MODE_PRIVATE);
        service = new AppTimeService(getContext());

        OpeningsViewModel openingsViewModel =
                new ViewModelProvider(this).get(OpeningsViewModel.class);

        binding = FragmentOpeningsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        openingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        requireActivity().startService(new Intent(requireContext(), AppTimeService.class));
        PackageManager packageManager = requireContext().getPackageManager();

        List<Map<String, Integer>> usageStatsPastSevenDays = service.getUsageStatsPastSevenDays();
        System.out.println(usageStatsPastSevenDays);

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}