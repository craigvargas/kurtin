package com.travelguide.helpers;

import java.util.HashMap;

/**
 * Created by cvar on 10/5/16.
 */

public class AppCodesKeys {

    public final static int CHANGE_PROFILE_PIC_CODE = 1;

    //Parse User Keys
    public static final String PARSE_USER_PROFILE_PIC_KEY = "profileThumb";
    public static final String PARSE_USER_NICKNAME_KEY = "nickname";
    public static final String PARSE_USER_USERNAME_KEY = "username";

    //Parse Hunt Entry Keys (Hunt Leader Board)
    //A Hunt Entry is a unique player signing up to participate in a hunt
    public static final String PARSE_LEADER_BOARD_POINTS_KEY = "points";
    public static final String PARSE_LEADER_BOARD_USER_POINTER_KEY = "userID";
    public static final String PARSE_LEADER_BOARD_HUNT_POINTER_KEY = "huntPointer";
    public static final String PARSE_LEADER_BOARD_USER_POINTER_PROFILE_PIC_KEY =
            PARSE_LEADER_BOARD_USER_POINTER_KEY + "." + PARSE_USER_PROFILE_PIC_KEY;

    //Parse Hunt (Trip Plan) keys
    public static final String PARSE_TRIP_PLAN_ORDER_KEY = "startsWhen";

    //General app constants
    public static final String PROFILE_PIC_FILE_NAME = "profile.jpg";

    //Fragment Ids
    public static final String TRIP_PLAN_LIST_FRAGMENT_ID = "tripPlanList";
    public static final String TRIP_PLAN_DETAILS_FRAGMENT_ID = "tripPlanDetails";
    public static final String OVERALL_LEADER_BOARD_FRAGMENT_ID = "overallLeaderBoard";
    public static final String MY_HUNTS_LEADER_BOARD_FRAGMENT_ID = "myHuntsLeaderBoard";
    public static final String LEADER_BOARD_FRAGMENT_ID = "leaderBoard";
    public static final String KURTIN_LOGIN_FRAGMENT_ID = "kurtinLogin";
    public static final String KURTIN_SIGN_UP_FRAGMENT_ID = "kurtinSignUp";
    public static final String KURTIN_PROFILE_FRAGMENT_ID = "kurtinProfile";
    public static final String SCANNER_FRAGMENT_ID = "scanner";
    public static final String SETTINGS_FRAGMENT_ID = "settings";

    //Fragment Titles
    public static final String TRIP_PLAN_LIST_TITLE = "PUBLIC HUNTS";
    public static final String TRIP_PLAN_DETAILS_TITLE = "HUNT DETAILS";
    public static final String OVERALL_LEADER_BOARD_TITLE = "OVERALL LEADER BOARD";
    public static final String MY_HUNTS_LEADER_BOARD_TITLE = "HUNT LEADER BOARD";
    public static final String LEADER_BOARD_TITLE = "LEADER BOARDS";
    public static final String KURTIN_LOGIN_TITLE = "LOGIN TO KURTIN";
    public static final String KURTIN_SIGN_UP_TITLE = "KURTIN SIGN UP";
    public static final String KURTIN_PROFILE_TITLE = "YOUR PROFILE";
    public static final String SCANNER_TITLE = "SCAN YOUR IMAGE";
    public static final String SETTINGS_TITLE = "SETTINGS";

    //Map to associate fragments with their titles
    public static final HashMap<String, String> FRAGMENT_TITLE_MAP = new HashMap<String, String>(){{
        put(TRIP_PLAN_LIST_FRAGMENT_ID, TRIP_PLAN_LIST_TITLE);
        put(TRIP_PLAN_DETAILS_FRAGMENT_ID, TRIP_PLAN_DETAILS_TITLE);
        put(OVERALL_LEADER_BOARD_FRAGMENT_ID, OVERALL_LEADER_BOARD_TITLE);
        put(MY_HUNTS_LEADER_BOARD_FRAGMENT_ID, MY_HUNTS_LEADER_BOARD_TITLE);
        put(LEADER_BOARD_FRAGMENT_ID, LEADER_BOARD_TITLE);
        put(KURTIN_LOGIN_FRAGMENT_ID, KURTIN_LOGIN_TITLE);
        put(KURTIN_SIGN_UP_FRAGMENT_ID, KURTIN_SIGN_UP_TITLE);
        put(KURTIN_PROFILE_FRAGMENT_ID, KURTIN_PROFILE_TITLE);
        put(SCANNER_FRAGMENT_ID, SCANNER_TITLE);
        put(SETTINGS_FRAGMENT_ID, SETTINGS_TITLE);
    }};
}
