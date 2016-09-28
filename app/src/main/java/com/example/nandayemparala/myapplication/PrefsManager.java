package com.example.nandayemparala.myapplication;

import android.content.Context;

import com.example.nandayemparala.myapplication.model.ormlite.DatabaseHelper;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

/**
 * Created by nandayemparala on 9/28/16.
 */

public final class PrefsManager {

    private static PrefsManager sInstance;

    public static synchronized PrefsManager getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new PrefsManager();

            /// init Hawk
            Hawk.init(context)
                    .setEncryption(new NoEncryption())
                    .build();
        }
        return sInstance;
    }

    public static boolean isFirstStart(){
        return Hawk.get("firststart", true);
    }

    public static boolean setIntroAsShown(){
        return Hawk.put("firststart", false);
    }

    public static boolean routesLoaded(){
        return Hawk.get("routes_loaded", false);
    }
}
