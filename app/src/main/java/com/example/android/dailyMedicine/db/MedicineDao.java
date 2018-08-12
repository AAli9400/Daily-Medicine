package com.example.android.dailyMedicine.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MedicineDao {
    @Query("SELECT _ID FROM medicine ORDER BY _ID DESC LIMIT 1")
    Integer getLastInsertedMedicineId();

    @Query("SELECT * FROM medicine")
    LiveData<List<Medicine>> getAllMedicinesAsLiveData();

    @Query("SELECT * FROM medicine")
    List<Medicine> getAllMedicines();

    @Query("SELECT * FROM medicine WHERE _ID = :id")
    Medicine getMedicineById(int id);

    @Insert
    void insertMedicine(Medicine medicine);

    @Delete
    void deleteMedicine(Medicine medicine);

    @Query("DELETE FROM medicine")
    void deleteAllMedicines();

    @Query("DELETE FROM medicine WHERE _ID = :id")
    void deleteMedicineById(int id);

    @Update
    void updateMedicine(Medicine medicine);
}
