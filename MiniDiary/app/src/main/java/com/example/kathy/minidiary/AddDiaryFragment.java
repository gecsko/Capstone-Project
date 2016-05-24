package com.example.kathy.minidiary;

import android.Manifest;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.thebluealliance.spectrum.SpectrumPreference;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

public class AddDiaryFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String ACTION_RESP = "com.mamlambo.intent.action.MESSAGE_PROCESSED";
    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;

    public static final String ACTION_DATA_UPDATED =
            "com.example.kathy.minidiary.ACTION_DATA_UPDATED";

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    MenuItem savemenu;

    EditText titleEditText;
    TextView dateTextView;
    EditText contentEditText;
    TextView mColorTextView;
    String currentDateandTime;
    String mWeather = null;
    ImageView mWeatherImage;
    int mSelectedColor = 0xff000000; // default black
    Boolean mTwoPaneMode = false;
    Hashtable<String, Integer> table;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mWeather = intent.getStringExtra(SimpleIntentService.PARAM_OUT_MSG);

            if (mWeather.equals(getString(R.string.no_network))){
                Toast.makeText(getContext(), "No network connected. Cannot provide current weather!!!", Toast.LENGTH_SHORT).show();
            } else {
                if (table.containsKey(mWeather)) {
                    mWeatherImage.setImageResource(table.get(mWeather));
                } else {
                    // default icon
                    mWeatherImage.setImageResource(R.drawable.art_clear);
                }
            }
            //Toast.makeText(getContext(), mWeather, Toast.LENGTH_SHORT).show();
            //TextView result = (TextView) getView().findViewById(R.id.add_diary_location);
            //result.setText(text);
            //result.setText(text);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        table = new Hashtable<String, Integer>();
        table.put("Clear", R.drawable.art_clear);
        table.put("Clouds", R.drawable.art_clouds);
        table.put("Fog", R.drawable.art_fog);
        table.put("Light Clouds", R.drawable.art_light_clouds);
        table.put("Light Rain", R.drawable.art_light_rain);
        table.put("Rain", R.drawable.art_rain);
        table.put("Snow",R.drawable.art_snow);
        table.put("Storm",R.drawable.art_storm);

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

        //savemenu = menu.add(0, 0, 0,"Save");
        //save icon
        //fav.setIcon(R.drawable.);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_save:
                if (save() == true) {
                    updateWidgets();

                    if (mTwoPaneMode == false) {
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

        titleEditText = (EditText) rootView.findViewById(R.id.add_diary_title);
        dateTextView = (TextView) rootView.findViewById(R.id.add_diary_time_date);
        contentEditText = (EditText) rootView.findViewById(R.id.add_diary_content);
        mColorTextView = (TextView) rootView.findViewById(R.id.add_diary_mood_color);
        mWeatherImage = (ImageView) rootView.findViewById(R.id.add_diary_weather);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        currentDateandTime = sdf.format(new Date());

        dateTextView.setText(currentDateandTime);

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
                spectrumDialog.show(getFragmentManager(), "abc");
            }
        });

        return rootView;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //  LocationServices
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
            Log.d("TAG", "No right");

            requestPermissions(
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION
            );

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
        Log.d("Tag", "onRequestPermissionsResult(");

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("Tag", "Granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLocation();
                } else {
                    Log.d("Tag", "Denided");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void getLocation() {
        Log.d("TAG", "getLocation()");

        // adb shell pm revoke com.example.kathy.minidiary android.permission.ACCESS_FINE_LOCATION
        // adb shell pm revoke com.example.kathy.minidiary android.permission.ACCESS_COARSE_LOCATION
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // no right
            Log.d("TAG", "Not right");
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            Intent msgIntent = new Intent(getContext(), SimpleIntentService.class);
            msgIntent.putExtra(SimpleIntentService.PARAM_IN_LAT_MSG, mLastLocation.getLatitude());
            msgIntent.putExtra(SimpleIntentService.PARAM_IN_LON_MSG, mLastLocation.getLongitude());
            getActivity().startService(msgIntent);

            Log.d("TAG", mLastLocation.toString());
        } else {
            Log.d("TAG", "Location null");
        }
    }

    private boolean save()
    {
        String title = titleEditText.getText().toString();
        int content_length = contentEditText.getText().length();

        if (title.length() > 0) {
            if (content_length <= 130) {
                String content = contentEditText.getText().toString();

                ContentValues contentValues = new ContentValues();
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_TITLE, title);
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_DATE, currentDateandTime);
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_LAT, mLastLocation.getLatitude());
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_LON, mLastLocation.getLongitude());
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_WEATHER, mWeather);
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_MOOD, mSelectedColor);
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_CONTENT, content);

                // Uri
                Uri diaryUri = DiaryContract.DiaryEntry.CONTENT_URI;
                Log.d("Tag", diaryUri.toString());
                // insert
                getContext().getContentResolver().insert(diaryUri, contentValues);
                return true;

            } else {
                Toast.makeText(getContext(), "Content length over max (130 words)", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Title cannot be empty.", Toast.LENGTH_SHORT).show();

        }
        //Toast.makeText(getContext(), "YEAH", Toast.LENGTH_SHORT).show();
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