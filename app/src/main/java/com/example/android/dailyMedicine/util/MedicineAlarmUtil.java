package com.example.android.dailyMedicine.util;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.android.dailyMedicine.model.Medicine;
import com.example.android.dailyMedicine.service.AppService;
import com.example.android.dailyMedicine.ui.MainActivity;
import com.google.gson.Gson;

import java.util.Calendar;

public class MedicineAlarmUtil {
    private final AlarmManager alarmManager;
    private final Application application;
    private final Gson gson;

    private MedicineAlarmUtil(Application application) throws NullPointerException {
        this.application = application;
        alarmManager = (AlarmManager) this.application.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) throw new NullPointerException("Cannot create AlarmManager.");

        gson = new Gson();
    }

    private static MedicineAlarmUtil ourInstance = null;

    public static MedicineAlarmUtil getInstance(Application application) {
        if (ourInstance == null) {
            ourInstance = new MedicineAlarmUtil(application);
        }
        return ourInstance;
    }

    public void set(@NonNull Long triggerTime, @NonNull Medicine medicine) {
        Intent intent = new Intent(application, AppService.class);
        intent.setAction(AppService.ACTION_NOTIFICATION);
        intent.putExtra(AppService.MEDICINE_KEY, gson.toJson(medicine));

        PendingIntent pendingIntent = PendingIntent.getService(
                application,
                medicine.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        //create intent to the MainActivity
        Intent mainActivityIntent = new Intent(application, MainActivity.class);

        //add flags to handle the stack properly
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //create the pending intent
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(
                application,
                -1,
                mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager.AlarmClockInfo clockInfo = new AlarmManager.AlarmClockInfo(triggerTime, mainActivityPendingIntent);

        alarmManager.setAlarmClock(clockInfo, pendingIntent);
    }

    public Long createTriggerTime(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }

    public void cancelAlarm(Medicine medicine) {
        Intent intent = new Intent(application, AppService.class);
        intent.setAction(AppService.ACTION_NOTIFICATION);
        intent.putExtra(AppService.MEDICINE_KEY, gson.toJson(medicine));

        PendingIntent pendingIntent = PendingIntent.getService(
                application,
                medicine.getId(),
                intent,
                PendingIntent.FLAG_NO_CREATE
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
