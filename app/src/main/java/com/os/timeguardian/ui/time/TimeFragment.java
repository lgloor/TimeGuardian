package com.os.timeguardian.ui.time;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.os.timeguardian.databinding.FragmentTimeBinding;

public class TimeFragment extends Fragment {

    private FragmentTimeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TimeViewModel timeViewModel =
                new ViewModelProvider(this).get(TimeViewModel.class);

        binding = FragmentTimeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final RecyclerView recyclerView = binding.recycleView;
        //timeViewModel.getText().observe(getViewLifecycleOwner(), );
        //final TextView textView = binding.textHome;
        //timeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}