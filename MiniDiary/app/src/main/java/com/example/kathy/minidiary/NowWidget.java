package com.example.kathy.minidiary;

/**
 * Created by Kathy on 23/5/2016.
 */
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.example.kathy.minidiary.MainActivity;
import com.example.kathy.minidiary.NowWidgetIntentService;
import com.example.kathy.minidiary.R;

public class NowWidget extends AppWidgetProvider {
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        context.startService(new Intent(context, NowWidgetIntentService.class));
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {

    }
}