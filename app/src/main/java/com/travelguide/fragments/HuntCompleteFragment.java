package com.travelguide.fragments;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.travelguide.R;
import com.travelguide.listener.KurtinListener;
import com.travelguide.models.HuntJoin;

import static com.travelguide.R.id.tvHuntName;

/**
 * A simple {@link Fragment} subclass.
 */
public class HuntCompleteFragment extends Fragment {

    private Button btnNextHunt;

    private TextView tvCongrats;
    private TextView tvHuntComplete;
    private TextView tvPointsEarned;

    private KurtinListener mKurtinListener;


    public HuntCompleteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View huntCompleteView = inflater.inflate(R.layout.fragment_hunt_complete, container, false);

        btnNextHunt = (Button) huntCompleteView.findViewById(R.id.btnNextHunt);

        tvCongrats = (TextView) huntCompleteView.findViewById(R.id.tvCongrats);
        tvHuntComplete = (TextView) huntCompleteView.findViewById(R.id.tvHuntComplete);
        tvPointsEarned = (TextView) huntCompleteView.findViewById(R.id.tvPointsEarned);

        fillPointsEarnedTv(tvPointsEarned);

        Typeface cabinBoldFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/cabin_bold.ttf");
        Typeface cabinRegularFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/cabin_regular.ttf");
        tvCongrats.setTypeface(cabinBoldFont);
        tvCongrats.setTypeface(cabinRegularFont);
        tvCongrats.setTypeface(cabinRegularFont);

        btnNextHunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKurtinListener.onReturnToHomeScreen(true);
            }
        });

        return huntCompleteView;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try{
            mKurtinListener = (KurtinListener) context;
        }catch (Exception e){
            e.printStackTrace();
            Log.e("HuntCompleteFragment","Must implement KurtinListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mKurtinListener = null;
    }

    private void fillPointsEarnedTv(TextView pointsEarnedTextView){
        HuntJoin huntJoinRecord = mKurtinListener.getHuntJoinRecord();
        Integer pointsEarned = huntJoinRecord.getPointsEarned();
        String pointsEarnedString = pointsEarnedTextView.getText().toString();
        pointsEarnedString += " " + pointsEarned.toString();
        pointsEarnedTextView.setText(pointsEarnedString);
    }

}
