package com.travelguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;


/**
 * Created by cvar on 10/20/16.
 */

@ParseClassName("HuntJoin")
public class HuntJoin extends ParseObject {

    //Constructor
    public HuntJoin(){ super(); }

    public Boolean getCompletionStatus(){ return getBoolean("userHasCompletedHunt"); }
}
