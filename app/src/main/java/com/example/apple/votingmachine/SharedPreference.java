package com.example.apple.votingmachine;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static com.google.android.gms.flags.impl.SharedPreferencesFactory.getSharedPreferences;

public class SharedPreference {

    public static void saveUserName(Context context, String username) throws Exception {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("username", username);
        editor.apply();
    }

    public static String getUserName(Context context) throws Exception {
        return getSharedPreferences(context).getString("username","default name");
    }
}
