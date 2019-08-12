package com.elegion.tracktor.di;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;

import com.elegion.tracktor.ui.map.MainViewModel;
import com.elegion.tracktor.ui.results.ResultsViewModel;
import com.elegion.tracktor.util.CustomViewModelFactory;

import toothpick.config.Module;

public class ModelsModule extends Module {
    private CustomViewModelFactory mFactory = new CustomViewModelFactory();
    Fragment mFragment;

    public ModelsModule(Fragment fragment) {
        mFragment = fragment;
        bind(ResultsViewModel.class).toInstance(provideResultsViewModel());
        bind(MainViewModel.class).toInstance(provideMainViewModel());
    }

    public ResultsViewModel provideResultsViewModel(){
        return ViewModelProviders.of(mFragment, mFactory).get(ResultsViewModel.class);
    }

    public MainViewModel provideMainViewModel(){
        return ViewModelProviders.of(mFragment, mFactory).get(MainViewModel.class);
    }
}
