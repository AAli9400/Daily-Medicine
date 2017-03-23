package com.example.android.dailyMedicine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.android.dailyMedicine.data.MedicineContract.MedicineEntry;

/**
 * Created by Abdel-Rhman on 3/7/2017.
 */

public class MedicineProvider extends ContentProvider {
    public static final String LOG_TAG = MedicineProvider.class.getSimpleName();

    // Defining constant that will be returned by uri matcher to identify uri for a whole table.
    private static final int MEDICINE = 100;

    // Defining constant that will be returned by uri matcher to identify uri for a specific row in table.
    private static final int MEDICINE_ID = 101;

    // Setting up the uri matcher.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MedicineContract.CONTENT_AUTHORITY, MedicineContract.PATH_MEDICINE, MEDICINE);
        sUriMatcher.addURI(MedicineContract.CONTENT_AUTHORITY, MedicineContract.PATH_MEDICINE + "/#", MEDICINE_ID);
    }

    // Defining a MedicineDpHelper object to get a readable or writable database with it.
    private MedicineDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        // Initializing mDbHelper.
        mDbHelper = new MedicineDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Getting readable database.
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        // Check if this method where requested for a row or the whole table.
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MEDICINE:
                cursor = database.query(MedicineEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case MEDICINE_ID:
                selection = MedicineEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MedicineEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Register to watch the uri for changes.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        // Check if this method where requested for the whole table.
        // Insertion can't be done on one row.
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICINE:
                // Getting writable database.
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                long id = database.insert(MedicineEntry.TABLE_NAME, null, contentValues);
                if (id == -1) {
                    // If nothing inserted, return null.
                    return null;
                }

                // Notify of uri changes.
                getContext().getContentResolver().notifyChange(uri, null);

                // Return the inserted row uri.
                return ContentUris.withAppendedId(uri, id);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        // Getting writable database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Check if this method where requested for a row or the whole table.
        int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;
        switch (match) {
            case MEDICINE:
                rowsUpdated = database.update(MedicineEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;

            case MEDICINE_ID:
                selection = MedicineEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsUpdated = database.update(MedicineEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
        if (rowsUpdated != 0) {
            // Notify of uri changes.
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Getting writable database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Check if this method where requested for a row or the whole table.
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MEDICINE:
                rowsDeleted = database.delete(MedicineEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MEDICINE_ID:
                selection = MedicineEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(MedicineEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            // Notify of uri changes.
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {

        // Getting the uri type;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICINE:
                return MedicineEntry.CONTENT_LIST_TYPE;
            case MEDICINE_ID:
                return MedicineEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
