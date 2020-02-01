package com.example.mylife.di.module;

import android.app.Activity;
import android.content.Context;

import com.example.mylife.di.scope.ContextLife;
import com.example.mylife.di.scope.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @PerActivity
    @ContextLife("Activity")
    public Context provideActivityContext() {
        return mActivity;
    }

    @Provides
    @PerActivity
    public Activity provideActivity() {
        return mActivity;
    }
}