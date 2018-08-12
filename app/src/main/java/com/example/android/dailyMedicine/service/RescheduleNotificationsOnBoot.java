package com.example.android.dailyMedicine.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.android.dailyMedicine.Util.Constants;
import com.example.android.dailyMedicine.db.AppDatabase;
import com.example.android.dailyMedicine.db.Medicine;

import java.util.Calendar;
import java.util.List;

public class RescheduleNotificationsOnBoot extends IntentService {

    public RescheduleNotificationsOnBoot() {
        super("RescheduleNotificationsOnBoot");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppDatabase mDb = AppDatabase.getInstance(this);
        List<Medicine> medicines = mDb.medicineDao().getAllMedicines();
        for (int i = medicines.size() - 1; i >= 0; --i) {
            Medicine medicine = medicines.get(i);

            scheduleAlarm(medicine);
        }
    }

    private void scheduleAlarm(Medicine medicine) {
        final int INTERVAL =
                (24 / medicine.getMedicineTotalNumberOfTakeTimesPerDay());

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
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, medicine.getFirstHour());
        calendar.set(Calendar.MINUTE, medicine.getFirstMin());

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.HOUR_OF_DAY, INTERVAL);
        }

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
