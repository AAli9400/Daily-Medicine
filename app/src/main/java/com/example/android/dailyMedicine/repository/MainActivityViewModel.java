package com.example.android.dailyMedicine.repository;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.dailyMedicine.db.Medicine;

import java.util.List;

//extends AndroidViewModel class not ViewModel class cause we need the context
public class MainActivityViewModel extends AndroidViewModel {
    private DatabaseRepository mRepository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mRepository = new DatabaseRepository(application);
    }

    public LiveData<List<Medicine>> getAllMedicines() {
        return mRepository.getAllMedicines();
    }

    public int getMedicinesSize() {
        try {
            return mRepository.getAllMedicines().getValue().size();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public void deleteAllMedicines() {
        mRepository.deleteAllMedicines();
    }
}
