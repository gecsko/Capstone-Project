package com.example.kathy.minidiary;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.thebluealliance.spectrum.SpectrumDialog;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddDiaryFragment extends Fragment {

    public AddDiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_diary, container, false);

        EditText titleEditText = (EditText) rootView.findViewById(R.id.add_diary_title);
        TextView dateTextView = (TextView) rootView.findViewById(R.id.add_diary_time_date);
        TextView locationTextView = (TextView) rootView.findViewById(R.id.add_diary_location);
        EditText contentEditText = (EditText) rootView.findViewById(R.id.add_diary_content);
        TextView colorTextView = (TextView) rootView.findViewById(R.id.add_diary_mood_color);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());

        dateTextView.setText(currentDateandTime);

        locationTextView.setText("Causeway Bay");

        colorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpectrumDialog spectrumDialog = new SpectrumDialog();
                spectrumDialog.show(getFragmentManager(), "abc");
            }
        });

        return rootView;
    }
}
