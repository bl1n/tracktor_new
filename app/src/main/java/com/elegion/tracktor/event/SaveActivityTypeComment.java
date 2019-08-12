package com.elegion.tracktor.event;

public class SaveActivityTypeComment {
    private String mComment;
    private String mActivityType;

    public SaveActivityTypeComment(String comment, String activityType) {
        mComment = comment;
        mActivityType = activityType;
    }

    public String getComment() {
        return mComment;
    }

    public String getActivityType() {
        return mActivityType;
    }
}
