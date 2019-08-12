package com.elegion.tracktor.event;

public class OpenResultEvent {
    public long getTrackId() {
        return mTrackId;
    }

    private final long mTrackId;

    public OpenResultEvent(long trackId) {
        mTrackId = trackId;
    }
}
