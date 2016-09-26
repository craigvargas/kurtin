package com.travelguide.foursquare.listeners;

import java.util.ArrayList;

import com.travelguide.foursquare.models.Venues;

public interface VenuesHistoryListener extends ErrorListener {

	public void onGotVenuesHistory(ArrayList<Venues> list);

}
