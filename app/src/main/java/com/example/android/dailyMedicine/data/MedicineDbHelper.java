package com.example.android.dailyMedicine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.dailyMedicine.data.MedicineContract.MedicineEntry;

/**
 * Created by Abdel-Rhman on 3/7/2017.
 */

public class MedicineDbHelper extends SQLiteOpenHelper {

    // Defining name of the database.
    private static final String DATABASE_NAME = "dailymedicine.db";

    // Defining the version of the database.
    private static final int DATABASE_VERSION = 1;

    static Context context;

    public MedicineDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // If the database first created, create table medicine.
        String SQL_CREATE_MEDICINE_TABLE = "CREATE TABLE " + MedicineEntry.TABLE_NAME + " ("
                + MedicineEntry._ID + " INTEGER PRIMARY KEY , "
                + MedicineEntry.COLUMN_MEDICINE_NAME + " TEXT NOT NULL, "
                + MedicineEntry.COLUMN_MEDICINE_TAKE_TIMES + " INTEGER NOT NULL, "
                + MedicineEntry.COLUMN_MEDICINE_TAKEN_TIMES + " INTEGER DEFAULT 0, "
                + MedicineEntry.COLUMN_MEDICINE_TOTAL_TAKEN_TIMES + " INTEGER DEFAULT 0, ";
        for (int i = 0; i < 11; ++i) {
            SQL_CREATE_MEDICINE_TABLE += MedicineEntry.COLUMN_TIMES_NAMES[i] + " TEXT, ";
        }
        SQL_CREATE_MEDICINE_TABLE += MedicineEntry.COLUMN_TIMES_NAMES[11] + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_MEDICINE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
