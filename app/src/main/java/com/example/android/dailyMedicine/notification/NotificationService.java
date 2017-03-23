package com.example.android.dailyMedicine.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.android.dailyMedicine.MainActivity;
import com.example.android.dailyMedicine.R;
import com.example.android.dailyMedicine.data.MedicineContract;

public class NotificationService extends IntentService {
    public NotificationService() {
        super("NotificationService");
    }

    @Override
    public void onHandleIntent(Intent intent) {

        Cursor cursor = null;
        try {
            // Getting the medicine id.
            String medicineId = intent.getStringExtra(Constants.MEDICINE_ID);

            // Getting the medicine name.
            cursor = getContentResolver().query(ContentUris.withAppendedId(MedicineContract.MedicineEntry.CONTENT_URI, Long.valueOf(medicineId)), null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String medicineName = cursor.getString(cursor.getColumnIndex(MedicineContract.MedicineEntry.COLUMN_MEDICINE_NAME));

                // Prepare an UpdateService pending intent to handle took it button on the notification.
                Intent tookMedicine = new Intent(this, UpdateService.class);
                tookMedicine.putExtra(Constants.MEDICINE_ID, medicineId);
                tookMedicine.setAction(Constants.ACTION_TOOK_IT);
                PendingIntent pTookMedicine = PendingIntent.getService(this, Integer.valueOf(medicineId), tookMedicine, 0);

                // Prepare an UpdateService pending intent to handle missed it button on the notification.
                Intent missedMedicine = new Intent(this, UpdateService.class);
                missedMedicine.putExtra(Constants.MEDICINE_ID, medicineId);
                missedMedicine.setAction(Constants.ACTION_MISSED_IT);
                PendingIntent pMissedMedicine = PendingIntent.getService(this, Integer.valueOf(medicineId), missedMedicine, 0);

                // Building the notification.
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(medicineName)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(getResources().getString(R.string.its_time_to_take_your_medicine1) + medicineName + getResources().getString(R.string.its_time_to_take_your_medicine2)))
                                .addAction(R.drawable.ic_done_white_24dp, getResources().getString(R.string.took_it), pTookMedicine)
                                .addAction(R.drawable.ic_clear_white_24dp, getResources().getString(R.string.missed_it), pMissedMedicine)
                                .setPriority(2)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setOngoing(true);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                    builder.addAction(0, "", null);
                }
                // Prepare MainActivity intent to be opened when clicking the notification itself.
                Intent mainActivity = new Intent(this, MainActivity.class);
                PendingIntent pMainActivity = PendingIntent.getActivity(this, 0, mainActivity, PendingIntent.FLAG_UPDATE_CURRENT);

                // Setting the notification content Intent to be MainActivity intent.
                builder.setContentIntent(pMainActivity);

                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                // Fire the notification
                mNotifyMgr.notify(Integer.valueOf(medicineId), builder.build());

            }
        } catch (Exception ex) {
            Toast.makeText(this, "Notification Service Error, " + ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            // Close the cursor if opened.
            if (cursor != null) cursor.close();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Don't let this service restart automatically if it has been stopped by the OS.
        return START_NOT_STICKY;
    }
}
