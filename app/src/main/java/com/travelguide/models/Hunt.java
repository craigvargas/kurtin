package com.travelguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;


/**
 * Created by cvar on 10/20/16.
 */

@ParseClassName("Hunt")
public class Hunt extends ParseObject {

    //Constructor
    public Hunt(){ super(); }

    //*
    //Getters
    //*

    //Wikitude Data from parse
    public String getWikitudeTargetCollectionId(){ return getString("wikitudeTargetCollectionId"); }
    public String getWikitudeClientID(){ return getString("wikitudeClientID"); }

    //Hunt data from parse
    public String getHuntName(){ return getString("huntName"); }
    public String getHuntLocation(){ return getString("huntLocation"); }
    public String getHuntDescription(){ return getString("huntDescription"); }
    public String getHuntTimeString(){ return getString("huntTimeString"); }
    public String getHuntAddress(){ return getString("huntAddress"); }
    public String getHuntPrize(){ return getString("huntPrize"); }
    public String getHuntPosterUrl(){ return getString("huntPosterUrl"); }
    public ParseRelation getCheckpointRelations() { return getRelation("checkpoints"); }
    public ParseObject getHuntCheckpoint(Integer checkpointNumber){ return getParseObject("huntCheckpoint" + checkpointNumber.toString()); }


    //*
    //Setters
    //*

    //Wikitude Data from parse
    public void putWikitudeTargetCollectionId(String collectionId){ put("wikitudeTargetCollectionId", collectionId); }
    public void putWikitudeClientID(String clientId){ put("wikitudeClientID", clientId); }

    //Hunt data from parse
    public void putHuntName(String name){ put("huntName", name); }
    public void putHuntLocation(String location){ put("huntLocation", location); }
    public void putHuntDescription(String description){ put("huntDescription", description); }
    public void putHuntTimeString(String time){ put("huntTimeString", time); }
    public void putHuntAddress(String address){ put("huntAddress", address); }
    public void putHuntPrize(String prize){ put("huntPrize", prize); }
    public void putHuntPosterUrl(String url){ put("huntPosterUrl", url); }
    public void putHuntCheckpoint(ParseObject checkpoint, Integer checkpointNumber ){
        put("huntCheckpoint" + checkpointNumber.toString(), checkpoint);
    }

}
