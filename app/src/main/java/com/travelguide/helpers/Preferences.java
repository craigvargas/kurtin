package com.travelguide.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static final String PREF_NAME = "userInfo";
    public static final String DEF_VALUE = "missing";

    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();
    }

    public static String readString(Context context, String key) {
        return getPreferences(context).getString(key, DEF_VALUE);
    }

    public static void writeBoolean(Context context, String key, boolean value) {
        getEditor(context).putBoolean(key, value).commit();
    }

    public static boolean readBoolean(Context context, String key) {
        return getPreferences(context).getBoolean(key, false);
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static class User {
        public static final String USER_OBJECT_ID = "userObjectId";
        public static final String PROFILE_PIC_URL = "profilePicUrl";
        public static final String COVER_PIC_URL = "coverPicUrl";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String LOG_IN_STATUS = "login_status";
    }
}
