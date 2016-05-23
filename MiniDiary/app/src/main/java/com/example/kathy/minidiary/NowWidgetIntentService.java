package com.example.kathy.minidiary;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.example.kathy.minidiary.data.DiaryContract;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


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
        Log.d("NowWidgetIntentService", "onHandleIntent()");

        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, NowWidgetProvider.class));

        Log.d("NowWidgetIntentService", "appWidgetIds: " + appWidgetIds.length);

        //Uri matchForCurrentUri = DatabaseContract.scores_table.buildScoreWithDate();
        //Cursor data = getContentResolver().query(matchForCurrentUri, SCORES_COLUMNS, null, null, DatabaseContract.scores_table.DATE_COL + " ASC");
        String sortOrder = DiaryContract.DiaryEntry._ID + " DESC";
        Uri matchUri =  DiaryContract.DiaryEntry.CONTENT_URI;
        Cursor data = getContentResolver().query(matchUri, null, null, null, sortOrder);

        if (data == null) {
            return;
        }

        Log.d("NowWidgetIntentService", "onHandleIntent()2");

        if (!data.moveToFirst()) {
            data.close();
            return;
        }
        Log.d("NowWidgetIntentService", "onHandleIntent()3");

        String title = data.getString(COL_TITLE);
        String date = data.getString(COL_DATE);
        String weather = data.getString(COL_WEATHER);
        int mood = data.getInt(COL_MOOD);

        data.close();

        // Perform this loop procedure for each Today widget
        //  for (int appWidgetId : appWidgetIds) {
        //      Log.d("NowWidgetIntentService", "onHandleIntent()3.5");
        // Find the correct layout based on the widget's width
        //     int layoutId = R.layout.widget_now_small;
        //     RemoteViews views = new RemoteViews(getPackageName(), layoutId);

        //   views.setTextViewText(R.id.widget_high_temperature, home);
        //    Log.d("NowWidgetIntentService", "44444");

        /// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
        //    setRemoteContentDescription(views, description);
        //}

        // Create an Intent to launch MainActivity
        //      Intent launchIntent = new Intent(this, MainActivity.class);
        //     PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
        //     views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        // Tell the AppWidgetManager to perform an update on the current app widget
        //       appWidgetManager.updateAppWidget(appWidgetId, views);
        // }
    }
}