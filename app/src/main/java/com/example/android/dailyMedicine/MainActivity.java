package com.example.android.dailyMedicine;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.dailyMedicine.data.MedicineContract.MedicineEntry;
import com.example.android.dailyMedicine.notification.Constants;
import com.example.android.dailyMedicine.notification.NotificationService;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // The cursor adapter that used to load data on the screen.
    MedicineCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enabling the support for the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting the click listener of the floating action button.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the MedicineActivity to add new medicine.
                Intent intent = new Intent(MainActivity.this, MedicineActivity.class);
                startActivity(intent);
            }
        });


        ListView medicineListView = (ListView) findViewById(R.id.medicine_list);

        // Setting the empty view of the list view,
        // in case no medicine added.
        View emptyView = findViewById(R.id.empty_view);
        medicineListView.setEmptyView(emptyView);

        // Setting the cursor adapter of the list view,
        // to load all medicines in the database.
        mCursorAdapter = new MedicineCursorAdapter(this, null);
        medicineListView.setAdapter(mCursorAdapter);

        // Registering the list view to the context menu,
        // that will set the context menu to each item in the list.
        registerForContextMenu(medicineListView);

        // Kick off the loader.
        getLoaderManager().initLoader(0, null, this);

        // Setting the broadcast to start to reset take time daily at 12:00 AM.
        Intent update = new Intent(Constants.RESET_DAILY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, update, PendingIntent.FLAG_CANCEL_CURRENT);

        // Defining calender and set it's time to 12:00 AM.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis())
            calendar.add(Calendar.DAY_OF_YEAR, 1);

        // Firing the alarm ever day at 12:00 AM.
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main,
        // this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                // Delete all medicines if any.
                deleteAllMedicines();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Load the context menu items from menu_context.
        getMenuInflater().inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Getting selected item info to use it's id.
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_delete:
                // Delete long clicked medicine.
                deleteMedicine(info.id);
                return true;

            case R.id.action_edit:
                // If user choose edit.
                // Starting the MedicineActivity with this medicine data loaded on it.
                Intent medicineActivityIntent = new Intent(this, MedicineActivity.class);

                // Sending the URI of the medicine that user want to edit.
                medicineActivityIntent.setData(Uri.withAppendedPath(MedicineEntry.CONTENT_URI, String.valueOf(info.id)));

                // Start MedicineActivity.
                startActivity(medicineActivityIntent);
                return true;

            case R.id.action_details:
                // Show long clicked menu details.
                showMedicineDetails(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                MedicineEntry._ID,
                MedicineEntry.COLUMN_MEDICINE_NAME,
                MedicineEntry.COLUMN_MEDICINE_TAKE_TIMES,
                MedicineEntry.COLUMN_MEDICINE_TAKEN_TIMES,
        };

        String sortOrder =
                MedicineEntry.COLUMN_MEDICINE_NAME + " ASC";
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                MedicineEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                sortOrder);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void deleteAllMedicines() {
        // Get all medicines to the cursor before deleting them,
        // to ba able to use each medicine id to cancel it's alarm(s).
        final Cursor cursor = getContentResolver().query(MedicineEntry.CONTENT_URI, null, null, null, null);

        // If there id medicines then confirm user to delete them all.
        if (cursor != null && cursor.getCount() > 0) {

            // Building an alert dialog to make user confirm to delete all medicines in the app.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_all_dialog_msg);
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    /* If user confirmed to delete all,
                    * Delete all Medicine in yhe database,
                    * and cancel all alarms also.
                    */

                    // Delete all medicines,
                    getContentResolver().delete(MedicineEntry.CONTENT_URI, null, null);

                    while (cursor.moveToNext()) {
                        // Number of alarms to cancel for each medicine.
                        int takeTimes = cursor.getInt(cursor.getColumnIndex(MedicineEntry.COLUMN_MEDICINE_TAKE_TIMES));

                        // Id of each medicine.
                        int _id = cursor.getInt(cursor.getColumnIndex(Constants.MEDICINE_ID));

                        // To cancel the alarm, AlarmService can cancel one alarm every time.
                        // Send broadcast for each alarm of each medicine to delete it.
                        for (int i = 1; i <= takeTimes; i++) {

                            // Create the pending intent that was set in the alarm.
                            Intent notificationIntent = new Intent(MainActivity.this, NotificationService.class);
                            PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this,
                                    _id + i, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                            // Defining the alarm manager.
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                            // Cancelling the alarm.
                            alarmManager.cancel(pendingIntent);
                        }
                    }
                    cursor.close();
                    // Confirm the user of medicines deleted.
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.all_medicines_deleted)
                            , Toast.LENGTH_SHORT).show();

                }
            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // If user canceled the deleting action.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            // Create and show the AlertDialog.
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        // If query returned with no data then tell user of no data to delete.
        else {
            // Notify the user with no medicine to delete.
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.No_medicines_to_delete)
                    , Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param idOfSelectedItem is the id of the selected item in the lest view.
     */
    private void deleteMedicine(final long idOfSelectedItem) {
        // If user choose to delete.
        // Building an alert dialog to make user confirm deleting this medicine.
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.delete_one_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // If user confirm to delete.
                // Get this medicine data before deleting it to cancel it's alarm(s).
                Cursor cursor = getContentResolver().query(Uri.withAppendedPath(MedicineEntry.CONTENT_URI, String.valueOf(idOfSelectedItem)), null, null, null, null);

                // Delete the medicine and get number of row deleted.
                int numberOfRowDeleted = getContentResolver().delete(ContentUris.withAppendedId(MedicineEntry.CONTENT_URI, idOfSelectedItem), null, null);

                if (cursor != null && cursor.moveToFirst() && numberOfRowDeleted == 1) {
                    // Number of alarm(s) to be .
                    int takeTimes = cursor.getInt(cursor.getColumnIndex(MedicineEntry.COLUMN_MEDICINE_TAKE_TIMES));

                    // Looping to delete each alarm.
                    for (int i = 0; i < takeTimes; ++i) {
                        // Create the pending intent that was set in the alarm.
                        Intent notificationIntent = new Intent(MainActivity.this, NotificationService.class);
                        PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, (int) (idOfSelectedItem + i), notificationIntent, 0);

                        // Defining the alarm manager.
                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                        // Cancelling the alarm.
                        alarmManager.cancel(pendingIntent);
                    }

                    // Confirm user of deletion.
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.medicine_deleted)
                            , Toast.LENGTH_SHORT).show();
                    cursor.close();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // If user canceled deleting.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * @param idOfSelectedItem is the id of the selected item in the lest view.
     */
    private void showMedicineDetails(long idOfSelectedItem) {
        // If user choose details.
        // Load all details into string then,
        // set a dialog with that string.

        // Get medicine data.
        Cursor cursor = getContentResolver().query(Uri.withAppendedPath(MedicineEntry.CONTENT_URI, String.valueOf(idOfSelectedItem))
                , null
                , null
                , null
                , null);

        if (cursor != null && cursor.moveToFirst()) {

            // Getting columns indices.
            int medicineNameColumnIndex = cursor.getColumnIndex(MedicineEntry.COLUMN_MEDICINE_NAME);
            int takeTimesColumnIndex = cursor.getColumnIndex(MedicineEntry.COLUMN_MEDICINE_TAKE_TIMES);
            int takenTimesColumnIndex = cursor.getColumnIndex(MedicineEntry.COLUMN_MEDICINE_TAKEN_TIMES);
            int totalTakenTimesColumnIndex = cursor.getColumnIndex(MedicineEntry.COLUMN_MEDICINE_TOTAL_TAKEN_TIMES);

            // Times to take this medicine daily.
            int takeTimes = cursor.getInt(takeTimesColumnIndex);

            // Used to retrieve column name of each hour and minute of each alarm.
            int k = 0;

            // Assigning basic details to the string.
            String details = cursor.getString(medicineNameColumnIndex)
                    + "\n"
                    + getResources().getString(R.string.you_take_this_medicine)
                    + cursor.getInt(takeTimesColumnIndex)
                    + getResources().getString(R.string.times_per_day)
                    + "\n"
                    + getResources().getString(R.string.you_took_this_medicine)
                    + cursor.getInt(takenTimesColumnIndex)
                    + getResources().getString(R.string.times_today)
                    + "\n"
                    + getResources().getString(R.string.you_took_this_medicine)
                    + cursor.getString(totalTakenTimesColumnIndex)
                    + getResources().getString(R.string.so_far)
                    + "\n"
                    + getResources().getString(R.string.you_take_this_medicine_at)
                    + "\n";

            // Assigning the each alarm details to the string.
            for (int i = 0; i < takeTimes; ++i) {

                // Getting columns indices.
                int hourColumnIndex = cursor.getColumnIndex(MedicineEntry.COLUMN_TIMES_NAMES[k++]);
                int minuteColumnIndex = cursor.getColumnIndex(MedicineEntry.COLUMN_TIMES_NAMES[k++]);

                // Getting the alarm time.
                int h = cursor.getInt(hourColumnIndex);
                int m = cursor.getInt(minuteColumnIndex);

                // Reformatting Time to be like 09:08 AM or 03:17 PM.
                if (h > 12) {
                    if (h % 12 > 9) {
                        details += String.valueOf(h % 12) + ":";
                        details += (m > 9) ? String.valueOf(m) : "0" + String.valueOf(m);
                        details += " PM\n";
                    } else {
                        details += "0" + String.valueOf(h % 12) + ":";
                        details += (m > 9) ? String.valueOf(m) : "0" + String.valueOf(m);
                        details += " PM\n";
                    }
                } else if (h < 12) {
                    if (h > 9) {
                        details += String.valueOf(h) + ":";
                        details += (m > 9) ? String.valueOf(m) : "0" + String.valueOf(m);
                        details += " AM\n";
                    } else {
                        details += "0" + String.valueOf(h) + ":";
                        details += (m > 9) ? String.valueOf(m) : "0" + String.valueOf(m);
                        details += " AM\n";
                    }
                } else {
                    details += String.valueOf(h) + ":";
                    details += (m > 9) ? String.valueOf(m) : "0" + String.valueOf(m);
                    details += " PM\n";
                }
            }

            // Building a dialog to show all details on it.
            AlertDialog.Builder detailsBuilder = new AlertDialog.Builder(this);
            detailsBuilder.setMessage(details)
                    .setTitle(R.string.action_details)
                    .setPositiveButton(R.string.action_done, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    })
                    .show();
            cursor.close();
        }
    }
}
