package com.example.android.dailyMedicine.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Abdel-Rhman on 3/7/2017.
 */

public final class MedicineContract {
    MedicineContract() {
    }

    // Defining the content authority.
    public static final String CONTENT_AUTHORITY = "com.example.android.dailyMedicine";

    // Defining the base content uri.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Defining the path of the table.
    public static final String PATH_MEDICINE = "medicine";

    public static final class MedicineEntry implements BaseColumns {

        // Defining the content uri of the table medicine.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MEDICINE);

        // Defining the content list type uri of the table.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEDICINE;

        // Defining the content item type uri of the table.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEDICINE;

        // Defining the table name.
        public final static String TABLE_NAME = "medicine";

        // Defining name of column id.
        public final static String _ID = BaseColumns._ID;

        // Defining name of column name.
        public final static String COLUMN_MEDICINE_NAME = "name";

        // Defining name of column take times.
        public final static String COLUMN_MEDICINE_TAKE_TIMES = "take_times";

        // Defining name of column taken times.
        public final static String COLUMN_MEDICINE_TAKEN_TIMES = "taken_times";

        // Defining name of column total taken times.
        public final static String COLUMN_MEDICINE_TOTAL_TAKEN_TIMES = "total_taken_times";

        // Defining names of each nine column hour and minute
        public final static String[] COLUMN_TIMES_NAMES = {
                "hour_1", "min_1",
                "hour_2", "min_2",
                "hour_3", "min_3",
                "hour_4", "min_4",
                "hour_5", "min_5",
                "hour_6", "min_6"
        };
    }
}
