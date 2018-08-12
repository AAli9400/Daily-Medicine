package com.example.android.dailyMedicine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.dailyMedicine.Util.Constants;
import com.example.android.dailyMedicine.db.AppDatabase;
import com.example.android.dailyMedicine.db.Medicine;
import com.example.android.dailyMedicine.service.NotificationService;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MedicineAdapter.Resources,
        MedicineAdapter.Listener {

    private AppDatabase mDb;

    private View mEmptyView;

    private MedicineViewModel mMedicinesModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = AppDatabase.getInstance(this);

        RecyclerView medicinesRecyclerView = findViewById(R.id.rv_medicines);
//        medicinesRecyclerView.setHasFixedSize(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        medicinesRecyclerView.setLayoutManager(linearLayoutManager);

        mMedicinesModel =
                ViewModelProviders.of(this).get(MedicineViewModel.class);

        final MedicineAdapter medicineAdapter =
                new MedicineAdapter(mMedicinesModel.getAllMedicines(),
                        this,
                        this
                );
        medicinesRecyclerView.setAdapter(medicineAdapter);

        mMedicinesModel.getAllMedicinesAsLiveData()
                .observe(this, new Observer<List<Medicine>>() {
                    @Override
                    public void onChanged(@Nullable List<Medicine> medicines) {
                        medicineAdapter.swapData(medicines);

                        setEmptyViewVisibility(
                                ((medicines != null ? medicines.size() : 0) == 0) ?
                                        View.VISIBLE : View.INVISIBLE
                        );
                    }
                });


        // Setting the empty view of the list view,
        // in case no medicine added.
        mEmptyView = findViewById(R.id.empty_view);

        // Setting the click listener of the floating action button.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the MedicineActivity to add new medicine.
                Intent intent = new Intent(MainActivity.this, MedicineActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setEmptyViewVisibility(int visibility) {
        mEmptyView.setVisibility(visibility);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                if (mMedicinesModel.getAllMedicines().size() == 0) {
                    Toast.makeText(
                            this,
                            getString(R.string.no_medicine_added),
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    confirmAndDeleteAllMedicines();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getStringResources(int resId) {
        return getResources().getString(resId);
    }

    @Override
    public void onClick(View view, int medicineId) {
        Intent medicineActivityIntent = new Intent(this, MedicineActivity.class);
        medicineActivityIntent.putExtra(Constants.MEDICINE_ID_EXTRA, medicineId);
        startActivity(medicineActivityIntent);
    }

    public void confirmAndDeleteAllMedicines() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllMedicines();

            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteAllMedicines() {
        new DeleteAllMedicinesAsyncTask().execute();
    }

    private void cancelAllAlarms() {
        List<Medicine> medicines = mMedicinesModel.getAllMedicines();
        if (medicines != null) {
            for (int i = medicines.size() - 1; i >= 0; --i) {
                Medicine medicine = medicines.get(i);

                Intent intent = new Intent(this, NotificationService.class);
                intent.setAction(Constants.ACTION_NOTIFICATION);
                intent.putExtra(Constants.MEDICINE_ID_EXTRA, medicine.get_ID());

                PendingIntent alarmPendingIntent =
                        PendingIntent.getService(
                                this,
                                medicine.getMedicineIdForPendingIntent(),
                                intent,
                                PendingIntent.FLAG_NO_CREATE);

                AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                if (alarmPendingIntent != null) {
                    if (alarmMgr != null) {
                        alarmMgr.cancel(alarmPendingIntent);
                    }
                }
            }
        }
    }

    private class DeleteAllMedicinesAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mDb.medicineDao().deleteAllMedicines();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cancelAllAlarms();

            Toast.makeText(
                    MainActivity.this,
                    getString(R.string.all_medicines_deleted),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
