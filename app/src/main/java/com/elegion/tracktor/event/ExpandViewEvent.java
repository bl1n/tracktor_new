package com.elegion.tracktor.event;

import android.util.Log;

public class ExpandViewEvent {
    private long trackId;
    private int position;


    public ExpandViewEvent(long trackId, int position) {
        this.trackId = trackId;
        this.position = position;
        Log.d("Debug", "ExpandViewEvent: ");
//        Log.d("Debug", "bind: " + trackId);

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

}
