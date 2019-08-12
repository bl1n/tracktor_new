package com.elegion.tracktor.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.elegion.tracktor.App;
import com.elegion.tracktor.R;


//todo create class PreferencesHelper with static methods instead of this class
public class SharedPreferencesHelper {

    private static SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(App.getContext());
    }

    public static int getTrackColorFromPreferences(){
        int trackColor = Integer.parseInt(getPreferences().getString("trackColor", "1"));
        switch (trackColor){
            case 1:{
                return R.color.color_black;
            }
            case 2:{
                return R.color.color_red;
            }
            case 3:{
                return R.color.color_green;
            }
            case 4:{
                return R.color.color_blue;
            }
        }
        return R.color.color_black;
    }

    public static int getTrackWidthFromPreferences(){
        return Integer.parseInt(getPreferences().getString("trackWidth", "10"));
    }

    public static int getTrackIcon(){
        int iconType = Integer.parseInt(getPreferences().getString("iconType", "1"));
        return iconType==1?R.drawable.ic_action_name:R.drawable.ic_man_name;
    }


}
