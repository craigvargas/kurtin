package com.travelguide.helpers;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by htammare on 10/25/2015.
 */
public class GoogleImageSearch {
    private String url = "http://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=";
    private String urlModified;
    private String imageURL;
    private String queryFromDetails;

    public interface OnImageFetchListener {
        public void onImageFetched(String url);
    }

    public void fetchPlaceImage(String searchString, final String parseObjectId,String queryFrom, final OnImageFetchListener listener) {
        String sizeOfList = "&rsz=1";
        final String imageSize = "&imgsz=Medium";
        queryFromDetails = queryFrom;
        urlModified = url + searchString + imageSize + sizeOfList;
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(urlModified, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseData) {
                try {
                    JSONObject response = responseData.getJSONObject("responseData");
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject imageData = results.getJSONObject(i);
                        imageURL = imageData.getString("url");
                        if (listener != null)
                            listener.onImageFetched(imageURL);
                        if(queryFromDetails.contains("PlanDetails")){
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("PlanDetails");
                            query.whereEqualTo("objectId", parseObjectId);
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                public void done(ParseObject object, ParseException e) {
                                    if (object != null) {
                                        object.put("cityImageUrl", imageURL);
                                        object.saveInBackground();
                                    } else {
                                        Log.d("ERROR", "Couldn't Retrieve the object.");
                                    }
                                }
                            });
                        }else{
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("PlaceDetails");
                            query.whereEqualTo("objectId", parseObjectId);
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                public void done(ParseObject object, ParseException e) {
                                    if (object != null) {
                                        object.put("placeImageUrl", imageURL);
                                        object.saveInBackground();
                                    } else {
                                        Log.d("ERROR", "Couldn't Retrieve the object.");
                                    }
                                }
                            });

                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable error) {
            }
        });
    }
}

