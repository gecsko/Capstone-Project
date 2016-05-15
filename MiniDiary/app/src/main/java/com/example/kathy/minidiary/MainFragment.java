package com.example.kathy.minidiary;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Hashtable;

public class MainFragment extends Fragment {

    private ListView listView;
    private SimpleAdapter simpleAdapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ArrayList<Hashtable<String, String>> items = new ArrayList<Hashtable<String, String>>();

        Hashtable<String, String> e1 = new Hashtable<String, String>();
        e1.put("Title", "ABC");
        e1.put("Date", "13:15 02052016");
        e1.put("Content", "Happy");

        items.add(e1);

        Hashtable<String, String> e2 = new Hashtable<String, String>();
        e2.put("Title", "Shopping");
        e2.put("Date", "14:00 03052016");
        e2.put("Content", "Sad");

        items.add(e2);

        /// / Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, null);

        listView = (ListView) rootView.findViewById(R.id.listview_diary);

        simpleAdapter = new SimpleAdapter(this.getActivity(), items, R.layout.fragment_listview_item, new String[]{"Title", "Date", "Content"},
                new int[]{R.id.listview_title, R.id.listview_item_data_time, R.id.detail_location});
        listView.setAdapter(simpleAdapter);
        //listView.setAdapter(mUserAdapter);
        //listView.setOnItemClickListener(this);
        return rootView;
    }
}
