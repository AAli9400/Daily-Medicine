package com.example.android.dailyMedicine.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.android.dailyMedicine.util.Constants;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if(intent.getAction().equals(Constants.ACTION_BOOT_COMPLETED)) {

            Intent RescheduleNotificationService = new Intent(context, RescheduleNotificationsOnBoot.class);
            context.startService(RescheduleNotificationService);
        }
    }
}
