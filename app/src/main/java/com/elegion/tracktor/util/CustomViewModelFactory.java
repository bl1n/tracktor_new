package com.elegion.tracktor.util;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.elegion.tracktor.App;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.di.RepositoryModule;
import com.elegion.tracktor.ui.map.MainViewModel;
import com.elegion.tracktor.ui.results.ResultsViewModel;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;

/**
 * @author Azret Magometov
 */
public class CustomViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @Inject
    IRepository mRepository;

    public CustomViewModelFactory() {
        Scope scope = Toothpick.openScopes(App.class, this);
        scope.installModules(new RepositoryModule());
        Toothpick.inject(this, scope);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ResultsViewModel.class)) {
            return (T) new ResultsViewModel();
        }
        else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel();
        }
        throw new IllegalArgumentException("Wrong ViewModel class");
    }

}
