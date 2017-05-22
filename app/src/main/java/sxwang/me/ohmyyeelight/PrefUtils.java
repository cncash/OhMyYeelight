package sxwang.me.ohmyyeelight;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Shaoxing on 03/05/2017.
 */

public class PrefUtils {
    private PrefUtils() {
    }

    public static String getLightTransitionMode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("smooth_transition", true)
                ? "smooth"
                : "sudden";
    }
}
