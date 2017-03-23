package com.example.android.dailyMedicine.notification;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import com.example.android.dailyMedicine.data.MedicineContract;

import java.util.Calendar;

public class AlarmService extends IntentService {
    public AlarmService() {
        super("AlarmService");

    }

    @Override
    public void onHandleIntent(Intent intent) {

        Cursor cursor = getContentResolver().query(MedicineContract.MedicineEntry.CONTENT_URI, null, null, null, null);
        try {
            // Reset all alarms for all medicines in the database.
            // Getting all data in the database.

            while (cursor != null && cursor.moveToNext()) {
                // Getting the take times of the medicine.
                int takeTimes = cursor.getInt(cursor.getColumnIndex(MedicineContract.MedicineEntry.COLUMN_MEDICINE_TAKE_TIMES));

                // Getting the id of the medicine.
                int _id = cursor.getInt(cursor.getColumnIndex(MedicineContract.MedicineEntry._ID));

                // Define an integer variable to be an index of the array string of MedicineEntry.COLUMN_TIMES_NAMES.
                int k = 0;
                // Setting a new alarm for each take time;
                for (int i = 1; i <= takeTimes; i++) {
                    // Get the hour of the alarm.
                    int hour = cursor.getInt(cursor.getColumnIndex(MedicineContract.MedicineEntry.COLUMN_TIMES_NAMES[k++]));

                    // Get the minute of the alarm.
                    int minute = cursor.getInt(cursor.getColumnIndex(MedicineContract.MedicineEntry.COLUMN_TIMES_NAMES[k++]));
                    // Setting the broadcast to start NotificationService when the alarm time comes.
                    Intent notificationIntent = new Intent(Constants.NOTIFICATION);
                    notificationIntent.putExtra(Constants.MEDICINE_ID, String.valueOf(_id));

                    // Setting the pending intent with NotificationService intend and the request code.
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, _id + i, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                    // Defining an alarm manager.
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                    // Setting the time of the alarm.
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());

                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    if (calendar.getTimeInMillis() < System.currentTimeMillis())
                        calendar.add(Calendar.DAY_OF_YEAR, 1);

                    // Setting the repeating alarm with the specific time created inside the calendar.
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY, pendingIntent);
                }
            }

            // Setting the broadcast to start to reset take time daily at 12:00 AM.
            Intent updateIntent = new Intent(Constants.RESET_DAILY);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            // Defining calender and set it's time to 12:00 AM.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (calendar.getTimeInMillis() < System.currentTimeMillis())
                calendar.add(Calendar.DAY_OF_YEAR, 1);

            // Firing the alarm ever day at 12:00 AM.
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        } catch (Exception ex) {
            Toast.makeText(this, "Alarm Service Error, " + ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) cursor.close();
        }
    }
}
