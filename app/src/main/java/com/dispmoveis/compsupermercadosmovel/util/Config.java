package com.dispmoveis.compsupermercadosmovel.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Config {

    //public static final String SERVER_URL_BASE = "http://192.168.0.196/";
    public static final String SERVER_URL_BASE = "https://comparador-supermercados.herokuapp.com/";

    public static final Locale currencyLocale = Locale.GERMANY;
    // Germany... ALEMANHA? POR QUÊ?
    // Porque não há locale pro Brasil, mas a Alemanha usa o mesmo formato numérico (ex. 10.000,00)
    private static final DecimalFormat currencyFormat = (DecimalFormat) NumberFormat.getNumberInstance(currencyLocale);

    public static DecimalFormat getCurrencyFormat() {
        currencyFormat.setMinimumFractionDigits(2);
        return currencyFormat;
    }

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

    public static void setUserId(Context context, int id) {
        SharedPreferences mPrefs = context.getSharedPreferences("configs", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putInt("userId", id).commit();
    }

    public static int getUserId(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("configs", 0);
        return mPrefs.getInt("userId", 0);
    }

    public static void setUserName(Context context, String name) {
        SharedPreferences mPrefs = context.getSharedPreferences("configs", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("userName", name).commit();
    }

    public static String getUserName(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("configs", 0);
        return mPrefs.getString("userName", "");
    }
}
