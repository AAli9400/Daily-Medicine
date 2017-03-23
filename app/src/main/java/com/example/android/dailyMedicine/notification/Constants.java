package com.example.android.dailyMedicine.notification;

import com.example.android.dailyMedicine.data.MedicineContract;

/**
 * Created by Abdel-Rhman on 3/9/2017.
 */

public final class Constants {

    public static final String RESET_ALARM = "android.intent.action.BOOT_COMPLETED";
    public final static String RESET_DAILY = "com.example.android.dailyMedicine.RESET_DAILY";
    public final static String NOTIFICATION = "com.example.android.dailyMedicine.NOTIFICATION";

    public static final String MEDICINE_ID = MedicineContract.MedicineEntry._ID;
    public static final String MEDICINE_NOTIFICATION_TYPE = "MEDICINE_NOTIFICATION_TYPE";

    public final static String ACTION_TOOK_IT = "ACTION_TOOK_IT";
    public final static String ACTION_MISSED_IT = "ACTION_MISSED_IT";
}
