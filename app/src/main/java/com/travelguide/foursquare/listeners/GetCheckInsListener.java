package com.travelguide.foursquare.listeners;

import java.util.ArrayList;

import com.travelguide.foursquare.models.Checkin;



public interface GetCheckInsListener extends ErrorListener {

	public void onGotCheckIns(ArrayList<Checkin> list);
	
}
