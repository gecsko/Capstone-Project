package com.example.kathy.minidiary;

import android.Manifest;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathy.minidiary.data.DiaryContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.thebluealliance.spectrum.SpectrumDialog;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddDiaryFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String ACTION_RESP = "com.mamlambo.intent.action.MESSAGE_PROCESSED";
    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    MenuItem savemenu;

    EditText titleEditText;
    TextView dateTextView;
    EditText contentEditText;
    TextView colorTextView;
    TextView locationTextView;
    String currentDateandTime;
    String location;
    String mWeather;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mWeather= intent.getStringExtra(SimpleIntentService.PARAM_OUT_MSG);

            Toast.makeText(getContext(), mWeather, Toast.LENGTH_SHORT).show();
            //TextView result = (TextView) getView().findViewById(R.id.add_diary_location);
            //result.setText(text);
            //result.setText(text);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                    .enableAutoManage(this.getActivity(), this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .build();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        savemenu = menu.add(0, 0, 0,"Save");
        //save icon
        //fav.setIcon(R.drawable.);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case 0:
                Save();
                getActivity().finish();
                return true;
            default:
                super.onOptionsItemSelected(item);
            return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter(AddDiaryFragment.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onStop();
    }

    public AddDiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_diary, container, false);

        titleEditText = (EditText) rootView.findViewById(R.id.add_diary_title);
        dateTextView = (TextView) rootView.findViewById(R.id.add_diary_time_date);
        contentEditText = (EditText) rootView.findViewById(R.id.add_diary_content);
        colorTextView = (TextView) rootView.findViewById(R.id.add_diary_mood_color);
        locationTextView = (TextView) rootView.findViewById(R.id.add_diary_location);

        locationTextView.setText("Unknown");

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        currentDateandTime = sdf.format(new Date());

        dateTextView.setText(currentDateandTime);

        colorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpectrumDialog spectrumDialog = new SpectrumDialog();
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

            TextView locationTextView = (TextView) getActivity().findViewById(R.id.add_diary_location);
            locationTextView.setText(mLastLocation.toString());

            Intent msgIntent = new Intent(getContext(), SimpleIntentService.class);
            msgIntent.putExtra(SimpleIntentService.PARAM_IN_LAT_MSG, mLastLocation.getLatitude());
            msgIntent.putExtra(SimpleIntentService.PARAM_IN_LON_MSG, mLastLocation.getLongitude());
            getActivity().startService(msgIntent);

            Log.d("TAG", mLastLocation.toString());
        } else {
            Log.d("TAG", "Location null");
        }
    }

    private void Save()
    {
        String title = titleEditText.getText().toString();
        int content_length = contentEditText.getText().length();

        if (title.length() > 0) {
            if (content_length <= 130) {
                String content = contentEditText.getText().toString();

                ContentValues contentValues = new ContentValues();
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_TITLE, title);
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_DATE, currentDateandTime);
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_LOCATION, location);
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_LAT, mLastLocation.getLatitude());
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_LON, mLastLocation.getLongitude());
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_WEATHER, mWeather);
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_CONTENT, content);

                // Uri
                Uri diaryUri = DiaryContract.DiaryEntry.CONTENT_URI;
                Log.d("Tag", diaryUri.toString());
                // insert
                getContext().getContentResolver().insert(diaryUri, contentValues);

            } else {
                Toast.makeText(getContext(), "Content length over max (130 words)", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Title cannot be empty.", Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(getContext(), "YEAH", Toast.LENGTH_SHORT).show();
    }
}