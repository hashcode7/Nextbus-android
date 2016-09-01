package com.example.nandayemparala.myapplication.application;

import android.support.annotation.NonNull;
import android.widget.Toast;

/**
 * Created by Nanda Yemparala on 8/30/16.
 */
public class App extends android.app.Application {


    static App application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }


    public static void showToast(@NonNull String message){
        Toast.makeText(application.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}