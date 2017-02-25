package com.vasilkoff.easyvpnfree.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.vasilkoff.easyvpnfree.App;

/**
 * Created by Kusenko on 16.12.2016.
 */

public class PropertiesService {

    private static SharedPreferences prefs;
    private static final String DOWNLOADED_DATA_KEY = "downloaded_data";
    private static final String UPLOADED_DATA_KEY = "uploaded_data";
    private static final String AUTOMATIC_SWITCHING = "automaticSwitching";
    private static final String COUNTRY_PRIORITY = "countryPriority";
    private static final String CONNECT_ON_START = "connectOnStart";
    private static final String AUTOMATIC_SWITCHING_SECONDS = "automaticSwitchingSeconds";
    private static final String SELECTED_COUNTRY = "selectedCountry";
    private static final String SHOW_RATING = "show_rating";
    private static final String SHOW_NOTE = "show_note";

    private synchronized static SharedPreferences getPrefs(){
        if (prefs == null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
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

    public static boolean getConnectOnStart(){
        return getPrefs().getBoolean(CONNECT_ON_START, false);
    }

    public static boolean getAutomaticSwitching(){
        return getPrefs().getBoolean(AUTOMATIC_SWITCHING, true);
    }

    public static int getAutomaticSwitchingSeconds(){
        return getPrefs().getInt(AUTOMATIC_SWITCHING_SECONDS, 40);
    }

    public static boolean getCountryPriority(){
        return getPrefs().getBoolean(COUNTRY_PRIORITY, false);
    }

    public static String getSelectedCountry(){
        return getPrefs().getString(SELECTED_COUNTRY, null);
    }

    public static boolean getShowRating(){
        return getPrefs().getBoolean(SHOW_RATING, true);
    }

    public static void setShowRating(boolean showRating){
        getPrefs().edit().putBoolean(SHOW_RATING, showRating).apply();
    }

    public static boolean getShowNote(){
        return getPrefs().getBoolean(SHOW_NOTE, true);
    }

    public static void setShowNote(boolean showNote){
        getPrefs().edit().putBoolean(SHOW_NOTE, showNote).apply();
    }


}
