package com.elegion.tracktor.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.elegion.tracktor.App;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

public class StringUtil {


    private static Context getContext() {
        return App.getContext();
    }

    private static String getUnit() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return preferences.getString("unit", "");
    }

    public static String getDistanceText(double value) {
        int unit = Integer.parseInt(getUnit());
        switch (unit) {
            case 1: {
                return round(value, 0) + " м";
            }
            case 2: {
                return round(value / 1000, 0) + " км";
            }
            case 3: {
                return round(value / 1609.34, 0) + " m";
            }
            case 4: {
                return round(value / 0.3048, 0) + " ft";
            }
        }
        return round(value, 0) + " м.";
    }

    public static String getTimeText(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String getSpeedText(double value) {
        int unit = Integer.parseInt(getUnit());
        switch (unit) {
            case 1: {
                return round(value, 0) + " м/c";
            }
            case 2: {
                return round(value / 1000, 0) + " км/c";
            }
            case 3: {
                return round(value / 1609.34, 0) + " m/s";
            }
            case 4: {
                return round(value / 0.3048, 0) + " ft/s";
            }
        }
        return round(value, 0) + " м/c";
    }

    public static String getEnergyText(double value) {
        return round(value, 0) + " кКал";
    }

    public static String getDateText(Date date) {

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(date);
    }

    public static String round(double value, int places) {
        return String.format("%." + places + "f", value);
    }
}
