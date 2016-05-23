package com.example.kathy.minidiary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Objects;

public class DetailFragment extends Fragment {

    private HashMap<String, Object> mSelectedDiary;
    Hashtable<String, Integer> table;
    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra("selectedDiary")) {
            mSelectedDiary = (HashMap<String, Object>) intent.getSerializableExtra("selectedDiary");

        } else if (getArguments() != null){
            mSelectedDiary = (HashMap<String, Object>)  getArguments().getSerializable("selectedDiary");
        }

        TextView titleTextView = (TextView) rootView.findViewById(R.id.detail_title);
        TextView dateTimeTextView = (TextView) rootView.findViewById(R.id.detail_date_time);
        TextView contentTextView = (TextView) rootView.findViewById(R.id.detail_content);
        TextView mapTextView = (TextView) rootView.findViewById(R.id.detail_map);
        ImageView weatherView = (ImageView) rootView.findViewById(R.id.detail_weather);

        String text = (String) mSelectedDiary.get("Title");
        titleTextView.setText(text);

        text = (String) mSelectedDiary.get("Date");
        dateTimeTextView.setText(text);

        text = (String) mSelectedDiary.get("Content");
        contentTextView.setText(text);

        final Double lat = (Double) mSelectedDiary.get("Lat");
        final Double lon = (Double) mSelectedDiary.get("Lon");

        int mood = (int) mSelectedDiary.get("Mood");
        Log.d("Color", Integer.toString(mood));
        contentTextView.setTextColor(mood);

        table = new Hashtable<String, Integer>();
        table.put("Clear", R.drawable.art_clear);
        table.put("Clouds", R.drawable.art_clouds);
        table.put("Fog", R.drawable.art_fog);
        table.put("Light Clouds", R.drawable.art_light_clouds);
        table.put("Light Rain", R.drawable.art_light_rain);
        table.put("Rain", R.drawable.art_rain);
        table.put("Snow",R.drawable.art_snow);
        table.put("Storm",R.drawable.art_storm);

        String weather = (String) mSelectedDiary.get("Weather");

        if (table.containsKey(weather)) {
            weatherView.setImageResource(table.get(weather));
        } else {
            // default icon
            weatherView.setImageResource(R.drawable.art_clear);
        }

        //weatherView.setImageResource(R.drawable.art_clear);
        //Toast.makeText(getContext(), (String) mSelectedDiary.get("Weather"), Toast.LENGTH_SHORT).show();

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
                } else
                {
                    Toast.makeText(getContext(), "Please install googl map app to support map function", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }
}
