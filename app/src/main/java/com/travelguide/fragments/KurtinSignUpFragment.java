package com.travelguide.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseUser;
import com.parse.ParseException;
import com.parse.SignUpCallback;
import com.travelguide.R;
import com.travelguide.helpers.AppCodesKeys;
import com.travelguide.helpers.Preferences;

import static com.wikitude.native_android_sdk.a.c;

/**
 * A simple {@link Fragment} subclass.
 */
public class KurtinSignUpFragment extends Fragment {

    private static final int ZERO_POINTS = 0;

    SignUpViewHolder viewHolder;

    SignUpListener mSignUpListener;

    Boolean mIsLoggedIn = false;

    private MaterialDialog progressDialog;

    public KurtinSignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View signUpView = inflater.inflate(R.layout.fragment_kurtin_sign_up, container, false);
        viewHolder = new SignUpViewHolder(signUpView);

        defineSignUpClickListener();

        progressDialog = new MaterialDialog.Builder(getContext())
                .title(R.string.logging_in)
                .content(R.string.please_wait)
                .progress(true, 0)
                .build();

        return signUpView;
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        attachSignUpListener(context);
    }

    private class SignUpViewHolder {
        Button btnSignup;

        EditText etUsername;
        EditText etEmail;
        EditText etPassword;
        EditText etConfirmPassword;

        SignUpViewHolder(View view){
            this.btnSignup = (Button) view.findViewById(R.id.btnSignUp);

            this.etUsername = (EditText) view.findViewById(R.id.etUsername);
            this.etEmail = (EditText) view.findViewById(R.id.etEmail);
            this.etPassword = (EditText) view.findViewById(R.id.etPassword);
            this.etConfirmPassword = (EditText) view.findViewById(R.id.etConfirmPassword);
        }
    }

    public interface SignUpListener{
        public void onSignUpCompleted(Boolean isLoggedIn);
    }

    private void attachSignUpListener(Context context){
        this.mSignUpListener = (SignUpListener) context;
    }

    private void defineSignUpClickListener(){
        viewHolder.btnSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(allSignUpInputValid()){
                    signUpParseUser();
//                    Toast.makeText(getContext(), "Input is valid", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Input not valid", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private Boolean allSignUpInputValid(){
        if (usernameIsValid()){
            if (emailIsValid()){
                if(passwordIsConfirmed()){
                    return true;
                }else{
                    String msg = "Passwords don't match";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }else{
                String msg = "Email is not valid";
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }else{
            String msg = "Username is empty";
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private Boolean usernameIsValid(){
        return !TextUtils.isEmpty(viewHolder.etUsername.getText().toString());
    }

    private Boolean emailIsValid(){
        return !TextUtils.isEmpty(viewHolder.etEmail.getText().toString()) &&
                Patterns.EMAIL_ADDRESS.matcher(viewHolder.etEmail.getText().toString()).matches();
    }

    private Boolean passwordIsConfirmed(){
        return viewHolder.etPassword.getText().toString()
                .equals(viewHolder.etConfirmPassword.getText().toString());
    }

    private void signUpParseUser(){
        progressDialog.show();
        ParseUser user = new ParseUser();
//        user.setUsername(viewHolder.etUsername.getText().toString());
        user.setUsername(viewHolder.etEmail.getText().toString());
        user.setEmail(viewHolder.etEmail.getText().toString());
        user.setPassword(viewHolder.etPassword.getText().toString());
        user.put(AppCodesKeys.PARSE_USER_NICKNAME_KEY, viewHolder.etUsername.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    mIsLoggedIn = true;
                    saveUserLocally();
                    initializeNonDefaultFeilds();
                    completeSignUp();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
    }

    private void saveUserLocally(){
        Context context = getContext();
        ParseUser parseUser = ParseUser.getCurrentUser();
        Preferences.writeString(context, Preferences.User.USER_OBJECT_ID, parseUser.getObjectId());
        Preferences.writeString(context, Preferences.User.NAME, parseUser.getUsername());
        Preferences.writeString(context, Preferences.User.EMAIL, parseUser.getEmail());
        Preferences.writeBoolean(context, Preferences.User.LOG_IN_STATUS, mIsLoggedIn);
    }

    private void initializeNonDefaultFeilds(){
        ParseUser parseUser = ParseUser.getCurrentUser();
        parseUser.put(AppCodesKeys.PARSE_USER_TOTAL_POINTS_KEY, ZERO_POINTS);
    }

    private void completeSignUp(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
        mSignUpListener.onSignUpCompleted(mIsLoggedIn);
    }

}
