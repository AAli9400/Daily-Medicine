package com.example.android.dailyMedicine.repository;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.android.dailyMedicine.db.AppDatabase;
import com.example.android.dailyMedicine.db.Medicine;
import com.example.android.dailyMedicine.service.NotificationService;
import com.example.android.dailyMedicine.util.Constants;

import java.util.Calendar;
import java.util.List;

public class DatabaseRepository {

    private Application mApplication;
    private AppDatabase mDatabase;
    private LiveData<List<Medicine>> mMedicines;

    public DatabaseRepository(Application application) {
        this.mApplication = application;

        this.mDatabase = AppDatabase.getInstance(mApplication);

        this.mMedicines = mDatabase.medicineDao().getAllMedicines();
    }

    public LiveData<List<Medicine>> getAllMedicines() {
        return mMedicines;
    }

    public void insertMedicine(final Medicine medicine) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long id = mDatabase.medicineDao().insertMedicine(medicine);

                medicine.set_ID((int) id);
                setAlarm(medicine);
            }
        }).start();
    }

    public void deleteMedicine(final Medicine medicine) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDatabase.medicineDao().deleteMedicine(medicine);

                cancelAlarm(medicine);
            }
        }).start();
    }

    public void deleteAllMedicines() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cancelAllAlarms();

                mDatabase.medicineDao().deleteAllMedicines();
            }
        }).start();
    }

    public void updateMedicine(final Medicine medicine) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDatabase.medicineDao().updateMedicine(medicine);

                setAlarm(medicine);
            }
        }).start();
    }

    private void setAlarm(Medicine medicine) {
        Intent intent = new Intent(mApplication, NotificationService.class);
        intent.setAction(Constants.ACTION_NOTIFICATION);
        intent.putExtra(Constants.MEDICINE_ID_EXTRA, medicine.get_ID());

        PendingIntent alarmPendingIntent =
                PendingIntent.getService(
                        mApplication,
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
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmMgr = (AlarmManager) mApplication.getSystemService(Context.ALARM_SERVICE);

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

    private void cancelAlarm(Medicine medicine) {
        Intent intent = new Intent(mApplication, NotificationService.class);
        intent.setAction(Constants.ACTION_NOTIFICATION);
        intent.putExtra(Constants.MEDICINE_ID_EXTRA, medicine.get_ID());

        PendingIntent alarmPendingIntent =
                PendingIntent.getService(
                        mApplication, medicine.getMedicineIdForPendingIntent(),
                        intent,
                        PendingIntent.FLAG_NO_CREATE);

        AlarmManager alarmMgr = (AlarmManager) mApplication.getSystemService(Context.ALARM_SERVICE);
        if (alarmMgr != null && alarmPendingIntent != null) {
            alarmMgr.cancel(alarmPendingIntent);
        }
    }

    private void cancelAllAlarms() {
        List<Medicine> medicines = mMedicines.getValue();
        if (medicines != null) {
            for (int i = medicines.size() - 1; i >= 0; --i) {
                Medicine medicine = medicines.get(i);

                cancelAlarm(medicine);
            }
        }
    }

}
