package com.example.android.dailyMedicine.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.android.dailyMedicine.util.Constants;

@Database(entities = {Medicine.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MedicineDao medicineDao();

    private static AppDatabase INSTANCE;
    private static final Object LOCK = new Object();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LOCK) {
                INSTANCE = Room.databaseBuilder(context, AppDatabase.class, Constants.DATABASE_NAME)
                        .build();
            }
        }

        return INSTANCE;
    }
}
