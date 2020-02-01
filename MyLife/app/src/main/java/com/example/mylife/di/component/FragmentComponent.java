package com.example.mylife.di.component;

import android.app.Activity;
import android.content.Context;

import com.example.mylife.di.module.FragmentModule;
import com.example.mylife.di.scope.ContextLife;
import com.example.mylife.di.scope.PerFragment;
import com.example.mylife.fragment.done.DoneFragment;
import com.example.mylife.fragment.undo.UndoneFragment;

import dagger.Component;

@PerFragment
@Component(dependencies = ApplicationComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {
    @ContextLife("Activity")
    Context getActivityContext();

    @ContextLife("Application")
    Context getApplicationContext();

    Activity getActivity();

    void inject(UndoneFragment fragment);


    void inject(DoneFragment fragment);
}