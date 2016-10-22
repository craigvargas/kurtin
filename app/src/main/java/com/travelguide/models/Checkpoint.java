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

    private boolean mIsSelected;

    //Constructor
    public Checkpoint(){ super(); }

    //*
    //Getters
    //*
    public String getScannerImageUrl(){ return getString("scannerImageUrl"); }
    public Integer getLat(){ return getInt("lat"); }
    public Integer getLng(){ return getInt("lng"); }
    public ParseGeoPoint getGeoPoint(){ return getParseGeoPoint("geoPoint");}
    public ParseRelation getInteractions(){ return getRelation("kurtinInteractions"); }
    public ParseObject getInteraction(Integer questionNumber){ return getParseObject("kurtinInteraction" + questionNumber.toString());}

    public boolean isSelected() {
        return mIsSelected;
    }
    public void setSelected(boolean isSelected) {
        this.mIsSelected = isSelected;
    }

}
