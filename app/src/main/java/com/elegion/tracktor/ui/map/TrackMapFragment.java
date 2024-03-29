package com.elegion.tracktor.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.elegion.tracktor.App;
import com.elegion.tracktor.R;
import com.elegion.tracktor.di.ModelsModule;
import com.elegion.tracktor.event.AddPositionToRouteEvent;
import com.elegion.tracktor.event.GetRouteEvent;
import com.elegion.tracktor.event.StartTrackEvent;
import com.elegion.tracktor.event.StopTrackEvent;
import com.elegion.tracktor.event.UpdateRouteEvent;
import com.elegion.tracktor.ui.results.ResultsActivity;
import com.elegion.tracktor.util.SharedPreferencesHelper;
import com.elegion.tracktor.util.ScreenshotMaker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * @author Azret Magometov
 */
public class TrackMapFragment extends SupportMapFragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    public static final int DEFAULT_ZOOM = 15;

    private GoogleMap mMap;

    @Inject
    MainViewModel mMainViewModel;
    private SharedPreferences mPreferences;
    private String mTrackColor;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    public void configure() {
        getMapAsync(this);
        final Scope scope =
                Toothpick.openScopes(App.class, this);
        scope.installModules(new ModelsModule(this));
        Toothpick.inject(this, scope);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        mTrackColor = mPreferences.getString("trackColor", "#000");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this::initMap);
        if(getActivity().getIntent().getAction().equals("stop")){
            App.counterServiceRunning = false;
            getActivity().stopService(((MainActivity)getActivity()).getServiceIntent());
        }
    }

    private void initMap() {
        Context context = getContext();
        if (context != null && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new GetRouteEvent());
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPositionToRoute(AddPositionToRouteEvent event) {

        int trackColorFromPreferences = SharedPreferencesHelper.getTrackColorFromPreferences();
        int trackWidthFromPreferences = SharedPreferencesHelper.getTrackWidthFromPreferences();
        mMap.addPolyline(new PolylineOptions()
                .add(event.getLastPosition(), event.getNewPosition())
                .color(getResources().getColor(trackColorFromPreferences))
                .width(trackWidthFromPreferences));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(event.getNewPosition(), DEFAULT_ZOOM));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateRoute(UpdateRouteEvent event) {
        mMap.clear();

        List<LatLng> route = event.getRoute();
        mMap.addPolyline(new PolylineOptions().addAll(route));
        addMarker(route.get(0), getString(R.string.start));
        zoomRoute(route);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartRoute(StartTrackEvent event) {
        mMap.clear();
        addMarker(event.getStartPosition(), getString(R.string.start));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopRoute(StopTrackEvent event) {
        List<LatLng> route = event.getRoute();
        if (route.isEmpty()) {
            Toast.makeText(getContext(), R.string.dont_stay, Toast.LENGTH_SHORT).show();
        } else {
            addMarker(route.get(route.size() - 1), getString(R.string.end));

            takeMapScreenshot(route, bitmap -> {
                String base64image = ScreenshotMaker.toBase64(bitmap);
                long resultId = mMainViewModel.saveResults(base64image);
                ResultsActivity.start(App.getContext(), resultId);
            });
        }
    }

    private void addMarker(LatLng position, String text) {
        int icon = SharedPreferencesHelper.getTrackIcon();
        mMap.addMarker(new MarkerOptions().position(position).title(text)
                .icon(BitmapDescriptorFactory.fromResource(icon)));
    }

    private void takeMapScreenshot(List<LatLng> route, GoogleMap.SnapshotReadyCallback snapshotCallback) {
        zoomRoute(route);
        mMap.snapshot(snapshotCallback);
    }

    private void zoomRoute(List<LatLng> route) {
        if (route.size() == 1) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.get(0), DEFAULT_ZOOM));
        } else {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : route) {
                builder.include(point);
            }
            int padding = 100;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
            mMap.moveCamera(cu);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

}
