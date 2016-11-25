package com.travelguide.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.ParseException;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


/**
 * Created by cvar on 10/20/16.
 */

@ParseClassName("HuntJoin")
public class HuntJoin extends ParseObject {

    public static final String USER_POINTER_KEY = "user";
    public static final String HUNT_POINTER_KEY = "hunt";
    public static final String COMPLETION_STATUS_KEY = "userHasCompletedHunt";
    public static final String RESULTS_KEY = "interactionResults";
    public static final String POINTS_EARNED_KEY = "pointsEarned";

    public static final String JSON_OBJ_CHECKPOINT_ID_KEY = "id";
    public static final String JSON_OBJ_CHECKPOINT_POINTS_KEY = "points";
    public static final String JSON_OBJ_CHECKPOINT_IS_FINISHED = "isFinished";
    public static final String JSON_OBJ_CHECKPOINT_INTERACTIONS_KEY = "interactions";

    public static final String JSON_OBJ_INTERACTION_ID_KEY = "id";
    public static final String JSON_OBJ_INTERACTION_POINTS_KEY = "points";
    public static final String JSON_OBJ_INTERACTION_USER_ANSWER_KEY = "userAnswer";
    public static final String JSON_OBJ_INTERACTION_NO_WRONG_ANSWER = "noWrongAnswer";
    public static final String JSON_OBJ_INTERACTION_NONE = "None";


    //Constructor
    public HuntJoin() {
        super();
    }

    public ParseObject getUser() {
        return getParseObject(USER_POINTER_KEY);
    }

    public ParseObject getHunt() {
        return getParseObject(HUNT_POINTER_KEY);
    }

    public Boolean getCompletionStatus() {
        return getBoolean(COMPLETION_STATUS_KEY);
    }

    public JSONArray getResults() {
        return getJSONArray(RESULTS_KEY);
    }

    public int getPointsEarned() {
        return getInt(POINTS_EARNED_KEY);
    }

    public void putUser(ParseUser parseUser) {
        put(USER_POINTER_KEY, parseUser);
    }

    public void putHunt(Hunt hunt) {
        put(HUNT_POINTER_KEY, hunt);
    }

    public void putCompletionStatus(Boolean isCompleted) {
        put(COMPLETION_STATUS_KEY, isCompleted);
    }

    public void putResults(JSONArray results) {
        put(RESULTS_KEY, results);
    }

    public void putPointsEarned(int pointsEarned) {
        put(POINTS_EARNED_KEY, pointsEarned);
    }


    //Create a new HuntJoin Record
    public static HuntJoin createHuntJoinRecord(ParseUser parseUser, Hunt hunt, String checkpointId) {
        try {
            HuntJoin huntJoinRecord = new HuntJoin();
            huntJoinRecord.putHunt(hunt);
            huntJoinRecord.putUser(parseUser);
            huntJoinRecord.putPointsEarned(0);
            huntJoinRecord.putCompletionStatus(false);
            JSONObject checkpointResult = new JSONObject();

            checkpointResult.put(HuntJoin.JSON_OBJ_CHECKPOINT_ID_KEY, checkpointId);
            checkpointResult.put(HuntJoin.JSON_OBJ_CHECKPOINT_POINTS_KEY, 0);
            checkpointResult.put(HuntJoin.JSON_OBJ_CHECKPOINT_INTERACTIONS_KEY, new JSONArray());
            checkpointResult.put(HuntJoin.JSON_OBJ_CHECKPOINT_IS_FINISHED, false);

            JSONArray resultsArray = new JSONArray();
            resultsArray.put(checkpointResult);
            huntJoinRecord.putResults(resultsArray);

            return huntJoinRecord;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Create a new interactionResult using information from the user's selection/selected-option
    public static JSONObject createInteractionResultObjectWithUserSelection(KurtinInteraction interaction, int selectedOptionIndex){
        JSONObject interactionResult = new JSONObject();
        try {
            JSONObject answerObject;
            String correctAnswer = interaction.getCorrectAnswer();

            String interactionID = interaction.getObjectId();
            String selectedAnswer;
            int pointsEarned = 0;

            answerObject = (JSONObject) interaction.getAnswerChoices().get(selectedOptionIndex);
            selectedAnswer = (String) answerObject.get(KurtinInteraction.JSON_OBJ_ANSWER_KEY);
            if(correctAnswer.equals(selectedAnswer) || correctAnswer.equals(HuntJoin.JSON_OBJ_INTERACTION_NO_WRONG_ANSWER)){
                pointsEarned = interaction.getPoints();
            }
            interactionResult.put(HuntJoin.JSON_OBJ_INTERACTION_ID_KEY, interactionID);
            interactionResult.put(HuntJoin.JSON_OBJ_INTERACTION_USER_ANSWER_KEY, selectedAnswer);
            interactionResult.put(HuntJoin.JSON_OBJ_INTERACTION_POINTS_KEY, pointsEarned);

            return interactionResult;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //pull an interactionResult out of the nested results array
    public static JSONObject getInteractionFromResults(
            String checkpointId,
            String interactionId,
            HuntJoin huntJoinRecord) {

        JSONArray checkpointResultJsonArray = huntJoinRecord.getResults();
        JSONObject checkpointResultItem;
        JSONArray interactionResultJsonArray;
        JSONObject interactionResultItem;
        if (checkpointResultJsonArray == null) {
            //No results data
            return null;
        } else {
            try {
                //Try to find a record of the user completing this interaction
                //First look for checkpoint
                for (int checkpointIndex = 0; checkpointIndex < checkpointResultJsonArray.length(); checkpointIndex++) {
                    checkpointResultItem = checkpointResultJsonArray.getJSONObject(checkpointIndex);
                    if (checkpointId.equals(
                            checkpointResultItem
                                    .getString(HuntJoin.JSON_OBJ_CHECKPOINT_ID_KEY))) {
                        interactionResultJsonArray =
                                checkpointResultItem
                                        .getJSONArray(HuntJoin.JSON_OBJ_CHECKPOINT_INTERACTIONS_KEY);
                        //Now look for interaction
                        for (int interactionIndex = 0; interactionIndex < interactionResultJsonArray.length(); interactionIndex++) {
                            interactionResultItem = interactionResultJsonArray.getJSONObject(interactionIndex);
                            if (interactionId.equals(
                                    interactionResultItem.getString(HuntJoin.JSON_OBJ_INTERACTION_ID_KEY))) {
                                //Found a matching record in the results Array
                                return interactionResultItem;
                            }

                        }

                    }
                }
            } catch (Exception e) {
                Log.e("CheckComplete", "Something went wrong while checking if interaction has been completed already");
                e.printStackTrace();
            }
            //Could not find the record in the results Array
            return null;
        }
    }

    //use interaction points to calculate points for the entire huntjoin record
    public static int calculatePointTotalsAndReturnPointIncrease(
            HuntJoin huntJoinRecord, int totalNumberOfCheckpoints, int totalNumberOfInteractions){
        JSONArray checkpointResultsArray = huntJoinRecord.getResults();
        JSONObject checkpointResult;
        JSONArray interactionResultsArray;
        JSONObject interactionResult;
        JSONArray newCheckpointResultsArray = new JSONArray();
        if(checkpointResultsArray == null){
            Log.e("HuntJoin","Something went wrong. HuntJoin record should have been created with a results array");
            return 0;
        }
        int pointIncrease = 0;
        int oldHuntPointsSum = huntJoinRecord.getPointsEarned();
        int newHuntPointsSum = 0;
        int checkpointTally = 0;
        for (int checkpointIndex = 0; checkpointIndex < checkpointResultsArray.length(); checkpointIndex++) {
            try {
                checkpointResult = checkpointResultsArray.getJSONObject(checkpointIndex);
                interactionResultsArray = checkpointResult.getJSONArray(JSON_OBJ_CHECKPOINT_INTERACTIONS_KEY);
                if (interactionResultsArray == null) {
                    continue;
                }else {
                    int checkpointPointsSum = 0;
                    int interactionTally = 0;
                    for (int interactionIndex = 0; interactionIndex < interactionResultsArray.length(); interactionIndex++) {
                        interactionResult = interactionResultsArray.getJSONObject(interactionIndex);
                        if (interactionResult == null) {
                            continue;
                        } else {
                            checkpointPointsSum += interactionResult.getInt(HuntJoin.JSON_OBJ_INTERACTION_POINTS_KEY);
                            interactionTally++;
                        }
                    }
                    newHuntPointsSum += checkpointPointsSum;
                    checkpointResult.put(HuntJoin.JSON_OBJ_CHECKPOINT_POINTS_KEY, checkpointPointsSum);
                    if (interactionTally == totalNumberOfInteractions) {
                        checkpointResult.put(HuntJoin.JSON_OBJ_CHECKPOINT_IS_FINISHED, true);
                        checkpointTally++;
                    }
                    newCheckpointResultsArray.put(checkpointResult);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        pointIncrease = newHuntPointsSum - oldHuntPointsSum;
        huntJoinRecord.putPointsEarned(newHuntPointsSum);
        if(checkpointTally == totalNumberOfCheckpoints){
            huntJoinRecord.putCompletionStatus(true);
        }
        huntJoinRecord.putResults(newCheckpointResultsArray);
        return pointIncrease;
    }

    //Determine if a given interaction has already been completed by the user in the HuntJoin record
    public static Boolean isInteractionCompleted(HuntJoin huntJoinRecord, String checkpointId, String interactionId, String interactionType){
        JSONArray checkpointResultsArray = huntJoinRecord.getResults();
        JSONObject checkpointResult;
        JSONArray interactionResultsArray;
        JSONObject interactionResult;
        JSONArray newCheckpointResultsArray = new JSONArray();

        if(checkpointResultsArray == null){
            Log.v("HuntJoin","Interaction not completed");
            return false;
        }

        if(interactionType.equals(HuntJoin.JSON_OBJ_INTERACTION_NONE)){
            //No interaction available
            return false;
        }


        for (int checkpointIndex = 0; checkpointIndex < checkpointResultsArray.length(); checkpointIndex++) {
            try {
                checkpointResult = checkpointResultsArray.getJSONObject(checkpointIndex);
                if (checkpointId.equals(checkpointResult.getString(HuntJoin.JSON_OBJ_CHECKPOINT_ID_KEY))) {

                    interactionResultsArray = checkpointResult.getJSONArray(JSON_OBJ_CHECKPOINT_INTERACTIONS_KEY);
                    if (interactionResultsArray == null) {
                        Log.v("HuntJoin","Interaction not completed");
                        return false;
                    } else {
                        for (int interactionIndex = 0; interactionIndex < interactionResultsArray.length(); interactionIndex++) {
                            interactionResult = interactionResultsArray.getJSONObject(interactionIndex);
                            if (interactionId.equals(interactionResult.getString(HuntJoin.JSON_OBJ_INTERACTION_ID_KEY))) {
                                if (interactionResult.isNull(HuntJoin.JSON_OBJ_INTERACTION_POINTS_KEY)) {
                                    Log.v("HuntJoin","Interaction not completed");
                                    return false;
                                } else {
                                    Log.v("HuntJoin","Interaction not completed");
                                    return true;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.v("HuntJoin","Interaction not completed");
        return false;
    }


    //Create a checkpointResultsArray to be stored in the HuntJoin record's Results field
    public static JSONArray createCheckpointResultsArray(String checkpointId, JSONObject interactionResult){
        try {
            JSONObject checkpointResult = new JSONObject();
            checkpointResult.put(HuntJoin.JSON_OBJ_CHECKPOINT_ID_KEY, checkpointId);
            checkpointResult.put(HuntJoin.JSON_OBJ_CHECKPOINT_POINTS_KEY, 0);
            JSONArray interactionResultsArray = new JSONArray();
            interactionResultsArray.put(interactionResult);
            checkpointResult.put(HuntJoin.JSON_OBJ_CHECKPOINT_INTERACTIONS_KEY, interactionResultsArray);
            JSONArray newCheckpointResultsArray = new JSONArray();
            newCheckpointResultsArray.put(checkpointResult);
            return newCheckpointResultsArray;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static HuntJoin getHuntJoinFromDatabase(ParseUser parseUser, Hunt hunt){
        //Retrieve huntJoin record from database
        ParseQuery<HuntJoin> huntJoinParseQuery = ParseQuery.getQuery(HuntJoin.class);
        huntJoinParseQuery.whereEqualTo(HuntJoin.USER_POINTER_KEY, parseUser);
        huntJoinParseQuery.whereEqualTo(HuntJoin.HUNT_POINTER_KEY, hunt);


        try {
            HuntJoin huntJoinRecord = huntJoinParseQuery.getFirst();
            return huntJoinRecord;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
