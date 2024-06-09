package com.os.timeguardian.utils;

import android.annotation.SuppressLint;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class HourValueFormatter extends ValueFormatter {

    @SuppressLint("DefaultLocale")
    @Override
    public String getFormattedValue(float value) {
        return String.format("%02dh", (int) value);
    }
}
