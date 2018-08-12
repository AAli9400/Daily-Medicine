package com.example.android.dailyMedicine;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.android.dailyMedicine.db.AppDatabase;
import com.example.android.dailyMedicine.db.Medicine;

import java.util.List;

//extends AndroidViewModel class not ViewModel class cause we need the context
public class MedicineViewModel extends AndroidViewModel {
    private LiveData<List<Medicine>> medicinesLiveData;
    private List<Medicine> medicines;

    public MedicineViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Medicine>> getAllMedicinesAsLiveData() {
        if (medicinesLiveData == null) {
            medicinesLiveData = AppDatabase.getInstance(getApplication()).medicineDao().getAllMedicinesAsLiveData();
        }
        return medicinesLiveData;
    }

    public List<Medicine> getAllMedicines() {
        if (medicinesLiveData == null) {
            getAllMedicinesAsLiveData();
        }
        if (medicines == null) {
            medicines = medicinesLiveData.getValue();
        }

        return medicines;
    }
}
