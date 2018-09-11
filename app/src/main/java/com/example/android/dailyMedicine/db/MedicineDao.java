package com.example.android.dailyMedicine.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MedicineDao {
    @Query("SELECT * FROM medicine")
    LiveData<List<Medicine>> getAllMedicines();

    @Query("SELECT * FROM medicine WHERE _ID = :id")
    Medicine getMedicineById(long id);

    @Insert(onConflict = REPLACE)
    long insertMedicine(Medicine medicine);

    @Delete
    void deleteMedicine(Medicine medicine);

    @Query("DELETE FROM medicine")
    void deleteAllMedicines();

    @Update(onConflict = REPLACE)
    void updateMedicine(Medicine medicine);
}
