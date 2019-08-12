package com.elegion.tracktor;

import android.app.Application;
import android.content.Context;

import com.elegion.tracktor.di.AppModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;
import toothpick.registries.FactoryRegistryLocator;
import toothpick.registries.MemberInjectorRegistryLocator;
import toothpick.smoothie.module.SmoothieApplicationModule;

public class App extends Application {
    private static Scope sAppScope;
    private static Context sContext;
    public static boolean counterServiceRunning = false;


    @Override
    public void onCreate() {
        super.onCreate();

        sContext = this;
        Realm.init(this);



        Toothpick.setConfiguration(Configuration.forProduction().disableReflection());
        MemberInjectorRegistryLocator.setRootRegistry(new com.elegion.tracktor.MemberInjectorRegistry());
        FactoryRegistryLocator.setRootRegistry(new com.elegion.tracktor.FactoryRegistry());

        sAppScope = Toothpick.openScope(App.class);
        sAppScope.installModules(new SmoothieApplicationModule(this), new AppModule(this));
    }

    public static Context getContext(){
        return sContext;
    }



}
