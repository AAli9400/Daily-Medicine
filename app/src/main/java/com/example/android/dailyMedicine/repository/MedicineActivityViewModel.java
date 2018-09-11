package com.example.android.dailyMedicine.repository;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.android.dailyMedicine.db.Medicine;

public class MedicineActivityViewModel extends AndroidViewModel {
    private DatabaseRepository mRepository;

    private Integer mMedicineId;
    private String mMedicineName;
    private Integer mMedicineTakeTime;

    public MedicineActivityViewModel(@NonNull Application application) {
        super(application);
        mRepository = new DatabaseRepository(application);
    }

    public Integer getMedicineId() {
        return mMedicineId;
    }

    public void setMedicineId(Integer mMedicineId) {
        this.mMedicineId = mMedicineId;
    }

    public String getMedicineName() {
        return mMedicineName;
    }

    public void setMedicineName(String mMedicineName) {
        this.mMedicineName = mMedicineName;
    }

    public Integer getMedicineTakeTime() {
        return mMedicineTakeTime;
    }

    public void setMedicineTakeTime(Integer mMedicineTakeTime) {
        this.mMedicineTakeTime = mMedicineTakeTime;
    }

    public void UpdateMedicine() {
        mRepository.updateMedicine(getMedicine(false));
    }

    public void AddNewMedicine() {
        mRepository.insertMedicine(getMedicine(true));
    }

    public void deleteMedicine() {
        mRepository.deleteMedicine(getMedicine(false));
    }

    private Medicine getMedicine(Boolean isNewMedicine) {
        if (isNewMedicine) {
            return new Medicine(getMedicineName(), getMedicineTakeTime());
        } else {
            return new Medicine(getMedicineId(), getMedicineName(), getMedicineTakeTime());
        }
    }
}
