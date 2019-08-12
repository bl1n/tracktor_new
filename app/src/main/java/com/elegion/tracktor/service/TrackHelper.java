package com.elegion.tracktor.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.elegion.tracktor.R;
import com.elegion.tracktor.event.AddPositionToRouteEvent;
import com.elegion.tracktor.event.StartTrackEvent;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TrackHelper {

    @Inject
    Context context;

    private Location mLastLocation;
    private LatLng mLastPosition;
    private List<LatLng> mRoute = new ArrayList<>();
    private double mDistance;

    public void onLocationResult(LocationResult locationResult){
        if (locationResult != null) {

            if (isFirstPoint()) {
                addPointToRoute(locationResult.getLastLocation());
                EventBus.getDefault().post(new StartTrackEvent(mLastPosition));

            } else {

                Location newLocation = locationResult.getLastLocation();
                LatLng newPosition = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());

                if (positionChanged(newPosition)) {
                    mRoute.add(newPosition);
                    LatLng prevPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    mDistance += SphericalUtil.computeDistanceBetween(prevPosition, newPosition);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    long unit = Long.valueOf(preferences.getString(context.getString(R.string.pref_key_unit), context.getString(R.string.pref_default_value_unit)));

                    EventBus.getDefault().post(new AddPositionToRouteEvent(prevPosition, newPosition, mDistance));
                }

                mLastLocation = newLocation;
                mLastPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }
        }
    }

    private boolean positionChanged(LatLng newPosition) {
        return mLastLocation.getLongitude() != newPosition.longitude || mLastLocation.getLatitude() != newPosition.latitude;
    }

    private void addPointToRoute(Location lastLocation) {
        mLastLocation = lastLocation;
        mLastPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mRoute.add(mLastPosition);
    }

    private boolean isFirstPoint() {
        return mRoute.size() == 0 && mLastLocation == null && mLastPosition == null;
    }

    public double getDistance(){
        return mDistance;
    }

    public List<LatLng> getRoute(){
        return mRoute;
    }

}
