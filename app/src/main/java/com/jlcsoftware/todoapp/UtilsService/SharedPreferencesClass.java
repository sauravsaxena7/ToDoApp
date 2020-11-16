package com.jlcsoftware.todoapp.UtilsService;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesClass {

    private static final String USER_PREF = "USER_TODO";
    private SharedPreferences appShared;

    private SharedPreferences.Editor prefEditor;

    public SharedPreferencesClass(Context context){
        appShared = context.getSharedPreferences(USER_PREF, Activity.MODE_PRIVATE);
        this.prefEditor = appShared.edit();
    }

    //int

    public int getValue_int(String key) {
        return appShared.getInt(key,0);
    }

    public void setValue_int(String key,int value){
        prefEditor.putInt(key,value).commit();
    }

    //String

    public String getValue_string(String key) {
        return appShared.getString(key,"");
    }

    public void setValue_string(String key,String value){
        prefEditor.putString(key,value).commit();
    }

    //boolean

    public boolean getValue_boolean(String key) {
        return appShared.getBoolean(key,false);
    }

    public void setValue_int(String key,boolean value){
        prefEditor.putBoolean(key,value).commit();
    }

    //to clear data or user token

    public void clear(){
        prefEditor.clear().commit();
    }








}
