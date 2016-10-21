package com.travelguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by cvar on 10/20/16.
 */

@ParseClassName("KurtinInteraction")
public class KurtinInteraction extends ParseObject{

    //Constructor
    public KurtinInteraction(){ super(); }

    //*
    //Getters
    //*
    public Integer getPoints(){ return getInt("points"); }
    public String getInteractionType(){ return getString("interactionType"); }
    public String getQuestion(){ return getString("question"); }
    public String getCorrectAnswer(){ return getString("correctAnswer"); }
    public String getAnswerChoice(Integer choiceNumber){ return getString("choice" + choiceNumber.toString()); }
    public Integer getQuadrant(){ return getInt("quadrant"); }
    public String getContentType() { return getString("contentType"); }
    public String getContentData() { return getString("contentData"); }
    public String getUserAnswer() { return getString("answer"); }

}
