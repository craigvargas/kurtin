package com.travelguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.travelguide.helpers.AppCodesKeys;

/**
 * Created by htammare on 10/16/2015.
 */
@ParseClassName("HuntLeaderBoard")
public class MasterLeaderBoard extends ParseObject {

    //Constructor
    public MasterLeaderBoard(){
        super();
    }

    //Getting Data
    //public String getPlayerID() {return getString("playerID");}
    public Integer getPoints() {return getInt("points");}
    public String getHuntID() {return getString("huntID");}
    public String getTempDetails(){return getString("tempDetails");}
    public boolean getCompletionStatus(){return getBoolean("completed");}
    //Commenting out the function below because hunt is only returned if you specify the "include" in your query
//    public ParseObject getHuntObject(){return getParseObject(AppCodesKeys.PARSE_LEADER_BOARD_HUNT_POINTER_KEY);}
    //Saving Data
    //public void putPlayerID(String playerID) { put("playerID", playerID);}
    public void putPoints(Integer points) {put("points",points);}
    public void putHuntID(String huntID) {put("huntID",huntID);}
    public void putTempDetails(String tempDetails) {put("tempDetails",tempDetails);}
    public void putCompletionStatus(boolean isCompleted) {put("completed", isCompleted);}
}
