package com.elegion.tracktor.event;

public class ChangeCommentEvent {
    private String mComment;
    private long mTrackId;

    public ChangeCommentEvent(String comment, long trackId) {
        mComment = comment;
        mTrackId = trackId;
    }

    public String getComment() {
        return mComment;
    }

    public long getTrackId() {
        return mTrackId;
    }
}
