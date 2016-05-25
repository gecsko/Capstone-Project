package com.example.kathy.minidiary;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Hashtable;

public class DetailFragment extends Fragment {

    private HashMap<String, Object> mSelectedDiary;
    Hashtable<String, Integer> mTable;
    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra("selectedDiary")) {
            mSelectedDiary = (HashMap<String, Object>) intent.getSerializableExtra("selectedDiary");

        } else if (getArguments() != null){
            mSelectedDiary = (HashMap<String, Object>)  getArguments().getSerializable("selectedDiary");
        }

        //TextView titleTextView = (TextView) rootView.findViewById(R.id.detail_title);
        TextView dateTimeTextView = (TextView) rootView.findViewById(R.id.detail_date_time);
        TextView contentTextView = (TextView) rootView.findViewById(R.id.detail_content);
        TextView mapTextView = (TextView) rootView.findViewById(R.id.detail_map);
        ImageView weatherView = (ImageView) rootView.findViewById(R.id.detail_weather);

        String text = "";
        //String text = (String) mSelectedDiary.get("Title");
        //titleTextView.setText(text);

        text = (String) mSelectedDiary.get("Title");

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(text);
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

        ((Toolbar)rootView.findViewById(R.id.actionbar)).setTitle(text);


        text = (String) mSelectedDiary.get("Date");
        dateTimeTextView.setText(text);

        text = (String) mSelectedDiary.get("Content");
        contentTextView.setText(text);

        final Double lat = (Double) mSelectedDiary.get("Lat");
        final Double lon = (Double) mSelectedDiary.get("Lon");

        int mood = (int) mSelectedDiary.get("Mood");
        //contentTextView.setTextColor(mood);

        // set the toolbar background
        ((AppBarLayout)rootView.findViewById(R.id.app_bar)).setBackgroundColor(mood);
        int inv = Color.WHITE - mood + Color.BLACK;
        collapsingToolbarLayout.setBackgroundColor(mood);
        ((Toolbar)rootView.findViewById(R.id.actionbar)).setBackgroundColor(mood);
        collapsingToolbarLayout.setCollapsedTitleTextColor(inv);

        mTable = new Hashtable<String, Integer>();
        mTable.put("Clear", R.drawable.art_clear);
        mTable.put("Clouds", R.drawable.art_clouds);
        mTable.put("Fog", R.drawable.art_fog);
        mTable.put("Light Clouds", R.drawable.art_light_clouds);
        mTable.put("Light Rain", R.drawable.art_light_rain);
        mTable.put("Rain", R.drawable.art_rain);
        mTable.put("Snow",R.drawable.art_snow);
        mTable.put("Storm",R.drawable.art_storm);

        String weather = (String) mSelectedDiary.get("Weather");

        // default value
        weatherView.setImageResource(R.drawable.art_unknown);

        if (!(weather.equals(getActivity().getString(R.string.no_network)))) {
            if (mTable.containsKey(weather)) {
                weatherView.setImageResource(mTable.get(weather));
            }
        }


        if ((lat == 0.0d) && (lon == 0.0d)) {
            // no map detail available
            // dim the text
            mapTextView.setBackgroundColor(Color.GRAY);
            mapTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), R.string.no_location_info_available, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mapTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Create a Uri from an intent string. Use the result to create an Intent.
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + lat.toString() + "," + lon.toString());
                    //Uri gmmIntentUri = Uri.parse("geo:0,0?q=37.7749,-122.4194");
                    // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                    // Make the Intent explicit by setting the Google Maps package
                    mapIntent.setPackage("com.google.android.apps.maps");

                    // Attempt to start an activity that can handle the Intent
                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {
                        Toast.makeText(getContext(), R.string.google_map_not_found, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return rootView;
    }
}
