package com.travelguide.foursquare.listeners;

import com.travelguide.foursquare.models.Checkin;



public interface CheckInListener extends ErrorListener {

	public void onCheckInDone(Checkin checkin);
	
}
