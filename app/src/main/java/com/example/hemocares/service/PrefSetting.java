package com.example.hemocares.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.hemocares.MainActivity;
import com.example.hemocares.service.SessionManager;

public class PrefSetting {
    public static String guid;
    public static String noreg;
    public static String fullname;
    public static String username;
    public static String password;
    public static String email;
    public static String phone;
    public static String blood_type;
    public static String address;
    public static String age;
    public static String birthdate;
    public static String status;
    public static String religion;
    public static String photo;
    public static String gender;
    public static String role;
    public static String created_at;
    public static String updated_at;

    Activity activity;

    public PrefSetting(Activity activity) {
        this.activity = activity;
    }

    public SharedPreferences getSharedPreferences() {
        SharedPreferences preferences = activity.getSharedPreferences("AccessDetails", Context.MODE_PRIVATE);
        return preferences;
    }

    public void isLogin(SessionManager sessionManager, SharedPreferences preferences) {
        sessionManager = new SessionManager(activity);
        if (sessionManager.isLoggedIn()) {
            preferences = getSharedPreferences();
            guid = preferences.getString("GUID", "");
            noreg = preferences.getString("GUID", "");
            fullname = preferences.getString("FULLNAME", "");
            username = preferences.getString("USERNAME", "");
            password = preferences.getString("PASSWORD", "");
            email = preferences.getString("EMAIL", "");
            phone = preferences.getString("PHONE", "");
            blood_type = preferences.getString("BLOOD_TYPE", "");
            address = preferences.getString("ADDRESS", "");
            age = preferences.getString("AGE", "");
            birthdate = preferences.getString("BIRTHDATE", "");
            status = preferences.getString("STATUS", "");
            religion = preferences.getString("RELIGION", "");
            photo = preferences.getString("PHOTO", "");
            gender = preferences.getString("GENDER", "");
            role = preferences.getString("ROLE", "");
            created_at = preferences.getString("CREATED_AT", "");
            updated_at = preferences.getString("UPDATED_AT", "");
        } else {
            sessionManager.setLogin(false);
            sessionManager.setSessId(0);
            Intent i = new Intent(activity, activity.getClass());
            activity.startActivity(i);
            activity.finish();
        }
    }

    public void checkLogin(SessionManager sessionManager, SharedPreferences preferences) {
        sessionManager = new SessionManager(activity);
        guid = preferences.getString("GUID", "");
        noreg = preferences.getString("GUID", "");
        fullname = preferences.getString("FULLNAME", "");
        username = preferences.getString("USERNAME", "");
        password = preferences.getString("PASSWORD", "");
        email = preferences.getString("EMAIL", "");
        phone = preferences.getString("PHONE", "");
        blood_type = preferences.getString("BLOOD_TYPE", "");
        address = preferences.getString("ADDRESS", "");
        age = preferences.getString("AGE", "");
        birthdate = preferences.getString("BIRTHDATE", "");
        status = preferences.getString("STATUS", "");
        religion = preferences.getString("RELIGION", "");
        photo = preferences.getString("PHOTO", "");
        gender = preferences.getString("GENDER", "");
        role = preferences.getString("ROLE", "");
        created_at = preferences.getString("CREATED_AT", "");
        updated_at = preferences.getString("UPDATED_AT", "");
        if (sessionManager.isLoggedIn()) {
            if (role.equals("2")) {
                Intent i = new Intent(activity, MainActivity.class);
                activity.startActivity(i);
                activity.finish();
            }
        }
    }

    public void storeRegIdSharedPreferences(Context context, String guid, String noreg, String fullname, String username,
                                            String password, String email, String phone, String blood_type,
                                            String address, String age, String birthdate, String status, String religion,
                                            String photo, String gender, String role, String created_at, String updated_at, SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("GUID", guid);
        editor.putString("NOREG", noreg);
        editor.putString("FULLNAME", fullname);
        editor.putString("USERNAME", username);
        editor.putString("PASSWORD", password);
        editor.putString("EMAIL", email);
        editor.putString("PHONE", phone);
        editor.putString("BLOOD_TYPE", blood_type);
        editor.putString("ADDRESS", address);
        editor.putString("AGE", age);
        editor.putString("BIRTHDATE", birthdate);
        editor.putString("STATUS", status);
        editor.putString("RELIGION", religion);
        editor.putString("PHOTO", photo);
        editor.putString("GENDER", gender);
        editor.putString("ROLE", role);
        editor.putString("CREATED_AT", created_at);
        editor.putString("UPDATED_AT", updated_at);
        editor.commit();
    }
}
