package com.payments.app.seey.storage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.payments.app.seey.App;

public class StorageUtils {

    public static String GetUserName(Context context){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(App.User,null);
    }

    public static void SaveString(String id,Context context, String value){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(id ,value);
        editor.apply();
    }

    public static String GetString(String id,Context context){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(id,null);
    }





    public static void SavePassword(Context context, String password){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(App.Password ,password);
        editor.apply();
    }

    public static String GetPassword(Context context){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(App.Password,null);
    }
}
