package sxwang.me.ohmyyeelight;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

/**
 * Created by Shaoxing on 22/04/2017.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(Utils.isNightMode(this)
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
