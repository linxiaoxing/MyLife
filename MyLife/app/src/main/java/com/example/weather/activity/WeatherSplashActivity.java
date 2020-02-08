package com.example.weather.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mylife.R;

import java.lang.ref.WeakReference;

public class WeatherSplashActivity extends Activity{

    private SwitchHandler mHandler = new SwitchHandler(this);
    //  SplashViewSettings splashViewSettings = new SplashViewSettings();
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //activity切换的淡入淡出效果
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);

        setContentView( R.layout.activity_weather_splash);

        TextView text = (TextView)findViewById(R.id.splash_tv);
        /* text.setGravity(Gravity.CENTER);*/
        text.setText("多知天气");
        text.setTextSize(20 * getResources().getDisplayMetrics().density);
        text.setPadding(20, 20, 20, 20);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setDuration(2000);
        animationSet.setFillAfter(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0,1,0,1);
        scaleAnimation.setDuration(2000);
        animationSet.addAnimation(scaleAnimation);

        TranslateAnimation translateAnimation = new TranslateAnimation(0,0,0,-200);
        translateAnimation.setDuration(2000);
        animationSet.addAnimation(translateAnimation);

        text.setAnimation(animationSet);
        mHandler.sendEmptyMessageDelayed(1, 2000);
    }

    private static class SwitchHandler extends Handler{
        private WeakReference<WeatherSplashActivity> mWeakReference;

        SwitchHandler(WeatherSplashActivity activity) {
            mWeakReference = new WeakReference<WeatherSplashActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            WeatherSplashActivity activity = mWeakReference.get();
            if (activity != null) {
                //WeatherActivity.launch(activity);
                activity.finish();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 开屏展示界面的 onDestroy() 回调方法中调用
        // SpotManager.getInstance(SplashActivity.this).onDestroy();
    }

}