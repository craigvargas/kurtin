package com.travelguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by htammare on 10/16/2015.
 */

@ParseClassName("LeaderBoard")
public class LeaderBoard extends ParseObject{

    private boolean selected;

    //Constructor
    public LeaderBoard(){
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
    public Integer getLevelPoints() {return getInt("totalLevelPoints");}
    public String getLinkedImage() {return getString("linkedImageID");}


    //Saving Data

    public void puthuntID(String huntID) { put("huntID", huntID);}
    public void putlevelID(String levelID) { put("levelID", levelID);}
    public void putQuadrantNo(Integer quadrantNo) { put("quadrantNo", quadrantNo);}
    public void putQuestionDetails(String questionDetails) { put("questionDetails", questionDetails);}
    public void putPoints(Integer points) { put("points", points);}
    public void putSelectedOption(String selectedOption) { put("selectedOption", selectedOption);}
    public void putQuestionNo(Integer questionNo) { put("questionNo", questionNo);}
    public void putCorrectAnswer(String correctAnswer) { put("correctAnswer", correctAnswer);}
    public void putTotalLevelPoints(Integer totalLevelPoints) { put("totalLevelPoints", totalLevelPoints);}
    public void putTotalHuntPoints(Integer totalHuntPoints) { put("totalHuntPoints", totalHuntPoints);}
}
