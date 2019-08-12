package com.elegion.tracktor.di;

import android.content.Context;

import com.elegion.tracktor.App;

import toothpick.config.Module;

public class AppModule extends Module {
    private final App mApp;

    public AppModule(App app) {
        this.mApp = app;
        bind(App.class).toInstance(mApp);
        bind(Context.class).toInstance(mApp);
    }
}
