package com.example.android.dailyMedicine.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            //if the intent is not null
            //get the action
            final String action = intent.getAction();

            final String boot = Intent.ACTION_BOOT_COMPLETED;
            final String date = Intent.ACTION_BOOT_COMPLETED;
            final String time = Intent.ACTION_BOOT_COMPLETED;
            final String timeZone = Intent.ACTION_BOOT_COMPLETED;

            if (action != null &&
                    (action.equals(boot) || action.equals(date) || action.equals(time) || action.equals(timeZone))
                    ) {
                //create intent to AppService
                Intent rescheduleIntent = new Intent(context, AppService.class);

                //set the action of it to reschedule
                rescheduleIntent.setAction(AppService.ACTION_RESCHEDULE);

                //start the AppService from the created intent
                context.startService(rescheduleIntent);
            }
        }
    }
}
