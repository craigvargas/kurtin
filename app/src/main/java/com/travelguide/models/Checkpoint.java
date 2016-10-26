package com.travelguide.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;

/**
 * Created by cvar on 10/20/16.
 */

@ParseClassName("Checkpoint")
public class Checkpoint extends ParseObject {

    public static String CHECKPOINT_ID = "checkPointId";

    public static String SCANNER_IMAGE_URL = "scannerImageUrl";
    public static String CHECKPOINT_LAT_COORDINATE = "lat";
    public static String CHECKPOINT_LNG_COORDINATE = "lng";
    public static String CHECKPOINT_PARSE_GEO_POINT = "geoPoint";
    public static String INTERACTION_RELATIONS = "kurtinInteractions";
    public static String INTERACTION_PONTER_FREFIX = "kurtinInteraction";

    private boolean mIsSelected;

    //Constructor
    public Checkpoint(){ super(); }

    //*
    //Getters
    //*
    public String getScannerImageUrl(){ return getString(SCANNER_IMAGE_URL); }
    public Integer getLat(){ return getInt(CHECKPOINT_LAT_COORDINATE); }
    public Integer getLng(){ return getInt(CHECKPOINT_LNG_COORDINATE); }
    public ParseGeoPoint getGeoPoint(){ return getParseGeoPoint(CHECKPOINT_PARSE_GEO_POINT);}
    public ParseRelation getInteractions(){ return getRelation(INTERACTION_RELATIONS); }
    public ParseObject getInteraction(Integer questionNumber){ return getParseObject(INTERACTION_PONTER_FREFIX + questionNumber.toString());}

    public boolean isSelected() {
        return mIsSelected;
    }
    public void setSelected(boolean isSelected) {
        this.mIsSelected = isSelected;
    }

}
