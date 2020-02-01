package com.example.mylife.di.component;

import android.app.Activity;
import android.content.Context;

import com.example.mylife.activity.addtask.AddTaskActivity;
import com.example.mylife.activity.login.LoginActivity;
import com.example.mylife.activity.register.RegisterActivity;
import com.example.mylife.di.module.ActivityModule;
import com.example.mylife.di.scope.ContextLife;
import com.example.mylife.di.scope.PerActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    @ContextLife("Activity")
    Context getActivityContext();

    @ContextLife("Application")
    Context getApplicationContext();

    Activity getActivity();

    void inject(AddTaskActivity activity);

    void inject(LoginActivity activity);

    void inject(RegisterActivity activity);
}
