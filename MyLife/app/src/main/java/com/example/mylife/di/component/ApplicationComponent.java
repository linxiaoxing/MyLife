package com.example.mylife.di.component;

import android.content.Context;

import com.example.mylife.di.module.ApplicationModule;
import com.example.mylife.di.scope.ContextLife;
import com.example.mylife.di.scope.PerApp;

import dagger.Component;

@PerApp
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    @ContextLife("Application")
    Context getApplication();
}
