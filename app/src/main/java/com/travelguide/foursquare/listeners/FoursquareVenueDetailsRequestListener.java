package com.travelguide.foursquare.listeners;

import com.travelguide.foursquare.models.Venue;

public interface FoursquareVenueDetailsRequestListener extends ErrorListener {

    public void onVenueDetailFetched(Venue venues);

}
