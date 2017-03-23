package com.example.android.dailyMedicine;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.dailyMedicine.data.MedicineContract;
import com.example.android.dailyMedicine.data.MedicineContract.MedicineEntry;
import com.example.android.dailyMedicine.notification.Constants;
import com.example.android.dailyMedicine.notification.NotificationService;

import java.util.Calendar;

public class MedicineActivity extends AppCompatActivity {

    // Store number of time pickers to be displayed,
    // depends on user selection
    private int take_times;

    // Used to check weather this activity is for new medicine or for edit one.
    private Uri uriEdit;

    private TextView medicineNameTextView;

    // Using array for all time pickers and their hints to control showing or hiding them.
    TimePicker[] timePickers = new TimePicker[6];
    TextView[] timePickersHint = new TextView[6];


    // Used to check weather the view where touched or not.
    Boolean ViewTouched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        medicineNameTextView = (TextView) findViewById(R.id.medicine_name_edit_text);
        Spinner totalTakeTimeSpinner = (Spinner) findViewById(R.id.total_take_times);

        // Default value of take_times.
        take_times = 1;


        timePickers[0] = (TimePicker) findViewById(R.id.time_1);
        timePickers[1] = (TimePicker) findViewById(R.id.time_2);
        timePickers[2] = (TimePicker) findViewById(R.id.time_3);
        timePickers[3] = (TimePicker) findViewById(R.id.time_4);
        timePickers[4] = (TimePicker) findViewById(R.id.time_5);
        timePickers[5] = (TimePicker) findViewById(R.id.time_6);

        timePickersHint[0] = (TextView) findViewById(R.id.time_hint_1);
        timePickersHint[1] = (TextView) findViewById(R.id.time_hint_2);
        timePickersHint[2] = (TextView) findViewById(R.id.time_hint_3);
        timePickersHint[3] = (TextView) findViewById(R.id.time_hint_4);
        timePickersHint[4] = (TextView) findViewById(R.id.time_hint_5);
        timePickersHint[5] = (TextView) findViewById(R.id.time_hint_6);

        // Setting the onTouch listener of the medicineNameTextView,
        // to check weather it was touched or not.
        medicineNameTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewTouched = true;
                return false;
            }
        });

        // Setting the onTouch listener of the totalTakeTimeSpinner,
        // to check weather it was touched or not.
        totalTakeTimeSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewTouched = true;
                return false;
            }
        });

        // Loading the totalTakeTimeSpinner data.
        ArrayAdapter totalTakeTimeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_take_times, android.R.layout.simple_spinner_item);

        totalTakeTimeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        totalTakeTimeSpinner.setAdapter(totalTakeTimeSpinnerAdapter);
        totalTakeTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    // Assign the selected number to take_times.
                    take_times = Integer.valueOf(selection);
                }

                // Make time pickers appear as many as take_times,
                // and hide the rest.
                for (int i = 0; i < take_times; i++) {
                    timePickersHint[i].setVisibility(View.VISIBLE);
                    timePickers[i].setVisibility(View.VISIBLE);
                }
                for (int i = take_times; i < 6; i++) {
                    timePickersHint[i].setVisibility(View.GONE);
                    timePickers[i].setVisibility(View.GONE);
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                take_times = 0;
            }
        });


        uriEdit = getIntent().getData();

        // If this activity is for editing, load the medicine data on it.
        if (uriEdit != null) {

            // Change the title to be Edit Medicine.
            setTitle(getResources().getString(R.string.edit_medicine));

            // Get the medicine data.
            Cursor cursor = getContentResolver().query(uriEdit, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                medicineNameTextView.setText(
                        cursor.getString(cursor.getColumnIndex(MedicineEntry.COLUMN_MEDICINE_NAME)));

                totalTakeTimeSpinner.setSelection(
                        cursor.getInt(cursor.getColumnIndex(MedicineEntry.COLUMN_MEDICINE_TAKE_TIMES)) - 1);

                take_times = totalTakeTimeSpinner.getSelectedItemPosition() + 1;

                totalTakeTimeSpinner.setSelection(
                        cursor.getInt(cursor.getColumnIndex(MedicineEntry.COLUMN_MEDICINE_TAKE_TIMES)) - 1);

                int k = 0;
                for (int i = 0; i < take_times; ++i) {
                    timePickers[i].setCurrentHour(
                            cursor.getInt(cursor.getColumnIndex(MedicineEntry.COLUMN_TIMES_NAMES[k++])));

                    timePickers[i].setCurrentMinute(
                            cursor.getInt(cursor.getColumnIndex(MedicineEntry.COLUMN_TIMES_NAMES[k++])));
                }

                cursor.close();
            }
        }

        // If not for editing,
        // Change the title to be New Medicine.
        else setTitle(getResources().getString(R.string.add_medicine));
    }

    // Loading the spinner data from the take_times array in array resources.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medicine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // When user click done icon.
            case R.id.action_done:
                try {
                    // Validate medicine name.
                    if (medicineNameTextView.getText().toString().trim().matches("")) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.medicine_requires_name)
                                , Toast.LENGTH_LONG).show();
                    } else {

                        // Get medicine details from the activity and saving it into ContentValues variable.
                        ContentValues values = new ContentValues();

                        values.put(MedicineEntry.COLUMN_MEDICINE_NAME, String.valueOf(medicineNameTextView.getText()));
                        values.put(MedicineEntry.COLUMN_MEDICINE_TAKE_TIMES, take_times);
                        int k = 0;
                        for (int i = 0; i < take_times; i++) {
                            values.put(MedicineEntry.COLUMN_TIMES_NAMES[k++], timePickers[i].getCurrentHour());
                            values.put(MedicineEntry.COLUMN_TIMES_NAMES[k++], timePickers[i].getCurrentMinute());
                        }

                        // If this is an edit activity then cancel all old alarms before editing.
                        if (uriEdit != null) {
                            editMedicine(values);
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.medicine_edited), Toast.LENGTH_SHORT).show();
                        }
                        // IF this is New medicine activity  then insert the new medicine.
                        else {
                            addMedicine(values);
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.medicine_added), Toast.LENGTH_SHORT).show();
                        }

                        // Close this activity and back to the previous one.
                        finish();
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "Error !", Toast.LENGTH_LONG).show();
                }
                return true;

            // If user pressed back button on the toolbar.
            case android.R.id.home:

                // Check weather the view where touched or not.
                if (!ViewTouched) {
                    NavUtils.navigateUpFromSameTask(MedicineActivity.this);
                    return true;
                }

                // If the view where touched,
                // Confirm user of discard changes.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(MedicineActivity.this);
                            }
                        };

                // Send the positive button onClick listener and show the confirmation dialogue.
                showUnsavedStateDialogue(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Check weather the view touched or not,
        if (!ViewTouched) {
            super.onBackPressed();
            return;
        }

        // If the view where touched,
        // Confirm user of discard changes.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        // Send the positive button onClick listener and show the confirmation dialogue.
        showUnsavedStateDialogue(discardButtonClickListener);
    }

    private void showUnsavedStateDialogue(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_changes);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * @param values contains basic details about medicine to be edited.
     */
    private void editMedicine(ContentValues values) {
        Cursor cursor = getContentResolver().query(uriEdit, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int takeTimes = cursor.getInt(cursor.getColumnIndex(MedicineEntry.COLUMN_MEDICINE_TAKE_TIMES));

            int _id = cursor.getInt(cursor.getColumnIndex(MedicineEntry._ID));

            for (int i = 1; i <= takeTimes; ++i) {
                // Create the pending intent that was set in the alarm.
                Intent notificationIntent = new Intent(this, NotificationService.class);
                PendingIntent pendingIntent = PendingIntent.getService(this, _id + i,
                        notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                // Defining the alarm manager.
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                // Cancelling the alarm.
                alarmManager.cancel(pendingIntent);
            }

            // Update the medicine
            getContentResolver().update(uriEdit, values, null, null);
            Uri newUri = uriEdit;

            // Update all medicine's alarms.
            setAlarms(newUri);

            // Close the cursor
            cursor.close();
        }
    }

    /**
     * @param values contains basic details about medicine to be added.
     */
    private void addMedicine(ContentValues values) {
        int lastId = 2;

        String[] projection = {MedicineEntry._ID};
        Cursor cursor = getContentResolver().query(MedicineEntry.CONTENT_URI,
                projection, null, null, MedicineEntry._ID + " ASC");

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToLast()) {
            // Getting the last id in the database.
            lastId = cursor.getInt(cursor.getColumnIndex(MedicineEntry._ID));

            // Add the medicine new id to the ContentValues values variable.
            values.put(MedicineEntry._ID, lastId + 7);

            // Close the cursor after finishing.
            cursor.close();
        } else
            values.put(MedicineEntry._ID, lastId); // If no data in the database, the id should = to 1.

        Uri newUri = getContentResolver().insert(MedicineEntry.CONTENT_URI, values);
        setAlarms(newUri);
    }

    /**
     * @param newUri is the uri of the newly added of edited medicine.
     */
    private void setAlarms(Uri newUri) {
        Cursor cursor = getContentResolver().query(newUri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int newMedicineId;
            newMedicineId = cursor.getInt(cursor.getColumnIndex(MedicineEntry._ID));
            int k = 0;
            for (int i = 1; i <= take_times; i++) {
                // Get the hour of the alarm.
                int hour = cursor.getInt(cursor.getColumnIndex(MedicineContract.MedicineEntry.COLUMN_TIMES_NAMES[k++]));

                // Get the minute of the alarm.
                int minute = cursor.getInt(cursor.getColumnIndex(MedicineContract.MedicineEntry.COLUMN_TIMES_NAMES[k++]));

                // Setting the broadcast to start NotificationService when the alarm time comes.
                Intent notificationIntent = new Intent(Constants.NOTIFICATION);
                notificationIntent.putExtra(Constants.MEDICINE_ID, String.valueOf(newMedicineId));

                // Setting the pending intent with NotificationService intend and the request code.
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, newMedicineId + i,
                        notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                // Defining an alarm manager.
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                // Setting the time of the alarm.
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (calendar.getTimeInMillis() < System.currentTimeMillis())
                    calendar.add(Calendar.DAY_OF_YEAR, 1);

                // Setting the repeating alarm with the specific time created inside the calendar.
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            }
            cursor.close();
        }
    }
}
