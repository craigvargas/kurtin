package com.travelguide.helpers;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.travelguide.foursquare.EasyFoursquareAsync;
import com.travelguide.foursquare.criterias.TrendingVenuesCriteria;
import com.travelguide.foursquare.criterias.VenuesCriteria;
import com.travelguide.foursquare.listeners.FoursquareTrendingVenuesRequestListener;
import com.travelguide.foursquare.listeners.FoursquareVenuesRequestListener;
import com.travelguide.foursquare.models.Venue;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * @author kprav
 *
 * History:
 *   10/27/2015     kprav       Initial Version
 */
public class NearbyPlacesFoursquare {

    private static final String TAG = NearbyPlacesFoursquare.class.getSimpleName();

    private static VenuesCriteria venuesCriteria;
    private static TrendingVenuesCriteria trendingVenuesCriteria;
    private static TreeMap<String, Location> venuesMap;
    private static TreeMap<String, Location> trendingVenuesMap;

    // Foursquare categories
    private static final String airport         = "4bf58dd8d48988d1eb931735";
    private static final String amusementPark   = "4bf58dd8d48988d182941735";
    private static final String aquarium        = "4fceea171983d5d06c3e9823";
    private static final String artGallery      = "4bf58dd8d48988d1e2931735";
    private static final String bowlingAlley    = "4bf58dd8d48988d1e4931735";
    private static final String campground      = "4bf58dd8d48988d1e4941735";
    private static final String casino          = "4bf58dd8d48988d17c941735";
    private static final String circus          = "52e81612bcbc57f1066b79e7";
    private static final String concertHall     = "5032792091d4c4b30a586d5c";
    private static final String food            = "4d4b7105d754a06374d81259";
    private static final String movieTheater    = "4bf58dd8d48988d17f941735";
    private static final String museum          = "4bf58dd8d48988d181941735";
    private static final String nightLifeSpot   = "4d4b7105d754a06376d81259";
    private static final String nightClub       = "4bf58dd8d48988d11f941735";
    private static final String shoppingMall    = "4bf58dd8d48988d1fd941735";
    private static final String spa             = "4bf58dd8d48988d1ed941735";
    private static final String stadium         = "4bf58dd8d48988d184941735";
    private static final String busStation      = "4bf58dd8d48988d1fe931735";
    private static final String trainStation    = "4bf58dd8d48988d129951735";
    private static final String university      = "4bf58dd8d48988d1ae941735";
    private static final String zoo             = "4bf58dd8d48988d17b941735";

    private static Location location;
    private static final int radius = 8000; // 8km/5mi
    private static final int resultSize = 50;
    private static final String categoryIds = airport + "," + amusementPark + "," + aquarium + "," +
            artGallery + "," + bowlingAlley + "," + campground + "," + casino + "," + circus + "," +
            concertHall + "," + food + "," + movieTheater + "," + museum + "," + nightLifeSpot + "," +
            nightClub + "," + shoppingMall + "," + spa + "," + stadium + "," + busStation + "," +
            trainStation + "," + university + "," + zoo;

    private static EasyFoursquareAsync async;

    static {
        venuesCriteria = new VenuesCriteria();
        trendingVenuesCriteria = new TrendingVenuesCriteria();
        location = new Location(LocationManager.GPS_PROVIDER);
    }

    public interface OnPlacesFetchListener {
        public void onPlacesFetched(TreeMap<String, Location> places);
    }

    public static void getNearbyPlaces(Activity activity,double lat, double lng,
                                       final OnPlacesFetchListener listener) {
        venuesMap = new TreeMap<String, Location>();
        location.setLatitude(lat);
        location.setLongitude(lng);
        venuesCriteria.setLocation(location);
        venuesCriteria.setRadius(radius);
        venuesCriteria.setQuantity(resultSize);
        venuesCriteria.setCategoryIds(categoryIds);
        async = new EasyFoursquareAsync(activity);
        async.getVenuesNearby(new FoursquareVenuesRequestListener() {
            @Override
            public void onVenuesFetched(ArrayList<Venue> venues) {
                if (venues != null && venues.size() > 0) {
                    for (Venue venue : venues) {
                        Location loc = new Location(LocationManager.GPS_PROVIDER);
                        loc.setLatitude(venue.getLocation().getLat());
                        loc.setLongitude(venue.getLocation().getLng());
                        venuesMap.put(venue.getName().trim(), loc);
                    }
                } else {
                    Log.d(TAG, "No venues returned by API");
                }
                if (listener != null) {
                    listener.onPlacesFetched(venuesMap);
                }
            }

            @Override
            public void onError(String errorMsg) {
                Log.d(TAG, errorMsg);
                if (listener != null) {
                    listener.onPlacesFetched(venuesMap);
                }
            }
        }, venuesCriteria);
    }

    public static void getTrendingNearbyPlaces(Activity activity, double lat, double lng,
                                               final OnPlacesFetchListener listener) {
        trendingVenuesMap = new TreeMap<String, Location>();
        location.setLatitude(lat);
        location.setLongitude(lng);
        trendingVenuesCriteria.setRadius(radius);
        trendingVenuesCriteria.setLocation(location);
        trendingVenuesCriteria.setlimit(resultSize);
        async = new EasyFoursquareAsync(activity);
        async.getTrendingVenuesNearby(new FoursquareTrendingVenuesRequestListener() {
            @Override
            public void onTrendedVenuesFetched(ArrayList<Venue> venues) {
                if (venues != null && venues.size() > 0) {
                    for (Venue venue : venues) {
                        Location loc = new Location(LocationManager.GPS_PROVIDER);
                        loc.setLatitude(venue.getLocation().getLat());
                        loc.setLongitude(venue.getLocation().getLng());
                        trendingVenuesMap.put(venue.getName().trim(), loc);
                    }
                } else {
                    Log.d(TAG, "No venues returned by API");
                }
                if (listener != null) {
                    listener.onPlacesFetched(trendingVenuesMap);
                }
            }

            @Override
            public void onError(String errorMsg) {
                Log.d(TAG, errorMsg);
                if (listener != null) {
                    listener.onPlacesFetched(trendingVenuesMap);
                }
            }
        }, trendingVenuesCriteria);
    }

}
