package com.vasilkoff.easyvpnfree.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.vasilkoff.easyvpnfree.App;

/**
 * Created by Kusenko on 16.12.2016.
 */

public class PropertiesService {

    private static SharedPreferences prefs;
    private static final String SHARED_PREFERENCES_NAME = "properties";
    private static final String DOWNLOADED_DATA_KEY = "downloaded_data";
    private static final String UPLOADED_DATA_KEY = "uploaded_data";

    private synchronized static SharedPreferences getPrefs(){
        if (prefs == null) {
            prefs = App.getInstance().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
        return prefs;
    }

    public static long getDownloaded(){
        return getPrefs().getLong(DOWNLOADED_DATA_KEY, 0);
    }

    public static void setDownloaded(long count){
        getPrefs().edit().putLong(DOWNLOADED_DATA_KEY, count).apply();
    }

    public static long getUploaded(){
        return getPrefs().getLong(UPLOADED_DATA_KEY, 0);
    }

    public static void setUploaded(long count){
        getPrefs().edit().putLong(UPLOADED_DATA_KEY, count).apply();
    }
}
