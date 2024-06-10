package com.os.timeguardian.ui.timeplan;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.core.content.ContextCompat;

import com.os.timeguardian.R;
import com.os.timeguardian.databinding.FragmentTimeplanBinding;
import com.os.timeguardian.utils.PackageUtil;

import java.util.Arrays;
import java.util.List;

public class DialogWindow {

    private FragmentTimeplanBinding binding;
    private Context context;
    private Dialog dialog;
    private Button cancelButton, logoutButton;
    private Spinner spinner;
    private RadioButton soft, middle, hard;
    String[] listItems;

    public DialogWindow(FragmentTimeplanBinding binding, Context context) {
        this.binding = binding;
        this.context = context;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_file);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);

        cancelButton = dialog.findViewById(R.id.cancelButton);
        logoutButton = dialog.findViewById(R.id.logoutButton);
        spinner = dialog.findViewById(R.id.spinner);
        soft = dialog.findViewById(R.id.radioButton1);
        middle = dialog.findViewById(R.id.radioButton2);
        hard = dialog.findViewById(R.id.radioButton3);

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

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        for (String s : listItems) spinnerAdapter.add(PackageUtil.getUserFriendlyAppName(s, context));
        spinnerAdapter.notifyDataSetChanged();
    }

    public Dialog getDialog() {
        return this.dialog;
    }
}
