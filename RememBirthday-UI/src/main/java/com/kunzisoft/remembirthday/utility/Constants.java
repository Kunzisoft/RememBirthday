package com.kunzisoft.remembirthday.utility;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by joker on 17/07/17.
 */

public class Constants {

    public static final String DEVELOPER = "Jeremy JAMET";
    public static final String EMAIL = "contact@kunzisoft.com";
    public static final String URL_WEB_SITE = "http://kunzisoft.com/";
    public static final String URL_PARTICIPATION = URL_WEB_SITE+"#contribute";

    /**
     * Get the current package version.
     *
     * @return The current version.
     */
    public static String getVersion(Context context) {
        String result;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

            result = String.format("%s (%s)", info.versionName, info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(Utility.class.getSimpleName(), "Unable to get application version", e);
            result = "Unable to get application version.";
        }

        return result;
    }
}
