package com.example.android.dailyMedicine.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.android.dailyMedicine.R;
import com.example.android.dailyMedicine.db.Medicine;
import com.example.android.dailyMedicine.repository.MainActivityViewModel;
import com.example.android.dailyMedicine.util.Constants;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MedicineAdapter.Resources,
        MedicineAdapter.ActionListener {

    private View mEmptyView;

    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView medicinesRecyclerView = findViewById(R.id.rv_medicines);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        medicinesRecyclerView.setLayoutManager(linearLayoutManager);

        viewModel =
                ViewModelProviders.of(this).get(MainActivityViewModel.class);

        final MedicineAdapter medicineAdapter =
                new MedicineAdapter(null, this, this);
        medicinesRecyclerView.setAdapter(medicineAdapter);

        viewModel.getAllMedicines()
                .observe(this, new Observer<List<Medicine>>() {
                    @Override
                    public void onChanged(@Nullable List<Medicine> medicines) {
                        medicineAdapter.updateData(medicines);

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
                if (viewModel.getMedicinesSize() == 0) {
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
    public void onListItemClickListener(int medicineId, String medicineName, int medicineTakeTimes) {
        Intent medicineActivityIntent = new Intent(this, MedicineActivity.class);
        medicineActivityIntent.putExtra(Constants.MEDICINE_ID_EXTRA, medicineId);
        medicineActivityIntent.putExtra(Constants.MEDICINE_NAME_EXTRA, medicineName);
        medicineActivityIntent.putExtra(Constants.MEDICINE_TAKE_TIMES_EXTRA, medicineTakeTimes);
        startActivity(medicineActivityIntent);
    }

    public void confirmAndDeleteAllMedicines() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                viewModel.deleteAllMedicines();
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
}
