package com.dispmoveis.compsupermercadosmovel;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {

    static String SERVER_URL_BASE = "https://comparador-supermercados.herokuapp.com/";

    public static void setLogin(Context context, String login) {
        SharedPreferences mPrefs = context.getSharedPreferences("configs", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("login", login).commit();
    }

    public static String getLogin(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("configs", 0);
        return mPrefs.getString("login", "");
    }

    public static void setPassword(Context context, String password) {
        SharedPreferences mPrefs = context.getSharedPreferences("configs", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("password", password).commit();
    }

    public static String getPassword(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("configs", 0);
        return mPrefs.getString("password", "");
    }

    public static void setNotifications(Context context, boolean isEnable) {
        SharedPreferences mPrefs = context.getSharedPreferences("configs", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putBoolean("notications", isEnable).commit();
    }

    public static boolean getNotificationsStatus(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("configs", 0);
        return mPrefs.getBoolean("notications", true);
    }
}
