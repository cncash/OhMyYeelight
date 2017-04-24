package sxwang.me.ohmyyeelight;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Shaoxing on 23/04/2017.
 */

public class Utils {
    private Utils() {
    }

    public static double squareSum(double x, double y) {
        return x * x + y * y;
    }

    public static int squareSum(int x, int y) {
        return x * x + y * y;
    }

    public static boolean isNightMode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("night_mode", true);
    }
}
