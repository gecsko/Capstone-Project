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
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;

public class DetailFragment extends Fragment {

    private HashMap<String, String> mSelectedDiary;
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
            Log.d("hi", "r1");
            mSelectedDiary = (HashMap<String, String>) intent.getSerializableExtra("selectedDiary");

        } else if (getArguments() != null){
            Log.d("hi", "r2");
            //mTopTen = getArguments().getString("selectedArtistId");
        }

        TextView titleTextView = (TextView) rootView.findViewById(R.id.detail_title);
        TextView dateTimeTextView = (TextView) rootView.findViewById(R.id.detail_date_time);
        TextView contentTextView = (TextView) rootView.findViewById(R.id.detail_content);
        TextView mapTextView = (TextView) rootView.findViewById(R.id.detail_map);

        String text = mSelectedDiary.get("Title");
        titleTextView.setText(text);

        text = mSelectedDiary.get("Date");
        dateTimeTextView.setText(text);

        text = mSelectedDiary.get("Content");
        contentTextView.setText(text);

        mapTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
            }
        });

        return rootView;
    }
}
