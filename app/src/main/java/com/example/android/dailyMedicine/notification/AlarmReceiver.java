package com.example.android.dailyMedicine.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Constants.NOTIFICATION)) {
            // If action requested == com.example.android.dailyMedicine.SET_ALARM.
            // Get the alarm medicine id from the intent.
            String medicineId = intent.getStringExtra(Constants.MEDICINE_ID);

            // Setting a NotificationService to start when the alarm time comes.
            Intent notificationIntent = new Intent(context, NotificationService.class);

            // Sending the medicine id to it's notification.
            notificationIntent.putExtra(Constants.MEDICINE_ID, medicineId);

            // Starting the service.
            context.startService(notificationIntent);

        } else if (intent.getAction().equals(Constants.RESET_ALARM)) {
            // If action requested == android.intent.action.BOOT_COMPLETED
            // then start the alarm service to reset all alarms.
            Intent alarmIntent = new Intent(context, AlarmService.class);

            // Send the action required from the alarm service as android.intent.action.BOOT_COMPLETED.
            alarmIntent.setAction(Constants.RESET_ALARM);

            // Start the service.
            context.startService(alarmIntent);
        } else if (intent.getAction().equals(Constants.RESET_DAILY)) {
            // Setting a NotificationService to start when the alarm time comes.
            Intent updateServiceIntent = new Intent(context, UpdateService.class);

            // Setting the notificationIntent action to com.example.android.dailyMedicine.RESET_DAILY.
            updateServiceIntent.setAction(Constants.RESET_DAILY);

            // Starting the service.
            context.startService(updateServiceIntent);
        }
    }
}
