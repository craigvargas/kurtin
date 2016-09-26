package com.travelguide.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.travelguide.R;
import com.travelguide.helpers.DeviceDimensionsHelper;
import com.travelguide.helpers.NetworkAvailabilityCheck;
import com.travelguide.helpers.Preferences;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kprav
 *
 * History:
 *   10/17/2015     kprav       Initial Version
 */
public class LoginFragment extends DialogFragment {

    private Button btnLogin;

    private ParseUser parseUser;
    private String name = null;
    private String email = null;
    private String profilePicUrl = null;
    private String coverPicUrl = null;

    private ImageView ivProfilePic;
    private ImageView ivCoverPic;

    private OnLoginLogoutListener mLoginLogoutListener;
    private boolean saveOrUpdate = false;

    private MaterialDialog progressDialog;

    public static final List<String> permissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
    }};

    public LoginFragment() {

    }

    public interface OnLoginLogoutListener {
        public void onLoginOrLogout(boolean status);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.dialogFragment);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ivProfilePic = (ImageView) view.findViewById(R.id.ivProfilePic);
        ivCoverPic = (ImageView) view.findViewById(R.id.ivCoverPic);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);

        if (Preferences.readBoolean(getContext(), Preferences.User.LOG_IN_STATUS)) {
            btnLogin.setText(R.string.label_logout);
        }

        setHasOptionsMenu(false);

        btnLogin.setEnabled(true);
        btnLogin.setVisibility(View.VISIBLE);
        setLoginButtonOnClickListener();

        // To refresh user from parse followed by refreshing user from FB. Not used currently, but left for possible future use
        // if (ParseUser.getCurrentUser() != null) {
        //     ParseFacebookUtils.linkWithReadPermissionsInBackground(ParseUser.getCurrentUser(), getActivity(), permissions, new SaveCallback() {
        //         @Override
        //         public void done(ParseException e) {
        //             getUserDetailsFromParse();
        //             Preferences.writeString(getContext(),
        //                     Preferences.User.USER_OBJECT_ID, parseUser.getObjectId());
        //         }
        //     });
        // }

        progressDialog = new MaterialDialog.Builder(getContext())
                .title(R.string.logging_in)
                .content(R.string.please_wait)
                .progress(true, 0)
                .build();

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Request a dialog without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Make dialog background translucent, leaving it opaque for now
        // final Drawable d = new ColorDrawable(Color.WHITE);
        // d.setAlpha(225);
        // dialog.getWindow().setBackgroundDrawable(d);

        // Set dialog size
        int layoutWidth = DeviceDimensionsHelper.getDisplayWidth(getActivity());
        int layoutHeight = DeviceDimensionsHelper.getDisplayHeight(getActivity());
        dialog.getWindow().setLayout((6 * layoutWidth) / 7, (4 * layoutHeight) / 5);

        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void login(RequestType requestType) {
        if (requestType == RequestType.UPDATE)
            getUserDetailsFromParse();
        if (requestType == RequestType.NEW)
            getUserDetailsFromFB(requestType);
    }

    public void logout(ParseUser parseUser, final Context context) {
        if (parseUser != null
                && ParseFacebookUtils.isLinked(parseUser)
                && Preferences.readBoolean(context, Preferences.User.LOG_IN_STATUS)) {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        e.printStackTrace();
                    } else {
                        String userName = Preferences.readString(context, Preferences.User.NAME);
                        clearPrefs(context);
                        Toast.makeText(context, "User " + userName + " logged out!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void clearPrefs(Context context) {
        parseUser = null;
        Preferences.writeString(context, Preferences.User.USER_OBJECT_ID, Preferences.DEF_VALUE);
        Preferences.writeString(context, Preferences.User.PROFILE_PIC_URL, Preferences.DEF_VALUE);
        Preferences.writeString(context, Preferences.User.COVER_PIC_URL, Preferences.DEF_VALUE);
        Preferences.writeString(context, Preferences.User.NAME, Preferences.DEF_VALUE);
        Preferences.writeString(context, Preferences.User.EMAIL, Preferences.DEF_VALUE);
        Preferences.writeBoolean(context, Preferences.User.LOG_IN_STATUS, false);
        if (mLoginLogoutListener == null) {
            attachListener(context);
        }
        dismissDialog(context);
    }

    private void setLoginButtonOnClickListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (!Preferences.readBoolean(getContext(), Preferences.User.LOG_IN_STATUS)) {
                    ParseFacebookUtils.logInWithReadPermissionsInBackground(getActivity(), permissions, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException err) {
                            if (err == null) {
                                if (user == null) {
                                    Log.d("CANCEL", "The user cancelled the Facebook login.");
                                    clearPrefs(getContext());
                                } else if (user.isNew()) {
                                    Log.d("SIGN IN", "User signed up and logged in through Facebook!");
                                    login(RequestType.NEW);
                                } else {
                                    Log.d("LOGGED IN", "User logged in through Facebook!");
                                    login(RequestType.UPDATE);
                                }
                            }
                            if (err != null) {
                                clearPrefs(getContext());
                                err.printStackTrace();
                            }
                        }
                    });
                } else {
                    logout(parseUser, getContext());
                }
            }
        });
    }

    private void getUserDetailsFromFB(final RequestType requestType) {
        if (!NetworkAvailabilityCheck.networkAvailable(getActivity())) {
            if (requestType == RequestType.NEW)
                NetworkAvailabilityCheck.showToast(getActivity());
            return;
        }
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email, picture, cover");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(final GraphResponse response) {
                        /* handle the result */
                        try {
                            Log.d("response", response.toString());

                            boolean updateCoverPicUrl = true;
                            saveOrUpdate = true;

                            name = response.getJSONObject().getString("name");
                            Preferences.writeString(getContext(), Preferences.User.NAME, name);

                            if (response.getJSONObject().optString("email") != null) {
                                email = response.getJSONObject().getString("email");
                                Preferences.writeString(getContext(), Preferences.User.EMAIL, email);
                            } else {
                                email = "";
                            }

                            profilePicUrl = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");

                            if (response.getJSONObject().optJSONObject("cover") != null) {
                                coverPicUrl = response.getJSONObject().getJSONObject("cover").getString("source");
                            } else
                                coverPicUrl = null;

                            if (!Preferences.readString(getContext(), Preferences.User.PROFILE_PIC_URL).equals(profilePicUrl)) {
                                updateCoverPicUrl = false;
                                Picasso.with(getContext()).load(profilePicUrl).into(ivProfilePic, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Preferences.writeString(getContext(), Preferences.User.PROFILE_PIC_URL, profilePicUrl);
                                        if (coverPicUrl != null) {
                                            if (!Preferences.readString(getContext(), Preferences.User.COVER_PIC_URL).equals(coverPicUrl)) {
                                                Picasso.with(getContext()).load(coverPicUrl).into(ivCoverPic, new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Preferences.writeString(getContext(), Preferences.User.COVER_PIC_URL, coverPicUrl);
                                                        saveOrUpdateParseUser(requestType);
                                                    }

                                                    @Override
                                                    public void onError() {
                                                        saveOrUpdateParseUser(requestType);
                                                    }
                                                });
                                            }
                                        } else {
                                            ivCoverPic.setImageResource(android.R.color.transparent);
                                            saveOrUpdateParseUser(requestType);
                                        }
                                    }

                                    @Override
                                    public void onError() {
                                        // TODO: Handle Error
                                        // saveOrUpdateParseUser(requestType);
                                    }
                                });
                            } else {
                                saveOrUpdate = false;
                            }

                            if (updateCoverPicUrl) {
                                updateCoverPicUrl = false;
                                if (coverPicUrl != null) {
                                    if (!Preferences.readString(getContext(), Preferences.User.COVER_PIC_URL).equals(coverPicUrl)) {
                                        saveOrUpdate = true;
                                        Picasso.with(getContext()).load(coverPicUrl).resize(getView().getWidth(), 0).into(ivCoverPic, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Preferences.writeString(getContext(), Preferences.User.COVER_PIC_URL, coverPicUrl);
                                                saveOrUpdateParseUser(requestType);
                                            }
                                            @Override
                                            public void onError() {
                                                saveOrUpdateParseUser(requestType);
                                            }
                                        });
                                    } else {
                                        saveOrUpdate = false;
                                    }
                                } else {
                                    saveOrUpdate = true;
                                    ivCoverPic.setImageResource(android.R.color.transparent);
                                    saveOrUpdateParseUser(requestType);
                                }
                            }

                            Preferences.writeBoolean(getContext(), Preferences.User.LOG_IN_STATUS, true);
                            if (!saveOrUpdate) {
                                dismissDialog(getContext());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }

    private void saveOrUpdateParseUser(RequestType requestType) {
        parseUser = ParseUser.getCurrentUser();
        Preferences.writeString(getContext(), Preferences.User.USER_OBJECT_ID, parseUser.getObjectId());
        parseUser.setUsername(name);
        parseUser.setEmail(email);
        // Saving profile photo as a ParseFile
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) ivProfilePic.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();
        String thumbName = parseUser.getUsername().replaceAll("\\s+", "");
        ParseFile profilePicture = new ParseFile(thumbName + "_thumb.jpg", data);
        ParseFile coverPicture = null;
        // Saving cover photo as a ParseFile
        if (coverPicUrl != null && ivCoverPic.getDrawable() != null) {
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            Bitmap bitmap2 = ((BitmapDrawable) ivCoverPic.getDrawable()).getBitmap();
            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, stream2);
            byte[] data2 = stream2.toByteArray();
            String fileName = parseUser.getUsername().replaceAll("\\s+", "");
            coverPicture = new ParseFile(fileName + "_cover.jpg", data2);
        }
        // else {
        //     int width = DeviceDimensionsHelper.getDisplayWidth(getContext());
        //     int height = DeviceDimensionsHelper.getDisplayHeight(getContext());
        //     int[] colors = new int[width * height];
        //     for (int i = 0; i < width * height; i++) {
        //         colors[i] = R.color.colorPrimary;
        //     }
        //     bitmap = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
        //     bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        //     data = stream.toByteArray();
        //     String fileName = parseUser.getUsername().replaceAll("\\s+", "");
        //     coverPicture = new ParseFile(fileName + "_cover.jpg", data);
        // }
        if (requestType == RequestType.NEW) {
            parseUser.put("favTrips", new ArrayList<String>());
            saveNewParseUser(parseUser, profilePicture, coverPicture);
        } else {
            updateExistingParseUser(parseUser, profilePicture, coverPicture);
        }
    }

    private void saveNewParseUser(final ParseUser parseUser, final ParseFile profilePicture, final ParseFile coverPicture) {
        coverPicture.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                parseUser.put("coverPic", coverPicture);
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        // Toast.makeText(getActivity(), "New user: " + name + " Signed up", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        profilePicture.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                parseUser.put("profileThumb", profilePicture);
                //Finally save all the user details
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(getActivity(), "New user: " + name + " Signed up", Toast.LENGTH_SHORT).show();
                        dismissDialog(getContext());
                    }
                });
            }
        });
    }

    private void updateExistingParseUser(final ParseUser parseUser, final ParseFile profilePicture, final ParseFile coverPicture) {
        String parseUserObjectId = parseUser.getObjectId();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", parseUserObjectId);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    user.put("profileThumb", profilePicture);
                    if (coverPicture != null)
                        user.put("coverPic", coverPicture);
                    else {
                        // Toast.makeText(getActivity(), "NULL & REMOVED", Toast.LENGTH_SHORT).show();
                        user.remove("coverPic");
                    }
                    user.saveInBackground();
                    // Toast.makeText(getActivity(), "Existing User: " + name + " Updated", Toast.LENGTH_SHORT).show();
                }
                if (e != null) {
                    e.printStackTrace();
                }
                dismissDialog(getContext());
            }
        });
    }

    private void getUserDetailsFromParse() {
        parseUser = ParseUser.getCurrentUser();
        // Fetch profile photo
        try {
            ParseFile parseFile = parseUser.getParseFile("profileThumb");
            byte[] data = parseFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            ivProfilePic.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ParseFile parseFile = parseUser.getParseFile("coverPic");
            if (parseFile != null) {
                byte[] data = parseFile.getData();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                ivCoverPic.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getActivity(), "Welcome back " + parseUser.getUsername(), Toast.LENGTH_SHORT).show();
        getUserDetailsFromFB(RequestType.UPDATE);
    }

    enum RequestType {
        NEW, UPDATE;
    }

    public void attachListener(Context context) {
        try {
            mLoginLogoutListener = (OnLoginLogoutListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoginLogoutListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachListener(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLoginLogoutListener = null;
    }

    private void dismissDialog(Context context) {
        if (progressDialog != null)
            progressDialog.dismiss();
        if (mLoginLogoutListener != null)
            mLoginLogoutListener.onLoginOrLogout(Preferences.readBoolean(context, Preferences.User.LOG_IN_STATUS));
        if (getDialog() != null)
            dismiss();
    }

}
