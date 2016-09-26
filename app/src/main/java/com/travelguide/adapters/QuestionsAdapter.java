package com.travelguide.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedDrawable;
import com.squareup.picasso.Transformation;
import com.travelguide.R;
import com.travelguide.fragments.TripPlanDetailsFragment;
import com.travelguide.models.Questions;
import com.travelguide.scanner.OnClickCloudTrackingActivity;

import java.util.ArrayList;
import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private   final List<Questions> mQuestions;
    private final Context mContext;

    private final float mRadius;

    //private String huntID;
    //private String levelID;
    //private String quadrantID;
    //private Integer points;
    //private Integer questionNo;
    //private String questionDetails;
    private  String parentQuestionID;
    List answerlist;


    public QuestionsAdapter(List<Questions> questions, Context context) {
        this.mQuestions = questions;
        this.mContext = context;
        answerlist = new ArrayList();

        mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30,
                mContext.getResources().getDisplayMetrics());
    }

    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View placesView = inflater.inflate(R.layout.item_question, parent, false);
        return new ViewHolder(placesView);
    }

    @Override
    public void onBindViewHolder(final QuestionsAdapter.ViewHolder holder, final int position) {
        //System.out.print(mQuestions.get(position).toString());
        //Log.e("test",mQuestions.get(position).toString());
        final Questions question =  mQuestions.get(position);
        if(position == 0){
            holder.ivPlace.setBackgroundResource(R.drawable.ic_one100);

        }else if(position == 1){
            holder.ivPlace.setBackgroundResource(R.drawable.ic_two100);
        }else  if(position ==2){
            holder.ivPlace.setBackgroundResource(R.drawable.ic_three100);
        }else{
            holder.ivPlace.setBackgroundResource(R.drawable.ic_questiondefault100);
        }
        holder.tvVisitingTime.setText(question.getQuestionDetails());
        holder.rbOptionOne.setText(question.getOption1());
        holder.rbOptionTwo.setText(question.getOption2());
        holder.rbOptionThree.setText(question.getOption3());
        holder.tvQuestionID.setText(question.getObjectId());

//        answerlist.add(position,"0");




        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.rgOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton checked_rb = (RadioButton) group.findViewById(checkedId);

            int    pos=holder.rgOptions.indexOfChild(group.findViewById(checkedId));

                Log.e("onCheckedChanged", "onCheckedChanged:33333:  "+position);

                try {
//                    if(pos < answerlist.size())
//                    answerlist.remove(position);

                    TripPlanDetailsFragment.UpdateSelectedValue(holder.tvQuestionID.getText().toString(),holder.rbOptionOne.getText().toString());

                    if(position == 0){
                        if(answerlist.size() > position){
                            answerlist.set(0,pos);
                        }else {
                            answerlist.add(0,pos);
                        }
                    }else if(position == 1){
                        if(answerlist.size() > position){
                            answerlist.set(1,pos);
                        }else {
                            answerlist.add(1,pos);
                        }
                    }else if(position == 2){
                        if(answerlist.size() > position){
                            answerlist.set(2,pos);
                        }else {
                            answerlist.add(2,pos);
                        }
                    }

                    String ans = "";

                    if(pos == 0){
                        ans = question.getOption1();
                    }else if(pos == 1){
                        ans = question.getOption2();
                    }else if(pos == 2){
                        ans = question.getOption3();
                    }


//                    Toast.makeText(mContext, question.getQuestionDetails()+"--:"+ans, Toast.LENGTH_SHORT).show();


                    Log.e("onCheckedChanged", "onCheckedChanged:1111111 "+answerlist.toString() );
                    Log.e("onCheckedChanged", "onCheckedChanged:2222222 "+answerlist.get(position) );
                } catch (Exception e) {
                    e.printStackTrace();
                }

                switch (checkedId){

                }

            }
        });

        /** Commented for questions as image is not needed
        Picasso.with(mContext)

                .load(place.getPlaceImageUrl())
                .fit()
                .placeholder(R.drawable.ic_public_white_48dp)
                .transform(mTransformation)
                .into(holder.ivPlace);
        */
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

       static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivPlace;
        TextView tvVisitingTime;
          RadioGroup rgOptions;
        RadioButton rbOptionOne;
        RadioButton rbOptionTwo;
        RadioButton rbOptionThree;
        TextView tvQuestionID;
        public ViewHolder(View itemView) {
            super(itemView);
            ivPlace = (ImageView) itemView.findViewById(R.id.ivPlace);
            tvVisitingTime = (TextView) itemView.findViewById(R.id.tvVisitingTime);
            rbOptionOne = (RadioButton) itemView.findViewById(R.id.rb_option1);
            rbOptionTwo = (RadioButton) itemView.findViewById(R.id.rb_option2);
            rbOptionThree = (RadioButton) itemView.findViewById(R.id.rb_option3);
            tvQuestionID = (TextView) itemView.findViewById(R.id.tv_QuestionID);
            rgOptions = (RadioGroup) itemView.findViewById(R.id.rg_RadioGroup);


            rbOptionOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int sam = ViewHolder.this.getAdapterPosition();
                    if(sam == 0){
                        try {
                            RecyclerView r = (RecyclerView)  ((v.getParent()).getParent()).getParent();
                            RadioGroup rgOptionTwo = (RadioGroup) r.getChildAt(1).findViewById(R.id.rg_RadioGroup);
                            rgOptionTwo.clearCheck();
                            RadioGroup rgOptionThree = (RadioGroup) r.getChildAt(2).findViewById(R.id.rg_RadioGroup);
                            rgOptionThree.clearCheck();
                        } catch (NullPointerException e) {
                            System.out.print("Caught the NullPointerException");
                        }


                        //RadioGroup rgOptionTwo = (RadioGroup) r.getChildAt(1).findViewById(R.id.rg_RadioGroup);
                        //rgOptionTwo.clearCheck();
                        //rgOptionThree.clearCheck();
                    }else if(sam ==1){
                        try{
                            RecyclerView r = (RecyclerView)  ((v.getParent()).getParent()).getParent();
                            RadioGroup rgOptionTwo = (RadioGroup) r.getChildAt(0).findViewById(R.id.rg_RadioGroup);
                            RadioGroup rgOptionThree = (RadioGroup) r.getChildAt(2).findViewById(R.id.rg_RadioGroup);
                            rgOptionTwo.clearCheck();
                            rgOptionThree.clearCheck();

                        }catch(NullPointerException e){
                            System.out.print("Caught the NullPointerException");
                        }
                    }else{
                        try{
                            RecyclerView r = (RecyclerView)  ((v.getParent()).getParent()).getParent();
                            RadioGroup rgOptionTwo = (RadioGroup) r.getChildAt(0).findViewById(R.id.rg_RadioGroup);
                            RadioGroup rgOptionThree = (RadioGroup) r.getChildAt(1).findViewById(R.id.rg_RadioGroup);
                            rgOptionTwo.clearCheck();
                            rgOptionThree.clearCheck();
                        }catch (NullPointerException e){
                            System.out.print("Caught the NullPointerException");
                        }

                    }
                    //CoordinatorLayout cl = (CoordinatorLayout) v.getParent().getParent().getParent().getParent().getParent();
                    OnClickCloudTrackingActivity.UpdateSelectedValue(tvQuestionID.getText().toString(),rbOptionOne.getText().toString());
                }
            });
            rbOptionTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    //String[] paramsOptionTwo = new String[2];
                    //paramsOptionTwo[0] = tvQuestionID.getText().toString();
                    //paramsOptionTwo[1] = rbOptionTwo.getText().toString();

                    int sam = ViewHolder.this.getAdapterPosition();
                    if(sam == 0){
                        try {
                            RecyclerView r = (RecyclerView) ((v.getParent()).getParent()).getParent();
                            RadioGroup rgOptionTwo = (RadioGroup) r.getChildAt(1).findViewById(R.id.rg_RadioGroup);
                            RadioGroup rgOptionThree = (RadioGroup) r.getChildAt(2).findViewById(R.id.rg_RadioGroup);
                            rgOptionTwo.clearCheck();
                            rgOptionThree.clearCheck();
                        }catch (NullPointerException e){
                            System.out.print("Caught the NullPointerException");

                        }
                    }else if(sam ==1){
                        try {
                            RecyclerView r = (RecyclerView) ((v.getParent()).getParent()).getParent();
                            RadioGroup rgOptionTwo = (RadioGroup) r.getChildAt(0).findViewById(R.id.rg_RadioGroup);
                            RadioGroup rgOptionThree = (RadioGroup) r.getChildAt(2).findViewById(R.id.rg_RadioGroup);
                            rgOptionTwo.clearCheck();
                            rgOptionThree.clearCheck();
                        }catch (NullPointerException e){
                            System.out.print("Caught the NullPointerException");

                        }
                    }else{
                        try {
                            RecyclerView r = (RecyclerView) ((v.getParent()).getParent()).getParent();
                            RadioGroup rgOptionTwo = (RadioGroup) r.getChildAt(0).findViewById(R.id.rg_RadioGroup);
                            RadioGroup rgOptionThree = (RadioGroup) r.getChildAt(1).findViewById(R.id.rg_RadioGroup);
                            rgOptionTwo.clearCheck();
                            rgOptionThree.clearCheck();
                        }catch (NullPointerException e){
                            System.out.print("Caught the NullPointerException");

                        }
                    }
                    //CoordinatorLayout cl = (CoordinatorLayout) v.getParent().getParent().getParent().getParent().getParent();
                    ////////////////////////////////////////////////////////cl.findViewById(R.id.btnSave).setVisibility(View.VISIBLE);
                    OnClickCloudTrackingActivity.UpdateSelectedValue(tvQuestionID.getText().toString(),rbOptionTwo.getText().toString());




                }
            });


            rbOptionThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //String[] paramsOptionThree = new String[2];
                    //paramsOptionThree[0] = tvQuestionID.getText().toString();
                    //paramsOptionThree[1] = rbOptionThree.getText().toString();

                    int sam = ViewHolder.this.getAdapterPosition();
                    if(sam == 0){
                        try {
                            RecyclerView r = (RecyclerView) ((v.getParent()).getParent()).getParent();
                            RadioGroup rgOptionTwo = (RadioGroup) r.getChildAt(1).findViewById(R.id.rg_RadioGroup);
                            RadioGroup rgOptionThree = (RadioGroup) r.getChildAt(2).findViewById(R.id.rg_RadioGroup);
                            rgOptionTwo.clearCheck();
                            rgOptionThree.clearCheck();
                        }catch (NullPointerException e){

                        }
                    }else if(sam ==1){
                        try {
                            RecyclerView r = (RecyclerView) ((v.getParent()).getParent()).getParent();
                            RadioGroup rgOptionTwo = (RadioGroup) r.getChildAt(0).findViewById(R.id.rg_RadioGroup);
                            RadioGroup rgOptionThree = (RadioGroup) r.getChildAt(2).findViewById(R.id.rg_RadioGroup);
                            rgOptionTwo.clearCheck();
                            rgOptionThree.clearCheck();
                        }catch (NullPointerException e){
                            System.out.print("Caught the NullPointerException");

                        }
                    }else{
                        try {
                            RecyclerView r = (RecyclerView) ((v.getParent()).getParent()).getParent();
                            RadioGroup rgOptionTwo = (RadioGroup) r.getChildAt(0).findViewById(R.id.rg_RadioGroup);
                            RadioGroup rgOptionThree = (RadioGroup) r.getChildAt(1).findViewById(R.id.rg_RadioGroup);
                            rgOptionTwo.clearCheck();
                            rgOptionThree.clearCheck();
                        }catch(NullPointerException e){
                            System.out.print("Caught the NullPointerException");

                        }
                    }
                   // CoordinatorLayout cl = (CoordinatorLayout) v.getParent().getParent().getParent().getParent().getParent();
                    ////////////////////////////////////////////////////////////////////cl.findViewById(R.id.btnSave).setVisibility(View.VISIBLE);

                    OnClickCloudTrackingActivity.UpdateSelectedValue(tvQuestionID.getText().toString(),rbOptionThree.getText().toString());
                }

            });


        }

           @Override
           public void onClick(View v) {

               int position = getLayoutPosition(); // gets item position
               // We can access the data within the views

           }
       }

    private final Transformation mTransformation = new Transformation() {

        final boolean oval = false;

        @Override
        public Bitmap transform(Bitmap bitmap) {
            Bitmap transformed = RoundedDrawable.fromBitmap(bitmap)
                    .setCornerRadius(mRadius)
                    .setOval(oval)
                    .toBitmap();
            if (!bitmap.equals(transformed)) {
                bitmap.recycle();
            }
            return transformed;
        }

        @Override
        public String key() {
            return "rounded_radius_" + mRadius + "_oval_" + oval;
        }
    };


}


