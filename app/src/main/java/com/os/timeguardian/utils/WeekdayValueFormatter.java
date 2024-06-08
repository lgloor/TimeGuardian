package com.os.timeguardian.utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.LocalDate;

public class WeekdayValueFormatter extends ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        return LocalDate.now().minusDays(6 - (int) value)
                .getDayOfWeek()
                .toString()
                .substring(0, 3);
    }

}
