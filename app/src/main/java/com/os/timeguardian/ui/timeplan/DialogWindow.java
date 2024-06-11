package com.os.timeguardian.ui.timeplan;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.os.timeguardian.R;
import com.os.timeguardian.databinding.FragmentTimeplanBinding;
import com.os.timeguardian.utils.PackageUtil;

import java.util.List;

public class DialogWindow {

    private FragmentTimeplanBinding binding;
    private Context context;
    private Dialog dialog;
    private TimeplanFragment timeplanFragment;
    private Button cancelButton, applyButton;
    private Spinner spinner;
    private RadioButton soft, middle, hard;
    private RadioGroup group;
    String[] listItems;

    public DialogWindow(FragmentTimeplanBinding binding, Context context, TimeplanFragment timeplanFragment) {
        this.binding = binding;
        this.context = context;
        this.timeplanFragment = timeplanFragment;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_file);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);

        cancelButton = dialog.findViewById(R.id.cancelButton);
        applyButton = dialog.findViewById(R.id.logoutButton);
        spinner = dialog.findViewById(R.id.spinner);
        soft = dialog.findViewById(R.id.radioButton1);
        middle = dialog.findViewById(R.id.radioButton2);
        hard = dialog.findViewById(R.id.radioButton3);
        group = dialog.findViewById(R.id.radiogroup);

        setButtonFunctionality();
        setSpinnerItems();
    }

    private void setButtonFunctionality() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            String pLevel = "Nothing";
            @Override
            public void onClick(View v) {
                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (soft.isChecked()) {
                            pLevel = "Soft";
                        } else if (middle.isChecked()) {
                            pLevel = "Middle";
                        } else if (hard.isChecked()) {
                            pLevel = "Hard";
                        }
                    }
                });
                String item = (String) spinner.getSelectedItem();
                String str = "App: " + item + ", Punishment level: " + pLevel;
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                timeplanFragment.addPunishment(item, pLevel);
                timeplanFragment.notifyEditDialog();
                timeplanFragment.onResume();
                dialog.dismiss();
            }
        });
    }

    private void setSpinnerItems() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        List<String> tempList = PackageUtil.getAllPackageNames(context);
        listItems = tempList.stream().toArray(String[]::new);
        for (String s : listItems)
            spinnerAdapter.add(PackageUtil.getUserFriendlyAppName(s, context));
        spinnerAdapter.notifyDataSetChanged();
    }
    public Dialog getDialog() {
        return this.dialog;
    }
}
