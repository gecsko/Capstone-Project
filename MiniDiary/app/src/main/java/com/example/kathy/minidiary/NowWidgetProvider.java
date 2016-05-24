package com.example.kathy.minidiary;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.kathy.minidiary.data.DiaryContract;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

public class NowWidgetProvider extends AppWidgetProvider {
    private static final String[] DIARY_COLUMNS = {
            DiaryContract.DiaryEntry.COLUMN_TITLE,
            DiaryContract.DiaryEntry.COLUMN_DATE,
            DiaryContract.DiaryEntry.COLUMN_WEATHER,
    };

    public static final int COL_TITLE = 1;
    public static final int COL_DATE = 2;
    public static final int COL_WEATHER = 5;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        String sortOrder = DiaryContract.DiaryEntry._ID + " DESC";
        Uri matchUri =  DiaryContract.DiaryEntry.CONTENT_URI;
        Cursor data = context.getContentResolver().query(matchUri, null, null, null, sortOrder);

        if (data == null) {
            return;
        }

        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        String title = data.getString(COL_TITLE);
        String date = data.getString(COL_DATE);
        String weather = data.getString(COL_WEATHER);

        data.close();

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            // Find the correct layout based on the widget's width
            int layoutId = R.layout.widget_now_small;
            RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);

            views.setTextViewText(R.id.widget_title, title);
            views.setTextViewText(R.id.widget_date, date);

            Hashtable<String, Integer> table;
            table = new Hashtable<String, Integer>();
            table.put("Clear", R.drawable.art_clear);
            table.put("Clouds", R.drawable.art_clouds);
            table.put("Fog", R.drawable.art_fog);
            table.put("Light Clouds", R.drawable.art_light_clouds);
            table.put("Light Rain", R.drawable.art_light_rain);
            table.put("Rain", R.drawable.art_rain);
            table.put("Snow",R.drawable.art_snow);
            table.put("Storm",R.drawable.art_storm);

            if (!(weather.equals(context.getString(R.string.no_network)))) {
                if (table.containsKey(weather)) {
                    views.setImageViewResource(R.id.widget_weather, table.get(weather));
                } else if (weather != null) {
                    // default icon
                    views.setImageViewResource(R.id.widget_weather, R.drawable.art_clear);
                }
            }

            /// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            //    setRemoteContentDescription(views, description);
            //}

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        Log.d("NowWidgetProvider", intent.getAction());
        //if (FootballSyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction())) {
        // update the data
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, NowWidgetProvider.class));

        onUpdate(context, appWidgetManager, appWidgetIds);
        //}
    }
}