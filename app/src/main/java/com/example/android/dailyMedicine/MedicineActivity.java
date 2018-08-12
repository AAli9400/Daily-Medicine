package com.example.android.dailyMedicine;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.dailyMedicine.Util.Constants;
import com.example.android.dailyMedicine.db.AppDatabase;
import com.example.android.dailyMedicine.db.Medicine;
import com.example.android.dailyMedicine.service.NotificationService;

import java.util.Calendar;

public class MedicineActivity extends AppCompatActivity {

    private int daily_take_times;
    private int mMedicineId;

    private EditText medicineNameEditText;

    private AppDatabase mDb;
    private AlarmManager mAlarmMgr;

    private Medicine mCurrentMedicine;
    private Medicine mNewMedicine;

    private TimePicker mFirstTimePicker;
    private Spinner mTotalTakeTimeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        mDb = AppDatabase.getInstance(this);
        mAlarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        medicineNameEditText = findViewById(R.id.medicine_name_edit_text);
        mTotalTakeTimeSpinner = findViewById(R.id.total_take_times);

        // Default value of daily_take_times.
        daily_take_times = 1;

        mFirstTimePicker = findViewById(R.id.first_time);

        // Loading the totalTakeTimeSpinner data.
        ArrayAdapter totalTakeTimeSpinnerAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.spinner_take_times,
                        android.R.layout.simple_spinner_dropdown_item
                );

        totalTakeTimeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mTotalTakeTimeSpinner.setAdapter(totalTakeTimeSpinnerAdapter);
        mTotalTakeTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    // Assign the selected number to daily_take_times.
                    daily_take_times = Integer.valueOf(selection);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                daily_take_times = 0;
            }
        });


        // If this activity is for editing, load the mCurrentMedicine data on it.
        mMedicineId = getIntent().getIntExtra(Constants.MEDICINE_ID_EXTRA, -1);
        if (mMedicineId != -1) {

            // Change the title to be Edit Medicine.
            setTitle(getResources().getString(R.string.edit_medicine));

            // Load the current medicine data
            new LoadCurrentMedicineDataAsyncTask().execute();
        }

        // If not for editing,
        else {
            // Change the title to be New Medicine.
            setTitle(getResources().getString(R.string.add_medicine));

            mNewMedicine = new Medicine();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medicine, menu);
        if (mMedicineId == -1) {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // When user click done icon.
            case R.id.action_done:
                // Validate mCurrentMedicine name.
                if (medicineNameEditText.getText().toString().trim().matches("")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.medicine_requires_name)
                            , Toast.LENGTH_LONG).show();
                } else {
                    mNewMedicine.setMedicineName(String.valueOf(medicineNameEditText.getText()));

                    mNewMedicine.setMedicineTotalNumberOfTakeTimesPerDay(daily_take_times);

                    mNewMedicine.setFirstHour(mFirstTimePicker.getCurrentHour());
                    mNewMedicine.setFirstMin(mFirstTimePicker.getCurrentMinute());

                    if (mMedicineId != -1) {
                        editMedicine();
                    } else {
                        addMedicine();
                    }
                }
                return true;

            case R.id.action_delete:
                showDeleteMedicineDialogue();
                break;

            // If user pressed back button on the toolbar.
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If activity is for edit and user edited nothing
        if (mMedicineId != -1 && mNewMedicine.equals(mCurrentMedicine)) {
            finish();
        } else {
            showUnsavedStateDialogue();
        }
    }

    private void showUnsavedStateDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MedicineActivity.this);
        builder.setMessage(R.string.discard_changes)
                .setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

    private void showDeleteMedicineDialogue() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MedicineActivity.this);
        builder.setMessage(R.string.delete_one_dialog_msg)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteMedicine();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

    private void deleteMedicine() {
        new DeleteMedicineAsyncTask().execute();
    }

    private void editMedicine() {
        new EditMedicineAsyncTask().execute();
    }

    private void addMedicine() {
        new AddMedicineAsyncTask().execute();
    }

    private boolean setAlarm() {
        Intent intent = new Intent(MedicineActivity.this, NotificationService.class);
        intent.setAction(Constants.ACTION_NOTIFICATION);
        intent.putExtra(Constants.MEDICINE_ID_EXTRA, mNewMedicine.get_ID());

        PendingIntent alarmPendingIntent =
                PendingIntent.getService(
                        MedicineActivity.this,
                        mNewMedicine.getMedicineIdForPendingIntent(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, mNewMedicine.getFirstHour());
        calendar.set(Calendar.MINUTE, mNewMedicine.getFirstMin());
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            Toast.makeText(MedicineActivity.this, getString(R.string.not_valid_time), Toast.LENGTH_LONG)
                    .show();
        } else if (mAlarmMgr != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mAlarmMgr.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        alarmPendingIntent
                );
            } else {
                mAlarmMgr.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        alarmPendingIntent
                );
            }
            return true;
        }

        return false;
    }

    private void cancelAlarm() {
        Intent intent = new Intent(MedicineActivity.this, NotificationService.class);
        intent.setAction(Constants.ACTION_NOTIFICATION);
        intent.putExtra(Constants.MEDICINE_ID_EXTRA, mNewMedicine.get_ID());

        PendingIntent alarmPendingIntent =
                PendingIntent.getService(
                        MedicineActivity.this,
                        mNewMedicine.getMedicineIdForPendingIntent(),
                        intent,
                        PendingIntent.FLAG_NO_CREATE);
        if (mAlarmMgr != null && alarmPendingIntent != null) {
            mAlarmMgr.cancel(alarmPendingIntent);
        }
    }

    private class LoadCurrentMedicineDataAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            mCurrentMedicine = mDb.medicineDao().getMedicineById(mMedicineId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //set the new medicine to be the current medicine
            //before the user change any data of the current medicine
            mNewMedicine = mCurrentMedicine;

            medicineNameEditText.setText(mCurrentMedicine.getMedicineName());

            daily_take_times = mCurrentMedicine.getMedicineTotalNumberOfTakeTimesPerDay();

            mTotalTakeTimeSpinner.setSelection(daily_take_times - 1);

            mFirstTimePicker.setCurrentHour(mCurrentMedicine.getFirstHour());
            mFirstTimePicker.setCurrentMinute(mCurrentMedicine.getFirstMin());

        }
    }

    private class DeleteMedicineAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            mDb.medicineDao().deleteMedicineById(mMedicineId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cancelAlarm();

            Toast.makeText(
                    MedicineActivity.this,
                    getString(R.string.medicine_deleted),
                    Toast.LENGTH_SHORT
            ).show();

            MedicineActivity.this.finish();
        }
    }

    private class EditMedicineAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (!setAlarm()) {
                this.cancel(true);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mNewMedicine.setMedicineTotalNumberOfTakenTimesPerDay(0);
            mDb.medicineDao().updateMedicine(mNewMedicine);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(
                    MedicineActivity.this,
                    getString(R.string.medicine_edited),
                    Toast.LENGTH_SHORT
            ).show();

            MedicineActivity.this.finish();
        }
    }

    private class AddMedicineAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, mNewMedicine.getFirstHour());
            calendar.set(Calendar.MINUTE, mNewMedicine.getFirstMin());
            calendar.set(Calendar.SECOND, 0);

            //if the next time is invalid, cancel the insertion
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                Toast.makeText(MedicineActivity.this, getString(R.string.not_valid_time), Toast.LENGTH_LONG)
                        .show();

                this.cancel(true);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDb.medicineDao().insertMedicine(mNewMedicine);

            //get the new inserted medicine id
            mNewMedicine.set_ID(mDb.medicineDao().getLastInsertedMedicineId());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (setAlarm()) {
                Toast.makeText(
                        MedicineActivity.this,
                        getString(R.string.medicine_added),
                        Toast.LENGTH_SHORT
                ).show();

                MedicineActivity.this.finish();
            }
        }
    }
}

