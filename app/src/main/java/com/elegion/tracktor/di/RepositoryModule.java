package com.elegion.tracktor.di;

import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.RealmRepository;

import toothpick.config.Module;

public class RepositoryModule extends Module {

    private final IRepository mRepository = new RealmRepository();

    public RepositoryModule() {
        bind(IRepository.class).toInstance(mRepository);
    }
}
