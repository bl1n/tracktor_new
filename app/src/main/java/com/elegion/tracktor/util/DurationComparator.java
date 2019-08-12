package com.elegion.tracktor.util;

import com.elegion.tracktor.data.model.Track;

import java.util.Comparator;

public class DurationComparator implements Comparator<Track> {

    @Override
    public int compare(Track o1, Track o2) {
        int returnVal = 0;

        if(o1.getDuration()<o2.getDuration()){
            returnVal = -1;
        } else if(o1.getDuration()>o2.getDuration()){
            returnVal=1;
        }

        return returnVal;
    }
}
