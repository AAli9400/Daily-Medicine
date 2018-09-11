package com.example.android.dailyMedicine.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.android.dailyMedicine.db.Medicine;
import com.example.android.dailyMedicine.repository.DatabaseRepository;
import com.example.android.dailyMedicine.util.Constants;

import java.util.Calendar;
import java.util.List;

public class RescheduleNotificationsOnBoot extends IntentService {

    public RescheduleNotificationsOnBoot() {
        super("RescheduleNotificationsOnBoot");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DatabaseRepository repository = new DatabaseRepository(getApplication());
        List<Medicine> medicines = repository.getAllMedicines().getValue();

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
        int hour = 0;
        while (hour <= calendar.get(Calendar.HOUR_OF_DAY)) {
            hour += (24 / medicine.getMedicineTotalNumberOfTakeTimesPerDay());
            if (hour >= 24) {
                hour %= 24;
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);

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
