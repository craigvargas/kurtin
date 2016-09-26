package com.travelguide.foursquare.constants;

import com.travelguide.R;

public class FoursquareConstants {
    /**
     * Change this!!! Generate yours at https://foursquare.com/developers/register
     */
    public static String CLIENT_ID = "ChangeMe";
    public static String CLIENT_SECRET = "ChangeMe";

    public static final String CALLBACK_URL = "http://localhost:8888";
    public static final String SHARED_PREF_FILE = "shared_pref";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String USER_INFO = "user_info";
    public static final String API_DATE_VERSION = "20140714";

    public static void setClientIdAndSecret(String clientId, String clientSecret) {
        FoursquareConstants.CLIENT_ID = clientId;
        FoursquareConstants.CLIENT_SECRET = clientSecret;
    }
}
