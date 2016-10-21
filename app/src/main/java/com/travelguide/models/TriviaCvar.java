package com.travelguide.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cvar on 10/20/16.
 */

public class TriviaCvar {

    public static String POINTS_KEY = "points";
    public static String QUESTION_KEY = "question";
    public static String CORRECT_ANSWER_KEY = "correctAnswer";
    public static String ANSWER_CHOICES_KEY = "answerChoices";
    public static String QUADRANT_KEY = "quadrant";
    public static String CONTENT_TYPE_KEY = "contentType";
    public static String CONTENT_DATA_KEY = "contentData";

    private Integer points;
    private String question;
    private String correctAnswer;
    private List<String> answerChoices;
    private int quadrant;
    private String contentType;
    private String contentData;

    public TriviaCvar(){
        this.points = -1;
        this.question = "";
        this.correctAnswer = "";
        this.answerChoices = new ArrayList<String>();
        this.quadrant = -1;
        this.contentType = "";
        this.contentData = "";
    }

    public TriviaCvar newInstanceFromJsonObject(JSONObject jsonObject){
        TriviaCvar trivia = new TriviaCvar();
        pullDatafromJsonObject(jsonObject);
        return trivia;
    }

    //Start with a default TriviaCvar object and try to populate values from the JSONObject parameter
    private TriviaCvar pullDatafromJsonObject(JSONObject jsonObject){

        TriviaCvar trivia = new TriviaCvar();
        //Points
        try{
            trivia.setPoints(jsonObject.getInt(POINTS_KEY));
        }catch(Exception e){
            e.printStackTrace();
        }
        //Question
        try{
            trivia.setQuestion(jsonObject.getString(QUESTION_KEY));
        }catch(Exception e){
            e.printStackTrace();
        }
        //Correct Answer
        try{
            trivia.setCorrectAnswer(jsonObject.getString(CORRECT_ANSWER_KEY));
        }catch(Exception e){
            e.printStackTrace();
        }
        //Answer Choices
        try{
            JSONArray jsonArray = jsonObject.getJSONArray(ANSWER_CHOICES_KEY);
            List<String> answers = new ArrayList<>();
            for(int index=0; index<jsonArray.length(); index++){
                answers.add(jsonArray.getString(index));
            }
            trivia.setAnswerChoices(answers);
        }catch(Exception e){
            e.printStackTrace();
        }
        //Quadrant
        try{
            trivia.setQuadrant(jsonObject.getInt(QUADRANT_KEY));
        }catch(Exception e){
            e.printStackTrace();
        }
        //Content Type
        try{
            trivia.setContentType(jsonObject.getString(CONTENT_TYPE_KEY));
        }catch(Exception e){
            e.printStackTrace();
        }
        //Content Data
        try{
            trivia.setContentData(jsonObject.getString(CONTENT_DATA_KEY));
        }catch(Exception e){
            e.printStackTrace();
        }

        return trivia;
    }



    //*
    //Getters
    //*
    public Integer getPoints() {
        return points;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getAnswerChoices() {
        return answerChoices;
    }

    public int getQuadrant() {
        return quadrant;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentData() {
        return contentData;
    }


    //*
    //Setters
    //*


    public void setPoints(Integer points) {
        this.points = points;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setAnswerChoices(List<String> answerChoices) {
        this.answerChoices = answerChoices;
    }

    public void setQuadrant(int quadrant) {
        this.quadrant = quadrant;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setContentData(String contentData) {
        this.contentData = contentData;
    }
}

