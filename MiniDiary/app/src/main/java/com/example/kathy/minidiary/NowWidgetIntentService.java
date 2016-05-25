package com.example.kathy.minidiary;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.kathy.minidiary.data.DiaryContract;

public class NowWidgetIntentService extends IntentService {
    private static final String[] DIARY_COLUMNS = {
            DiaryContract.DiaryEntry.COLUMN_TITLE,
            DiaryContract.DiaryEntry.COLUMN_DATE,
            DiaryContract.DiaryEntry.COLUMN_WEATHER,
            DiaryContract.DiaryEntry.COLUMN_MOOD
    };

    public static final int COL_TITLE = 0;
    public static final int COL_DATE = 1;
    public static final int COL_WEATHER = 2;
    public static final int COL_MOOD = 3;

    public NowWidgetIntentService() {
        super("NowWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, NowWidgetProvider.class));

        //Uri matchForCurrentUri = DatabaseContract.scores_table.buildScoreWithDate();
        //Cursor data = getContentResolver().query(matchForCurrentUri, SCORES_COLUMNS, null, null, DatabaseContract.scores_table.DATE_COL + " ASC");
        String sortOrder = DiaryContract.DiaryEntry._ID + " DESC";
        Uri matchUri =  DiaryContract.DiaryEntry.CONTENT_URI;
        Cursor data = getContentResolver().query(matchUri, null, null, null, sortOrder);

        if (data == null) {
            return;
        }

        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        data.close();
    }
}