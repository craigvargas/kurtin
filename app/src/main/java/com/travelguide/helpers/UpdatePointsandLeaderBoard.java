package com.travelguide.helpers;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.travelguide.models.LeaderBoard;
import com.travelguide.models.MasterLeaderBoard;
import com.travelguide.models.Questions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by htammare on 8/26/2016.
 */

public class UpdatePointsandLeaderBoard extends AsyncTask<String, Void,Void> {
    // Async task to parse the downloaded results and build NPlace list
    String huntID = null;



    @Override
    protected Void doInBackground(String... params) {

        final String questionId = params[0];
        final String selectedText = params[1];




        //Toast.makeText(getApplicationContext(), "Your answer "+selectedText+" is saved", Toast.LENGTH_SHORT).show();
        final ParseUser user = ParseUser.getCurrentUser();

        //fetch question details and save it in leader board
        ParseQuery<Questions> query = ParseQuery.getQuery(Questions.class);
        query.whereEqualTo("objectId",questionId);
        query.findInBackground(new FindCallback<Questions>() {
            @Override
            public void done(final List<Questions> questions, ParseException e) {
                if (e == null) {

                    //Set master hunt id first
                    huntID =  questions.get(0).getHuntID().toString();

                    LeaderBoard leaderBoard = new LeaderBoard();
                    leaderBoard.put("parentQuestionID", ParseObject.createWithoutData("Questions", questionId));
                    leaderBoard.put("userID", ParseObject.createWithoutData("_User",user.getObjectId()));
                    leaderBoard.puthuntID(questions.get(0).getHuntID());
                    leaderBoard.putlevelID(questions.get(0).getLevelID());
                    leaderBoard.putQuadrantNo(questions.get(0).getQuadrantNo());
                    leaderBoard.putQuestionDetails(questions.get(0).getQuestionDetails());
                    leaderBoard.putSelectedOption(selectedText);
                    leaderBoard.putQuestionNo(questions.get(0).getQuestionNo());
                    leaderBoard.putCorrectAnswer(questions.get(0).getCorrectOption());
                    if(selectedText.equals(questions.get(0).getCorrectOption().toString())) {
                        leaderBoard.putPoints(questions.get(0).getPoints());
                    }else{
                        leaderBoard.putPoints(0);
                    }
                    //Save quadrant Points First
                    leaderBoard.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            //When done saving quadrant points -Calculate Level Points-
                            ParseQuery<LeaderBoard> query = ParseQuery.getQuery(LeaderBoard.class);
                            query.whereEqualTo("huntID",questions.get(0).getHuntID());
                            query.whereEqualTo("levelID",questions.get(0).getLevelID());
                            query.findInBackground(new FindCallback<LeaderBoard>() {
                                @Override
                                public void done(List<LeaderBoard> list, ParseException e) {
                                    Integer totalLevelPoints = 0;
                                    for(int i=0;i<list.size();i++){
                                        totalLevelPoints = totalLevelPoints + list.get(i).getPoints();
                                    }
                                    //save level points to that level items
                                    for(int i=0;i<list.size();i++){
                                        LeaderBoard updateleaderBoard = list.get(i);
                                        updateleaderBoard.putTotalLevelPoints(totalLevelPoints);
                                        updateleaderBoard.putTotalHuntPoints(0);
                                        updateleaderBoard.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                ParseQuery<LeaderBoard> query1 = ParseQuery.getQuery(LeaderBoard.class);
                                                query1.whereEqualTo("huntID",questions.get(0).getHuntID());
                                                query1.whereNotEqualTo("points",0);
                                                query1.findInBackground(new FindCallback<LeaderBoard>() {
                                                    @Override
                                                    public void done(List<LeaderBoard> list, ParseException e) {

                                                        //Adding
                                                        HashSet<String> levelIdsSet = new HashSet<String>();
                                                        for(int l=0;l<list.size();l++){
                                                            levelIdsSet.add(list.get(l).getLevelID().toString());
                                                        }
                                                        List<String> completedLevelIDs = new ArrayList<String>(levelIdsSet);
                                                        List<String> levelIdObjectId = new ArrayList<String>();

                                                        for (String value : completedLevelIDs) {
                                                            ParseQuery<LeaderBoard> query2 = ParseQuery.getQuery(LeaderBoard.class);
                                                            query2.whereEqualTo("huntID",questions.get(0).getHuntID());
                                                            query2.whereNotEqualTo("points",0);
                                                            query2.whereEqualTo("levelID",value);
                                                            try {
                                                                LeaderBoard tempLB = query2.getFirst();
                                                                levelIdObjectId.add(tempLB.getObjectId());
                                                            } catch (ParseException e1) {
                                                                e1.printStackTrace();
                                                            }

                                                        }

                                                        Integer totalHuntPoints = 0;
                                                        final Integer huntPoints;

                                                        for(String objectId : levelIdObjectId){
                                                            ParseQuery<LeaderBoard> query3 = ParseQuery.getQuery(LeaderBoard.class);
                                                            query3.whereEqualTo("huntID",questions.get(0).getHuntID());
                                                            query3.whereNotEqualTo("points",0);
                                                            query3.whereEqualTo("objectId",objectId);
                                                            try {
                                                                LeaderBoard tempHuntTotals = query3.getFirst();
                                                                totalHuntPoints = totalHuntPoints + tempHuntTotals.getLevelPoints();
                                                            } catch (ParseException e1) {
                                                                e1.printStackTrace();
                                                            }

                                                        }

                                                        huntPoints = totalHuntPoints;
                                                        totalHuntPoints = huntPoints;
                                                        for(int i=0;i<list.size();i++){
                                                            LeaderBoard updateleaderBoard = list.get(i);
                                                            updateleaderBoard.putTotalHuntPoints(totalHuntPoints);
                                                            updateleaderBoard.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if(huntPoints!=0){
                                                                        ParseQuery<MasterLeaderBoard> query4 = ParseQuery.getQuery(MasterLeaderBoard.class);
                                                                        query4.whereEqualTo("huntID",questions.get(0).getHuntID());
                                                                        query4.findInBackground(new FindCallback<MasterLeaderBoard>() {
                                                                            @Override
                                                                            public void done(List<MasterLeaderBoard> list1, ParseException e) {
                                                                                if(list1 != null) {


                                                                                    if (list1.size() > 0) {
                                                                                        MasterLeaderBoard updateRecord = list1.get(0);
                                                                                        updateRecord.putPoints(huntPoints);
                                                                                        try {
                                                                                            updateRecord.save();
                                                                                        } catch (ParseException e1) {
                                                                                            e1.printStackTrace();
                                                                                        }

                                                                                    } else {
                                                                                        //Make sure we are not creating new again
                                                                                        MasterLeaderBoard saveNewRecord = new MasterLeaderBoard();
                                                                                        saveNewRecord.putPoints(huntPoints);
                                                                                        saveNewRecord.putHuntID(questions.get(0).getHuntID());
                                                                                        saveNewRecord.put("userID", ParseObject.createWithoutData("_User", user.getObjectId()));
                                                                                        try {
                                                                                            saveNewRecord.save();
                                                                                        } catch (ParseException e1) {
                                                                                            e1.printStackTrace();
                                                                                        }




                                                                                    }
                                                                                }else{

                                                                                    //Make sure we are not creating new again

                                                                                    MasterLeaderBoard saveNewRecord = new MasterLeaderBoard();
                                                                                    saveNewRecord.putPoints(huntPoints);
                                                                                    saveNewRecord.putHuntID(questions.get(0).getHuntID());
                                                                                    saveNewRecord.put("userID", ParseObject.createWithoutData("_User", user.getObjectId()));
                                                                                    try {
                                                                                        saveNewRecord.save();
                                                                                    } catch (ParseException e1) {
                                                                                        e1.printStackTrace();
                                                                                    }

                                                                                }

                                                                            }
                                                                        });
                                                                    }

                                                                }
                                                            });
                                                        }
                                                    }
                                                });

                                            }
                                        });                                                }

                                }
                            });

                        }
                    });}
                else {
                    Log.d("ERROR", "Data not fetched");
                }
            }
        });
        //Make sure to cleanup Leader Board if we missed items. As the issues where you play level 1 with zero points..play 2 with zero points--and now come back to level1 and get 100 points..
        //Master leader board is adding 2 rows -- so to cover that issue---we gave below fix to double check and delete
        ParseQuery<MasterLeaderBoard> query4 = ParseQuery.getQuery(MasterLeaderBoard.class);
        query4.whereEqualTo("huntID",huntID);
        //query4.whereEqualTo("points",totalHuntPoints);
        query4.findInBackground(new FindCallback<MasterLeaderBoard>() {
            @Override
            public void done(List<MasterLeaderBoard> list, ParseException e) {
                // Clean Duplicates if any
                if(list.size()== 2){
                    String deleteObjectId = list.get(1).getObjectId();
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Favourite");

                    ParseQuery<MasterLeaderBoard> query5 = ParseQuery.getQuery(MasterLeaderBoard.class);
                    query5.whereEqualTo("objectId",deleteObjectId);
                    try {
                        MasterLeaderBoard deleteRecord =  query5.getFirst();
                        deleteRecord.delete();
                        deleteRecord.saveInBackground();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }


                }else{
                    //Do Nothing
                }
            }
        });

        return null;
    }
}

