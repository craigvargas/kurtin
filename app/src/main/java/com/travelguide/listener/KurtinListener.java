package com.travelguide.listener;

import com.travelguide.models.Checkpoint;
import com.travelguide.models.Hunt;
import com.travelguide.models.HuntJoin;
import com.travelguide.models.KurtinInteraction;
import com.travelguide.models.Questions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public interface KurtinListener {

    void onHuntSelected(String tripPlanObjectId);

    void onHuntSelected(Hunt hunt);

    void onCheckpointScanSelected(Checkpoint checkpoint);

    void onTripPlanCreated(String tripPlanObjectId, String imageUrl);

    void onTripPlanNew();

    void  onDisplayLeaderBoardFromHuntDetails(String currentHuntID);

    void onShowImageSlideShow(ArrayList<String> imageUrlSet);

    Hunt getCurrentHunt();

    void setCurrentHunt(Hunt hunt);

    List<Checkpoint> getCurrentCheckpoints();

    void setCurrentCheckpoints(List<Checkpoint> checkpoints);

    Checkpoint getSelectedCheckpoint();

    void setSelectedCheckpoint(Checkpoint checkpoint);

    List<KurtinInteraction> getCurrentInteractions();

    void setCurrentInteractions(List<KurtinInteraction> interactions);

    void onSuccessfulCloudScanRecognition(JSONArray contentToDisplay);

    void onHuntCompleted(HuntJoin huntJoinRecord);
}
