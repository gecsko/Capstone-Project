package com.example.kathy.minidiary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import com.example.kathy.minidiary.data.DiaryContract;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener{

    private static final int DIARY_LOADER = 0;

    private DiaryAdapter mDiaryAdapter;

    //private SimpleAdapter simpleAdapter;

    private static final String[] DIARY_COLUMNS = {
            DiaryContract.DiaryEntry._ID,
            DiaryContract.DiaryEntry.COLUMN_TITLE,
            DiaryContract.DiaryEntry.COLUMN_DATE,
            DiaryContract.DiaryEntry.COLUMN_LAT,
            DiaryContract.DiaryEntry.COLUMN_LON,
            DiaryContract.DiaryEntry.COLUMN_WEATHER,
            DiaryContract.DiaryEntry.COLUMN_MOOD,
            DiaryContract.DiaryEntry.COLUMN_CONTENT
    };

    // These indices are tied to USER_COLUMNS.  If USER_COLUMNS changes, these  must change.
    static final int COL_ID = 0;
    static final int COL_TITLE = 1;
    static final int COL_DATE = 2;
    static final int COL_LAT = 3;
    static final int COL_LON = 4;
    static final int COL_WEATHER= 5;
    static final int COL_MOOD = 6;
    static final int COL_CONTENT = 7;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mDiaryAdapter = new DiaryAdapter(getActivity(), null, 0);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, null);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_diary);
        listView.setAdapter(mDiaryAdapter);
        listView.setOnItemClickListener(this);

        /*ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();

            HashMap<String, String> e1 = new HashMap<String, String>();
            e1.put("Title", "ABC");
            e1.put("Date", "13:15 02052016");
            e1.put("Content", "Happy");

            items.add(e1);

            HashMap<String, String> e2 = new HashMap<String, String>();
            e2.put("Title", "Shopping");
            e2.put("Date", "14:00 03052016");
            e2.put("Content", "Sad");

            items.add(e2);
            // Get a reference to the ListView, and attach this adapter to it.

             simpleAdapter = new SimpleAdapter(this.getActivity(), items, R.layout.fragment_listview_item, new String[]{"Title", "Date", "Content"},
                    new int[]{R.id.listview_title, R.id.listview_item_data_time, R.id.detail_location});
             listView.setAdapter(simpleAdapter);*/

        rootView.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).openAddDiaryFragment();
            }
        });
        //listView.setAdapter(mUserAdapter);
        //listView.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DIARY_LOADER, getArguments(), this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Sort order:  Ascending, by date.
        String sortOrder = DiaryContract.DiaryEntry._ID + " DESC";
        Uri diaryUri = DiaryContract.DiaryEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                diaryUri,
                DIARY_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDiaryAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDiaryAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor)mDiaryAdapter.getItem(position);

        String title = cursor.getString(COL_TITLE);
        String date = cursor.getString(COL_DATE);
        Double lat = cursor.getDouble(COL_LAT);
        Double lon = cursor.getDouble(COL_LON);
        String weather = cursor.getString(COL_WEATHER);
        int mood = cursor.getInt(COL_MOOD);
        String content = cursor.getString(COL_CONTENT);

        HashMap<String, Object> selectedDiary = new HashMap<String, Object>();
        selectedDiary.put("Title", title);
        selectedDiary.put("Date", date);
        selectedDiary.put("Lat", lat);
        selectedDiary.put("Lon", lon);
        selectedDiary.put("Weather", weather);
        selectedDiary.put("Mood", mood);
        selectedDiary.put("Content", content);

        ((MainActivity)getActivity()).openDetailFragment(selectedDiary);
        /*detailActivity.putExtra("title", title);
        detailActivity.putExtra("date", date);
        detailActivity.putExtra("location", location);
        detailActivity.putExtra("lat", lat);
        detailActivity.putExtra("lon", lon);
        detailActivity.putExtra("weather", weather);
        detailActivity.putExtra("mood", mood);
        detailActivity.putExtra("content", content);*/

        //startActivity(userDetailActivity);
        //getActivity().startActivity(userDetailActivity, bundle);
    }
}
