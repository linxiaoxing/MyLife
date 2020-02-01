package com.example.mylife.di.module;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

import com.example.mylife.di.scope.ContextLife;
import com.example.mylife.di.scope.PerFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {
    private Fragment mFragment;

    public FragmentModule(Fragment fragment) {
        mFragment = fragment;
    }

    @Provides
    @PerFragment
    @ContextLife("Activity")
    public Context provideActivityContext() {
        return mFragment.getActivity();
    }

    @Provides
    @PerFragment
    public Activity provideActivity() {
        return mFragment.getActivity();
    }

    @Provides
    @PerFragment
    public Fragment provideFragment() {
        return mFragment;
    }
}