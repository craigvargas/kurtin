package com.travelguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;


/**
 * Created by cvar on 10/20/16.
 */

@ParseClassName("Hunt")
public class Hunt extends ParseObject {

    public static String WIKITUDE_TARGET_COLLECTION_ID = "wikitudeTargetCollectionId";
    public static String WIKITUDE_CLIENT_ID = "wikitudeClientID";

    public static String HUNT_NAME = "huntName";
    public static String HUNT_LOCATION = "huntLocation";
    public static String HUNT_DESCRIPTION = "huntDescription";
    public static String HUNT_TIME_STRING = "huntTimeString";
    public static String HUNT_ADDRESS = "huntAddress";
    public static String HUNT_PRIZE = "huntPrize";
    public static String HUNT_POSTER_URL = "huntPosterUrl";
    public static String HUNT_CHECKPOINT_RELATIONS = "checkpoints";
    public static String HUNT_CHECKPOINT_POINTER_PREFIX = "huntCheckpoint";

    public static String HUNT_LIST_ORDER = "listOrder";

    //Constructor
    public Hunt(){ super(); }

    //*
    //Getters
    //*

    //Wikitude Data from parse
    public String getWikitudeTargetCollectionId(){ return getString(WIKITUDE_TARGET_COLLECTION_ID); }
    public String getWikitudeClientID(){ return getString(WIKITUDE_CLIENT_ID); }

    //Hunt data from parse
    public String getHuntName(){ return getString(HUNT_NAME); }
    public String getHuntLocation(){ return getString(HUNT_LOCATION); }
    public String getHuntDescription(){ return getString(HUNT_DESCRIPTION); }
    public String getHuntTimeString(){ return getString(HUNT_TIME_STRING); }
    public String getHuntAddress(){ return getString(HUNT_ADDRESS); }
    public String getHuntPrize(){ return getString(HUNT_PRIZE); }
    public String getHuntPosterUrl(){ return getString(HUNT_POSTER_URL); }
    public ParseRelation getCheckpointRelations() { return getRelation(HUNT_CHECKPOINT_RELATIONS); }
    public ParseObject getHuntCheckpoint(Integer checkpointNumber){
        return getParseObject(HUNT_CHECKPOINT_POINTER_PREFIX + checkpointNumber.toString());
    }


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
