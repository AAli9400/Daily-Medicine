package com.example.android.dailyMedicine.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.android.dailyMedicine.MainActivity;
import com.example.android.dailyMedicine.R;
import com.example.android.dailyMedicine.db.AppDatabase;
import com.example.android.dailyMedicine.db.Medicine;
import com.example.android.dailyMedicine.Util.Constants;

import java.util.Calendar;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

public class NotificationService extends IntentService {
    private AppDatabase mDb;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        mDb = AppDatabase.getInstance(this);

        String action = intent.getAction();
        if (action != null && intent.hasExtra(Constants.MEDICINE_ID_EXTRA)) {

            int medicineId = intent.getIntExtra(Constants.MEDICINE_ID_EXTRA, -1);

            switch (action) {
                case Constants.ACTION_NOTIFICATION:
                    performActionNotification(medicineId);
                    break;
                case Constants.ACTION_TOOK_IT:
                    performActionTookIt(medicineId);
                    break;
                case Constants.ACTION_MISSED_IT:
                    performActionMissedIt(medicineId);
                    break;
                default:
                    break;
            }
        }
    }

    private void performActionNotification(int medicineId) {
        Medicine medicine = mDb.medicineDao().getMedicineById(medicineId);
        Notification notification = buildNotification(medicineId, medicine.getMedicineName());

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(medicineId, notification);

        scheduleNextAlarm(medicine);
    }

    private void performActionMissedIt(int medicineId) {
        cancelNotification(medicineId);
    }

    private void performActionTookIt(int medicineId) {
        //update the medicine
        Medicine medicine = mDb.medicineDao().getMedicineById(medicineId);
        medicine.setMedicineTotalNumberOfTakenTimesPerDay(
                medicine.getMedicineTotalNumberOfTakenTimesPerDay() + 1
        );
        mDb.medicineDao().updateMedicine(medicine);

        cancelNotification(medicineId);
    }

    private Notification buildNotification(int medicineId, String medicineName) {
        createNotificationChannel();

        Intent openAppIntent = new Intent(this, MainActivity.class);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(
                this, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent tookItIntent = new Intent(this, NotificationService.class);
        tookItIntent.setAction(Constants.ACTION_TOOK_IT);
        tookItIntent.putExtra(Constants.MEDICINE_ID_EXTRA, medicineId);
        PendingIntent tookItPendingIntent = PendingIntent.getService(
                this, 1, tookItIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent missedItIntent = new Intent(this, NotificationService.class);
        missedItIntent.setAction(Constants.ACTION_MISSED_IT);
        missedItIntent.putExtra(Constants.MEDICINE_ID_EXTRA, medicineId);
        PendingIntent missedItPendingIntent = PendingIntent.getService(
                this, 2, missedItIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.its_time_to_take_your_medicine1) + medicineName)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(openAppPendingIntent)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(VISIBILITY_PUBLIC)
                .setVibrate(new long[]{100L, 100L, 100L})
                .addAction(
                        R.drawable.ic_done_white_24dp,
                        getString(R.string.took_it),
                        tookItPendingIntent
                )
                .addAction(
                        R.drawable.ic_clear_white_24dp,
                        getString(R.string.missed_it),
                        missedItPendingIntent
                );

        return mBuilder.build();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void cancelNotification(int id) {
        NotificationManagerCompat.from(this).cancel(id);
    }

    private void scheduleNextAlarm(Medicine medicine) {
        final long INTERVAL_IN_MILLIS =
                (24 / medicine.getMedicineTotalNumberOfTakeTimesPerDay()) * AlarmManager.INTERVAL_HOUR;

        Intent intent = new Intent(this, NotificationService.class);
        intent.setAction(Constants.ACTION_NOTIFICATION);
        intent.putExtra(Constants.MEDICINE_ID_EXTRA, medicine.get_ID());
        PendingIntent alarmPendingIntent =
                PendingIntent.getService(
                        this,
                        medicine.getMedicineIdForPendingIntent(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + INTERVAL_IN_MILLIS);

        AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        if (alarmMgr != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmMgr.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        alarmPendingIntent
                );
            } else {
                alarmMgr.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        alarmPendingIntent
                );
            }
        }
    }
}
