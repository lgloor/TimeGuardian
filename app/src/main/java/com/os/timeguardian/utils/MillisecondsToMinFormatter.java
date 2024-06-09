package com.os.timeguardian.utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class MillisecondsToMinFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        long minutes = (long) value / 1000 / 60;
        return minutes + "min";

    }
}
