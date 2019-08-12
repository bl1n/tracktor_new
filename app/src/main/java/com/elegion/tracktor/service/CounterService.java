package com.elegion.tracktor.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.elegion.tracktor.App;
import com.elegion.tracktor.R;
import com.elegion.tracktor.event.AutoShutdownEvent;
import com.elegion.tracktor.event.GetRouteEvent;
import com.elegion.tracktor.event.StopTrackEvent;
import com.elegion.tracktor.event.UpdateRouteEvent;
import com.elegion.tracktor.event.UpdateTimerEvent;
import com.elegion.tracktor.ui.map.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import toothpick.Scope;
import toothpick.Toothpick;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class CounterService extends Service {

    public static final int UPDATE_INTERVAL = 15_000;
    public static final int UPDATE_FASTEST_INTERVAL = 5_000;
    public static final int UPDATE_MIN_DISTANCE = 20;

    TrackHelper mTrackHelper;
    NotificationHelper mNotificationHelper;

    private Disposable mTimerDisposable;
    private long mShutDownDuration;
    private long mSeconds = 0;

    private FusedLocationProviderClient mFusedLocationClient;

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            mTrackHelper.onLocationResult(locationResult);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        App.counterServiceRunning = true;
        mTrackHelper = new TrackHelper();
        mNotificationHelper = new NotificationHelper();

        Scope scope = Toothpick.openScope(App.class);
        Toothpick.inject(this, scope);

        EventBus.getDefault().register(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {

            mNotificationHelper.createNotification(this);

            final LocationRequest locationRequest = new LocationRequest()
                    .setInterval(UPDATE_INTERVAL)
                    .setFastestInterval(UPDATE_FASTEST_INTERVAL)
                    .setSmallestDisplacement(UPDATE_MIN_DISTANCE)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);

            startTimer();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            mShutDownDuration = Long.valueOf(preferences.getString(getString(R.string.pref_key_shutdown), "-1"));

        } else {
            Toast.makeText(this, R.string.permissions_denied, Toast.LENGTH_SHORT).show();
        }

    }

    private void startTimer() {
        mTimerDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(CounterService.this::onTimerUpdate);
    }

    private void onTimerUpdate(long totalSeconds) {

        mSeconds = totalSeconds;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        long unit = Long.valueOf(preferences.getString(getString(R.string.pref_key_unit), getString(R.string.pref_default_value_unit)));

        EventBus.getDefault().post(new UpdateTimerEvent(totalSeconds, mTrackHelper.getDistance()));

        mNotificationHelper.notifyNotification(totalSeconds, mTrackHelper.getDistance());

        if (mShutDownDuration != -1 && totalSeconds == mShutDownDuration) {
            App.counterServiceRunning = false;
            EventBus.getDefault().post(new AutoShutdownEvent());
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction("stop");
            startActivity(intent);
        }

    }

    @Override
    public void onDestroy() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        long unit = Long.valueOf(preferences.getString(getString(R.string.pref_key_unit), getString(R.string.pref_default_value_unit)));
        EventBus.getDefault().post(new UpdateTimerEvent(mSeconds, mTrackHelper.getDistance()));

        EventBus.getDefault().post(new StopTrackEvent(mTrackHelper.getRoute()));

        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        mTimerDisposable.dispose();

        stopForeground(true);

        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRoute(GetRouteEvent event) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        long unit = Long.valueOf(preferences.getString(getString(R.string.pref_key_unit), getString(R.string.pref_default_value_unit)));
        EventBus.getDefault().post(new UpdateRouteEvent(mTrackHelper.getRoute(), mTrackHelper.getDistance()));
    }

}
