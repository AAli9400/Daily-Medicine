package com.example.android.dailyMedicine.ui;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.dailyMedicine.R;
import com.example.android.dailyMedicine.repository.MedicineActivityViewModel;
import com.example.android.dailyMedicine.util.Constants;

public class MedicineActivity extends AppCompatActivity {

    private int mMedicineId;

    private TextInputEditText mMedicineNameEditText;

    private MedicineActivityViewModel viewModel;

    private Spinner mTotalTakeTimeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        viewModel = ViewModelProviders.of(this).get(MedicineActivityViewModel.class);

        setupViews();

        // If this activity is for editing, load the Medicine data on it.
        mMedicineId = getIntent().getIntExtra(Constants.MEDICINE_ID_EXTRA, -1);
        if (mMedicineId != -1) {
            // set the title to be Edit Medicine.
            setTitle(getResources().getString(R.string.edit_medicine));

            // Load the current medicine data
            viewModel.setMedicineId(mMedicineId);
            viewModel.setMedicineName(getIntent().getStringExtra(Constants.MEDICINE_NAME_EXTRA));
            viewModel.setMedicineTakeTime(getIntent().getIntExtra(Constants.MEDICINE_TAKE_TIMES_EXTRA, 1));

            mMedicineNameEditText.setText(viewModel.getMedicineName());
            mTotalTakeTimeSpinner.setSelection(viewModel.getMedicineTakeTime() - 1);
        }
        // If not for editing,
        else {
            // set the title to be New Medicine.
            setTitle(getResources().getString(R.string.add_medicine));
            if (viewModel.getMedicineName() != null) {
                mMedicineNameEditText.setText(viewModel.getMedicineName());
            }
            if (viewModel.getMedicineTakeTime() != null) {
                mTotalTakeTimeSpinner.setSelection(viewModel.getMedicineTakeTime() - 1);
            }
        }
    }

    private void setupViews() {
        mMedicineNameEditText = findViewById(R.id.medicine_name_edit_text);
        mTotalTakeTimeSpinner = findViewById(R.id.total_take_times);

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
                    // Assign the selected number to the medicine.
                    viewModel.setMedicineTakeTime(Integer.valueOf(selection));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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
                // Validate medicine name.
                Editable medicineNameText = mMedicineNameEditText.getText();
                if (medicineNameText != null) {
                    String medicineName = medicineNameText.toString().trim();

                    if (medicineName.matches("")) {
                        Toast.makeText(this, getResources().getString(R.string.medicine_requires_name)
                                , Toast.LENGTH_LONG).show();
                    } else {
                        viewModel.setMedicineName(medicineName);

                        if (mMedicineId != -1) {
                            viewModel.UpdateMedicine();
                        } else {
                            viewModel.AddNewMedicine();
                        }
                    }
                    finish();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.medicine_requires_name)
                            , Toast.LENGTH_LONG).show();
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
        showUnsavedStateDialogue();
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
                        viewModel.deleteMedicine();

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
}

