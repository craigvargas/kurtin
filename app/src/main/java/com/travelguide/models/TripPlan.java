package com.travelguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

/**
 * Created by htammare on 10/16/2015.
 */
@ParseClassName("PlanDetails")
public class TripPlan extends ParseObject {

    //Constructor
    public TripPlan(){
        super();
    }

    //Getting Data
    public String getCreatedUserId() {return getString("createdUserId");}
    public String getCreatedName() {return getString("createdUserName");}
    public String getPlanName() {return getString("planName");}
    public String getCityImageUrl() {return getString("cityImageUrl");}
    public String getCityName() {return getString("cityName");}
    public Integer getTripTime() {return getInt("totalTripTime");}
    public Integer getTravelMonthNumber() {return getInt("getTravelMonthNumber");}
    public String getTravelMonth() {return getString("travelMonth");}
    public String getTravelSeason() {return getString("travelSeason");}
    public Date getTripBeginDate() {return getDate("tripBeginDate");}
    public Date getTripEndDate() {return getDate("tripEndDate");}
    public String getTripNotes() {return getString("tripNotes");}
    public Integer getTripCost() {return getInt("tripCost");}
    public String getGroupType() {return getString("groupType");}
    public Boolean getEnabledFlag() {return getBoolean("enabledFlag");}
    public String getWikitudeTargetCollectionId() {return getString("wikitudeTargetCollectionId");}
    public String getWikitudeClientID() {return getString("wikitudeClientToken");}


    //Saving Data
    public void putCreatedUserId(String createdUserId) { put("createdUserId", createdUserId);}
    public void putCreatedUserName(String createdUserName) { put("createdUserName", createdUserName);}
    public void putPlanName(String planName) {put("planName",planName);}
    public void putCityName(String cityName) {put("cityName",cityName);}
    public void puCityImageURL(String cityImageUrl) {put("cityImageUrl",cityImageUrl);}
    public void putTripTime(Integer totalTripTime) {put("totalTripTime",totalTripTime);}
    public void putTravelMonthNumber(Integer travelMonthNumber) {put("travelMonthNumber",travelMonthNumber);}
    public void putTravelMonth(String travelMonth) {put("travelMonth",travelMonth);}
    public void putTravelSeason(String travelSeason) {put("travelSeason",travelSeason);}
    public void putTripBeginDate(Date tripBeginDate) {put("tripBeginDate",tripBeginDate);}
    public void putTripEndDate(Date tripEndDate) {put("tripEndDate",tripEndDate);}
    public void putTripNotes(String tripNotes) {put("tripNotes",tripNotes);}
    public void putTripCost(Integer tripCost) {put("tripCost",tripCost);}
    public void putGroupType(String groupType) {put("groupType",groupType);}
    public void putEnabledFlag(Boolean enabledFlag) {put("enabledFlag",enabledFlag);}

    public Boolean isFavorited() {
        final ParseUser user = ParseUser.getCurrentUser();

        if (user == null) {
            return false;
        } else {
            List<String> userFavDetails = user.getList("favTrips");
            return userFavDetails.contains(getObjectId());
        }
    }

    public void setFavorite(boolean favorite) {
        final ParseUser user = ParseUser.getCurrentUser();

        if (user == null)
            return;

        if (favorite) {
            user.addUnique("favTrips", getObjectId());
        } else {
            List<String> userFavDetails = user.getList("favTrips");
            userFavDetails.remove(getObjectId());
            user.remove("favTrips");
            user.addAllUnique("favTrips", userFavDetails);
        }

        user.saveInBackground();
    }

}
