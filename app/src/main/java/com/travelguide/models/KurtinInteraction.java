package com.travelguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;

/**
 * Created by cvar on 10/20/16.
 */

@ParseClassName("KurtinInteraction")
public class KurtinInteraction extends ParseObject{

    //Keys
    public static final String POINTS_KEY = "points";
    public static final String INTERACTION_TYPE_KEY = "interactionType";
    public static final String QUESTION_KEY = "question";
    public static final String CORRECT_ANSWER_KEY = "correctAnswer";
    public static final String CONTENT_TYPE_KEY = "contentType";
    public static final String SOURCE_KEY = "source";
    public static final String ANSWER_CHOICES_KEY = "answerChoices";
    public static final String QUADRANT_KEY = "quadrant";

    public static final String JSON_OBJ_ANSWER_KEY = "answer";

    public static final String ANSWER_NO_WRONG_ANSWER = "noWrongAnswer";
    public static final String INTERACTION_TYPE_NONE = "None";
    public static final String INTERACTION_TYPE_QUESTION = "Question";
    public static final String CONTENT_TYPE_IMAGE = "Image";


    public static final String KURTIN_INTERACTION_ID_KEY = "kurtinInteractionRelation";

    public static final Integer FIRST_OPTION_INDEX = 0;


    //Constructor
    public KurtinInteraction(){ super(); }

    //*
    //Getters
    //*
    public Integer getPoints(){ return getInt(POINTS_KEY); }
    public String getInteractionType(){ return getString(INTERACTION_TYPE_KEY); }
    public String getQuestion(){ return getString(QUESTION_KEY); }
    public String getCorrectAnswer(){ return getString(CORRECT_ANSWER_KEY); }
    public String getAnswerChoice(Integer choiceNumber){ return getString("choice" + choiceNumber.toString()); }
//    public Integer getQuadrant(){ return getInt("quadrant"); }
    public String getContentType() { return getString(CONTENT_TYPE_KEY); }
    public String getSource() { return getString(SOURCE_KEY); }
    public JSONArray getAnswerChoices(){return getJSONArray(ANSWER_CHOICES_KEY);}

}
