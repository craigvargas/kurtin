package com.travelguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by htammare on 10/16/2015.
 */

@ParseClassName("DayDetails")
public class Day extends ParseObject{

    private boolean selected;

    //Constructor
    public Day(){
        super();
    }

    //Getting Data
    public Integer getTravelDay() {return getInt("travelDay");}
    public Date getTravelDate() {return getDate("travelDate");}
    public String getCreatedUserId() {return getString("createdUserId");}
    public String getPlanName() {return getString("planName");}

    //Saving Data
    public void putTravelDay(Integer travelDay) { put("travelDay", travelDay);}
    public void putTravelDate(Date travelDate) {put("travelDate",travelDate);}
    public void putCreatedUserId(String createdUserId) { put("createdUserId", createdUserId);}
    public void putPlanName(String planName) {put("planName",planName);}

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
