package com.travelguide.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.travelguide.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kprav
 *
 * History:
 *   10/20/2015     kprav       Initial Version
 */
public class NearbyPlacesGoogle {

    public static class NPlace {
        public String iconUrl;
        public String placeId;
        public String name;
        public String vicinity; // This attribute in most cases is the address of the place
        public Double latitude;
        public Double longitude;
    }

    public static List<NPlace> nearbyPlaceList;
    public static Context mContext;

    // Get the nearby places based on the given latitude and longitude
    public static void getNearbyPlaces(Double mLatitude, Double mLongitude, Context context) {
        mContext = context;
        nearbyPlaceList = null;
        // San Francisco = 37.7833, -122.4167;
        String url = buildUrl(mLatitude, mLongitude);
        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(url);
    }

    // Build the Google Places Web Service API URL
    private static String buildUrl(Double mLatitude, Double mLongitude) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        url.append("location=" + mLatitude + "," + mLongitude);
        url.append("&radius=8000"); // 8km (5mi) radius
        url.append("&rankby=prominence"); // Change to "&rankby=distance" if needed
        url.append("&types=" + "airport|amusement_park|aquarium|art_gallery|bowling_alley|campground|" +
                "casino|establishment|food|movie_theater|museum|night_club|park|restaurant|shopping_mall" +
                "|spa|stadium|subway_station|train_station|university|zoo");
        url.append("&key=");
        url.append(mContext.getResources().getString(R.string.google_places_web_service_api_key));
        Log.d("Place List URL", url.toString());
        return url.toString();
    }

    // Async task to fire the API request and download the nearby place list
    private static class PlacesTask extends AsyncTask<String, Integer, String> {
        String data = null;
        private String downloadUrl(String strUrl) throws IOException {
            Log.d("Place", "Begin Download...");
            String downloadedData = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    Log.d("Place Line", line);
                    result.append(line);
                }
                downloadedData = result.toString();
                br.close();
            } catch (Exception e) {
                Log.d("Exception downloading", e.toString());
            } finally {
                assert iStream != null;
                iStream.close();
                urlConnection.disconnect();
            }
            return downloadedData;
        }

        @Override
        protected String doInBackground(String... url) {
            // Execute the HTTP request asynchronously
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Exception BG Task", e.toString());
            }
            Log.d("Place Data", data);
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            // Parse the JSON response
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    // Async task to parse the downloaded results and build NPlace list
    private static class ParserTask extends AsyncTask<String, Integer, List<NPlace>> {
        JSONObject jObject;

        @Override
        protected List<NPlace> doInBackground(String... jsonData) {
            // Convert the string JSON response into JSONObject and get list
            List<NPlace> places = null;
            PlaceJSON placeJson = new PlaceJSON();
            try {
                jObject = new JSONObject(jsonData[0]);
                places = placeJson.parse(jObject);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<NPlace> list) {
            for (int i = 0; i < list.size(); i++) {
                NPlace place = list.get(i);
                // Log.d("Place " + Integer.toString(i) + " ID", place.placeId);
                // Log.d("Place " + Integer.toString(i) + " Name", place.name);
                // Log.d("Place " + Integer.toString(i) + " Lat", Double.toString(place.latitude));
                // Log.d("Place " + Integer.toString(i) + " Lng", Double.toString(place.longitude));
                // Log.d("Place " + Integer.toString(i) + " Vicinity", place.vicinity);
                // Log.d("Place " + Integer.toString(i) + " ICON", place.iconUrl);
            }
            nearbyPlaceList = list;
        }
    }

    // Class with helper methods to return places in a list
    private static class PlaceJSON {
        public List<NPlace> parse(JSONObject jObject) {
            JSONArray jPlaces = null;
            try {
                jPlaces = jObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return getPlaces(jPlaces);
        }

        private List<NPlace> getPlaces(JSONArray jPlaces) {
            int placesCount = jPlaces.length();
            List<NPlace> placesList = new ArrayList<NPlace>();
            NPlace place = null;
            for (int i = 0; i < placesCount; i++) {
                try {
                    place = getPlace((JSONObject) jPlaces.get(i));
                    if (place != null)
                        placesList.add(place);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return placesList;
        }

        private NPlace getPlace(JSONObject jsonObject) {
            NPlace place = new NPlace();
            try {
                if (jsonObject.optString("icon") == null ||
                        jsonObject.optString("place_id") == null ||
                        jsonObject.optString("name") == null ||
                        jsonObject.optString("vicinity") == null ||
                        jsonObject.optJSONObject("geometry") == null ||
                        jsonObject.getJSONObject("geometry").optJSONObject("location") == null ||
                        jsonObject.getJSONObject("geometry").getJSONObject("location").optString("lat") == null ||
                        jsonObject.getJSONObject("geometry").getJSONObject("location").optString("lng") == null) {
                    return null;
                }
                if (!jsonObject.isNull("icon")) {
                    place.iconUrl = jsonObject.getString("icon");
                }
                if (!jsonObject.isNull("place_id")) {
                    place.placeId = jsonObject.getString("place_id");
                }
                if (!jsonObject.isNull("name")) {
                    place.name = jsonObject.getString("name");
                }
                if (!jsonObject.isNull("vicinity")) {
                    place.vicinity = jsonObject.getString("vicinity");
                }
                place.latitude = Double.parseDouble(jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat"));
                place.longitude = Double.parseDouble(jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return place;
        }
    }

}
