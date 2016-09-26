package com.travelguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by htammare on 10/16/2015.
 */
@ParseClassName("PlaceDetails")
public class Place extends ParseObject {

    //Constructor
    public Place(){
        super();
    }

    //Getting Data
    public String getCreatedUserId() {return getString("placeCreatedUserId");}
    public String getPlanName() {return getString("planName");}
    public String getPlaceName() {return getString("placeName");}
    public String getPlaceImageUrl() {return getString("placeImageUrl");}
    public String getCityName() {return getString("cityName");}
    public String getVisitingTime() {return getString("placeVisitingTime");}
    public String getPlaceNotes() {return getString("placeNotes");}
    public Integer getPlaceTravelCost() {return getInt("placeTravelCost");}
    public Integer getUserRatings() {return getInt("userRatings");}
    public Integer getPlacePositionInPlan() {return  getInt("placePositionInPlan");}

    //Saving Data
    public void putCreatedUserId(String createdUserId) { put("createdUserId", createdUserId);}
    public void putPlanName(String planName) {put("planName",planName);}
    public void putPlaceName(String placeName) {put("placeName",placeName);}
    public void putPlaceImageUrl(String placeImageUrl) {put("placeImageUrl",placeImageUrl);}
    public void putCityName(String cityName) {put("cityName",cityName);}
    public void putVisitingTime(String placeVisitingTime) {put("placeVisitingTime",placeVisitingTime);}
    public void putPlaceNotes(String placeNotes) {put("placeNotes",placeNotes);}
    public void putPlaceTravelCost(Integer getPlaceTravelCost) {put("getPlaceTravelCost",getPlaceTravelCost);}
    public void putUserRatings(Integer getUserRatings) {put("getUserRatings",getUserRatings);}
    public void putPlacePositionInPlan(Integer placePositionInPlan) {put("placePositionInPlan",placePositionInPlan);}
}
