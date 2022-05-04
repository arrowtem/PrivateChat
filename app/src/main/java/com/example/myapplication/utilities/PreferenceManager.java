package com.example.myapplication.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import java.math.BigInteger;
public class PreferenceManager {
    private final SharedPreferences sharedPreferences;
    private BigInteger[] g512 = new BigInteger[5];
    private BigInteger[] p512 = new BigInteger[5];
    private String[] bigString;
    public PreferenceManager(Context context)
    {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);

    }
    public void putBoolean(String key, Boolean value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();

    }

    public Boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,false);
    }
    public void putString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();

    }
    public String getString(String key){
        return sharedPreferences.getString(key,null);
    }


    public long getLong(String key){
        return sharedPreferences.getLong(key,0);
    }


    public void clear()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void putInteger(String key, Integer value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key,value);
        editor.apply();

    }

    public String getG(){
       return sharedPreferences.getString(Constants.KEY_G,"0");
    }
    public String getP(){
        return sharedPreferences.getString(Constants.KEY_P,"0");
    }

}
