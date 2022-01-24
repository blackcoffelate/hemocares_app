package com.example.hemocares.service;

import android.content.Context;

import androidx.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

public class Prefs extends TrayPreferences {
    public static final String PREF_FIRST_TIME = "first.time";
    public static final String PREF_IS_LOGEDIN = "is.login";
    public static String PREF_FIREBASE_TOKEN = "firebase.token";
    public static String PREF_FIREBASE_STORED = "firebase.is.stored";
    public static String PREF_ACCESS_TOKEN = "access.token";
    public static final String PREFS_LAST_REQUEST_FORGOT_PASSWORD = "last.request.forgot.password";
    public static final String PREF_MY_LATITUDE = "pref.my.latitude";
    public static final String PREF_MY_LONGITUDE = "pref.my.longitude";
    public static final String PREF_STORE_PROFILE = "pref.store.profile";
    public static String DEFAULT_INVALID_TOKEN = "0030";
    public static String PREF_ROLE = "role.users";

    public Prefs(@NonNull Context context) {
        super(context, "myAppPreferencesModule", 1);
    }
}
