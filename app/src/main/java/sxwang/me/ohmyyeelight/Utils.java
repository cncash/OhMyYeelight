package sxwang.me.ohmyyeelight;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    public static String toUnderlined(String s) {
        char[] chars = s.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i])) {
                if (i > 0 && !Character.isUpperCase(chars[i - 1])) {
                    builder.append("_");
                }
                builder.append(Character.toLowerCase(chars[i]));
            } else {
                builder.append(chars[i]);
            }
        }
        return builder.toString();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
