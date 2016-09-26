package com.travelguide.foursquare.listeners;

import java.util.ArrayList;

import com.travelguide.foursquare.models.Venue;

public interface FoursquareTrendingVenuesRequestListener extends ErrorListener {

    public void onTrendedVenuesFetched(ArrayList<Venue> venues);

}
