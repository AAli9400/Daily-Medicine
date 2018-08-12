package com.example.android.dailyMedicine.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.example.android.dailyMedicine.Util.Constants;

@Entity(tableName = Constants.MEDICINE_TABLE_NAME)
public class Medicine {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_ID")
    private Integer _ID;

    @ColumnInfo(name = "name")
    private String medicineName;

    @ColumnInfo(name = "total_number_of_times_per_day")
    private int medicineTotalNumberOfTakeTimesPerDay;

    @ColumnInfo(name = "total_number_of_taken_times_per_day")
    private int MedicineTotalNumberOfTakenTimesPerDay;

    @ColumnInfo(name = "first_hour")
    private int firstHour;

    @ColumnInfo(name = "first_min")
    private int firstMin;

    public Integer get_ID() {
        return _ID;
    }

    public void set_ID(Integer _ID) {
        this._ID = _ID;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getMedicineTotalNumberOfTakeTimesPerDay() {
        return medicineTotalNumberOfTakeTimesPerDay;
    }

    public void setMedicineTotalNumberOfTakeTimesPerDay(int medicineTotalNumberOfTakeTimesPerDay) {
        this.medicineTotalNumberOfTakeTimesPerDay = medicineTotalNumberOfTakeTimesPerDay;
    }

    public int getMedicineTotalNumberOfTakenTimesPerDay() {
        return MedicineTotalNumberOfTakenTimesPerDay;
    }

    public void setMedicineTotalNumberOfTakenTimesPerDay(int medicineTotalNumberOfTakenTimesPerDay) {
        MedicineTotalNumberOfTakenTimesPerDay = medicineTotalNumberOfTakenTimesPerDay;
    }

    public int getFirstHour() {
        return firstHour;
    }

    public void setFirstHour(int firstHour) {
        this.firstHour = firstHour;
    }

    public int getFirstMin() {
        return firstMin;
    }

    public void setFirstMin(int firstMin) {
        this.firstMin = firstMin;
    }

    public int getMedicineIdForPendingIntent() {
        return _ID + 3;
    }
}
