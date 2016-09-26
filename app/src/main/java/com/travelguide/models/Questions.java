package com.travelguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by htammare on 10/16/2015.
 */

@ParseClassName("Questions")
public class Questions extends ParseObject{

    private boolean selected;

    //Constructor
    public Questions(){
        super();
    }

    //Getting Data
    public String getHuntID() {return getString("huntID");}
    public String getLevelID() {return getString("levelID");}
    public Integer getQuadrantNo() {return getInt("quadrantNo");}
    public Integer getQuestionNo() {return getInt("questionNo");}
    public String getQuestionDetails() {return getString("questionDetails");}
    public String getOptions() {return getString("options");}
    public String getOption1() {return getString("option1");}
    public String getOption2() {return getString("option2");}
    public String getOption3() {return getString("option3");}
    public String getCorrectOption() {return getString("correctOptionorAnswer");}
    public Integer getPoints() {return getInt("points");}
    public String getLinkedImage() {return getString("linkedImageID");}


    //Saving Data
    /**
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
     */
}
