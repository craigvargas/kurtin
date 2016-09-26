package com.travelguide.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.travelguide.R;
import com.travelguide.adapters.TextWatcherAdapter;
import com.travelguide.helpers.GoogleImageSearch;
import com.travelguide.helpers.Preferences;
import com.travelguide.listener.OnTripPlanListener;
import com.travelguide.models.Day;
import com.travelguide.models.TripPlan;
import com.travelguide.viewmodel.TripDateViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.text.TextUtils.isEmpty;
import static com.travelguide.helpers.DateUtils.daysDifference;
import static com.travelguide.helpers.DateUtils.parse;

/**
 * Created by htammare on 10/20/2015.
 */
public class NewTripFragment extends TripBaseFragment {

    private static final String TAG = NewTripFragment.class.getSimpleName();

    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;
    private EditText planName;
    private AutoCompleteTextView destination;
    private ProgressDialog progressDialog;

    private TripDateViewModel startDateViewModel;
    private TripDateViewModel endDateViewModel;

    private OnTripPlanListener mListener;

    private String travellerType;
    private String parseNewTripObjectId;
    private Integer totalTravelDays;
    private ArrayList<String> cityNames;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setTitle(getString(R.string.new_trip));

        View view = inflater.inflate(R.layout.fragment_new_plan_creation, container, false);

        cityNames = new ArrayList<>();
        planName = (EditText) view.findViewById(R.id.tvPlanName);
        destination = (AutoCompleteTextView) view.findViewById(R.id.actvPlaceName);
        destination.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start >= 2) {
                    fetchCityNames();
                }
            }
        });

        startDate = (TextView) view.findViewById(R.id.tvStartDate);
        startDate.setOnClickListener(startDateOnClickListener());

        startTime = (TextView) view.findViewById(R.id.tvStartTime);
        startTime.setOnClickListener(startTimeOnClickListener());

        endDate = (TextView) view.findViewById(R.id.tvEndDate);
        endDate.setOnClickListener(endDateOnClickListener());

        endTime = (TextView) view.findViewById(R.id.tvEndTime);
        endTime.setOnClickListener(endTimeOnClickListener());

        RadioGroup rgGroupType = (RadioGroup) view.findViewById(R.id.rgGroupType);
        rgGroupType.setOnCheckedChangeListener(groupTypeOnCheckedChangeListener());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnTripPlanListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnTripPlanListener");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_plan_create, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                return true;
            case R.id.action_done:
                done();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @NonNull
    private RadioGroup.OnCheckedChangeListener groupTypeOnCheckedChangeListener() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbSingle:
                        travellerType = getString(R.string.single);
                        break;
                    case R.id.rbCouple:
                        travellerType = getString(R.string.couple);
                        break;
                    case R.id.rbFamily:
                        travellerType = getString(R.string.family);
                        break;
                }
                Log.d(TAG, "travellerType: " + travellerType);
            }
        };
    }

    private View.OnClickListener endDateOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        if (endDateViewModel == null) {
                            endDateViewModel = new TripDateViewModel();
                        }
                        endDateViewModel.setYear(year);
                        endDateViewModel.setMonthOfYear(monthOfYear);
                        endDateViewModel.setDayOfMonth(dayOfMonth);
                        endDate.setText(endDateViewModel.getFormattedDate());
                    }
                });
            }
        };
    }

    private View.OnClickListener endTimeOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                        if (endDateViewModel == null) {
                            endDateViewModel = new TripDateViewModel();
                        }
                        endDateViewModel.setHourOfDay(hourOfDay);
                        endDateViewModel.setMinute(minute);
                        endTime.setText(endDateViewModel.getFormattedTime());
                    }
                });
            }
        };
    }

    private View.OnClickListener startDateOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        if (startDateViewModel == null) {
                            startDateViewModel = new TripDateViewModel();
                        }
                        startDateViewModel.setYear(year);
                        startDateViewModel.setMonthOfYear(monthOfYear);
                        startDateViewModel.setDayOfMonth(dayOfMonth);
                        startDate.setText(startDateViewModel.getFormattedDate());
                    }
                });
            }
        };
    }

    private View.OnClickListener startTimeOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                        if (startDateViewModel == null) {
                            startDateViewModel = new TripDateViewModel();
                        }
                        startDateViewModel.setHourOfDay(hourOfDay);
                        startDateViewModel.setMinute(minute);
                        startTime.setText(startDateViewModel.getFormattedTime());
                    }
                });
            }
        };
    }

    private void done() {
        boolean valid = true;

        if (Preferences.DEF_VALUE.equals(Preferences.readString(getContext(), Preferences.User.USER_OBJECT_ID))) {
            Toast.makeText(getContext(), "You must be logged in first!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEmpty(planName.getText().toString().trim())) {
            planName.setError(getString(R.string.plan_name_is_required));
            valid = false;
        }

        if (isEmpty(destination.getText().toString().trim())) {
            destination.setError(getString(R.string.destination_is_required));
            valid = false;
        }

        if (startDateViewModel == null) {
            startDate.setError(getString(R.string.plan_start_date_is_required));
            valid = false;
        } else {

        }

        if (valid) {
            saveAndStartDetailsFragment();
        }
    }

    private void saveAndStartDetailsFragment() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(R.string.creating_plan);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        ParseUser user = ParseUser.getCurrentUser();

        final TripPlan planDetails = new TripPlan();
        planDetails.putCreatedUserId(user.getObjectId());
        planDetails.putCreatedUserName(user.getUsername());
        planDetails.putPlanName(planName.getText().toString());
        planDetails.putCityName(destination.getText().toString());
        planDetails.putTravelMonthNumber(startDateViewModel.getMonthOfYear() + 1);
        planDetails.putTravelMonth(startDateViewModel.getMonthName());
        planDetails.putTravelSeason(startDateViewModel.getSeasonName());

        planDetails.putTripBeginDate(startDateViewModel.getParsedDate());

        if (endDateViewModel != null) {
            planDetails.putTripEndDate(endDateViewModel.getParsedDate());
        }

        planDetails.putTripNotes("");
        planDetails.putTripCost(0);
        planDetails.putGroupType(travellerType);
        if (endDate.getText().toString().equals("")) {
            totalTravelDays = 1;
            planDetails.putTripTime(totalTravelDays);
        } else {
            totalTravelDays = daysDifference(parse(startDate.getText().toString()), parse(endDate.getText().toString())) + 1;
            planDetails.putTripTime(totalTravelDays);
        }
        planDetails.putEnabledFlag(false);

        planDetails.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    parseNewTripObjectId = planDetails.getObjectId();
                    Log.d(TAG, "The object id is: " + parseNewTripObjectId);
                    GoogleImageSearch googleImageSearch = new GoogleImageSearch();
                    googleImageSearch.fetchPlaceImage(destination.getText().toString(), parseNewTripObjectId, "PlanDetails", new GoogleImageSearch.OnImageFetchListener() {
                        @Override
                        public void onImageFetched(String url) {
                            planDetails.puCityImageURL(url);
                            saveDayDetails(parseNewTripObjectId, totalTravelDays, planName.getText().toString(), parse(startDate.getText().toString()), url);
                        }
                    });
                }
            }
        });
    }

    private void saveDayDetails(String parsePlanID, int totalTravelDays, String planName, Date startDate, final String imageUrl) {
        ParseUser user = ParseUser.getCurrentUser();
        Integer trackCount = 0;
        Date updatedStartDate = null;
        List<Day> dayList = new ArrayList<Day>();

        for (int i = 0; i < totalTravelDays; i++) {
            if (trackCount == 0) {
                updatedStartDate = startDate;
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(updatedStartDate);
                cal.add(Calendar.DATE, 1);
                updatedStartDate = cal.getTime();
            }
            trackCount = trackCount + 1;
            Day daysDetails = new Day();
            daysDetails.putCreatedUserId(user.getObjectId());
            daysDetails.putPlanName(planName);
            daysDetails.putTravelDay(trackCount);
            daysDetails.putTravelDate(updatedStartDate);
            daysDetails.put("parent", ParseObject.createWithoutData("PlanDetails", parsePlanID));
            dayList.add(daysDetails);
        }
        ParseObject.saveAllInBackground(dayList, new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    progressDialog.dismiss();
                    getFragmentManager().popBackStack();
                    if (mListener != null) {
                        mListener.onTripPlanCreated(parseNewTripObjectId, imageUrl);
                    }
                }
            }
        });
    }

    private void fetchCityNames() {
        cityNames.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("CityDetails");
        query.whereEqualTo("CountryCode", "US");
        query.whereEqualTo("TargetType", "City");
        // query.whereContains("CanonicalName", destination.getText().toString());
        query.whereStartsWith("CanonicalName", destination.getText().toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                for (int i = 0; i < list.size(); i++) {
                    cityNames.add(list.get(i).getString("CanonicalName").trim());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, cityNames);
                destination.setAdapter(adapter);
                destination.showDropDown();
            }
        });
    }

    private void showTimePickerDialog(RadialTimePickerDialogFragment.OnTimeSetListener timeSetListener) {
        final Calendar c = Calendar.getInstance();
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int minuteOfHour = c.get(Calendar.MINUTE);
        RadialTimePickerDialogFragment timePickerDialog = RadialTimePickerDialogFragment
                .newInstance(timeSetListener, hourOfDay, minuteOfHour,
                        DateFormat.is24HourFormat(getContext()));

        FragmentManager fm = getActivity().getSupportFragmentManager();
        timePickerDialog.show(fm, "FRAG_TAG_TIME_PICKER");
    }

    private void showDatePickerDialog(CalendarDatePickerDialogFragment.OnDateSetListener dateSetListener) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = CalendarDatePickerDialogFragment
                .newInstance(dateSetListener, year, month, day);
        calendarDatePickerDialogFragment.show(fm, "FRAG_TAG_DATE_PICKER");
    }

}
