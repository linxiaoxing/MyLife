package com.example.ocr.module;

import android.graphics.Bitmap;

public interface MainContract {

    interface View{
        void updateUI(String s);
        void nullData();
    }

    interface Presenter{
        void getAccessToken();
        void getRecognitionResultByImage(Bitmap bitmap);
    }

}