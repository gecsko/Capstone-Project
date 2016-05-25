package com.example.kathy.minidiary;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathy.minidiary.data.DiaryContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.thebluealliance.spectrum.SpectrumDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class AddDiaryFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String ACTION_RESP = "com.example.kathy.minidiary.intent.action.MESSAGE_PROCESSED";
    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;

    public static final String ACTION_DATA_UPDATED =
            "com.example.kathy.minidiary.ACTION_DATA_UPDATED";

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    EditText mTitleEditText;
    TextView mDateTextView;
    EditText mContentEditText;
    TextView mColorTextView;
    String mCurrentDateandTime;
    String mWeather = null;
    ImageView mWeatherImage;
    int mSelectedColor = 0xff000000; // default black
    Boolean mTwoPaneMode = false;
    Hashtable<String, Integer> mTable;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mWeather = intent.getStringExtra(SimpleIntentService.PARAM_OUT_MSG);

            if (mWeather.equals(getString(R.string.no_network))){
                Toast.makeText(getContext(), R.string.no_network_msg_weather, Toast.LENGTH_SHORT).show();
            } else {
                if (mTable.containsKey(mWeather)) {
                    mWeatherImage.setImageResource(mTable.get(mWeather));
                } else {
                    // default icon
                    mWeatherImage.setImageResource(R.drawable.art_clear);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTable = new Hashtable<String, Integer>();
        mTable.put("Clear", R.drawable.art_clear);
        mTable.put("Clouds", R.drawable.art_clouds);
        mTable.put("Fog", R.drawable.art_fog);
        mTable.put("Light Clouds", R.drawable.art_light_clouds);
        mTable.put("Light Rain", R.drawable.art_light_rain);
        mTable.put("Rain", R.drawable.art_rain);
        mTable.put("Snow",R.drawable.art_snow);
        mTable.put("Storm",R.drawable.art_storm);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .build();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_diary_save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_save:
                if (save()) {
                    updateWidgets();

                    if (!mTwoPaneMode) {
                        getActivity().finish();
                    } else {
                        ((MainActivity) getActivity()).destroyFragment();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();

        IntentFilter filter = new IntentFilter(AddDiaryFragment.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        getActivity().unregisterReceiver(mBroadcastReceiver);

    }

    public AddDiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null){
            mTwoPaneMode = (Boolean) getArguments().getBoolean("twoPaneMode");
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_diary, container, false);

        mTitleEditText = (EditText) rootView.findViewById(R.id.add_diary_title);
        mDateTextView = (TextView) rootView.findViewById(R.id.add_diary_time_date);
        mContentEditText = (EditText) rootView.findViewById(R.id.add_diary_content);
        mColorTextView = (TextView) rootView.findViewById(R.id.add_diary_mood_color);
        mWeatherImage = (ImageView) rootView.findViewById(R.id.add_diary_weather);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        mCurrentDateandTime = sdf.format(new Date());

        mDateTextView.setText(mCurrentDateandTime);

        mColorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                int [] colors = {Color.BLACK, Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.rgb(0x00,0x77,0xff), Color.rgb(0xff, 0x77, 0x00), Color.rgb(0xff, 0x40, 0x81)};
                data.putCharSequence("title", "Mood Color");
                data.putIntArray("colors", colors);

                SpectrumDialog spectrumDialog = new SpectrumDialog();
                spectrumDialog.setArguments(data);
                spectrumDialog.setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult)
                        {
                            mSelectedColor = color;
                            mColorTextView.setBackgroundColor(color);
                        }
                    }
                });
                spectrumDialog.show(getFragmentManager(), "spectrum");
            }
        });

        return rootView;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //  LocationServices
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                builder.setMessage(R.string.reminde_msg);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        requestPermissions(
                                new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                },
                                MY_PERMISSIONS_REQUEST_FINE_LOCATION
                        );

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION
                );
            }

            return;
        } else {
            getLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {

                }
                return;
            }
        }
    }

    private void getLocation() {

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            Intent msgIntent = new Intent(getContext(), SimpleIntentService.class);
            msgIntent.putExtra(SimpleIntentService.PARAM_IN_LAT_MSG, mLastLocation.getLatitude());
            msgIntent.putExtra(SimpleIntentService.PARAM_IN_LON_MSG, mLastLocation.getLongitude());
            getActivity().startService(msgIntent);
        }
    }

    private boolean save()
    {
        String title = mTitleEditText.getText().toString();
        int content_length = mContentEditText.getText().length();

        if (title.length() > 0) {
            if (content_length <= 130) {
            //if (content_length <= 5000) {
                String content = mContentEditText.getText().toString();

                ContentValues contentValues = new ContentValues();
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_TITLE, title);
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_DATE, mCurrentDateandTime);


                contentValues.put(DiaryContract.DiaryEntry.COLUMN_MOOD, mSelectedColor);
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_CONTENT, content);

                // what if mLastLocation is null?

                if (mLastLocation == null) {
                    contentValues.put(DiaryContract.DiaryEntry.COLUMN_LAT, 0.0d);
                    contentValues.put(DiaryContract.DiaryEntry.COLUMN_LON, 0.0d);
                    contentValues.put(DiaryContract.DiaryEntry.COLUMN_WEATHER, "");
                } else {
                    contentValues.put(DiaryContract.DiaryEntry.COLUMN_LAT, mLastLocation.getLatitude());
                    contentValues.put(DiaryContract.DiaryEntry.COLUMN_LON, mLastLocation.getLongitude());
                    contentValues.put(DiaryContract.DiaryEntry.COLUMN_WEATHER, mWeather);
                }

                // Uri
                Uri diaryUri = DiaryContract.DiaryEntry.CONTENT_URI;
                // insert
                getContext().getContentResolver().insert(diaryUri, contentValues);
                return true;

            } else {
                Toast.makeText(getContext(), R.string.over_max_word_msg, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), R.string.emtry_title_msg, Toast.LENGTH_SHORT).show();

        }
        return false;
    }
    private void updateWidgets() {
        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }
}