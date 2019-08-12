package com.elegion.tracktor.ui.results;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.util.Log;

import com.elegion.tracktor.App;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.RealmRepository;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.di.RepositoryModule;
import com.elegion.tracktor.event.DeleteTrackEvent;
import com.elegion.tracktor.event.ExpandViewEvent;
import com.elegion.tracktor.util.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;

/**
 * @author Azret Magometov
 */
public class ResultsViewModel extends ViewModel {
    public static final int SORT_ORDER_ASC = 1;
    public static final int SORT_ORDER_DESC = 2;
    public static final int SORT_BY_START_DATE = 1;
    public static final int SORT_BY_DURATION = 2;
    public static final int SORT_BY_DISTANCE = 3;

    private int mRepositorySortOrder;
    private int mRepositorySortBy;

    @Inject
    RealmRepository mRepository;

    private MutableLiveData<List<Track>> mTracks = new MutableLiveData<>();

    private MutableLiveData<Track> mTrack = new MutableLiveData<>();

    private MutableLiveData<Boolean> deleted = new MutableLiveData<>();

    private MutableLiveData<String> mEnergy = new MutableLiveData<>();

    private MutableLiveData<Integer> mSortOrder = new MutableLiveData<>();// add observer to change icons

    private MutableLiveData<Integer> mSortBy = new MutableLiveData<>();// add observer to change icons




    private final Scope mScope;

    private int mField = 1;


    public ResultsViewModel() {
        EventBus.getDefault().register(this);
        mScope = Toothpick.openScopes(App.class, this);
        mScope.installModules(new RepositoryModule());
        Toothpick.inject(this, mScope);
        deleted.postValue(false);

    }

    //tracks
    public void loadTracks() {
        mTracks.postValue(mRepository.getAll(mRepositorySortOrder, mRepositorySortBy));
    }

    public void changeSortOrder(){
        if (mRepositorySortOrder == IRepository.SORT_ORDER_ASC) {
            mRepositorySortOrder = IRepository.SORT_ORDER_DESC;
            mSortOrder.postValue(SORT_ORDER_DESC);
        } else{
            mRepositorySortOrder = IRepository.SORT_ORDER_ASC;
            mSortOrder.postValue(SORT_ORDER_ASC);
        }
        loadTracks();
    }

    public void changeSortBy(){
        switch (mRepositorySortBy){
            case IRepository.SORT_BY_START_DATE:{
                mRepositorySortBy = IRepository.SORT_BY_DURATION;
                mSortBy.postValue(SORT_BY_DURATION);
                break;
            }
            case IRepository.SORT_BY_DURATION:{
                mRepositorySortBy = IRepository.SORT_BY_DISTANCE;
                mSortBy.postValue(SORT_BY_DISTANCE);
                break;
            }
            default:{
                mRepositorySortBy = IRepository.SORT_BY_START_DATE;
                mSortBy.postValue(SORT_BY_START_DATE);
                break;
            }
        }
        loadTracks();
    }

    public MutableLiveData<List<Track>> getTracks() {
        return mTracks;
    }

    //track
    public void loadTrack(long trackId) {
        final Track track = mRepository.getItem(trackId);
        mTrack.postValue(track);
    }

    public MutableLiveData<Track> getTrack() {
        return mTrack;
    }

    //delete
    public MutableLiveData<Boolean> isDeleted() {
        return deleted;
    }

    public void deleteTrack(long id) {
        mRepository.deleteItem(id);
        deleted.postValue(true);
    }

    //energy
    public MutableLiveData<String> getEnergy() {
        return mEnergy;
    }

    public void loadEnergy(long trackId, int activityType, SharedPreferences preferences) {
        Track track = mRepository.getItem(trackId);
        double weight = Double.parseDouble(preferences.getString("weight", "1"));
        double energy = track.getDuration() * (activityType + 1) * weight;
        mEnergy.postValue(StringUtil.getEnergyText(energy));
    }

    public void updateTrackComment(long trackId, String string) {
        Track track = mRepository.getItem(trackId);
        track.setComment(string);
        mRepository.updateItem(track);
        mTrack.postValue(track);
        loadTracks();
    }


//    @Subscribe(threadMode = ThreadMode.MAIN)
    public void expandHolder(ExpandViewEvent event) {
        Track track = mRepository.getItem(event.getTrackId());
        track.setExpanded(!track.isExpanded());
        mRepository.updateItem(track);
        if(track.isExpanded()){
            Log.d("Debug", "expandHolder: ");
        }
        loadTracks();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteTrack(DeleteTrackEvent event) {
        mRepository.deleteItem(event.getTrackId());
        loadTracks();
    }


    public void sortTracks() {
        List<Track> value = mTracks.getValue();
        Collections.sort(value, (o1, o2) -> o1.getDistance().compareTo(o2.getDistance()));
    }

    @Override
    protected void onCleared() {
        EventBus.getDefault().unregister(this);
        Toothpick.closeScope(mScope);
        super.onCleared();
    }

    public void createExample(){
        mRepository.createAndInsertTrackFrom(123123, 123," ",12);
    }


}
