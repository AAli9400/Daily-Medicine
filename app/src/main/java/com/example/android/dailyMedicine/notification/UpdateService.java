package com.example.android.dailyMedicine.notification;

import android.app.IntentService;
import android.app.LoaderManager;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.example.android.dailyMedicine.data.MedicineContract;


public class UpdateService extends IntentService {
    public UpdateService() {
        super("UpdateService");
    }

    @Override
    public void onHandleIntent(Intent intent) {

        if (intent.getAction().equals(Constants.ACTION_TOOK_IT)) {
            // If action requested == ACTION_TOOK_IT.
            // increase the medicine taken time by one.
            // increase the total taken times by one.

            Cursor cursor = null;
            try {
                // Get medicine id.
                String medicineId = intent.getStringExtra(Constants.MEDICINE_ID);

                String[] projection = {
                        MedicineContract.MedicineEntry._ID,
                        MedicineContract.MedicineEntry.COLUMN_MEDICINE_TAKEN_TIMES,
                        MedicineContract.MedicineEntry.COLUMN_MEDICINE_TOTAL_TAKEN_TIMES};

                String selection = MedicineContract.MedicineEntry._ID + " = " + medicineId;

                // Getting the medicine data.
                cursor = getContentResolver().query(MedicineContract.MedicineEntry.CONTENT_URI, projection, selection, null, null);
                cursor.moveToFirst();

                // increase the taken time an total taken time by one.
                ContentValues values = new ContentValues();
                values.put(MedicineContract.MedicineEntry.COLUMN_MEDICINE_TAKEN_TIMES
                        , (cursor.getInt(cursor.getColumnIndex(MedicineContract.MedicineEntry.COLUMN_MEDICINE_TAKEN_TIMES)) + 1));
                values.put(MedicineContract.MedicineEntry.COLUMN_MEDICINE_TOTAL_TAKEN_TIMES
                        , (cursor.getInt(cursor.getColumnIndex(MedicineContract.MedicineEntry.COLUMN_MEDICINE_TOTAL_TAKEN_TIMES)) + 1));

                // update the medicine.
                getContentResolver().update(MedicineContract.MedicineEntry.CONTENT_URI, values, selection, null);

                // Canceling the notification.
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(Integer.valueOf(medicineId));
            } catch (Exception ex) {
                Toast.makeText(this, "Update Service Error, " + ex.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                if (cursor != null) cursor.close();
            }

        } else if (intent.getAction().equals(Constants.ACTION_MISSED_IT)) {
            // If action requested == ACTION_MISSED_IT.
            // Just cancel the notification.

            // Get medicine id.
            String medicineId = intent.getStringExtra(Constants.MEDICINE_ID);

            // Canceling the notification.
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(Integer.valueOf(medicineId));

        } else if (intent.getAction().equals(Constants.RESET_DAILY)) {
            // If action requested == com.example.android.dailyMedicine.RESET_DAILY.
            try {
                // update every taken time for all medicines to 0
                // to start counting for the new day.
                ContentValues values = new ContentValues();
                values.put(MedicineContract.MedicineEntry.COLUMN_MEDICINE_TAKEN_TIMES, 0);
                getContentResolver().update(MedicineContract.MedicineEntry.CONTENT_URI, values, null, null);

                // Also clear any remained notifications.
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();

            } catch (Exception ex) {
                Toast.makeText(this, "Update Service Error, " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
