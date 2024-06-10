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
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.os.timeguardian.R;
import com.os.timeguardian.databinding.FragmentTimeplanBinding;
import com.os.timeguardian.utils.PackageUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DialogWindowEdit {

    private FragmentTimeplanBinding binding;
    private Context context;
    private Dialog dialog;
    private TimeplanFragment timeplanFragment;
    private Button cancelButton, applyButton;
    private Spinner spinner;
    private RadioButton soft, middle, hard;
    private RadioGroup group;
    private TextView textView;
    String[] listItems;

    public DialogWindowEdit(FragmentTimeplanBinding binding, Context context, TimeplanFragment timeplanFragment) {
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
        textView = dialog.findViewById(R.id.textView);
        textView.setText("Delete Punishment.");
        group.setVisibility(View.GONE);
        soft.setClickable(false);
        middle.setClickable(false);
        hard.setClickable(false);

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
            @Override
            public void onClick(View v) {
                String item = (String) spinner.getSelectedItem();
                String str = "App: " + " will be deleted";
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                timeplanFragment.deletePunishment(item);
                updateSpinnerItems();
                dialog.dismiss();
            }
        });
    }

    public void updateSpinnerItems() {
        setSpinnerItems();
    }
    private void setSpinnerItems() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        HashMap<String, String> tempList = timeplanFragment.getAllPunishments();
        Set<String> keySet = tempList.keySet();
        listItems = keySet.toArray(new String[0]);
        for (String s : listItems) {
            spinnerAdapter.add(s);
        }
        //List<String> tempList = PackageUtil.getAllPackageNames(context);
        //listItems = tempList.stream().toArray(String[]::new);
        //for (String s : listItems)
            //spinnerAdapter.add(PackageUtil.getUserFriendlyAppName(s, context));
        spinnerAdapter.notifyDataSetChanged();
    }

    public Dialog getDialog() {
        return this.dialog;
    }
}

