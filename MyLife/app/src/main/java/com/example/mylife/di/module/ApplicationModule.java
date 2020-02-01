package com.example.mylife.di.module;

import android.content.Context;

import com.example.mylife.base.App;
import com.example.mylife.di.scope.ContextLife;
import com.example.mylife.di.scope.PerApp;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private App mApplication;

    public ApplicationModule(App application) {
        mApplication = application;
    }

    @Provides
    @PerApp
    @ContextLife("Application")
    public Context provideApplicationContext() {
        return mApplication.getApplicationContext();
    }
}
