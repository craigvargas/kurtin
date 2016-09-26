package com.travelguide.foursquare.listeners;

import java.util.ArrayList;

import com.travelguide.foursquare.models.Venue;

public interface FoursquareVenuesRequestListener extends ErrorListener {

    public void onVenuesFetched(ArrayList<Venue> venues);

}
