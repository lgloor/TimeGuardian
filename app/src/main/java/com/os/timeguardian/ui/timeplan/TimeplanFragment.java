package com.os.timeguardian.ui.timeplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.os.timeguardian.R;
import com.os.timeguardian.databinding.FragmentTimeplanBinding;
import com.os.timeguardian.utils.PackageUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimeplanFragment extends Fragment {

    private FragmentTimeplanBinding binding;
    private FloatingActionButton addButton;
    private Button editButton;
    TextView textView;
    private Context context;
    private Dialog dialog;
    private Button cancelButton, logoutButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TimeplanViewModel timeplanViewModel =
                new ViewModelProvider(this).get(TimeplanViewModel.class);

        binding = FragmentTimeplanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        addButton = binding.floatingActionButton;
        editButton = binding.button;
        textView = binding.text;

        addButton.findViewById(R.id.floatingActionButton);
        editButton.findViewById(R.id.button);
        textView.findViewById(R.id.text);
        context = requireContext();


        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog_file);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);

        logoutButton = dialog.findViewById(R.id.logoutButton);
        cancelButton = dialog.findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.show();
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
}