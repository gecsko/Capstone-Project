package com.example.kathy.minidiary;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import java.util.HashMap;

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

        rootView.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).openAddDiaryFragment();
            }
        });

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


        // animation
        View weatherIcon = view.findViewById(R.id.listview_item_weather);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this.getActivity(), weatherIcon, ViewCompat.getTransitionName(weatherIcon)).toBundle();

        ((MainActivity)getActivity()).openDetailFragment(selectedDiary, bundle);

    }
}
