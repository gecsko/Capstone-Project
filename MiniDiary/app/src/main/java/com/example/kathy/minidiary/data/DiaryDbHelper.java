package com.example.kathy.minidiary.data;

/**
 * Created by Kathy on 22/5/2016.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DiaryDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "diary.db";

    public DiaryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_DIARY_TABLE = "CREATE TABLE " + DiaryContract.DiaryEntry.TABLE_NAME + " (" +

                DiaryContract.DiaryEntry._ID + " INTEGER PRIMARY KEY," +

                // the ID of the location entry associated with this diary data
                DiaryContract.DiaryEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                DiaryContract.DiaryEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                DiaryContract.DiaryEntry.COLUMN_LAT + " REAL, " +
                DiaryContract.DiaryEntry.COLUMN_LON + " REAL, " +
                DiaryContract.DiaryEntry.COLUMN_WEATHER + " TEXT," +
                DiaryContract.DiaryEntry.COLUMN_MOOD + " INTEGER," +
                DiaryContract.DiaryEntry.COLUMN_CONTENT + " TEXT);";

        db.execSQL(SQL_CREATE_DIARY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DiaryContract.DiaryEntry.TABLE_NAME);
        onCreate(db);
    }
}