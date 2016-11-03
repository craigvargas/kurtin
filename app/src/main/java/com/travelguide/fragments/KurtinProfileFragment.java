package com.travelguide.fragments;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.travelguide.R;
import com.travelguide.helpers.Preferences;
import com.travelguide.helpers.AppCodesKeys;
import com.travelguide.listener.KurtinListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.travelguide.R.id.tvHuntName;

/**
 * A simple {@link Fragment} subclass.
 */
public class KurtinProfileFragment extends Fragment {

    private static final String IS_NEW_USER_KEY = "isNewUser";

    ViewHolder vh;

    String mProfilePicUrl;
    String mProfilePicLocalFilePath;

    ParseUser mParseUser;

    Boolean mIsNewUser;

    private KurtinListener mKurtinListener;


    public KurtinProfileFragment() {
        // Required empty public constructor
    }

    public static KurtinProfileFragment newInstance(Boolean isNewUser){
        KurtinProfileFragment fragment = new KurtinProfileFragment();

        Bundle arguments = new Bundle();
        arguments.putBoolean(IS_NEW_USER_KEY, isNewUser);
        fragment.setArguments(arguments);

        return  fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View kurtinProfileView = inflater.inflate(R.layout.fragment_kurtin_profile, container, false);

        Bundle arguments = this.getArguments();

        vh = new ViewHolder();
        vh.bindViews(kurtinProfileView, arguments);

        defineClickListeners();

        ParseUser parseUser = ParseUser.getCurrentUser();
        Boolean userIsLoggedIn = (parseUser != null);
        if (userIsLoggedIn) {
            loadProfilePic();
        }

        return kurtinProfileView;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mKurtinListener = (KurtinListener) context;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class ViewHolder {
        ImageView ivProfilePic;
        Button btnChangePic;
        Button btnFinish;

        ViewHolder() {
            ivProfilePic = null;
            btnChangePic = null;
            btnFinish = null;
        }

        private void bindViews(View view, Bundle arguments) {
            ivProfilePic = (ImageView) view.findViewById(R.id.ivProfilePic);
            btnChangePic = (Button) view.findViewById(R.id.btnChangePic);
            btnFinish = (Button) view.findViewById(R.id.btnFinish);

            Typeface cabinBoldFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/cabin_bold.ttf");
            btnFinish.setTypeface(cabinBoldFont);

            try {
                mIsNewUser = arguments.getBoolean(IS_NEW_USER_KEY);
            }catch (Exception e){
                mIsNewUser = false;
            }finally {
                if (mIsNewUser){
                    btnFinish.setVisibility(View.VISIBLE);
                }else {
                    btnFinish.setVisibility(View.GONE);
                }
            }
        }
    }

    private void defineClickListeners() {
        //Clicking the profile pic
        vh.ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
            }
        });

        //Clicking the "change profile pic" text
        vh.btnChangePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
            }
        });

        //Clicking the finish button that appears if you just signed up
        vh.btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mKurtinListener.onReturnToHomeScreen(true);
//                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void loadProfilePic() {
        Boolean userIsLoggedIn;

        //If user is logged in then see if we can load picture from profile URL
        try {
            userIsLoggedIn = Preferences.readBoolean(getContext(), Preferences.User.LOG_IN_STATUS);
            if (userIsLoggedIn) {
                mProfilePicUrl = Preferences.readString(getContext(), Preferences.User.PROFILE_PIC_URL);
                mProfilePicLocalFilePath = Preferences.readString(getContext(), Preferences.User.PROFILE_PIC_LOCAL_PATH);

                if (TextUtils.isEmpty(mProfilePicLocalFilePath)) {
                    vh.ivProfilePic.setImageResource(R.drawable.profile_placeholder);
                } else {
                    File profilePicFile = new File(mProfilePicLocalFilePath);
                    Bitmap profilePicBitmap = BitmapFactory.decodeStream(new FileInputStream(profilePicFile));
                    vh.ivProfilePic.setImageBitmap(profilePicBitmap);
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        vh.ivProfilePic.setImageResource(R.drawable.profile_placeholder);
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, AppCodesKeys.CHANGE_PROFILE_PIC_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == AppCodesKeys.CHANGE_PROFILE_PIC_CODE) {
                //Returned from a user request to change the profile pick
                try {
                    //extract the photo uri
                    Uri selectedPhotoUri = data.getData();
                    // get bitmap stored in photo uri
                    Bitmap selectedPic = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedPhotoUri);

                    vh.ivProfilePic.setImageBitmap(selectedPic);
                    saveProfilePicToInternalStorage(selectedPic);
                    saveProfilePicToParse(selectedPic);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveProfilePicToParse(Bitmap profilePicBitmap) {
        mParseUser = ParseUser.getCurrentUser();
        Boolean userIsLoggedIn = (mParseUser != null);
        if (userIsLoggedIn) {
            Log.v("Save Profile Pic Parse", "parseUser not null");
            ByteArrayOutputStream profilePicStream = new ByteArrayOutputStream();
            profilePicBitmap.compress(Bitmap.CompressFormat.JPEG, 100, profilePicStream);
            byte[] profilePicData = profilePicStream.toByteArray();
//            String thumbName = parseUser.getUsername().replaceAll("\\s+", "");
            final ParseFile profilePicParseFile = new ParseFile("profile_pic.jpg", profilePicData);
            profilePicParseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        mParseUser.put(AppCodesKeys.PARSE_USER_PROFILE_PIC_KEY, profilePicParseFile);
                        mParseUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null){
                                    //
                                }else{
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            Log.v("Save Profile Pic", "parseUser is null");
        }
    }

    private String saveProfilePicToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File imageDirectory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File profilePicPath = new File(imageDirectory, "profile.jpg");
        // Define output stream to write to the file
        FileOutputStream profilePicOutputStream = null;

        try {
            profilePicOutputStream = new FileOutputStream(profilePicPath, false);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, profilePicOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                profilePicOutputStream.close();
                Preferences.writeString(getContext(), Preferences.User.PROFILE_PIC_LOCAL_PATH, profilePicPath.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return imageDirectory.getAbsolutePath();
    }

    private void loadImageFromStorage(String fullFilePath) {

        try {
            File file = new File(fullFilePath);
            Bitmap fileBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
//            ImageView img=(ImageView)findViewById(R.id.imgPicker);
//            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void loadImageFromStorageIntoView(String fullFilePath, ImageView imageView) {

        //If user is logged in then see if we can load picture from profile URL
        try {
            if (TextUtils.isEmpty(fullFilePath)) {
                imageView.setImageResource(R.drawable.profile_placeholder);
            } else {
                File file = new File(fullFilePath);
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                imageView.setImageBitmap(bitmap);
            }
            return;

        } catch (Exception e) {
            e.printStackTrace();
        }

        imageView.setImageResource(R.drawable.profile_placeholder);
    }

    public static String savePicToInternalStorage(Bitmap bitmapImage, String fileName, Context context) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File imageDirectory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File picPath = new File(imageDirectory, fileName);
        // Define output stream to write to the file
        FileOutputStream picOutputStream = null;

        try {
            picOutputStream = new FileOutputStream(picPath, false);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, picOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                picOutputStream.close();
                Preferences.writeString(context, Preferences.User.PROFILE_PIC_LOCAL_PATH, picPath.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return imageDirectory.getAbsolutePath();
    }


    public static Bitmap getBitmapFromParseUser(ParseUser parseUser, String fieldKey) {
        Boolean userIsLoggedIn = (parseUser != null);

        //If parseUser is logged in then try to retrieve the profile pic
        if (userIsLoggedIn) {
            ParseFile parseFile;
            try {
                parseFile = (ParseFile) parseUser.get(AppCodesKeys.PARSE_USER_PROFILE_PIC_KEY);
                byte[] profilePicBytes = parseFile.getData();
                Bitmap profilePicBitmap = BitmapFactory.decodeByteArray(profilePicBytes, 0, profilePicBytes.length);
                return profilePicBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

    public static void loadImageFromParseFileIntoImageView(ParseFile parseFile, ImageView imageView) {
            try {
                byte[] profilePicBytes = parseFile.getData();
                Bitmap profilePicBitmap = BitmapFactory.decodeByteArray(profilePicBytes, 0, profilePicBytes.length);
                imageView.setImageBitmap(profilePicBitmap);
                return ;
            }catch (Exception e) {
                e.printStackTrace();
                return;
            }
    }

    private void loadProfilePicFromParse() {
        ParseUser parseUser = ParseUser.getCurrentUser();
        Boolean userIsLoggedIn = (parseUser != null);

        //If parseUser is logged in then try to retrieve the profile pic
        if (userIsLoggedIn) {
            ParseFile parseFile;
            try {
                parseFile = (ParseFile) parseUser.get(AppCodesKeys.PARSE_USER_PROFILE_PIC_KEY);
                byte[] profilePicBytes = parseFile.getData();
                Bitmap profilePicBitmap = BitmapFactory.decodeByteArray(profilePicBytes, 0, profilePicBytes.length);
                vh.ivProfilePic.setImageBitmap(profilePicBitmap);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                parseFile = null;
            }
        }
    }

}
