package com.example.kathy.minidiary.data;

/**
 * Created by Kathy on 22/5/2016.
 */

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class DiaryProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DiaryDbHelper mOpenHelper;

    static final int DIARY = 100;
    static final int DIARY_WITH_ID = 101;

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DiaryContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, DiaryContract.PATH_DIARY, DIARY);
        matcher.addURI(authority, DiaryContract.PATH_DIARY + "/#", DIARY_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DiaryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case DIARY:
                return DiaryContract.DiaryEntry.CONTENT_TYPE;
            case DIARY_WITH_ID:
                return DiaryContract.DiaryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        String keyword = null;

        switch(sUriMatcher.match(uri)) {
            case DIARY:
                // normal query
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DiaryContract.DiaryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            case DIARY_WITH_ID:
                retCursor = getDiaryByDiaryId(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // So we listen for this uri changes
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case DIARY:
                db.beginTransaction();
                try {
                        long _id = db.insert(DiaryContract.DiaryEntry.TABLE_NAME, null, values);
                        if (_id != -1) {
                            returnCount++;
                        }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return uri;
            default:
                return uri;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleteCount = 0;

        switch(sUriMatcher.match(uri)) {
            case DIARY:
                deleteCount = mOpenHelper.getReadableDatabase().delete(
                        DiaryContract.DiaryEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // So we listen for this uri changes
        if (deleteCount != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int returnCount = 0;
        switch (match) {
            case DIARY:
                returnCount = mOpenHelper.getReadableDatabase().update(
                        DiaryContract.DiaryEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (returnCount != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnCount;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {

            case DIARY:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DiaryContract.DiaryEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private Cursor getDiaryByDiaryId(Uri uri, String[] projection, String sortOrder) {
        long diaryId = DiaryContract.DiaryEntry.getDiaryIdFromUri(uri);
        return mOpenHelper.getReadableDatabase().query(
                DiaryContract.DiaryEntry.TABLE_NAME,
                projection,
                DiaryContract.DiaryEntry._ID + "=?",
                new String[] {diaryId + ""},
                null,
                null,
                sortOrder
        );
    }
}