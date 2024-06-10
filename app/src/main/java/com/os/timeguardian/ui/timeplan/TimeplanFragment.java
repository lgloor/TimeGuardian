package com.os.timeguardian.ui.timeplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.os.timeguardian.R;
import com.os.timeguardian.backend.service.PunishmentService;
import com.os.timeguardian.databinding.FragmentTimeplanBinding;
import com.os.timeguardian.model.AppTimeplanModel;
import com.os.timeguardian.utils.PackageUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeplanFragment extends Fragment {

    private FragmentTimeplanBinding binding;
    private FloatingActionButton addButton;
    private Button editButton;
    private RecyclerView recyclerView;
    private PunishmentService punishmentService;
    private Context context;
    private Dialog dialog;
    private Dialog editDialog;
    private DialogWindow dialogWindow;
    private DialogWindowEdit editWindow;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TimeplanViewModel timeplanViewModel =
                new ViewModelProvider(this).get(TimeplanViewModel.class);

        binding = FragmentTimeplanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        addButton = binding.floatingActionButton;
        editButton = binding.button;
        recyclerView = binding.punishmentRecycler;

        addButton.findViewById(R.id.floatingActionButton);
        editButton.findViewById(R.id.button);
        recyclerView.findViewById(R.id.punishmentRecycler);
        context = requireContext();

        punishmentService = new PunishmentService(context);

        dialogWindow = new DialogWindow(binding, context, this);
        dialog = dialogWindow.getDialog();

        editWindow = new DialogWindowEdit(binding, context, this);
        editDialog = editWindow.getDialog();

        initializeRecyclerView();


        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog.show();
            }
        });

        //final TextView textView = binding.textNotifications;
        //timeplanViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initializeRecyclerView() {
        HashMap<String, String> models = punishmentService.getAllPunishments();
        updateRecyclerView(models);
    }

    private void updateRecyclerView(HashMap<String, String> models) {
        List<AppTimeplanModel> list = setupRecyclerModels(models);
        TimeplanRecyclerAdapter adapter = new TimeplanRecyclerAdapter(context, list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private List<AppTimeplanModel> setupRecyclerModels(HashMap<String, String> entries) {
        List<AppTimeplanModel> models = new ArrayList<>(entries.size());
        for (Map.Entry<String,String> entry : entries.entrySet()) {
            models.add(new AppTimeplanModel(entry.getKey(), entry.getValue()));
        }
        return models;
    }

    public void addPunishment(String packageName, String level) {
        punishmentService.addNewPunishment(packageName, level);
        initializeRecyclerView();
    }

    public void deletePunishment(String packageName) {
        punishmentService.deletePunishment(packageName);
        initializeRecyclerView();
    }

    public HashMap<String, String> getAllPunishments() {
        return punishmentService.getAllPunishments();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeRecyclerView();
    }

    public void notifyEditDialog() {
        editWindow.updateSpinnerItems();
    }

}