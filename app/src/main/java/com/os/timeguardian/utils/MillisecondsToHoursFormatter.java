package com.os.timeguardian.utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class MillisecondsToHoursFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        long hours = (long) value / 1000 / 60 / 60;
        return hours + "h";
    }
}
