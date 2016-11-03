package com.travelguide.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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
import com.travelguide.helpers.AppCodesKeys;
import com.travelguide.helpers.NetworkAvailabilityCheck;
import com.travelguide.helpers.Preferences;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.travelguide.fragments.KurtinProfileFragment.getBitmapFromParseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class KurtinLoginFragment extends Fragment {

    private static final int ZERO_POINTS = 0;

    private ViewHolder viewHolder;

    private ParseUser parseUser;
    private String name = null;
    private String email = null;
    private String profilePicUrl = null;
    private String coverPicUrl = null;

    private LoginListener mLoginLogoutListener;
    private boolean saveOrUpdate = false;
    private boolean mIsLoggedIn = false;
    private boolean mIsNewUser = false;

    private MaterialDialog progressDialog;

    private KurtinLoginFragment kurtinLoginFragment = this;


    public static final List<String> permissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
    }};

    public KurtinLoginFragment() {
        // Required empty public constructor
    }

    public interface LoginListener {
        public void onCompletedLoginLogout(boolean isLoggedIn, boolean isNewUser);
        public void onSignUpRequested();
        public void onLoginRequested();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View loginView = inflater.inflate(R.layout.fragment_kurtin_login, container, false);
        viewHolder = new ViewHolder(loginView);

        setupButtonListeners();

        progressDialog = new MaterialDialog.Builder(getContext())
                .title(R.string.logging_in)
                .content(R.string.please_wait)
                .progress(true, 0)
                .build();

        return loginView;
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

    //Class to hold all view data bindings for this fragment
    private class ViewHolder {
        Button btnFacebookLogin;
        Button btnTwitterLogin;
        Button btnSnapchatLogin;
        Button btnSignUp;
        Button btnLogin;

        EditText etEmail;
        EditText etPassword;

        ImageView ivProfilePic;
        ImageView ivCoverPic;

        ViewHolder(View view){
            btnFacebookLogin = (Button) view.findViewById(R.id.btnFacebookLogin);
            btnTwitterLogin = (Button) view.findViewById(R.id.btnTwitertLogin);
            btnSnapchatLogin = (Button) view.findViewById(R.id.btnSnapchatLogin);
            btnSignUp = (Button) view.findViewById(R.id.btnSignUp);
            btnLogin = (Button) view.findViewById(R.id.btnLogin);

            etEmail = (EditText) view.findViewById(R.id.etEmail);
            etPassword = (EditText) view.findViewById(R.id.etPassword);

            ivProfilePic = (ImageView) view.findViewById(R.id.ivProfilePic);
            ivCoverPic = (ImageView) view.findViewById(R.id.ivCoverPic);
        }
    }

    private void setupButtonListeners(){
        setupFacebookBtnClickListener();
        setupSignUpBtnClickListener();
        setupLoginBtnClickListener();
    }

    private void setupLoginBtnClickListener(){
        viewHolder.btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(allLoginInputValid()){
                    loginParseUser();
                }else{
                    Toast.makeText(getContext(), "Input not valid", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Boolean allLoginInputValid(){
        if (emailIsValid()){
            return true;
        }else{
            String msg = "Email is not valid";
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    private Boolean emailIsValid(){
        return !TextUtils.isEmpty(viewHolder.etEmail.getText().toString()) &&
                Patterns.EMAIL_ADDRESS.matcher(viewHolder.etEmail.getText().toString()).matches();
    }

    private void loginParseUser(){
        progressDialog.show();
        String email = viewHolder.etEmail.getText().toString();
        String password = viewHolder.etPassword.getText().toString();

        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    mIsLoggedIn = true;
                    saveDataFromKurtinAccount();
                    completeLogin();
                } else {
                    // login failed. Look at the ParseException to see what happened.
                    progressDialog.dismiss();
                    String msg = "Login failed: " + e.toString();
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveDataFromKurtinAccount(){
        Context context = getContext();
        ParseUser parseUser = ParseUser.getCurrentUser();
        if(parseUser != null) {
            //User ObjectId
            Preferences.writeString(context, Preferences.User.USER_OBJECT_ID, parseUser.getObjectId());

            //User name
            String nickname = (String) parseUser.get(AppCodesKeys.PARSE_USER_NICKNAME_KEY);
            if(nickname != null){
                Preferences.writeString(context, Preferences.User.NAME, nickname);
            }

            //Email
            Preferences.writeString(context, Preferences.User.EMAIL, parseUser.getEmail());

            //login status
            Preferences.writeBoolean(context, Preferences.User.LOG_IN_STATUS, mIsLoggedIn);

            //Profile pic
            Bitmap profilePicBitmap = getBitmapFromParseUser(parseUser, AppCodesKeys.PARSE_USER_PROFILE_PIC_KEY);
            if (profilePicBitmap != null){
                KurtinProfileFragment.savePicToInternalStorage(profilePicBitmap, AppCodesKeys.PROFILE_PIC_FILE_NAME, context);
            }
        }
    }

    ///////////////////////////
    ////////////////////////////////
    //////////////////////////
    /////////////////////////
    //////////////////////////

    private void setupSignUpBtnClickListener(){
        viewHolder.btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mLoginLogoutListener.onSignUpRequested();
            }
        });
    }

    private void setupFacebookBtnClickListener(){
        viewHolder.btnFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if (!Preferences.readBoolean(getContext(), Preferences.User.LOG_IN_STATUS)) {
                    ParseFacebookUtils.logInWithReadPermissionsInBackground(kurtinLoginFragment, permissions, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException err) {
                            if (err == null) {
                                if (user == null) {
                                    Log.d("CANCEL", "The user cancelled the Facebook login.");
                                    clearPrefs(getContext());
                                } else if (user.isNew()) {
                                    Log.d("SIGN IN", "User signed up and logged in through Facebook!");
                                    login(LoginFragment.RequestType.NEW);
                                } else {
                                    Log.d("LOGGED IN", "User logged in through Facebook!");
                                    login(LoginFragment.RequestType.UPDATE);
                                }
                            }
                            if (err != null) {
                                clearPrefs(getContext());
                                err.printStackTrace();
                            }
                        }
                    });
                } else {
                    //User is already logged in
//                    parseUser = ParseUser.getCurrentUser();
//                    logout(parseUser, getContext());
                }
            }
        });
    }

    private void login(LoginFragment.RequestType requestType) {
        if (requestType == LoginFragment.RequestType.UPDATE) {
            getUserDetailsFromParse();
        }else if (requestType == LoginFragment.RequestType.NEW) {
            mIsNewUser = true;
            getUserDetailsFromFB(requestType);
        }
    }

    public void logoutKurtin(ParseUser parseUser, final Context context){
        if(Preferences.readBoolean(context, Preferences.User.LOG_IN_STATUS)){
            if(parseUser != null){
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            String username = Preferences.readString(context, Preferences.User.NAME);
                            clearPrefs(context);
                            String msg = "User " + username + " is logged out";
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                        }else{
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                //Parse user not logged in
                clearPrefs(context);
            }
        }
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
        }else{
            clearPrefs(context);
        }
    }

    private void clearPrefs(Context context) {
        parseUser = null;
        Preferences.writeString(context, Preferences.User.USER_OBJECT_ID, Preferences.DEF_VALUE);
        Preferences.writeString(context, Preferences.User.PROFILE_PIC_URL, Preferences.DEF_VALUE);
        Preferences.writeString(context, Preferences.User.COVER_PIC_URL, Preferences.DEF_VALUE);
        Preferences.writeString(context, Preferences.User.NAME, Preferences.DEF_VALUE);
        Preferences.writeString(context, Preferences.User.EMAIL, Preferences.DEF_VALUE);
        Preferences.writeString(context, Preferences.User.PROFILE_PIC_LOCAL_PATH, Preferences.DEF_VALUE);
        Preferences.writeBoolean(context, Preferences.User.LOG_IN_STATUS, false);
        if (mLoginLogoutListener == null) {
            attachListener(context);
        }
        completeLogin(context);
    }

    private void getUserDetailsFromFB(final LoginFragment.RequestType requestType) {
        if (!NetworkAvailabilityCheck.networkAvailable(getActivity())) {
            if (requestType == LoginFragment.RequestType.NEW)
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

                            //Cvar
                            if(response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url") != null) {
                                profilePicUrl = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");
                                Preferences.writeString(getContext(), Preferences.User.PROFILE_PIC_URL, profilePicUrl);
                            }else{
                                profilePicUrl = null;
                            }

                            if (response.getJSONObject().optJSONObject("cover") != null) {
                                coverPicUrl = response.getJSONObject().getJSONObject("cover").getString("source");
                                Preferences.writeString(getContext(), Preferences.User.COVER_PIC_URL, coverPicUrl);
                            } else
                                coverPicUrl = null;

                            Preferences.writeBoolean(getContext(), Preferences.User.LOG_IN_STATUS, true);
                            saveOrUpdateParseUser(requestType);

//
//                            if (!Preferences.readString(getContext(), Preferences.User.PROFILE_PIC_URL).equals(profilePicUrl)) {
//                                updateCoverPicUrl = false;
//                                Picasso.with(getContext()).load(profilePicUrl).into(viewHolder.ivProfilePic, new Callback() {
//                                    @Override
//                                    public void onSuccess() {
//                                        Preferences.writeString(getContext(), Preferences.User.PROFILE_PIC_URL, profilePicUrl);
//                                        if (coverPicUrl != null) {
//                                            if (!Preferences.readString(getContext(), Preferences.User.COVER_PIC_URL).equals(coverPicUrl)) {
//                                                Picasso.with(getContext()).load(coverPicUrl).into(viewHolder.ivCoverPic, new Callback() {
//                                                    @Override
//                                                    public void onSuccess() {
//                                                        Preferences.writeString(getContext(), Preferences.User.COVER_PIC_URL, coverPicUrl);
//                                                        saveOrUpdateParseUser(requestType);
//                                                    }
//
//                                                    @Override
//                                                    public void onError() {
//                                                        saveOrUpdateParseUser(requestType);
//                                                    }
//                                                });
//                                            }
//                                        } else {
//                                            viewHolder.ivCoverPic.setImageResource(android.R.color.transparent);
//                                            saveOrUpdateParseUser(requestType);
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onError() {
//                                        // TODO: Handle Error
//                                        // saveOrUpdateParseUser(requestType);
//                                    }
//                                });
//                            } else {
//                                saveOrUpdate = false;
//                            }
//
//                            if (updateCoverPicUrl) {
//                                updateCoverPicUrl = false;
//                                if (coverPicUrl != null) {
//                                    if (!Preferences.readString(getContext(), Preferences.User.COVER_PIC_URL).equals(coverPicUrl)) {
//                                        saveOrUpdate = true;
//                                        Picasso.with(getContext()).load(coverPicUrl).resize(getView().getWidth(), 0).into(viewHolder.ivCoverPic, new Callback() {
//                                            @Override
//                                            public void onSuccess() {
//                                                Preferences.writeString(getContext(), Preferences.User.COVER_PIC_URL, coverPicUrl);
//                                                saveOrUpdateParseUser(requestType);
//                                            }
//                                            @Override
//                                            public void onError() {
//                                                saveOrUpdateParseUser(requestType);
//                                            }
//                                        });
//                                    } else {
//                                        saveOrUpdate = false;
//                                    }
//                                } else {
//                                    saveOrUpdate = true;
//                                    viewHolder.ivCoverPic.setImageResource(android.R.color.transparent);
//                                    saveOrUpdateParseUser(requestType);
//                                }
//                            }
//
//                            Preferences.writeBoolean(getContext(), Preferences.User.LOG_IN_STATUS, true);
//                            if (!saveOrUpdate) {
//                                completeLogin();
//                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }

    private void saveOrUpdateParseUser(LoginFragment.RequestType requestType) {
        parseUser = ParseUser.getCurrentUser();
        Preferences.writeString(getContext(), Preferences.User.USER_OBJECT_ID, parseUser.getObjectId());
        parseUser.setUsername(name);
        parseUser.put(AppCodesKeys.PARSE_USER_NICKNAME_KEY, name);
        parseUser.setEmail(email);
        final Context context = getContext();

        if (requestType == LoginFragment.RequestType.NEW) {
            parseUser.put(AppCodesKeys.PARSE_USER_TOTAL_POINTS_KEY, ZERO_POINTS);
            //Save Profile and Cover Pics to parseFiles then Save User to Database

            //Load profile pic into a file if it exists
            if (profilePicUrl != null) {
                Glide.with(context)
                        .load(profilePicUrl)
                        .asBitmap()
                        .toBytes()
                        .fitCenter()
                        .into(new SimpleTarget<byte[]>(250, 250) {
                            @Override
                            public void onResourceReady(final byte[] data, GlideAnimation anim) {
                                // Post your bytes to a background thread and upload them here.
                                final ParseFile profilePic = new ParseFile("profile_pic.jpg", data);
                                profilePic.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        parseUser.put("profileThumb", profilePic);
                                        Bitmap profilePicBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        KurtinProfileFragment.savePicToInternalStorage(
                                                profilePicBitmap,
                                                AppCodesKeys.PROFILE_PIC_FILE_NAME,
                                                context);
                                        //Load cover pic into a file if it exists
                                        if (coverPicUrl != null) {
                                            Glide.with(context)
                                                    .load(coverPicUrl)
                                                    .asBitmap()
                                                    .toBytes()
                                                    .fitCenter()
                                                    .into(new SimpleTarget<byte[]>(500, 500) {
                                                        @Override
                                                        public void onResourceReady(byte[] data, GlideAnimation anim) {
                                                            // Post your bytes to a background thread and upload them here.
                                                            final ParseFile coverPic = new ParseFile("cover_pic.jpg", data);
                                                            coverPic.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    parseUser.put("coverPic", coverPic);
                                                                    parseUser.saveInBackground(new SaveCallback() {
                                                                        @Override
                                                                        public void done(ParseException e) {
                                                                        }
                                                                    });
                                                                    Log.v("Save", "2");
                                                                    completeLogin();
                                                                }
                                                            });
                                                        }
                                                    });
                                        } else {
                                            //Loaded profile pic but no cover pic to load
                                            parseUser.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                }
                                            });
                                            Log.v("Save", "1");
                                            completeLogin();
                                        }
                                    }
                                });
                            }
                        });
            } else {
                //No profile pic exists.  Don't bother with Cover pic.  Save user data to cloud.
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                    }
                });
                Log.v("Save", "0");
                completeLogin();
            }
        }else{
            //Not a new user so will only update name & email
            //User can choose to update a profile pic on the device
            parseUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                }
            });
            Log.v("Save", "0");
            completeLogin();
        }
    }

    //Last step in login process of new user
    private void saveNewParseUser(final ParseUser parseUser, final ParseFile profilePicture, final ParseFile coverPicture) {
        if(coverPicture != null) {
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
        }
        if(profilePicture != null) {
            profilePicture.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    parseUser.put("profileThumb", profilePicture);
                    //Finally save all the user details
                    parseUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(getActivity(), "New user: " + name + " Signed up", Toast.LENGTH_SHORT).show();
                            completeLogin();
                        }
                    });
                }
            });
        }
    }

    //Last step in login process of existing user
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
                completeLogin();
            }
        });
    }

    private void getUserDetailsFromParse() {
        parseUser = ParseUser.getCurrentUser();
        // Fetch profile photo
        try {
            //Download profile pic and store as bitmap
            Bitmap bitmapProfilePic = KurtinProfileFragment.getBitmapFromParseUser(
                    parseUser, AppCodesKeys.PARSE_USER_PROFILE_PIC_KEY);
            //Save bitmap to storage
            KurtinProfileFragment.savePicToInternalStorage(
                    bitmapProfilePic,
                    AppCodesKeys.PROFILE_PIC_FILE_NAME,
                    getContext());
            //Load bitmap into view
            viewHolder.ivProfilePic.setImageBitmap(bitmapProfilePic);
//            ParseFile parseFile = parseUser.getParseFile(AppCodesKeys.PARSE_USER_PROFILE_PIC_KEY);
//            byte[] data = parseFile.getData();
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            viewHolder.ivProfilePic.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            ParseFile parseFile = parseUser.getParseFile("coverPic");
//            if (parseFile != null) {
//                byte[] data = parseFile.getData();
//                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                viewHolder.ivCoverPic.setImageBitmap(bitmap);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try{
            name = parseUser.getString(AppCodesKeys.PARSE_USER_NICKNAME_KEY);
            if (name == null){
                name = parseUser.getUsername();
                if(name == null){
                    name = "Name not found";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            email = parseUser.getEmail();
            if (email == null){
                email = "";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Toast.makeText(getActivity(), "Welcome back " + parseUser.getUsername(), Toast.LENGTH_SHORT).show();
//        getUserDetailsFromFB(LoginFragment.RequestType.UPDATE);
        Context context = getContext();
        Preferences.writeBoolean(context, Preferences.User.LOG_IN_STATUS, true);
        Preferences.writeString(context, Preferences.User.NAME, name);
        Preferences.writeString(context, Preferences.User.EMAIL, email);

        completeLogin();
    }

    enum RequestType {
        NEW, UPDATE;
    }

    public void attachListener(Context context) {
        try {
            mLoginLogoutListener = (LoginListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement LoginListener");
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

    private void completeLogin (){
        Log.v("Login Completed", "Called");
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        mLoginLogoutListener.onCompletedLoginLogout(
                Preferences.readBoolean(getActivity(), Preferences.User.LOG_IN_STATUS),
                mIsNewUser);
    }

    //This version of completeLogin is used when you logout through a static method from another Activity or Fragment
    private void completeLogin (Context context){
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        mLoginLogoutListener.onCompletedLoginLogout(
                Preferences.readBoolean(context, Preferences.User.LOG_IN_STATUS),
                mIsNewUser);
    }

//    private void dismissDialog(Context context) {
//        if (progressDialog != null)
//            progressDialog.dismiss();
//        if (mLoginLogoutListener != null)
//            mLoginLogoutListener.onLoginOrLogout(Preferences.readBoolean(context, Preferences.User.LOG_IN_STATUS));
//        if (getDialog() != null)
//            dismiss();
//    }

}
