package com.example.kathy.minidiary.data;

/**
 * Created by Kathy on 22/5/2016.
 */
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DiaryContract {

    public static final String CONTENT_AUTHORITY = "com.example.kathy.minidiary";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DIARY = "diary";

    public static final class DiaryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DIARY).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIARY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIARY;

        public static final String TABLE_NAME = "Diary";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LON = "lon";
        public static final String COLUMN_WEATHER = "weather";
        public static final String COLUMN_MOOD = "mood";
        public static final String COLUMN_CONTENT = "content";

        // some utility to generate the uri
        public static Uri buildDiaryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildDiarySearchUri(String keyword) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_DIARY).appendQueryParameter("keyword", keyword).build();
        }

        // some utilites to parse the uri
        public static long getDiaryIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }
    }
}
