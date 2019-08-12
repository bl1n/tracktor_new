package com.elegion.tracktor.event;

public class StartActivityEvent {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public StartActivityEvent(String value) {
        this.value = value;
    }
}
