package com.travelguide.listener;

import java.util.ArrayList;

public interface OnTripPlanListener {

    void onTripPlanItemSelected(String tripPlanObjectId);

    void onTripPlanCreated(String tripPlanObjectId, String imageUrl);

    void onTripPlanNew();

    void onShowImageSlideShow(ArrayList<String> imageUrlSet);
}
